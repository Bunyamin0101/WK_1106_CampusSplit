package de.thm.campussplit.web;

import de.thm.campussplit.service.*;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups/{groupId}")
public class GroupController {
  private final ExpenseHistoryService history;
  private final RepaymentService repayments;
  private final ReceiptService receipts;
  private final GroupService groups;
  private final ExpenseService expenses;

  public GroupController(
      GroupService groups,
      ExpenseService expenses,
      RepaymentService repayments,
      ReceiptService receipts,
      ExpenseHistoryService history) {
    this.history = history;
    this.repayments = repayments;
    this.receipts = receipts;
    this.groups = groups;
    this.expenses = expenses;
  }

  public record Activity(
      String kind,
      java.time.Instant time,
      de.thm.campussplit.domain.ExpenseChange change,
      de.thm.campussplit.domain.Repayment payment) {}

  @GetMapping
  String show(
      @PathVariable Long groupId,
      @RequestParam(defaultValue = "all") String activity,
      Principal principal,
      Model model) {
    var summary = expenses.summary(groupId, principal.getName(), null, null);
    model.addAttribute("summary", summary);
    var expensePayments = new java.util.HashMap<String, java.util.List<RepaymentService.ExpenseOption>>();
    for (var settlement : summary.settlements())
      expensePayments.put(settlement.fromId() + ":" + settlement.toId(),
          repayments.options(summary, settlement.fromId(), settlement.toId()));
    model.addAttribute("expensePayments", expensePayments);
    model.addAttribute("receipts", receipts.list(groupId, principal.getName()));
    if (!java.util.Set.of("all", "expenses", "receipts", "payments").contains(activity))
      activity = "all";
    var rows = new java.util.ArrayList<Activity>();
    for (var change : history.list(groupId, principal.getName()))
      rows.add(
          new Activity(
              change.getDetails().startsWith("Beleg ") ? "receipts" : "expenses",
              change.getCreatedAt(),
              change,
              null));
    for (var payment : summary.repayments())
      rows.add(
          new Activity(
              "payments",
              payment.isCancelled() ? payment.getCancelledAt() : payment.getCreatedAt(),
              null,
              payment));
    rows.sort(java.util.Comparator.comparing(Activity::time).reversed());
    final String selected = activity;
    model.addAttribute("activityFilter", selected);
    model.addAttribute(
        "activities",
        rows.stream()
            .filter(row -> selected.equals("all") || row.kind().equals(selected))
            .toList());
    return "group";
  }

  @PostMapping("/delete")
  String deleteGroup(
      @PathVariable Long groupId,
      @RequestParam String confirmation,
      Principal principal,
      RedirectAttributes flash) {
    try {
      groups.delete(groupId, confirmation, principal.getName());
      flash.addFlashAttribute("message", "Die Gruppe wurde gelöscht.");
      return "redirect:/dashboard";
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
      return "redirect:/groups/" + groupId + "#delete-group";
    }
  }

  @PostMapping("/archive")
  String archive(
      @PathVariable Long groupId,
      @RequestParam boolean archived,
      Principal principal,
      RedirectAttributes flash) {
    groups.setArchived(groupId, archived, principal.getName());
    flash.addFlashAttribute(
        "message",
        archived
            ? "Gruppe archiviert. Sie bleibt im Archiv einsehbar."
            : "Gruppe wiederhergestellt.");
    return "redirect:/groups/" + groupId;
  }

  @PostMapping("/members")
  String add(
      @PathVariable Long groupId,
      @Valid @ModelAttribute MemberForm form,
      BindingResult result,
      Principal principal,
      RedirectAttributes flash) {
    if (result.hasErrors())
      flash.addFlashAttribute("message", "Bitte eine gültige E-Mail-Adresse eingeben.");
    else
      try {
        groups.addMember(groupId, form.getEmail(), principal.getName());
        flash.addFlashAttribute("message", "Mitglied hinzugefügt.");
      } catch (BusinessException ex) {
        flash.addFlashAttribute("message", ex.getMessage());
      }
    return "redirect:/groups/" + groupId;
  }

  @PostMapping("/repayments")
  String repay(
      @PathVariable Long groupId,
      @RequestParam Long senderId,
      @RequestParam Long recipientId,
      @RequestParam(required = false) java.math.BigDecimal amount,
      @RequestParam String requestId,
      @RequestParam(required = false) Long expenseId,
      Principal principal,
      RedirectAttributes flash) {
    try {
      repayments.record(groupId, senderId, recipientId, amount, principal.getName(), requestId, expenseId);
      flash.addFlashAttribute(
          "message", "Rückzahlung erfasst. Die offenen Salden wurden aktualisiert.");
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
    }
    return "redirect:/groups/" + groupId;
  }

  @PostMapping("/repayments/{paymentId}/cancel")
  String cancelPayment(
      @PathVariable Long groupId,
      @PathVariable Long paymentId,
      @RequestParam String reason,
      Principal principal,
      RedirectAttributes flash) {
    try {
      repayments.cancel(groupId, paymentId, reason, principal.getName());
      flash.addFlashAttribute("message", "Rückzahlung storniert. Die Salden wurden neu berechnet.");
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
    }
    return "redirect:/groups/" + groupId;
  }

  private String prepare(Long groupId, Long expenseId, Principal principal, Model model) {
    model.addAttribute("summary", expenses.summary(groupId, principal.getName(), null, null));
    model.addAttribute("categories", expenses.categories());
    model.addAttribute("expenseId", expenseId);
    model.addAttribute(
        "formAction",
        "/groups/" + groupId + "/expenses" + (expenseId == null ? "" : "/" + expenseId));
    return "expense-form";
  }

  @GetMapping("/expenses/new")
  String newExpense(@PathVariable Long groupId, Principal principal, Model model) {
    var membership = groups.requireMember(groupId, principal.getName());
    var form = new ExpenseCommand();
    form.setCurrency(membership.getGroup().getCurrency());
    form.setPayerId(membership.getUser().getId());
    form.setParticipants(
        groups.members(groupId, principal.getName()).stream()
            .map(m -> m.getUser().getId())
            .toList());
    model.addAttribute("form", form);
    return prepare(groupId, null, principal, model);
  }

  @GetMapping("/expenses/{expenseId}/edit")
  String edit(
      @PathVariable Long groupId, @PathVariable Long expenseId, Principal principal, Model model) {
    model.addAttribute("form", expenses.editForm(groupId, expenseId, principal.getName()));
    return prepare(groupId, expenseId, principal, model);
  }

  @PostMapping({"/expenses", "/expenses/{expenseId}"})
  String save(
      @PathVariable Long groupId,
      @PathVariable(required = false) Long expenseId,
      @Valid @ModelAttribute("form") ExpenseCommand form,
      BindingResult result,
      Principal principal,
      Model model) {
    groups.requireMember(groupId, principal.getName());
    if (!result.hasErrors())
      try {
        expenses.save(groupId, expenseId, form, principal.getName());
        return "redirect:/groups/" + groupId;
      } catch (BusinessException ex) {
        result.reject("expense", ex.getMessage());
      }
    return prepare(groupId, expenseId, principal, model);
  }

  @PostMapping("/expenses/{expenseId}/delete")
  String delete(
      @PathVariable Long groupId,
      @PathVariable Long expenseId,
      @RequestParam Long version,
      Principal principal,
      RedirectAttributes flash) {
    expenses.delete(groupId, expenseId, version, principal.getName());
    flash.addFlashAttribute("message", "Ausgabe gelöscht.");
    return "redirect:/groups/" + groupId;
  }
}
