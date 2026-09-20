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

  public record Settlement(String from, String to, BigDecimal amount) {}

  public List<Balance> calculate(List<Membership> members, List<Expense> expenses) {
    Map<Long, BigDecimal> amounts = new LinkedHashMap<>();
    members.forEach(m -> amounts.put(m.getUser().getId(), new BigDecimal("0.00")));
    for (var e : expenses) {
      amounts.compute(e.getPaidBy().getId(), (id, v) -> v.add(e.getSettlementAmount()));
      e.getShares()
          .forEach(
              s -> amounts.compute(s.getUser().getId(), (id, v) -> v.subtract(s.getShareAmount())));
    }
    return members.stream()
        .map(
            m ->
                new Balance(
                    m.getUser().getId(), m.getUser().getName(), amounts.get(m.getUser().getId())))
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
    int i = 0, j = 0;
    while (i < debtors.size() && j < creditors.size()) {
      var d = debtors.get(i);
      var c = creditors.get(j);
      var value = remaining.get(d.userId()).min(remaining.get(c.userId()));
      result.add(new Settlement(d.name(), c.name(), value));
      remaining.compute(d.userId(), (k, v) -> v.subtract(value));
      remaining.compute(c.userId(), (k, v) -> v.subtract(value));
      if (remaining.get(d.userId()).signum() == 0) i++;
      if (remaining.get(c.userId()).signum() == 0) j++;
    }
    return result;
  }
}
