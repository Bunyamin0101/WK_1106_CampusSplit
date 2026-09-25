package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.persistence.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepaymentService {
  @PersistenceContext private EntityManager em;
  private final GroupService groups;
  private final ExpenseService expenses;
  private final RepaymentRepository payments;

  public RepaymentService(
      GroupService groups, ExpenseService expenses, RepaymentRepository payments) {
    this.groups = groups;
    this.expenses = expenses;
    this.payments = payments;
  }

  @Transactional
  public void record(
      Long groupId, Long senderId, Long recipientId, BigDecimal amount, String actor) {
    record(groupId, senderId, recipientId, amount, actor, java.util.UUID.randomUUID().toString());
  }

  @Transactional
  public void record(
      Long groupId,
      Long senderId,
      Long recipientId,
      BigDecimal amount,
      String actor,
      String requestId) {
    record(groupId, senderId, recipientId, amount, actor, requestId, null);
  }

  @Transactional
  public void record(Long groupId, Long senderId, Long recipientId, BigDecimal amount,
      String actor, String requestId, Long expenseId) {
    var member = groups.requireMember(groupId, actor);
    if (member.getRole() != Role.ADMIN
        && !member.getUser().getId().equals(senderId)
        && !member.getUser().getId().equals(recipientId))
      throw new AccessDeniedException(
          "Nur Beteiligte oder Gruppenadministratoren dürfen den Ausgleich bestätigen.");
    em.lock(member.getGroup(), LockModeType.PESSIMISTIC_WRITE);
    try {
      java.util.UUID.fromString(requestId);
    } catch (IllegalArgumentException | NullPointerException ex) {
      throw new BusinessException("Bitte die Seite neu laden.");
    }
    if (payments.existsByRequestId(requestId))
      throw new BusinessException(
          "Diese Zahlung wurde bereits erfasst. Bitte die aktuellen Salden prüfen.");
    var summary = expenses.summary(groupId, actor, null, null);
    ExpenseOption selected = null;
    if (expenseId != null) {
      selected = options(summary, senderId, recipientId).stream()
          .filter(option -> option.id().equals(expenseId)).findFirst()
          .orElseThrow(() -> new BusinessException("Für diese Ausgabe ist kein zuordenbarer Anteil mehr offen. Bitte die Seite neu laden."));

    }
    if (amount == null || amount.signum() <= 0 || amount.scale() > 2 || amount.precision() > 15)
      throw new BusinessException("Bitte einen gültigen Betrag angeben.");
    if (selected != null && amount.compareTo(selected.amount()) > 0)
      throw new BusinessException("Der Betrag darf den offenen Anteil dieser Ausgabe nicht überschreiten.");
    final var paymentAmount = amount;
    boolean valid =
        summary.settlements().stream()
            .anyMatch(
                s ->
                    s.fromId().equals(senderId)
                        && s.toId().equals(recipientId)
                        && s.amount().compareTo(paymentAmount) >= 0);
    if (!valid)
      throw new BusinessException(
          "Dieser Vorschlag ist nicht mehr offen. Bitte die aktuellen Salden prüfen.");
    var byId = new java.util.HashMap<Long, User>();
    summary.members().forEach(m -> byId.put(m.getUser().getId(), m.getUser()));
    var p = new Repayment();
    p.setRequestId(requestId);
    if (selected != null) {
      p.setExpenseId(selected.id());
      p.setExpenseDescription(selected.description());
    }
    p.setGroup(member.getGroup());
    p.setSender(byId.get(senderId));
    p.setRecipient(byId.get(recipientId));
    p.setRecordedBy(member.getUser());
    p.setAmount(amount);
    p.setPaymentDate(LocalDate.now());
    payments.saveAndFlush(p);
  }

  public record ExpenseOption(Long id, String description, LocalDate date, BigDecimal amount) {}

  public java.util.List<ExpenseOption> options(ExpenseService.GroupSummary summary,
      Long senderId, Long recipientId) {
    var limit = summary.settlements().stream()
        .filter(s -> s.fromId().equals(senderId) && s.toId().equals(recipientId))
        .map(BalanceService.Settlement::amount).findFirst().orElse(BigDecimal.ZERO);
    var remaining = new java.util.LinkedHashMap<Long, BigDecimal>();
    var candidates = summary.expenses().stream()
        .filter(e -> e.getPaidBy().getId().equals(recipientId))
        .sorted(java.util.Comparator.comparing(Expense::getExpenseDate).thenComparing(Expense::getId))
        .toList();
    for (var expense : candidates) {
      var share = expense.getShares().stream().filter(s -> s.getUser().getId().equals(senderId))
          .map(ExpenseShare::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
      remaining.put(expense.getId(), share);
    }
    var unassigned = BigDecimal.ZERO;
    for (var payment : summary.repayments()) {
      if (payment.isCancelled() || !payment.getSender().getId().equals(senderId)
          || !payment.getRecipient().getId().equals(recipientId)) continue;
      if (payment.getExpenseId() == null) unassigned = unassigned.add(payment.getAmount());
      else remaining.computeIfPresent(payment.getExpenseId(),
          (id, value) -> value.subtract(payment.getAmount()).max(BigDecimal.ZERO));
    }
    var result = new java.util.ArrayList<ExpenseOption>();
    for (var expense : candidates) {
      var value = remaining.get(expense.getId());
      var applied = unassigned.min(value);
      unassigned = unassigned.subtract(applied);
      value = value.subtract(applied).min(limit);
      if (value.signum() > 0) result.add(new ExpenseOption(expense.getId(),
          expense.getDescription(), expense.getExpenseDate(), value));
    }
    return result;
  }

  @Transactional
  public void cancel(Long groupId, Long paymentId, String reason, String actor) {
    var member = groups.requireMember(groupId, actor);
    em.lock(member.getGroup(), LockModeType.PESSIMISTIC_WRITE);
    var payment =
        payments
            .findById(paymentId)
            .filter(p -> p.getGroup().getId().equals(groupId))
            .orElseThrow(() -> new BusinessException("Rückzahlung nicht gefunden."));
    if (member.getRole() != Role.ADMIN
        && !member.getUser().getId().equals(payment.getSender().getId())
        && !member.getUser().getId().equals(payment.getRecipient().getId()))
      throw new AccessDeniedException(
          "Nur Beteiligte oder Gruppenadministratoren dürfen stornieren.");
    if (payment.isCancelled())
      throw new BusinessException("Diese Rückzahlung wurde bereits storniert.");
    if (reason == null || reason.isBlank() || reason.strip().length() > 250)
      throw new BusinessException("Bitte einen Stornogrund mit 1 bis 250 Zeichen angeben.");
    payment.setCancelledAt(java.time.Instant.now());
    payment.setCancelledBy(member.getUser());
    payment.setCancellationReason(reason.strip());
    payments.saveAndFlush(payment);
  }
}
