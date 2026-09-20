package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(
    name = "membership",
    uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class Membership extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Role role;

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group value) {
    this.group = value;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User value) {
    this.user = value;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role value) {
    this.role = value;
  }
}
