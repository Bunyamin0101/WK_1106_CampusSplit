package de.thm.campussplit.web;

import de.thm.campussplit.service.*;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups/{groupId}/expenses/{expenseId}/receipts")
public class ReceiptController {
  private final ReceiptService receipts;

  public ReceiptController(ReceiptService receipts) {
    this.receipts = receipts;
  }

  @PostMapping
  String upload(
      @PathVariable Long groupId,
      @PathVariable Long expenseId,
      @RequestParam MultipartFile file,
      Principal actor,
      RedirectAttributes flash) {
    try {
      receipts.upload(groupId, expenseId, file, actor.getName());
      flash.addFlashAttribute("message", "Beleg hochgeladen.");
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
    }
    return "redirect:/groups/" + groupId;
  }

  @GetMapping("/{id}")
  ResponseEntity<byte[]> download(
      @PathVariable Long groupId,
      @PathVariable Long expenseId,
      @PathVariable Long id,
      Principal actor) {
    var r = receipts.get(groupId, expenseId, id, actor.getName());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(r.getMediaType()))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(r.getFilename(), StandardCharsets.UTF_8)
                .build()
                .toString())
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .header("X-Content-Type-Options", "nosniff")
        .body(r.getData());
  }

  @PostMapping("/{id}/delete")
  String delete(
      @PathVariable Long groupId,
      @PathVariable Long expenseId,
      @PathVariable Long id,
      Principal actor,
      RedirectAttributes flash) {
    receipts.delete(groupId, expenseId, id, actor.getName());
    flash.addFlashAttribute("message", "Beleg entfernt.");
    return "redirect:/groups/" + groupId;
  }
}
