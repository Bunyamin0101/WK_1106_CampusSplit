package de.thm.campussplit.service;

import de.thm.campussplit.domain.SplitMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ExpenseCommand {
  @NotBlank
  @Size(max = 250)
  private String description;

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  @NotNull
  @DecimalMin("0.01")
  @Digits(integer = 13, fraction = 2)
  private BigDecimal amount;

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal value) {
    this.amount = value;
  }

  @NotBlank
  @Pattern(regexp = "[A-Z]{3}")
  private String currency;

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String value) {
    this.currency = value;
  }

  @NotNull @PastOrPresent private LocalDate date = LocalDate.now();

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate value) {
    this.date = value;
  }

  @NotNull private Long payerId;

  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long value) {
    this.payerId = value;
  }

  private Long categoryId;

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long value) {
    this.categoryId = value;
  }

  @NotNull private SplitMethod splitMethod = SplitMethod.EQUAL;

  public SplitMethod getSplitMethod() {
    return splitMethod;
  }

  public void setSplitMethod(SplitMethod value) {
    this.splitMethod = value;
  }

  @NotEmpty private List<Long> participants = new ArrayList<>();

  public List<Long> getParticipants() {
    return participants;
  }

  public void setParticipants(List<Long> value) {
    this.participants = value;
  }

  private Map<Long, BigDecimal> custom = new LinkedHashMap<>();

  public Map<Long, BigDecimal> getCustom() {
    return custom;
  }

  public void setCustom(Map<Long, BigDecimal> value) {
    this.custom = value;
  }

  private Long version;

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long value) {
    this.version = value;
  }
}
