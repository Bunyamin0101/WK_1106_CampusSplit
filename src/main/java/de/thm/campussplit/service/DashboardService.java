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
    var totals = new TreeMap<String, BigDecimal[]>();
    for (var membership : groups.myGroups(actor)) {
      var group = membership.getGroup();
      var summary = expenses.summary(group.getId(), actor, null, null);
      var row =
          totals.computeIfAbsent(
              group.getCurrency(),
              k ->
                  new BigDecimal[] {
                    new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00")
                  });
      var balance =
          summary.balances().stream()
              .filter(b -> b.userId().equals(user.getId()))
              .findFirst()
              .orElseThrow()
              .amount();
      if (balance.signum() > 0) row[0] = row[0].add(balance);
      else row[1] = row[1].add(balance.abs());
      for (var expense : summary.expenses())
        if (!expense.getExpenseDate().isBefore(today.withDayOfMonth(1))
            && !expense.getExpenseDate().isAfter(today))
          for (var share : expense.getShares())
            if (share.getUser().getId().equals(user.getId()))
              row[2] = row[2].add(share.getShareAmount());
    }
    return totals.entrySet().stream()
        .map(e -> new CurrencyTotals(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]))
        .toList();
  }
}
