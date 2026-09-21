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
  private final GroupService groups;
  private final ExpenseService expenses;

  public GroupController(GroupService groups, ExpenseService expenses) {
    this.groups = groups;
    this.expenses = expenses;
  }

  @GetMapping
  String show(@PathVariable Long groupId, Principal principal, Model model) {
    model.addAttribute("summary", expenses.summary(groupId, principal.getName(), null, null));
    return "group";
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
