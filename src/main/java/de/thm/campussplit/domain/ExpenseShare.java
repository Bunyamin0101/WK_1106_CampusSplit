package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(
    name = "expense_share",
    uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "user_id"}))
public class ExpenseShare extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "expense_id")
  private Expense expense;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal shareAmount;

  public Expense getExpense() {
    return expense;
  }

  public void setExpense(Expense value) {
    this.expense = value;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User value) {
    this.user = value;
  }

  public BigDecimal getShareAmount() {
    return shareAmount;
  }

  public void setShareAmount(BigDecimal value) {
    this.shareAmount = value;
  }
}
