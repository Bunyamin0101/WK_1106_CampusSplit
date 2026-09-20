package de.thm.campussplit.service;

import de.thm.campussplit.domain.User;
import de.thm.campussplit.persistence.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public AccountService(UserRepository users, PasswordEncoder encoder) {
    this.users = users;
    this.encoder = encoder;
  }

  public static String normalize(String email) {
    return email.strip().toLowerCase(Locale.ROOT);
  }

  @Transactional
  public User register(String name, String email, String password) {
    email = normalize(email);
    if (users.existsByEmail(email))
      throw new BusinessException("Die Registrierung ist mit dieser E-Mail-Adresse nicht möglich.");
    if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
      throw new BusinessException("Das Passwort ist zu lang (maximal 72 UTF-8-Bytes).");
    var user = new User();
    user.setName(name.strip());
    user.setEmail(email);
    user.setPasswordHash(encoder.encode(password));
    return users.saveAndFlush(user);
  }
}
