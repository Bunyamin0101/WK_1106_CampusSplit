package de.thm.campussplit.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class LinkAuthorizationResolver implements OAuth2AuthorizationRequestResolver {
  private final DefaultOAuth2AuthorizationRequestResolver delegate;

  public LinkAuthorizationResolver(ClientRegistrationRepository registrations) {
    delegate =
        new DefaultOAuth2AuthorizationRequestResolver(registrations, "/oauth2/authorization");
  }

  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    return bind(request, delegate.resolve(request));
  }

  public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String id) {
    return bind(request, delegate.resolve(request, id));
  }

  private OAuth2AuthorizationRequest bind(
      HttpServletRequest request, OAuth2AuthorizationRequest auth) {
    if (auth == null) return null;
    var session = request.getSession(false);
    if (session != null
        && session.getAttribute(GoogleLinkIntent.KEY) instanceof GoogleLinkIntent intent) {
      var actor =
          org.springframework.security.core.context.SecurityContextHolder.getContext()
              .getAuthentication();
      if (intent.state() != null
          || intent.expires().isBefore(java.time.Instant.now())
          || actor == null
          || !intent.email().equals(actor.getName())) {
        session.removeAttribute(GoogleLinkIntent.KEY);
        throw new org.springframework.security.oauth2.core.OAuth2AuthenticationException(
            "Verknüpfung bitte erneut im Profil starten.");
      }
      session.setAttribute(
          GoogleLinkIntent.KEY,
          new GoogleLinkIntent(intent.email(), intent.expires(), auth.getState()));
      return OAuth2AuthorizationRequest.from(auth)
          .additionalParameters(p -> p.put("prompt", "select_account"))
          .build();
    }
    return auth;
  }
}
