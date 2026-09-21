package de.thm.campussplit.web;

import java.security.Principal;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class CurrentUserAdvice {
  @ModelAttribute("currentEmail")
  String currentEmail(Principal principal) {
    return principal == null ? null : principal.getName();
  }
}
