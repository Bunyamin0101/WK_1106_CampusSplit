package de.thm.campussplit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.thm.campussplit.integration.CurrencyRatePort;
import de.thm.campussplit.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class EuroOverviewServiceTest {
  private final CurrencyRatePort rates = mock(CurrencyRatePort.class);
  private final EuroOverviewService service = new EuroOverviewService(rates);

  private DashboardService.CurrencyTotals row(
      String currency, String credit, String debt, String monthly) {
    return new DashboardService.CurrencyTotals(
        currency, new BigDecimal(credit), new BigDecimal(debt), new BigDecimal(monthly));
  }

  @Test
  void convertsUsdAndKeepsDebtSeparateFromCredit() {
    when(rates.getRate(eq("USD"), eq("EUR"), any()))
        .thenReturn(
            new CurrencyRatePort.ExchangeRate(
                "USD", "EUR", LocalDate.of(2026, 9, 21), new BigDecimal("0.85")));
    var result =
        service.calculate(
            List.of(row("EUR", "37.40", "44.00", "300.00"), row("USD", "0", "20", "112")));
    assertThat(result.available()).isTrue();
    assertThat(result.total()).isEqualTo(row("EUR", "37.40", "61.00", "395.20"));
    assertThat(result.rates()).hasSize(1);
  }

  @Test
  void euroOnlyNeedsNoExternalRate() {
    assertThat(service.calculate(List.of(row("EUR", "1", "2", "3"))).total())
        .isEqualTo(row("EUR", "1.00", "2.00", "3.00"));
    verifyNoInteractions(rates);
  }

  @Test
  void missingRateDoesNotShowPartialTotal() {
    when(rates.getRate(any(), any(), any())).thenThrow(new BusinessException("Unavailable"));
    var result = service.calculate(List.of(row("EUR", "1", "2", "3"), row("USD", "1", "2", "3")));
    assertThat(result.available()).isFalse();
    assertThat(result.total()).isNull();
  }
}
