package de.thm.campussplit;

import static org.assertj.core.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import de.thm.campussplit.integration.FrankfurterCurrencyRateClient;
import de.thm.campussplit.service.BusinessException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FrankfurterClientTest {
  @Test
  void historicalRateAndInvalidResponses() throws Exception {
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    var response =
        new java.util.concurrent.atomic.AtomicReference<>(
            "{\"date\":\"2026-08-14\",\"base\":\"USD\",\"quote\":\"EUR\",\"rate\":0.86}");
    server.createContext(
        "/v2/rate/USD/EUR",
        exchange -> {
          assertThat(exchange.getRequestURI().getQuery()).isEqualTo("date=2026-08-15");
          var body = response.get().getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, body.length);
          exchange.getResponseBody().write(body);
          exchange.close();
        });
    server.start();
    try {
      var client =
          new FrankfurterCurrencyRateClient("http://127.0.0.1:" + server.getAddress().getPort());
      var date = LocalDate.of(2026, 8, 15);
      assertThat(client.getRate("USD", "EUR", date).date()).isEqualTo(LocalDate.of(2026, 8, 14));
      response.set("{\"date\":\"2026-08-16\",\"base\":\"USD\",\"quote\":\"EUR\",\"rate\":0.86}");
      assertThatThrownBy(() -> client.getRate("USD", "EUR", date))
          .isInstanceOf(BusinessException.class);
      response.set("{}");
      assertThatThrownBy(() -> client.getRate("USD", "EUR", date))
          .isInstanceOf(BusinessException.class);
    } finally {
      server.stop(0);
    }
  }
}
