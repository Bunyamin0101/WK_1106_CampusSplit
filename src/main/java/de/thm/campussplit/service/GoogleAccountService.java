package de.thm.campussplit.service;

import de.thm.campussplit.domain.User;
import de.thm.campussplit.persistence.UserRepository;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.oauth2.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleAccountService {
  private final UserRepository users;
  private final Validator validator;

  public GoogleAccountService(UserRepository users, Validator validator) {
    this.users = users;
    this.validator = validator;
  }

  private record Identity(
      @NotBlank @Size(max = 255) String subject, @NotBlank @Email @Size(max = 254) String email) {}

  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager em;

  @Transactional
  public User link(String actor, String subject, String email, boolean verified) {
    if (!verified || !validator.validate(new Identity(subject, email)).isEmpty())
      throw new OAuth2AuthenticationException(new OAuth2Error("invalid_google_identity"));
    var local =
        users
            .findByEmail(actor)
            .orElseThrow(
                () -> new OAuth2AuthenticationException(new OAuth2Error("account_conflict")));
    em.lock(local, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    em.refresh(local);
    if (local.getGoogleSubject() != null && !local.getGoogleSubject().equals(subject))
      throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
    var existing = users.findByGoogleSubject(subject);
    if (existing.isPresent() && !existing.get().getId().equals(local.getId()))
      throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
    local.setGoogleSubject(subject);
    return users.saveAndFlush(local);
  }

  @Transactional
  public User resolve(String subject, String email, boolean verified, String name) {
    if (!verified || !validator.validate(new Identity(subject, email)).isEmpty())
      throw new OAuth2AuthenticationException(new OAuth2Error("invalid_google_identity"));
    // Google's subject is stable. Email changes must never reassign an existing identity.
    var existing = users.findByGoogleSubject(subject);
    if (existing.isPresent()) return existing.get();
    var normalized = AccountService.normalize(email);
    if (users.existsByEmail(normalized))
      throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
    var user = new User();
    String displayName = name == null || name.isBlank() ? "CampusSplit-Mitglied" : name.strip();
    user.setName(displayName.substring(0, Math.min(displayName.length(), 100)));
    user.setEmail(normalized);
    user.setGoogleSubject(subject);
    return users.saveAndFlush(user);
  }
}
