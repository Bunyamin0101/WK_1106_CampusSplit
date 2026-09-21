package de.thm.campussplit.web;

import jakarta.validation.constraints.*;
import java.util.*;

public class RegistrationForm {
  @NotBlank
  @Size(max = 100)
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

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

  @NotBlank
  @Size(min = 10, max = 72)
  private String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String value) {
    this.password = value;
  }
}
