package de.thm.campussplit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "campussplit.google.enabled", havingValue = "true")
public class GoogleLoginConfig {
  @Bean
  ClientRegistrationRepository googleClientRegistration(
      @Value("${campussplit.google.client-id}") String clientId,
      @Value("${campussplit.google.client-secret}") String clientSecret) {
    if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
      throw new IllegalStateException(
          "Google-Login ist aktiviert. Bitte GOOGLE_CLIENT_ID und GOOGLE_CLIENT_SECRET lokal"
              + " setzen.");
    }
    var google =
        CommonOAuth2Provider.GOOGLE
            .getBuilder("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope("openid", "profile", "email")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .build();
    return new InMemoryClientRegistrationRepository(google);
  }
}
