package de.thm.campussplit.integration;

import de.thm.campussplit.service.BusinessException;
import java.net.http.HttpClient;
import java.time.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FrankfurterCurrencyRateClient implements CurrencyRatePort {
  private final RestClient client;

  public FrankfurterCurrencyRateClient(@Value("${campussplit.currency.base-url}") String baseUrl) {
    var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    var factory = new JdkClientHttpRequestFactory(http);
    factory.setReadTimeout(Duration.ofSeconds(5));
    client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
  }

  public ExchangeRate getRate(String from, String to, LocalDate date) {
    try {
      var result =
          client
              .get()
              .uri("/v2/rate/{from}/{to}?date={date}", from, to, date)
              .retrieve()
              .body(ExchangeRate.class);
      if (result == null
          || !from.equalsIgnoreCase(result.base())
          || !to.equalsIgnoreCase(result.quote())
          || result.date() == null
          || result.date().isAfter(date)
          || result.rate() == null
          || result.rate().signum() <= 0
          || result.rate().scale() > 12
          || result.rate().precision() > 28)
        throw new IllegalArgumentException("Ungültige Kursantwort");
      return result;
    } catch (RuntimeException ex) {
      throw new BusinessException(
          "Für diese Währung konnte aktuell kein Kurs ermittelt werden. Bitte später erneut"
              + " versuchen.");
    }
  }
}
