package de.thm.campussplit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class DashboardService {
  private final GroupService groups;
  private final ExpenseService expenses;

  public DashboardService(GroupService groups, ExpenseService expenses) {
    this.groups = groups;
    this.expenses = expenses;
  }

  public record CurrencyTotals(
      String currency, BigDecimal receivable, BigDecimal payable, BigDecimal monthlyExpenses) {}

  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  public List<CurrencyTotals> overview(String actor) {
    var user = groups.currentUser(actor);
    var today = LocalDate.now(java.time.ZoneId.of("Europe/Berlin"));
    var totals = new TreeMap<String, CurrencyTotals>();
    var monthStart = today.withDayOfMonth(1);
    for (var membership : groups.myGroups(actor)) {
      var group = membership.getGroup();
      var summary = expenses.summary(group.getId(), actor, null, null);
      var previous = totals.getOrDefault(group.getCurrency(), emptyTotals(group.getCurrency()));
      var balance =
          summary.balances().stream()
              .filter(b -> b.userId().equals(user.getId()))
              .findFirst()
              .orElseThrow()
              .amount();
      var receivable = previous.receivable();
      var payable = previous.payable();
      var monthlyExpenses = previous.monthlyExpenses();
      if (balance.signum() > 0) {
        receivable = receivable.add(balance);
      } else {
        payable = payable.add(balance.abs());
      }
      for (var expense : summary.expenses()) {
        if (expense.getExpenseDate().isBefore(monthStart)
            || expense.getExpenseDate().isAfter(today)) {
          continue;
        }
        for (var share : expense.getShares()) {
          if (share.getUser().getId().equals(user.getId())) {
            monthlyExpenses = monthlyExpenses.add(share.getShareAmount());
          }
        }
      }
      totals.put(
          group.getCurrency(),
          new CurrencyTotals(group.getCurrency(), receivable, payable, monthlyExpenses));
    }
    return List.copyOf(totals.values());
  }

  private CurrencyTotals emptyTotals(String currency) {
    var zero = new BigDecimal("0.00");
    return new CurrencyTotals(currency, zero, zero, zero);
  }
}
