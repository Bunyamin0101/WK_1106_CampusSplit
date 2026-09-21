package de.thm.campussplit;

import static org.assertj.core.api.Assertions.*;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.service.BusinessException;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;

class MoneyLogicTest {
  private final SplitService splits = new SplitService();

  @Test
  void remainderFollowsSortedMemberIds() {
    var result =
        splits.calculate(new BigDecimal("10.00"), List.of(3L, 1L, 2L), SplitMethod.EQUAL, Map.of());
    assertThat(result)
        .containsEntry(1L, new BigDecimal("3.34"))
        .containsEntry(2L, new BigDecimal("3.33"))
        .containsEntry(3L, new BigDecimal("3.33"));
  }

  @Test
  void tinyAmountsRemainExact() {
    for (int cents = 1; cents < 100; cents++)
      for (int count = 1; count < 20; count++) {
        var ids = java.util.stream.LongStream.rangeClosed(1, count).boxed().toList();
        var total = BigDecimal.valueOf(cents, 2);
        var result = splits.calculate(total, ids, SplitMethod.EQUAL, Map.of());
        assertThat(result.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo(total);
        assertThat(result.values()).allMatch(v -> v.signum() >= 0);
      }
  }

  @Test
  void validatesIndividualShares() {
    var total = new BigDecimal("10.00");
    assertThatThrownBy(
            () ->
                splits.calculate(
                    total,
                    List.of(1L, 2L),
                    SplitMethod.CUSTOM_AMOUNT,
                    Map.of(1L, new BigDecimal("3.00"), 2L, new BigDecimal("6.99"))))
        .isInstanceOf(BusinessException.class);
    assertThat(
            splits.calculate(
                total,
                List.of(1L, 2L),
                SplitMethod.CUSTOM_AMOUNT,
                Map.of(1L, new BigDecimal("3.00"), 2L, new BigDecimal("7.00"))))
        .hasSize(2);
    assertThatThrownBy(() -> splits.calculate(total, List.of(1L, 1L), SplitMethod.EQUAL, Map.of()))
        .isInstanceOf(BusinessException.class);
    assertThatThrownBy(() -> splits.calculate(total, List.of(), SplitMethod.EQUAL, Map.of()))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void settlementsClearEveryBalance() {
    var balances =
        List.of(
            new BalanceService.Balance(1L, "Anna", new BigDecimal("20.00")),
            new BalanceService.Balance(2L, "Ben", new BigDecimal("-7.50")),
            new BalanceService.Balance(3L, "Clara", new BigDecimal("-12.50")));
    var result = new BalanceService().settlements(balances);
    var remainder = new HashMap<String, BigDecimal>();
    balances.forEach(b -> remainder.put(b.name(), b.amount()));
    result.forEach(
        s -> {
          remainder.compute(s.from(), (k, v) -> v.add(s.amount()));
          remainder.compute(s.to(), (k, v) -> v.subtract(s.amount()));
        });
    assertThat(remainder.values()).allMatch(v -> v.signum() == 0);
  }
}
