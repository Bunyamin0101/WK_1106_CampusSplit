package de.thm.campussplit.web;

import de.thm.campussplit.service.*;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {
  @org.springframework.beans.factory.annotation.Value("${campussplit.google.enabled:false}")
  private boolean googleEnabled;

  private final DashboardService dashboard;
  private final EuroOverviewService euroOverview;
  private final AccountService accounts;
  private final GroupService groups;

  public PageController(
      AccountService accounts,
      GroupService groups,
      DashboardService dashboard,
      EuroOverviewService euroOverview) {
    this.euroOverview = euroOverview;
    this.dashboard = dashboard;
    this.accounts = accounts;
    this.groups = groups;
  }

  @GetMapping("/")
  String index() {
    return "index";
  }

  @GetMapping("/login")
  String login(Model model) {
    model.addAttribute("googleEnabled", googleEnabled);
    return "login";
  }

  @GetMapping("/register")
  String register(Model model) {
    model.addAttribute("form", new RegistrationForm());
    return "register";
  }

  @PostMapping("/register")
  String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
    if (result.hasErrors()) return "register";
    try {
      accounts.register(form.getName(), form.getEmail(), form.getPassword());
    } catch (BusinessException ex) {
      result.reject("registration", ex.getMessage());
      return "register";
    } catch (DataIntegrityViolationException ex) {
      result.reject(
          "registration", "Die Registrierung ist mit dieser E-Mail-Adresse nicht möglich.");
      return "register";
    }
    return "redirect:/login?registered";
  }

  @GetMapping({"/dashboard", "/groups"})
  String dashboard(Model model, Principal principal) {
    var overview = dashboard.overview(principal.getName());
    model.addAttribute("overview", overview);
    model.addAttribute("euroOverview", euroOverview.calculate(overview));
    model.addAttribute("memberships", groups.myGroups(principal.getName()));
    model.addAttribute("user", groups.currentUser(principal.getName()));
    return "dashboard";
  }

  @GetMapping("/groups/new")
  String newGroup(Model model) {
    model.addAttribute("form", new GroupForm());
    return "group-form";
  }

  @PostMapping("/groups")
  String createGroup(
      @Valid @ModelAttribute("form") GroupForm form, BindingResult result, Principal principal) {
    if (result.hasErrors()) return "group-form";
    try {
      var group =
          groups.create(
              form.getName(), form.getDescription(), form.getCurrency(), principal.getName());
      return "redirect:/groups/" + group.getId();
    } catch (BusinessException ex) {
      result.reject("group", ex.getMessage());
      return "group-form";
    }
  }
}
