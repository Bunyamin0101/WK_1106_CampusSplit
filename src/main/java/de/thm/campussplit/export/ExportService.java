package de.thm.campussplit.export;

import de.thm.campussplit.service.ExpenseService.GroupSummary;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.zip.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

@Service
public class ExportService {
  // Quote every cell and neutralize spreadsheet formulas, including leading whitespace.
  static String cell(Object value) {
    String s = value == null ? "" : value.toString();
    if (s.stripLeading().matches("(?s)^[=+@-].*")) s = "'" + s;
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }

  private static void row(StringBuilder out, Object... values) {
    out.append(
            Arrays.stream(values)
                .map(ExportService::cell)
                .collect(java.util.stream.Collectors.joining(",")))
        .append("\r\n");
  }

  public byte[] csv(GroupSummary summary, LocalDate from, LocalDate to, String scope)
      throws IOException {
    var expenseRows = new StringBuilder();
    row(
        expenseRows,
        "expense_date",
        "description",
        "category",
        "payer",
        "original_amount",
        "original_currency",
        "exchange_rate",
        "settlement_amount",
        "settlement_currency",
        "participant",
        "share");
    for (var e : summary.expenses())
      for (var s : e.getShares())
        row(
            expenseRows,
            e.getExpenseDate(),
            e.getDescription(),
            e.getCategory() == null ? "" : e.getCategory().getName(),
            e.getPaidBy().getName(),
            e.getOriginalAmount(),
            e.getOriginalCurrency(),
            e.getExchangeRate(),
            e.getSettlementAmount(),
            summary.group().getCurrency(),
            s.getUser().getName(),
            s.getShareAmount());
    var balances = new StringBuilder();
    row(balances, "member", "balance", "status");
    // Numeric cells are trusted BigDecimals, so negative balances stay machine-readable numbers.
    for (var b : summary.balances())
      balances
          .append(cell(b.name()))
          .append(',')
          .append(b.amount().toPlainString())
          .append(',')
          .append(cell(b.status()))
          .append("\r\n");
    var settlements = new StringBuilder();
    row(settlements, "from", "to", "amount");
    summary.settlements().forEach(s -> row(settlements, s.from(), s.to(), s.amount()));
    var payments = new StringBuilder();
    row(
        payments,
        "date",
        "from",
        "to",
        "amount",
        "currency",
        "recorded_by",
        "status",
        "cancelled_at",
        "cancelled_by",
        "cancellation_reason");
    summary
        .repayments()
        .forEach(
            p ->
                row(
                    payments,
                    p.getPaymentDate(),
                    p.getSender().getName(),
                    p.getRecipient().getName(),
                    p.getAmount(),
                    summary.group().getCurrency(),
                    p.getRecordedBy().getName(),
                    p.isCancelled() ? "CANCELLED" : "RECORDED",
                    p.getCancelledAt(),
                    p.getCancelledBy() == null ? "" : p.getCancelledBy().getName(),
                    p.getCancellationReason()));
    var metadata = new StringBuilder();
    row(metadata, "group", "currency", "exported_at", "from", "to", "scope");
    row(
        metadata,
        summary.group().getName(),
        summary.group().getCurrency(),
        Instant.now(),
        from,
        to,
        scope);
    var bytes = new ByteArrayOutputStream();
    try (var zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
      if (!scope.equals("open")) write(zip, "ausgaben.csv", expenseRows.toString());
      if (!scope.equals("expenses")) write(zip, "salden.csv", balances.toString());
      if (!scope.equals("expenses")) write(zip, "ausgleich.csv", settlements.toString());
      write(zip, "gruppe.csv", metadata.toString());
      if (scope.equals("all")) write(zip, "rueckzahlungen.csv", payments.toString());
    }
    return bytes.toByteArray();
  }

  private void write(ZipOutputStream zip, String name, String text) throws IOException {
    zip.putNextEntry(new ZipEntry(name));
    zip.write(text.getBytes(StandardCharsets.UTF_8));
    zip.closeEntry();
  }

