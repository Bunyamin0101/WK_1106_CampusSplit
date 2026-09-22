package de.thm.campussplit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "repayment")
public class Repayment extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  private Group group;

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group value) {
    group = value;
  }

  @ManyToOne(optional = false)
  private User sender;

  public User getSender() {
    return sender;
  }

  public void setSender(User value) {
    sender = value;
  }

  @ManyToOne(optional = false)
  private User recipient;

  public User getRecipient() {
    return recipient;
  }

  public void setRecipient(User value) {
    recipient = value;
  }

  @ManyToOne(optional = false)
  private User recordedBy;

  public User getRecordedBy() {
    return recordedBy;
  }

  public void setRecordedBy(User value) {
    recordedBy = value;
  }

  @Column(nullable = false, precision = 15, scale = 2)
  private java.math.BigDecimal amount;

  public java.math.BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(java.math.BigDecimal value) {
    amount = value;
  }

  @Column(nullable = false)
  private java.time.LocalDate paymentDate;

  public java.time.LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(java.time.LocalDate value) {
    paymentDate = value;
  }

  private java.time.Instant cancelledAt;
  @ManyToOne private User cancelledBy;

  @Column(length = 250)
  private String cancellationReason;

  @Column(unique = true, length = 36)
  private String requestId;

  public java.time.Instant getCancelledAt() {
    return cancelledAt;
  }

  public void setCancelledAt(java.time.Instant value) {
    cancelledAt = value;
  }

  public User getCancelledBy() {
    return cancelledBy;
  }

  public void setCancelledBy(User value) {
    cancelledBy = value;
  }

  public String getCancellationReason() {
    return cancellationReason;
  }

  public void setCancellationReason(String value) {
    cancellationReason = value;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String value) {
    requestId = value;
  }

  public boolean isCancelled() {
    return cancelledAt != null;
  }
}
