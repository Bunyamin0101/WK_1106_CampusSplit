package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.integration.CurrencyRatePort;
import de.thm.campussplit.persistence.*;
import java.math.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@Transactional(readOnly = true)
public class ExpenseService {
  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;
  private final ExpenseHistoryService history;
  private final RepaymentRepository repayments;
  private final GroupService groups;
  private final ExpenseRepository expenses;
  private final CategoryRepository categories;
  private final CurrencyRatePort rates;
  private final SplitService splits;
  private final BalanceService balances;

  public ExpenseService(
      GroupService groups,
      ExpenseRepository expenses,
      CategoryRepository categories,
      CurrencyRatePort rates,
      SplitService splits,
      BalanceService balances,
      RepaymentRepository repayments,
      ExpenseHistoryService history) {
    this.history = history;
    this.repayments = repayments;
    this.groups = groups;
    this.expenses = expenses;
    this.categories = categories;
    this.rates = rates;
    this.splits = splits;
    this.balances = balances;
  }

  public record GroupSummary(
      Group group,
      List<Membership> members,
      List<Expense> expenses,
      List<BalanceService.Balance> balances,
      List<BalanceService.Settlement> settlements,
      List<Repayment> repayments,
      Long actorId,
      boolean admin) {}

  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  public GroupSummary summary(Long groupId, String actor, LocalDate from, LocalDate to) {
    var membership = groups.requireMember(groupId, actor);
    if (from != null && to != null && from.isAfter(to))
      throw new BusinessException("Das Startdatum darf nicht nach dem Enddatum liegen.");
    var members = groups.members(groupId, actor);
    var rows =
        expenses.findByGroupIdOrderByExpenseDateDescIdDesc(groupId).stream()
            .filter(
                expense ->
                    (from == null || !expense.getExpenseDate().isBefore(from))
                        && (to == null || !expense.getExpenseDate().isAfter(to)))
            .toList();
    rows.forEach(expense -> expense.getShares().size());
    var payments =
        repayments.findByGroupIdOrderByPaymentDateDescIdDesc(groupId).stream()
            .filter(
                p ->
                    (from == null || !p.getPaymentDate().isBefore(from))
                        && (to == null || !p.getPaymentDate().isAfter(to)))
            .toList();
    var calculated = balances.calculate(members, rows, payments);
    return new GroupSummary(
        membership.getGroup(),
        members,
        rows,
        calculated,
        balances.settlements(calculated),
        payments,
        membership.getUser().getId(),
        membership.getRole() == Role.ADMIN);
  }

  public List<Category> categories() {
    return categories.findAllByOrderByName();
  }

  public ExpenseCommand editForm(Long groupId, Long id, String actor) {
    groups.requireMember(groupId, actor);
    var expense = find(groupId, id);
    var form = new ExpenseCommand();
    form.setDescription(expense.getDescription());
    form.setAmount(expense.getOriginalAmount());
    form.setCurrency(expense.getOriginalCurrency());
    form.setDate(expense.getExpenseDate());
    form.setPayerId(expense.getPaidBy().getId());
    form.setCategoryId(expense.getCategory() == null ? null : expense.getCategory().getId());
    form.setSplitMethod(expense.getSplitMethod());
    form.setVersion(expense.getVersion());
    form.setParticipants(expense.getShares().stream().map(s -> s.getUser().getId()).toList());
    expense.getShares().forEach(s -> form.getCustom().put(s.getUser().getId(), s.getShareAmount()));
    return form;
  }

  private Expense find(Long groupId, Long id) {
    return expenses
        .findByIdAndGroupId(id, groupId)
        .orElseThrow(() -> new BusinessException("Ausgabe nicht gefunden."));
  }

