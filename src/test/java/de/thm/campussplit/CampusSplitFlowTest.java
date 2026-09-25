package de.thm.campussplit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.integration.CurrencyRatePort;
import de.thm.campussplit.persistence.*;
import de.thm.campussplit.service.*;
import de.thm.campussplit.service.ExpenseCommand;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CampusSplitFlowTest {
  @Autowired MockMvc mvc;
  @Autowired AccountService accounts;
  @Autowired GroupService groups;
  @Autowired ExpenseService expenses;
  @Autowired UserRepository users;
  @Autowired ExpenseRepository expenseRepo;
  @Autowired PasswordEncoder encoder;
  @MockitoBean CurrencyRatePort rates;
  private User anna, ben, outsider;
  private Group group;
  private String email;

  @BeforeEach
  void setup() {
    var token = UUID.randomUUID().toString();
    email = "anna-" + token + "@example.org";
    anna = accounts.register("Änna", email, "sicheresPasswort123");
    ben = accounts.register("Ben", "ben-" + token + "@example.org", "sicheresPasswort123");
    outsider = accounts.register("Fremd", "fremd-" + token + "@example.org", "sicheresPasswort123");
    group = groups.create("WG Sonnenallee", "Gemeinsam wohnen", "EUR", email);
    groups.addMember(group.getId(), ben.getEmail(), email);
  }

  @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
  @Autowired RepaymentService repayments;

  @Test
  void deletingGroupRequiresAdminNameAndCsrf() throws Exception {
    mvc.perform(
            post("/groups/" + group.getId() + "/delete")
                .with(user(ben.getEmail()))
                .with(csrf())
                .param("confirmation", group.getName()))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/groups/" + group.getId() + "/delete")
                .with(user(email))
                .param("confirmation", group.getName()))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/groups/" + group.getId() + "/delete")
                .with(user(email))
                .with(csrf())
                .param("confirmation", "falsch"))
        .andExpect(redirectedUrl("/groups/" + group.getId() + "#delete-group"));
    assertThat(groups.myGroups(email)).anyMatch(m -> m.getGroup().getId().equals(group.getId()));
  }

  @Test
  void deletingGroupRemovesChildrenButPreservesUsersAndOtherGroups() throws Exception {
    var other = groups.create("Andere Gruppe", "", "EUR", email);
    expenses.save(group.getId(), null, form(), email);
    var expenseId =
        expenses.summary(group.getId(), email, null, null).expenses().getFirst().getId();
    jdbc.update(
        "INSERT INTO"
            + " receipt(created_at,updated_at,expense_id,uploaded_by_id,filename,media_type,data)"
            + " VALUES(CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?,?,?,?,?)",
        expenseId,
        anna.getId(),
        "test.pdf",
        "application/pdf",
        new byte[] {1, 2, 3});
    repayments.record(
        group.getId(),
        ben.getId(),
        anna.getId(),
        new BigDecimal("1.00"),
        email,
        UUID.randomUUID().toString());
    mvc.perform(
            post("/groups/" + group.getId() + "/delete")
                .with(user(email))
                .with(csrf())
                .param("confirmation", group.getName()))
        .andExpect(redirectedUrl("/dashboard"));
    for (var table :
        List.of("expense_group", "membership", "expense", "repayment", "expense_change")) {
      var key = table.equals("expense_group") ? "id" : "group_id";
      assertThat(
              jdbc.queryForObject(
                  "SELECT COUNT(*) FROM " + table + " WHERE " + key + " = ?",
                  Long.class,
                  group.getId()))
          .isZero();
    }
    for (var table : List.of("receipt", "expense_share")) {
      assertThat(
              jdbc.queryForObject(
                  "SELECT COUNT(*) FROM " + table + " WHERE expense_id = ?", Long.class, expenseId))
          .isZero();
    }
    assertThat(users.findById(anna.getId())).isPresent();
    assertThat(users.findById(ben.getId())).isPresent();
    assertThat(groups.myGroups(email)).anyMatch(m -> m.getGroup().getId().equals(other.getId()));
  }

  private ExpenseCommand form() {
    var f = new ExpenseCommand();
    f.setDescription("Einkauf");
    f.setAmount(new BigDecimal("10.01"));
    f.setCurrency("EUR");
    f.setDate(LocalDate.of(2026, 8, 12));
    f.setPayerId(anna.getId());
    f.setParticipants(List.of(anna.getId(), ben.getId()));
    return f;
  }

  @Test
  void realRegistrationLoginLogoutAndTemplates() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.containsString(
                        "<h1>Gemeinsame Kosten <span>einfach aufteilen</span></h1>")));
    mvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    mvc.perform(get("/register")).andExpect(status().isOk());
    var newEmail = "new-" + UUID.randomUUID() + "@example.org";
    mvc.perform(
            post("/register")
                .with(csrf())
                .param("name", "Clara")
                .param("email", newEmail.toUpperCase())
                .param("password", "sicheresPasswort123"))
        .andExpect(redirectedUrl("/login?registered"));
    assertThat(
            encoder.matches(
                "sicheresPasswort123", users.findByEmail(newEmail).orElseThrow().getPasswordHash()))
        .isTrue();
    var login =
        mvc.perform(
                post("/login")
                    .with(csrf())
                    .param("email", newEmail)
                    .param("password", "sicheresPasswort123"))
            .andExpect(redirectedUrl("/"))
            .andReturn();
    var session = (MockHttpSession) login.getRequest().getSession(false);
    mvc.perform(get("/dashboard").session(session)).andExpect(status().isOk());
    mvc.perform(post("/logout").session(session).with(csrf()))
        .andExpect(redirectedUrl("/login?logout"));
    assertThat(session.isInvalid()).isTrue();
    mvc.perform(post("/login").with(csrf()).param("email", newEmail).param("password", "wrong"))
        .andExpect(redirectedUrl("/login?error"));
  }

  @Test
  void formWorkflowPersistsAndRenders() throws Exception {
    mvc.perform(get("/groups/new").with(user(email))).andExpect(status().isOk());
    mvc.perform(
            post("/groups")
                .with(user(email))
                .with(csrf())
                .param("name", "Reise")
                .param("currency", "EUR"))
        .andExpect(status().is3xxRedirection());
    mvc.perform(get("/groups/" + group.getId() + "/expenses/new").with(user(email)))
        .andExpect(status().isOk());
    mvc.perform(
            post("/groups/" + group.getId() + "/expenses")
                .with(user(email))
                .with(csrf())
                .param("description", "Einkauf")
                .param("amount", "10.01")
                .param("currency", "EUR")
                .param("date", "2026-08-12")
                .param("payerId", anna.getId().toString())
                .param("splitMethod", "EQUAL")
                .param("participants", anna.getId().toString(), ben.getId().toString())
                .param("custom[" + anna.getId() + "]", "")
                .param("custom[" + ben.getId() + "]", ""))
        .andExpect(redirectedUrl("/groups/" + group.getId()));
    var s = expenses.summary(group.getId(), email, null, null);
    assertThat(s.expenses()).hasSize(1);
    assertThat(s.balances().get(0).amount()).isEqualByComparingTo("5.00");
    mvc.perform(get("/groups/" + group.getId()).with(user(email)))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Einkauf")));
    mvc.perform(
            get("/groups/"
                    + group.getId()
                    + "/expenses/"
                    + s.expenses().getFirst().getId()
                    + "/edit")
                .with(user(email)))
        .andExpect(status().isOk());
    verifyNoInteractions(rates);
  }

  @Test
  void accessAndCsrfAreEnforced() throws Exception {
    var base = "/groups/" + group.getId();
    mvc.perform(get(base).with(user(outsider.getEmail()))).andExpect(status().isForbidden());
    mvc.perform(get(base + "/export").param("format", "pdf").with(user(outsider.getEmail())))
        .andExpect(status().isForbidden());
    mvc.perform(
            post(base + "/members")
                .with(user(ben.getEmail()))
                .with(csrf())
                .param("email", outsider.getEmail()))
        .andExpect(status().isForbidden());
    mvc.perform(post(base + "/members").with(user(email)).param("email", outsider.getEmail()))
        .andExpect(status().isForbidden());
    var f = form();
    f.setParticipants(List.of(outsider.getId()));
    assertThatThrownBy(() -> expenses.save(group.getId(), null, f, email))
        .isInstanceOf(BusinessException.class);
    assertThat(expenses.summary(group.getId(), email, null, null).expenses()).isEmpty();
  }

  @Test
  void conversionIsStoredAndFailuresRollBack() {
    var f = form();
    f.setCurrency("USD");
    when(rates.getRate("USD", "EUR", f.getDate()))
        .thenReturn(
            new CurrencyRatePort.ExchangeRate(
                "USD", "EUR", f.getDate(), new BigDecimal("0.86645")));
    expenses.save(group.getId(), null, f, email);
    var row = expenses.summary(group.getId(), email, null, null).expenses().getFirst();
    assertThat(row.getSettlementAmount()).isEqualByComparingTo("8.67");
    assertThat(row.getExchangeRate()).isEqualByComparingTo("0.86645");
    var edit = expenses.editForm(group.getId(), row.getId(), email);
    edit.setDescription("Korrigiert");
    expenses.save(group.getId(), row.getId(), edit, email);
    verify(rates, times(1)).getRate("USD", "EUR", f.getDate());
    when(rates.getRate("USD", "EUR", f.getDate()))
        .thenThrow(new BusinessException("Dienst nicht erreichbar"));
    assertThatThrownBy(() -> expenses.save(group.getId(), null, f, email))
        .isInstanceOf(BusinessException.class);
    assertThat(expenses.summary(group.getId(), email, null, null).expenses()).hasSize(1);
  }

  @Test
  void editingDeletingAndStaleChanges() {
    expenses.save(group.getId(), null, form(), email);
    var row = expenses.summary(group.getId(), email, null, null).expenses().getFirst();
    var edit = expenses.editForm(group.getId(), row.getId(), email);
    edit.setAmount(new BigDecimal("20.00"));
    edit.setSplitMethod(SplitMethod.CUSTOM_AMOUNT);
    edit.setCustom(
        Map.of(anna.getId(), new BigDecimal("2.00"), ben.getId(), new BigDecimal("18.00")));
    expenses.save(group.getId(), row.getId(), edit, email);
    assertThat(expenses.summary(group.getId(), email, null, null).balances().getFirst().amount())
        .isEqualByComparingTo("18.00");
    assertThatThrownBy(() -> expenses.save(group.getId(), row.getId(), edit, email))
        .isInstanceOf(BusinessException.class);
    var latest = expenses.editForm(group.getId(), row.getId(), email);
    expenses.delete(group.getId(), row.getId(), latest.getVersion(), email);
    assertThat(expenses.summary(group.getId(), email, null, null).balances())
        .allMatch(b -> b.amount().signum() == 0);
  }

  @Test
  void invalidFormsAndDuplicateRegistration() throws Exception {
    mvc.perform(
            post("/register")
                .with(csrf())
                .param("name", "Test")
                .param("email", email)
                .param("password", "sicheresPasswort123"))
        .andExpect(status().isOk())
        .andExpect(model().hasErrors());
    mvc.perform(
            post("/groups/" + group.getId() + "/expenses")
                .with(user(email))
                .with(csrf())
                .param("description", "")
                .param("amount", "-1")
                .param("currency", "EUR"))
        .andExpect(status().isOk())
        .andExpect(model().hasErrors());
  }

  @Test
  void exportSelectionControlsPdfSectionsAndCsvFiles() throws Exception {
    expenses.save(group.getId(), null, form(), email);
    for (String scope : List.of("open", "expenses")) {
      var pdf = mvc.perform(get("/groups/" + group.getId() + "/export")
          .param("format", "pdf").param("scope", scope).with(user(email)))
          .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
      try (var doc = Loader.loadPDF(pdf)) {
        var text = new PDFTextStripper().getText(doc);
        assertThat(text).doesNotContain("Erfasste Rückzahlungen");
        if (scope.equals("open")) {
          assertThat(text).contains("Salden", "Ausgleichsvorschläge").doesNotContain("Ausgaben und Kostenanteile");
        } else {
          assertThat(text).contains("Ausgaben und Kostenanteile").doesNotContain("Salden", "Ausgleichsvorschläge");
        }
      }
      var zip = mvc.perform(get("/groups/" + group.getId() + "/export")
          .param("format", "csv").param("scope", scope).with(user(email)))
          .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
      var names = new java.util.HashSet<String>();
      try (var input = new ZipInputStream(new ByteArrayInputStream(zip))) {
        for (var entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) names.add(entry.getName());
      }
      if (scope.equals("open")) assertThat(names).containsExactlyInAnyOrder("gruppe.csv", "salden.csv", "ausgleich.csv");
      else assertThat(names).containsExactlyInAnyOrder("gruppe.csv", "ausgaben.csv");
    }
    mvc.perform(get("/groups/" + group.getId() + "/export").param("format", "pdf")
        .param("scope", "unknown").with(user(email))).andExpect(status().isBadRequest());
  }

  @Test
  void exportsContainSharesBalancesAndNoSecrets() throws Exception {
    var f = form();
    f.setDescription("=HYPERLINK(\"bad\")");
    expenses.save(group.getId(), null, f, email);
    var pdf =
        mvc.perform(
                get("/groups/" + group.getId() + "/export")
                    .param("format", "pdf")
                    .with(user(email)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    try (var doc = Loader.loadPDF(pdf)) {
      var text = new PDFTextStripper().getText(doc);
      assertThat(text)
          .contains("Änna", "Salden", "5,00", "Kostenanteile")
          .doesNotContain(email, "sicheresPasswort", "$2a$");
      Files.createDirectories(Path.of("target/export-check"));
      Files.write(Path.of("target/export-check/sample.pdf"), pdf);
      javax.imageio.ImageIO.write(
          new PDFRenderer(doc).renderImageWithDPI(0, 120),
          "png",
          Path.of("target/export-check/sample.png").toFile());
    }
    var zip =
        mvc.perform(
                get("/groups/" + group.getId() + "/export")
                    .param("format", "csv")
                    .with(user(email)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    var files = new HashMap<String, String>();
    try (var input = new ZipInputStream(new ByteArrayInputStream(zip))) {
      for (var e = input.getNextEntry(); e != null; e = input.getNextEntry())
        files.put(
            e.getName(), new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
    }
    assertThat(files).containsKeys("ausgaben.csv", "salden.csv", "ausgleich.csv", "gruppe.csv");
    assertThat(files.get("ausgaben.csv"))
        .contains("\"'=HYPERLINK")
        .doesNotContain(email, "password");
    assertThat(files.get("salden.csv")).contains("-5.00");
    assertThat(expenses.summary(group.getId(), email, LocalDate.of(2026, 9, 1), null).expenses())
        .isEmpty();
    mvc.perform(
            get("/groups/" + group.getId() + "/export")
                .param("format", "pdf")
                .param("from", "2026-09-01")
                .param("to", "2026-08-01")
                .with(user(email)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void cancelledPaymentsRemainVisibleButDoNotReduceOpenAmountsInExport() throws Exception {
    expenses.save(group.getId(), null, form(), email);
    repayments.record(group.getId(), ben.getId(), anna.getId(), new BigDecimal("5.00"), email);
    var payment = expenses.summary(group.getId(), email, null, null).repayments().getFirst();
    repayments.cancel(group.getId(), payment.getId(), "Noch nicht überwiesen", email);
    var summary = expenses.summary(group.getId(), email, null, null);
    assertThat(summary.settlements()).hasSize(1);
    assertThat(summary.settlements().getFirst().amount()).isEqualByComparingTo("5.00");
    var bytes = mvc.perform(get("/groups/" + group.getId() + "/export")
        .param("format", "pdf").with(user(email)))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
    try (var doc = Loader.loadPDF(bytes)) {
      String text = new PDFTextStripper().getText(doc);
      assertThat(text).contains("Storniert", "Noch nicht überwiesen", "Nicht in den offenen Beträgen berücksichtigt.", "Ausgaben gesamt: 10,01 EUR", "5,00")
          .doesNotContain("STORNIERT:", "Bestätigt von", "Original:");
      assertThat(text.indexOf("Offene Beträge")).isLessThan(text.indexOf("Ausgaben und Kostenanteile"));
    }
  }

  @Test
  void sharesOnlyChangesAlsoRejectStaleUpdates() {
    expenses.save(group.getId(), null, form(), email);
    var id = expenses.summary(group.getId(), email, null, null).expenses().getFirst().getId();
    var first = expenses.editForm(group.getId(), id, email);
    var stale = expenses.editForm(group.getId(), id, email);
    first.setParticipants(List.of(ben.getId()));
    expenses.save(group.getId(), id, first, email);
    assertThatThrownBy(() -> expenses.save(group.getId(), id, stale, email))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void expenseIdsCannotBeUsedAcrossGroups() throws Exception {
    expenses.save(group.getId(), null, form(), email);
    var id = expenses.summary(group.getId(), email, null, null).expenses().getFirst().getId();
    var other = groups.create("Andere Gruppe", "", "EUR", email);
    mvc.perform(get("/groups/" + other.getId() + "/expenses/" + id + "/edit").with(user(email)))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/groups/" + group.getId() + "/expenses/" + id + "/delete")
                .with(user(outsider.getEmail()))
                .with(csrf())
                .param("version", "0"))
        .andExpect(status().isForbidden());
    assertThat(expenses.summary(group.getId(), email, null, null).expenses()).hasSize(1);
  }

  @Test
  void longPdfUsesMultiplePagesAndKeepsLastSection() throws Exception {
    var f = form();
    f.setDescription("Übernachtung und Verpflegung ".repeat(8));
    for (int i = 0; i < 12; i++) expenses.save(group.getId(), null, f, email);
    var bytes =
        mvc.perform(
                get("/groups/" + group.getId() + "/export")
                    .param("format", "pdf")
                    .with(user(email)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    try (var doc = Loader.loadPDF(bytes)) {
      assertThat(doc.getNumberOfPages()).isGreaterThan(1);
      assertThat(new PDFTextStripper().getText(doc)).contains("außerhalb von CampusSplit");
      Files.createDirectories(Path.of("target/export-check"));
      for (int page = 0; page < doc.getNumberOfPages(); page++)
        javax.imageio.ImageIO.write(
            new PDFRenderer(doc).renderImageWithDPI(page, 80),
            "png",
            Path.of("target/export-check/long-" + page + ".png").toFile());
    }
  }
}
