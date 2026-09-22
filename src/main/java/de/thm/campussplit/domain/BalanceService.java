package de.thm.campussplit.domain;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
  public record Balance(Long userId, String name, BigDecimal amount) {
    public String status() {
      return amount.signum() > 0 ? "CREDIT" : amount.signum() < 0 ? "DEBT" : "BALANCED";
    }

    public String label() {
      return amount.signum() > 0
          ? "bekommt zurück"
          : amount.signum() < 0 ? "schuldet" : "ausgeglichen";
    }
  }

  public record Settlement(String from, String to, BigDecimal amount, Long fromId, Long toId) {}

  public List<Balance> calculate(List<Membership> members, List<Expense> expenses) {
    return calculate(members, expenses, List.of());
  }

  public List<Balance> calculate(
      List<Membership> members, List<Expense> expenses, List<Repayment> payments) {
    Map<Long, BigDecimal> amounts = new LinkedHashMap<>();
    members.forEach(
        membership -> amounts.put(membership.getUser().getId(), new BigDecimal("0.00")));
    for (var expense : expenses) {
      amounts.compute(
          expense.getPaidBy().getId(),
          (id, currentAmount) -> currentAmount.add(expense.getSettlementAmount()));
      expense
          .getShares()
          .forEach(
              share ->
                  amounts.compute(
                      share.getUser().getId(),
                      (id, currentAmount) -> currentAmount.subtract(share.getShareAmount())));
    }
    for (var payment : payments) {
      if (payment.isCancelled()) continue;
      amounts.compute(
          payment.getSender().getId(),
          (id, currentAmount) -> currentAmount.add(payment.getAmount()));
      amounts.compute(
          payment.getRecipient().getId(),
          (id, currentAmount) -> currentAmount.subtract(payment.getAmount()));
    }
    return members.stream()
        .map(
            membership ->
                new Balance(
                    membership.getUser().getId(),
                    membership.getUser().getName(),
                    amounts.get(membership.getUser().getId())))
        .toList();
  }

  public List<Settlement> settlements(List<Balance> balances) {
    if (balances.stream().map(Balance::amount).reduce(BigDecimal.ZERO, BigDecimal::add).signum()
        != 0) throw new IllegalArgumentException("Salden müssen in Summe null sein.");
    var debtors = balances.stream().filter(b -> b.amount().signum() < 0).toList();
    var creditors = balances.stream().filter(b -> b.amount().signum() > 0).toList();
    var remaining = new HashMap<Long, BigDecimal>();
    balances.forEach(b -> remaining.put(b.userId(), b.amount().abs()));
    var result = new ArrayList<Settlement>();
    int debtorIndex = 0, creditorIndex = 0;
    while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {
      var debtor = debtors.get(debtorIndex);
      var creditor = creditors.get(creditorIndex);
      var value = remaining.get(debtor.userId()).min(remaining.get(creditor.userId()));
      result.add(
          new Settlement(
              debtor.name(), creditor.name(), value, debtor.userId(), creditor.userId()));
      remaining.compute(debtor.userId(), (k, currentAmount) -> currentAmount.subtract(value));
      remaining.compute(creditor.userId(), (k, currentAmount) -> currentAmount.subtract(value));
      if (remaining.get(debtor.userId()).signum() == 0) debtorIndex++;
      if (remaining.get(creditor.userId()).signum() == 0) creditorIndex++;
    }
    return result;
  }
}
