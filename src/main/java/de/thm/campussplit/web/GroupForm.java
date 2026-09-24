package de.thm.campussplit.web;

import jakarta.validation.constraints.*;
import java.util.*;

public class GroupForm {
  @NotBlank
  @Size(max = 100)
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  @Size(max = 500)
  private String description;

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  @NotBlank
  @Pattern(regexp = "EUR|USD", message = "Bitte EUR oder USD wählen.")
  private String currency = "EUR";

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String value) {
    this.currency = value;
  }
}
