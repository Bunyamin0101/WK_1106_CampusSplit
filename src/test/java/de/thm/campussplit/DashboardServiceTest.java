package de.thm.campussplit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class DashboardServiceTest {
  @Test
  void keepsCurrenciesAndDebtsSeparateAndCountsOnlyCurrentUsersMonthlyShares() {
    var groups = mock(GroupService.class);
    var expenses = mock(ExpenseService.class);
    var user = mock(User.class);
    when(user.getId()).thenReturn(1L);
    when(groups.currentUser("anna")).thenReturn(user);
    var euroTrip = membership(10L, "EUR");
    var euroFlat = membership(20L, "EUR");
    var dollarTrip = membership(30L, "USD");
    when(groups.myGroups("anna")).thenReturn(List.of(euroTrip, euroFlat, dollarTrip));
    var today = LocalDate.now(ZoneId.of("Europe/Berlin"));
    var euroTripSummary =
        summary(
            euroTrip,
            "50.00",
            List.of(
                expense(today, user, "12.50"),
                expense(today.withDayOfMonth(1).minusDays(1), user, "99.00"),
                expense(today.plusDays(1), user, "88.00")));
    var euroFlatSummary = summary(euroFlat, "-20.00", List.of());
    var dollarTripSummary = summary(dollarTrip, "7.00", List.of());
    when(expenses.summary(10L, "anna", null, null)).thenReturn(euroTripSummary);
    when(expenses.summary(20L, "anna", null, null)).thenReturn(euroFlatSummary);
    when(expenses.summary(30L, "anna", null, null)).thenReturn(dollarTripSummary);

    var result = new DashboardService(groups, expenses).overview("anna");

    assertThat(result)
        .containsExactly(
            new DashboardService.CurrencyTotals(
                "EUR", new BigDecimal("50.00"), new BigDecimal("20.00"), new BigDecimal("12.50")),
            new DashboardService.CurrencyTotals(
                "USD", new BigDecimal("7.00"), new BigDecimal("0.00"), new BigDecimal("0.00")));
  }

  private Membership membership(Long id, String currency) {
    var group = mock(Group.class);
    when(group.getId()).thenReturn(id);
    when(group.getCurrency()).thenReturn(currency);
    var membership = mock(Membership.class);
    when(membership.getGroup()).thenReturn(group);
    return membership;
  }

  private ExpenseService.GroupSummary summary(
      Membership membership, String balance, List<Expense> expenses) {
    return new ExpenseService.GroupSummary(
        membership.getGroup(),
        List.of(membership),
        expenses,
        List.of(new BalanceService.Balance(1L, "Anna", new BigDecimal(balance))),
        List.of(),
        List.of(),
        1L,
        false);
  }

  private Expense expense(LocalDate date, User user, String amount) {
    var expense = new Expense();
    expense.setExpenseDate(date);
    var share = new ExpenseShare();
    share.setUser(user);
    share.setShareAmount(new BigDecimal(amount));
    var otherUser = mock(User.class);
    when(otherUser.getId()).thenReturn(2L);
    var otherShare = new ExpenseShare();
    otherShare.setUser(otherUser);
    otherShare.setShareAmount(new BigDecimal("100.00"));
    expense.getShares().addAll(List.of(share, otherShare));
    return expense;
  }
}
