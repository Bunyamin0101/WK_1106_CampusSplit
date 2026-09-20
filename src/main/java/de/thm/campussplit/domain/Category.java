package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {
  @Column(nullable = false, unique = true, length = 100)
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }
}
