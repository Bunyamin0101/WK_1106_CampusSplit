package de.thm.campussplit.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "expense")
public class Expense extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne(optional = false)
  @JoinColumn(name = "paid_by_id")
  private User paidBy;

  @ManyToOne(optional = false)
  @JoinColumn(name = "created_by_id", updatable = false)
  private User createdBy;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(nullable = false, length = 250)
  private String description;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal originalAmount;

  @Column(nullable = false, length = 3)
  private String originalCurrency;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal settlementAmount;

  @Column(precision = 28, scale = 12)
  private BigDecimal exchangeRate;

  private LocalDate rateDate;

  @Column(nullable = false)
  private LocalDate expenseDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SplitMethod splitMethod;

  @Version private Long version;

  @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id")
  private List<ExpenseShare> shares = new ArrayList<>();

  public List<ExpenseShare> getShares() {
    return shares;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group value) {
    this.group = value;
  }

  public User getPaidBy() {
    return paidBy;
  }

  public void setPaidBy(User value) {
    this.paidBy = value;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User value) {
    this.createdBy = value;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category value) {
    this.category = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public BigDecimal getOriginalAmount() {
    return originalAmount;
  }

  public void setOriginalAmount(BigDecimal value) {
    this.originalAmount = value;
  }

  public String getOriginalCurrency() {
    return originalCurrency;
  }

  public void setOriginalCurrency(String value) {
    this.originalCurrency = value;
  }

  public BigDecimal getSettlementAmount() {
    return settlementAmount;
  }

  public void setSettlementAmount(BigDecimal value) {
    this.settlementAmount = value;
  }

  public BigDecimal getExchangeRate() {
    return exchangeRate;
  }

  public void setExchangeRate(BigDecimal value) {
    this.exchangeRate = value;
  }

  public LocalDate getRateDate() {
    return rateDate;
  }

  public void setRateDate(LocalDate value) {
    this.rateDate = value;
  }

  public LocalDate getExpenseDate() {
    return expenseDate;
  }

  public void setExpenseDate(LocalDate value) {
    this.expenseDate = value;
  }

  public SplitMethod getSplitMethod() {
    return splitMethod;
  }

  public void setSplitMethod(SplitMethod value) {
    this.splitMethod = value;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long value) {
    this.version = value;
  }
}
