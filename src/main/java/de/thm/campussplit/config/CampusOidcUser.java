package de.thm.campussplit.config;

import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/** Preserves validated OIDC claims while exposing the local identity to existing group services. */
public class CampusOidcUser implements OidcUser, java.io.Serializable {
  private final OidcUser delegate;
  private final String localEmail;

  public CampusOidcUser(OidcUser delegate, String localEmail) {
    this.delegate = delegate;
    this.localEmail = localEmail;
  }

  @Override
  public String getName() {
    return localEmail;
  }

  @Override
  public Map<String, Object> getClaims() {
    return delegate.getClaims();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return delegate.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return delegate.getAuthorities();
  }

  @Override
  public OidcIdToken getIdToken() {
    return delegate.getIdToken();
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return delegate.getUserInfo();
  }
}
