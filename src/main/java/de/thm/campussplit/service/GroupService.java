package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.persistence.*;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GroupService {
  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager em;
  private final GroupRepository groups;
  private final MembershipRepository members;
  private final UserRepository users;

  public GroupService(GroupRepository groups, MembershipRepository members, UserRepository users) {
    this.groups = groups;
    this.members = members;
    this.users = users;
  }

  public User currentUser(String email) {
    return users
        .findByEmail(email)
        .orElseThrow(() -> new AccessDeniedException("Bitte erneut anmelden."));
  }

  public Membership requireMember(Long groupId, String email) {
    return members
        .findByGroupIdAndUserId(groupId, currentUser(email).getId())
        .orElseThrow(() -> new AccessDeniedException("Kein Zugriff auf diese Gruppe."));
  }

  public List<Membership> myGroups(String email) {
    return members.findByUserIdOrderById(currentUser(email).getId());
  }

  public List<Membership> members(Long groupId, String email) {
    requireMember(groupId, email);
    return members.findByGroupIdOrderByUserId(groupId);
  }

  public static void validateCurrency(String code) {
    if (!"EUR".equals(code) && !"USD".equals(code)) {
      throw new BusinessException("Bitte Euro (EUR) oder US-Dollar (USD) wählen.");
    }
  }

  @Transactional
  public Group create(String name, String description, String currency, String email) {
    validateCurrency(currency);
    var user = currentUser(email);
    var group = new Group();
    group.setName(name.strip());
    group.setDescription(description);
    group.setCurrency(currency);
    group.setOwner(user);
    groups.save(group);
    var membership = new Membership();
    membership.setGroup(group);
    membership.setUser(user);
    membership.setRole(Role.ADMIN);
    members.save(membership);
    return group;
  }

  @Transactional
  public void setArchived(Long groupId, boolean archived, String actor) {
    var membership = requireMember(groupId, actor);
    if (membership.getRole() != Role.ADMIN)
      throw new AccessDeniedException(
          "Nur Gruppenadministratoren dürfen Gruppen archivieren oder wiederherstellen.");
    var group = membership.getGroup();
    em.lock(group, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    if (group.isArchived() != archived)
      group.setArchivedAt(archived ? java.time.Instant.now() : null);
  }

  @Transactional
  public void delete(Long groupId, String confirmation, String actor) {
    var membership = requireMember(groupId, actor);
    if (membership.getRole() != Role.ADMIN) {
      throw new AccessDeniedException("Nur Gruppenadministratoren dürfen Gruppen löschen.");
    }
    var group = membership.getGroup();
    em.lock(group, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    if (confirmation == null || !group.getName().equals(confirmation.strip())) {
      throw new BusinessException("Bitte den Gruppennamen zur Bestätigung genau eingeben.");
    }
    // Abhängige Datensätze zuerst entfernen; alle Schritte gehören zu einer Transaktion.
    for (var sql :
        List.of(
            "DELETE FROM receipt WHERE expense_id IN (SELECT id FROM expense WHERE group_id = :id)",
            "DELETE FROM expense_share WHERE expense_id IN (SELECT id FROM expense WHERE group_id ="
                + " :id)",
            "DELETE FROM expense_change WHERE group_id = :id",
            "DELETE FROM repayment WHERE group_id = :id",
            "DELETE FROM expense WHERE group_id = :id",
            "DELETE FROM membership WHERE group_id = :id",
            "DELETE FROM expense_group WHERE id = :id")) {
      em.createNativeQuery(sql).setParameter("id", groupId).executeUpdate();
    }
    em.clear();
  }

  @Transactional
  public void addMember(Long groupId, String memberEmail, String actor) {
    var membership = requireMember(groupId, actor);
    if (membership.getRole() != Role.ADMIN)
      throw new AccessDeniedException("Nur Gruppenadministratoren dürfen Mitglieder hinzufügen.");
    var user =
        users
            .findByEmail(AccountService.normalize(memberEmail))
            .orElseThrow(
                () ->
                    new BusinessException(
                        "Diese Person muss sich zuerst bei CampusSplit registrieren."));
    if (members.findByGroupIdAndUserId(groupId, user.getId()).isPresent())
      throw new BusinessException("Diese Person ist bereits Mitglied.");
    var added = new Membership();
    added.setGroup(membership.getGroup());
    added.setUser(user);
    added.setRole(Role.MEMBER);
    members.saveAndFlush(added);
  }
}
