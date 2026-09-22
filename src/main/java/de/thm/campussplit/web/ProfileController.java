package de.thm.campussplit.web;

import de.thm.campussplit.config.GoogleLinkIntent;
import de.thm.campussplit.service.*;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
  private final GroupService groups;
  private final ProfileService profiles;

  @org.springframework.beans.factory.annotation.Value("${campussplit.google.enabled:false}")
  private boolean googleEnabled;

  public ProfileController(GroupService groups, ProfileService profiles) {
    this.groups = groups;
    this.profiles = profiles;
  }

  @GetMapping("/profile")
  String profile(Principal actor, Model model, HttpSession session) {
    session.removeAttribute(GoogleLinkIntent.KEY);
    model.addAttribute("profile", groups.currentUser(actor.getName()));
    model.addAttribute("googleEnabled", googleEnabled);
    return "profile";
  }

  @PostMapping("/profile")
  String rename(@RequestParam String name, Principal actor, RedirectAttributes flash) {
    try {
      profiles.rename(actor.getName(), name);
      flash.addFlashAttribute("message", "Anzeigename gespeichert.");
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
    }
    return "redirect:/profile";
  }

  @PostMapping("/profile/google")
  String link(
      @RequestParam String password,
      Principal actor,
      HttpSession session,
      RedirectAttributes flash) {
    session.removeAttribute(GoogleLinkIntent.KEY);
    try {
      if (!googleEnabled)
        throw new BusinessException("Google-Anmeldung ist derzeit nicht aktiviert.");
      profiles.authorizeLink(actor.getName(), password);
      session.setAttribute(
          GoogleLinkIntent.KEY,
          new GoogleLinkIntent(actor.getName(), java.time.Instant.now().plusSeconds(300), null));
      return "redirect:/oauth2/authorization/google";
    } catch (BusinessException ex) {
      flash.addFlashAttribute("message", ex.getMessage());
      return "redirect:/profile";
    }
  }
}
