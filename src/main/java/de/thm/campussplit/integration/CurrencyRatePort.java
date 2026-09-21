package de.thm.campussplit.integration;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CurrencyRatePort {
  record ExchangeRate(String base, String quote, LocalDate date, BigDecimal rate) {}

  ExchangeRate getRate(String from, String to, LocalDate date);
}
