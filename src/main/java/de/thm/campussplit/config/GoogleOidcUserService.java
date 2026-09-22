package de.thm.campussplit.config;

import de.thm.campussplit.service.GoogleAccountService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.client.oidc.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class GoogleOidcUserService extends OidcUserService {
  private final GoogleAccountService accounts;

  public GoogleOidcUserService(GoogleAccountService accounts) {
    this.accounts = accounts;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
    if (!"google".equals(request.getClientRegistration().getRegistrationId()))
      throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"));
    // Spring validates signature, issuer, audience, state and nonce before reaching this service.
    var google = super.loadUser(request);
    try {
      var attributes =
          (org.springframework.web.context.request.ServletRequestAttributes)
              org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
      var servletRequest = attributes == null ? null : attributes.getRequest();
      var session = servletRequest == null ? null : servletRequest.getSession(false);
      if (session != null
          && session.getAttribute(GoogleLinkIntent.KEY) instanceof GoogleLinkIntent intent) {
        session.removeAttribute(GoogleLinkIntent.KEY);
        var actor =
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (actor == null
            || !actor.isAuthenticated()
            || !intent.email().equals(actor.getName())
            || intent.expires().isBefore(java.time.Instant.now())
            || intent.state() == null
            || !intent.state().equals(servletRequest.getParameter("state")))
          throw new OAuth2AuthenticationException(new OAuth2Error("invalid_link"));
        var linked =
            accounts.link(
                intent.email(),
                google.getSubject(),
                google.getEmail(),
                Boolean.TRUE.equals(google.getEmailVerified()));
        session.setAttribute("googleLinkSuccess", true);
        return new CampusOidcUser(google, linked.getEmail());
      }
      var local =
          accounts.resolve(
              google.getSubject(),
              google.getEmail(),
              Boolean.TRUE.equals(google.getEmailVerified()),
              google.getFullName());
      return new CampusOidcUser(google, local.getEmail());
    } catch (DataIntegrityViolationException ex) {
      // Concurrent registrations must fail closed, without exposing database details.
      throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
    }
  }
}
