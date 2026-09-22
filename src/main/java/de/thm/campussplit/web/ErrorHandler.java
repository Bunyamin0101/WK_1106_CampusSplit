package de.thm.campussplit.web;

import de.thm.campussplit.service.BusinessException;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorHandler {
  @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  String uploadSize(Model model) {
    model.addAttribute("message", "Die Datei ist zu groß. Bitte einen Beleg bis 10 MB auswählen.");
    return "error";
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String business(BusinessException ex, Model model) {
    model.addAttribute("message", ex.getMessage());
    return "error";
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  String denied(Model model) {
    model.addAttribute("message", "Du hast keinen Zugriff auf diese Gruppe oder Aktion.");
    return "error";
  }

  @ExceptionHandler({
    DataIntegrityViolationException.class,
    OptimisticLockingFailureException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  String conflict(Model model) {
    model.addAttribute(
        "message",
        "Die Daten wurden inzwischen geändert oder der Eintrag existiert bereits. Bitte neu"
            + " laden.");
    return "error";
  }
}
