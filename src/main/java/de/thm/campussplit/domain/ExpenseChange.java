package de.thm.campussplit.domain;

import jakarta.persistence.*;

@Entity
public class ExpenseChange extends BaseEntity {
  @Column(nullable = false)
  private Long groupId;

  @Column(nullable = false)
  private Long expenseId;

  @Column(nullable = false)
  private Long actorId;

  @Column(nullable = false)
  private String actorName;

  @Column(nullable = false, columnDefinition = "text")
  private String details;

  protected ExpenseChange() {}

  public ExpenseChange(Long groupId, Long expenseId, User actor, String details) {
    this.groupId = groupId;
    this.expenseId = expenseId;
    this.actorId = actor.getId();
    this.actorName = actor.getName();
    this.details = details;
  }

  public Long getExpenseId() {
    return expenseId;
  }

  public Long getActorId() {
    return actorId;
  }

  public String getActorName() {
    return actorName;
  }

  public String getDetails() {
    return details;
  }
}
