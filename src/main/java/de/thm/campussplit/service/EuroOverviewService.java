package de.thm.campussplit.service;

import de.thm.campussplit.integration.CurrencyRatePort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EuroOverviewService {
  private final CurrencyRatePort rates;

  public EuroOverviewService(CurrencyRatePort rates) {
    this.rates = rates;
  }

  public record Overview(
      DashboardService.CurrencyTotals total,
      List<CurrencyRatePort.ExchangeRate> rates,
      boolean available) {}

  public Overview calculate(List<DashboardService.CurrencyTotals> rows) {
    var receivable = BigDecimal.ZERO;
    var payable = BigDecimal.ZERO;
    var monthlyExpenses = BigDecimal.ZERO;
    var usedRates = new ArrayList<CurrencyRatePort.ExchangeRate>();
    var today = LocalDate.now(ZoneId.of("Europe/Berlin"));
    try {
      for (var row : rows) {
        var rate = BigDecimal.ONE;
        if (!row.currency().equals("EUR")) {
          var quote = rates.getRate(row.currency(), "EUR", today);
          rate = quote.rate();
          usedRates.add(quote);
        }
        receivable = receivable.add(row.receivable().multiply(rate));
        payable = payable.add(row.payable().multiply(rate));
        monthlyExpenses = monthlyExpenses.add(row.monthlyExpenses().multiply(rate));
      }
    } catch (BusinessException ex) {
      // Ohne alle Kurse wäre eine Teilsumme als Gesamtbetrag irreführend.
      return new Overview(null, List.of(), false);
    }
    return new Overview(
        new DashboardService.CurrencyTotals(
            "EUR",
            receivable.setScale(2, RoundingMode.HALF_UP),
            payable.setScale(2, RoundingMode.HALF_UP),
            monthlyExpenses.setScale(2, RoundingMode.HALF_UP)),
        List.copyOf(usedRates),
        true);
  }
}