  @Transactional
  public void save(Long groupId, Long id, ExpenseCommand form, String actor) {
    var membership = groups.requireMember(groupId, actor);
    var group = membership.getGroup();
    entityManager.lock(group, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    var memberList = groups.members(groupId, actor);
    var membersById = new HashMap<Long, User>();
    memberList.forEach(m -> membersById.put(m.getUser().getId(), m.getUser()));
    if (!membersById.containsKey(form.getPayerId())
        || form.getParticipants() == null
        || !membersById.keySet().containsAll(form.getParticipants()))
      throw new BusinessException("Zahler und Beteiligte müssen Mitglieder dieser Gruppe sein.");
    GroupService.validateCurrency(form.getCurrency());
    if (form.getAmount() == null
        || form.getAmount().signum() <= 0
        || form.getAmount().scale() > 2
        || form.getAmount().precision() > 15
        || form.getDate() == null
        || form.getDate().isAfter(LocalDate.now()))
      throw new BusinessException("Bitte Betrag und Datum prüfen.");
    var expense = id == null ? new Expense() : find(groupId, id);
    if (id != null && !Objects.equals(expense.getVersion(), form.getVersion()))
      throw new BusinessException("Die Ausgabe wurde inzwischen geändert. Bitte erneut öffnen.");
    // Shares are inverse child entities; force an aggregate version change even when only shares
    // change.
    if (id != null)
      entityManager.lock(expense, jakarta.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    var before = id == null ? Map.<String, String>of() : history.snapshot(expense);
    boolean sameConversion =
        id != null
            && Objects.equals(expense.getOriginalCurrency(), form.getCurrency())
            && expense.getOriginalAmount().compareTo(form.getAmount()) == 0
            && Objects.equals(expense.getExpenseDate(), form.getDate());
    BigDecimal rate = null;
    LocalDate rateDate = null;
    BigDecimal total = form.getAmount().setScale(2);
    if (!form.getCurrency().equals(group.getCurrency())) {
      if (sameConversion) {
        rate = expense.getExchangeRate();
        rateDate = expense.getRateDate();
      } else {
        var quote = rates.getRate(form.getCurrency(), group.getCurrency(), form.getDate());
        rate = quote.rate();
        rateDate = quote.date();
      }
      total = total.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    if (total.signum() <= 0 || total.precision() > 15)
      throw new BusinessException(
          "Der umgerechnete Betrag liegt außerhalb des unterstützten Bereichs.");
    var shares =
        splits.calculate(total, form.getParticipants(), form.getSplitMethod(), form.getCustom());
    var category =
        form.getCategoryId() == null
            ? null
            : categories
                .findById(form.getCategoryId())
                .orElseThrow(() -> new BusinessException("Kategorie nicht gefunden."));
    expense.setGroup(group);
    if (id == null) expense.setCreatedBy(membership.getUser());
    expense.setPaidBy(membersById.get(form.getPayerId()));
    expense.setDescription(form.getDescription().strip());
    expense.setOriginalAmount(form.getAmount());
    expense.setOriginalCurrency(form.getCurrency());
    expense.setSettlementAmount(total);
    expense.setExchangeRate(rate);
    expense.setRateDate(rateDate);
    expense.setExpenseDate(form.getDate());
    expense.setCategory(category);
    expense.setSplitMethod(form.getSplitMethod());
    updateShares(expense, shares, membersById);
    expenses.saveAndFlush(expense);
    history.changed(expense, membership.getUser(), before);
  }

  private void updateShares(
      Expense expense, Map<Long, BigDecimal> shares, Map<Long, User> membersById) {
    // Reuse existing shares to avoid delete/insert ordering conflicts with the unique key.
    expense.getShares().removeIf(s -> !shares.containsKey(s.getUser().getId()));
    shares.forEach(
        (userId, value) -> {
          var share =
              expense.getShares().stream()
                  .filter(s -> s.getUser().getId().equals(userId))
                  .findFirst()
                  .orElseGet(
                      () -> {
                        var added = new ExpenseShare();
                        added.setExpense(expense);
                        added.setUser(membersById.get(userId));
                        expense.getShares().add(added);
                        return added;
                      });
          share.setShareAmount(value);
        });
  }

  @Transactional
  public void delete(Long groupId, Long id, Long version, String actor) {
    var membership = groups.requireMember(groupId, actor);
    entityManager.lock(membership.getGroup(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    var expense = find(groupId, id);
    if (!Objects.equals(expense.getVersion(), version))
      throw new BusinessException("Die Ausgabe wurde inzwischen geändert. Bitte Seite neu laden.");
    history.record(expense, membership.getUser(), "Ausgabe gelöscht: " + expense.getDescription());
    expenses.delete(expense);
    expenses.flush();
  }
}
