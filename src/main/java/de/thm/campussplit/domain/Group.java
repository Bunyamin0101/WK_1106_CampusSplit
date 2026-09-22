package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "expense_group")
public class Group extends BaseEntity {
  private Instant archivedAt;

  public Instant getArchivedAt() {
    return archivedAt;
  }

  public boolean isArchived() {
    return archivedAt != null;
  }

  public void setArchivedAt(Instant value) {
    archivedAt = value;
  }

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(nullable = false, length = 3, updatable = false)
  private String currency;

  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id")
  private User owner;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String value) {
    this.currency = value;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User value) {
    this.owner = value;
  }
}
