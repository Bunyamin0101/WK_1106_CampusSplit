package de.thm.campussplit.web;

import jakarta.validation.constraints.*;
import java.util.*;

public class MemberForm {
  @NotBlank
  @Email
  @Size(max = 254)
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }
}