  public byte[] pdf(GroupSummary summary, LocalDate from, LocalDate to, String scope)
      throws IOException {
    try (var document = new PDDocument();
        var out = new ByteArrayOutputStream();
        var fontStream = getClass().getResourceAsStream("/fonts/NotoSans-Regular.ttf")) {
      var font = PDType0Font.load(document, Objects.requireNonNull(fontStream));
      try (var writer = new PdfWriter(document, font)) {
        writer.line("Gruppenabrechnung", 22);
        writer.line("Gruppe: " + summary.group().getName(), 14);
        writer.line(
            "Export: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " | Währung: " + summary.group().getCurrency(), 10);
        writer.line(
            "Zeitraum: " + (from == null ? "Beginn" : from) + " bis " + (to == null ? "heute" : to),
            10);
        writer.line(
            "Inhalt: "
                + switch (scope) {
                  case "open" -> "Offene Beträge";
                  case "expenses" -> "Nur Ausgaben";
                  default -> "Gesamte Abrechnung";
                },
            10);
        writer.heading("Mitglieder");
        for (var m : summary.members()) writer.line(m.getUser().getName(), 11);
        if (!scope.equals("open")) {
          writer.heading("Ausgaben und Kostenanteile");
          if (summary.expenses().isEmpty())
            writer.line("Keine Ausgaben im gewählten Zeitraum.", 11);
          for (var e : summary.expenses()) {
            // Keep the description and first detail rows together at a page break.
            writer.reserve(140);
            writer.line(e.getExpenseDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " | " + e.getDescription(), 12);
            writer.line(
                "Zahler: "
                    + e.getPaidBy().getName()
                    + " | Kategorie: "
                    + (e.getCategory() == null ? "-" : e.getCategory().getName()),
                10);
            writer.line(
                "Original: "
                    + money(e.getOriginalAmount())
                    + " "
                    + e.getOriginalCurrency()
                    + " | Abrechnung: "
                    + money(e.getSettlementAmount())
                    + " "
                    + summary.group().getCurrency(),
                11);
            if (e.getExchangeRate() != null)
              writer.line("Kurs: " + e.getExchangeRate() + " vom " + e.getRateDate(), 10);
            for (var s : e.getShares())
              writer.line(
                  "  "
                      + s.getUser().getName()
                      + ": "
                      + money(s.getShareAmount())
                      + " "
                      + summary.group().getCurrency(),
                  10);
            writer.space();
          }
        }
        if (scope.equals("all")) {
          writer.heading("Erfasste Rückzahlungen");
          if (summary.repayments().isEmpty()) writer.line("Noch keine Rückzahlungen erfasst.", 11);
          for (var p : summary.repayments())
            writer.line(
                p.getPaymentDate()
                    + " | "
                    + p.getSender().getName()
                    + " -> "
                    + p.getRecipient().getName()
                    + ": "
                    + money(p.getAmount())
                    + " "
                    + summary.group().getCurrency()
                    + " | Bestätigt von "
                    + p.getRecordedBy().getName()
                    + (p.isCancelled()
                        ? " | STORNIERT: "
                            + p.getCancelledAt()
                            + " | "
                            + p.getCancelledBy().getName()
                            + " | "
                            + p.getCancellationReason()
                        : ""),
                11);
        }
        if (!scope.equals("expenses")) {
          writer.heading("Salden");
          for (var b : summary.balances())
            writer.line(
                b.name()
                    + ": "
                    + money(b.amount().abs())
                    + " "
                    + summary.group().getCurrency()
                    + " - "
                    + b.label(),
                11);
          writer.heading("Ausgleichsvorschläge - Wer zahlt wem?");
          if (summary.settlements().isEmpty()) writer.line("Alle Salden sind ausgeglichen.", 11);
          for (var s : summary.settlements())
            writer.line(
                s.from()
                    + " -> "
                    + s.to()
                    + ": "
                    + money(s.amount())
                    + " "
                    + summary.group().getCurrency(),
                11);
        }
        writer.space();
        writer.line("Zahlungen finden außerhalb von CampusSplit statt.", 10);
      }
      document.save(out);
      return out.toByteArray();
    }
  }

  private static String money(java.math.BigDecimal amount) {
    var format = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
    format.setMinimumFractionDigits(2);
    format.setMaximumFractionDigits(2);
    return format.format(amount);
  }

  private static class PdfWriter implements AutoCloseable {
    private final PDDocument doc;
    private final PDType0Font font;
    private PDPageContentStream stream;
    private float y;

    PdfWriter(PDDocument doc, PDType0Font font) throws IOException {
      this.doc = doc;
      this.font = font;
      newPage();
    }

    void newPage() throws IOException {
      if (stream != null) stream.close();
      var page = new PDPage(PDRectangle.A4);
      doc.addPage(page);
      stream = new PDPageContentStream(doc, page);
      stream.setNonStrokingColor(new java.awt.Color(8, 125, 64));
      stream.addRect(0, 778, page.getMediaBox().getWidth(), 64);
      stream.fill();
      stream.beginText();
      stream.setNonStrokingColor(java.awt.Color.WHITE);
      stream.setFont(font, 19);
      stream.newLineAtOffset(48, 800);
      stream.showText("CampusSplit");
      stream.endText();
      stream.setStrokingColor(new java.awt.Color(220, 229, 223));
      stream.moveTo(48, 46);
      stream.lineTo(547, 46);
      stream.stroke();
      stream.setNonStrokingColor(new java.awt.Color(95, 111, 103));
      y = 748;
      stream.beginText();
      stream.setFont(font, 9);
      stream.newLineAtOffset(48, 28);
      stream.showText("CampusSplit | Seite " + doc.getNumberOfPages());
      stream.endText();
    }

    void heading(String s) throws IOException {
      reserve(65);
      space();
      stream.setNonStrokingColor(new java.awt.Color(237, 246, 240));
      stream.addRect(42, y - 9, 511, 28);
      stream.fill();
      line(s, 13);
      y -= 8;
    }

    void reserve(int points) throws IOException {
      if (y - points < 55) newPage();
    }

    void space() {
      y -= 10;
    }

    void line(String raw, int size) throws IOException {
      // Unsupported glyphs become '?' instead of breaking a complete export.
      var clean = new StringBuilder();
      for (int cp : raw.replaceAll("[\\p{Cntrl}]", " ").codePoints().toArray()) {
        String ch = new String(Character.toChars(cp));
        try {
          font.getStringWidth(ch);
          clean.append(ch);
        } catch (IllegalArgumentException ex) {
          clean.append('?');
        }
      }
      var line = new StringBuilder();
      for (int cp : clean.toString().codePoints().toArray()) {
        String ch = new String(Character.toChars(cp));
        if (font.getStringWidth(line.toString() + ch) / 1000 * size > 495) {
          int boundary = line.lastIndexOf(" ");
          if (boundary > 0) {
            draw(line.substring(0, boundary), size);
            line.delete(0, boundary + 1);
          } else {
            draw(line.toString(), size);
            line.setLength(0);
          }
        }
        line.append(ch);
      }
      draw(line.toString(), size);
    }

    void draw(String text, int size) throws IOException {
      if (y < size + 55) newPage();
      stream.setNonStrokingColor(size >= 12
          ? new java.awt.Color(16, 43, 83) : new java.awt.Color(66, 83, 75));
      stream.beginText();
      stream.setFont(font, size);
      stream.newLineAtOffset(48, y);
      stream.showText(text);
      stream.endText();
      y -= size + 7;
    }

    public void close() throws IOException {
      if (stream != null) stream.close();
    }
  }
}
