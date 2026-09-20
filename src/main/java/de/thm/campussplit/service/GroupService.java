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
    try {
      Currency.getInstance(code);
    } catch (IllegalArgumentException | NullPointerException ex) {
      throw new BusinessException("Bitte einen gültigen Währungscode eingeben, zum Beispiel EUR.");
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
