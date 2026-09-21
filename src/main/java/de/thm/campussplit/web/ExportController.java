package de.thm.campussplit.web;

import de.thm.campussplit.export.ExportService;
import de.thm.campussplit.service.*;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExportController {
  private final ExpenseService expenses;
  private final ExportService exports;

  public ExportController(ExpenseService expenses, ExportService exports) {
    this.expenses = expenses;
    this.exports = exports;
  }

  @GetMapping("/groups/{groupId}/export")
  ResponseEntity<byte[]> export(
      @PathVariable Long groupId,
      @RequestParam String format,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      Principal principal) {
    var summary = expenses.summary(groupId, principal.getName(), from, to);
    if (!format.equals("pdf") && !format.equals("csv"))
      throw new BusinessException("Bitte PDF oder CSV wählen.");
    boolean pdf = format.equals("pdf");
    try {
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(pdf ? "application/pdf" : "application/zip"))
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=campussplit-" + groupId + (pdf ? ".pdf" : ".zip"))
          .cacheControl(CacheControl.noStore())
          .body(pdf ? exports.pdf(summary, from, to) : exports.csv(summary, from, to));
    } catch (IOException ex) {
      throw new BusinessException(
          "Der Export konnte nicht erstellt werden. Bitte erneut versuchen.");
    }
  }
}
