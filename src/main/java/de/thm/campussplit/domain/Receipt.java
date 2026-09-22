package de.thm.campussplit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "receipt")
public class Receipt extends BaseEntity {
  @ManyToOne(optional = false)
  private Expense expense;

  public Expense getExpense() {
    return expense;
  }

  public void setExpense(Expense value) {
    expense = value;
  }

  @Column(nullable = false, length = 180)
  private String filename;

  public String getFilename() {
    return filename;
  }

  public void setFilename(String value) {
    filename = value;
  }

  @Column(nullable = false, length = 40)
  private String mediaType;

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String value) {
    mediaType = value;
  }

  @Column(nullable = false)
  private byte[] data;

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] value) {
    data = value;
  }

  @ManyToOne(optional = false)
  private User uploadedBy;

  public User getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(User value) {
    uploadedBy = value;
  }
}
