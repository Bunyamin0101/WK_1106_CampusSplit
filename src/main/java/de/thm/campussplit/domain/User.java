package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "app_user")
public class User extends BaseEntity {
  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 254)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String value) {
    this.passwordHash = value;
  }
}
