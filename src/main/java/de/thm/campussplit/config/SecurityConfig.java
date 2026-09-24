package de.thm.campussplit.config;

import de.thm.campussplit.persistence.UserRepository;
import de.thm.campussplit.service.AccountService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(UserRepository users) {
    return email ->
        users
            .findByEmail(AccountService.normalize(email))
            .filter(u -> u.getPasswordHash() != null)
            .map(
                u ->
                    User.withUsername(u.getEmail())
                        .password(u.getPasswordHash())
                        .roles("USER")
                        .build())
            .orElseThrow(() -> new UsernameNotFoundException("Anmeldung fehlgeschlagen"));
  }

  @Bean
  SecurityFilterChain security(
      HttpSecurity http,
      ObjectProvider<ClientRegistrationRepository> registrations,
      GoogleOidcUserService googleUsers)
      throws Exception {
    if (registrations.getIfAvailable() != null) {
      http.oauth2Login(
          oauth ->
              oauth
                  .loginPage("/login")
                  .authorizationEndpoint(
                      endpoint ->
                          endpoint.authorizationRequestResolver(
                              new LinkAuthorizationResolver(registrations.getObject())))
                  .userInfoEndpoint(info -> info.oidcUserService(googleUsers))
                  .successHandler(
                      (request, response, authentication) -> {
                        var session = request.getSession(false);
                        boolean linked =
                            session != null
                                && Boolean.TRUE.equals(session.getAttribute("googleLinkSuccess"));
                        if (session != null) session.removeAttribute("googleLinkSuccess");
                        response.sendRedirect(
                            request.getContextPath() + (linked ? "/profile?linked" : "/"));
                      })
                  .failureHandler(
                      (request, response, exception) -> {
                        if (request.getSession(false) != null)
                          request.getSession(false).removeAttribute(GoogleLinkIntent.KEY);
                        String flag =
                            exception instanceof OAuth2AuthenticationException oauthError
                                    && "account_conflict"
                                        .equals(oauthError.getError().getErrorCode())
                                ? "googleConflict"
                                : "googleError";
                        response.sendRedirect(request.getContextPath() + "/login?" + flag);
                      }));
    }
    return http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/", "/register", "/login", "/css/**", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(
            login ->
                login
                    .loginPage("/login")
                    .usernameParameter("email")
                    .defaultSuccessUrl("/", true)
                    .permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
        .build();
  }
}
