package de.thm.campussplit.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager em;
  private final GroupService groups;
  private final PasswordEncoder passwords;

  public ProfileService(GroupService groups, PasswordEncoder passwords) {
    this.groups = groups;
    this.passwords = passwords;
  }

  @Transactional
  public void rename(String actor, String name) {
    if (name == null || name.isBlank() || name.strip().length() > 100)
      throw new BusinessException("Bitte einen Anzeigenamen mit 1 bis 100 Zeichen eingeben.");
    var user = groups.currentUser(actor);
    em.lock(user, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    em.refresh(user);
    user.setName(name.strip());
  }

  @Transactional(readOnly = true)
  public void authorizeLink(String actor, String password) {
    var user = groups.currentUser(actor);
    if (user.getGoogleSubject() != null)
      throw new BusinessException("Dieses Konto ist bereits mit Google verknüpft.");
    if (password == null
        || user.getPasswordHash() == null
        || !passwords.matches(password, user.getPasswordHash()))
      throw new BusinessException("Bitte dein aktuelles CampusSplit-Passwort bestätigen.");
  }
}
