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
        "Datum",
        "Beschreibung",
        "Kategorie",
        "Bezahlt von",
        "Originalbetrag",
        "Originalwährung",
        "Wechselkurs",
        "Ausgabenbetrag (nicht je Person summieren)",
        "Gruppenwährung",
        "Beteiligte Person",
        "Kostenanteil");
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
    row(balances, "Person", "Saldo", "Status");
    // Numeric cells are trusted BigDecimals, so negative balances stay machine-readable numbers.
    for (var b : summary.balances())
      balances
          .append(cell(b.name()))
          .append(',')
          .append(b.amount().toPlainString())
          .append(',')
          .append(cell(b.label()))
          .append("\r\n");
    var settlements = new StringBuilder();
    row(settlements, "Von", "An", "Betrag");
    summary.settlements().forEach(s -> row(settlements, s.from(), s.to(), s.amount()));
    var payments = new StringBuilder();
    row(
        payments,
        "Datum",
        "Von",
        "An",
        "Betrag",
        "Währung",
        "Erfasst von",
        "Status",
        "Storniert am",
        "Storniert von",
        "Stornogrund",
        "Zugeordnete Ausgabe");
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
                    p.isCancelled() ? "Storniert" : "Erfasst",
                    p.getCancelledAt(),
                    p.getCancelledBy() == null ? "" : p.getCancelledBy().getName(),
                    p.getCancellationReason(), p.getExpenseDescription()));
    var metadata = new StringBuilder();
    row(metadata, "Gruppe", "Währung", "Exportiert am", "Zeitraum von", "Zeitraum bis", "Inhalt");
    row(
        metadata,
        summary.group().getName(),
        summary.group().getCurrency(),
        Instant.now(),
        from,
        to,
        scopeLabel(scope));
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
      String currency = summary.group().getCurrency();
      try (var writer = new PdfWriter(document, font)) {
        writer.line("Gruppenabrechnung", 22);
        writer.line(summary.group().getName(), 15);
        writer.line("Erstellt am " + date(LocalDate.now(ZoneId.of("Europe/Berlin")))
            + " | Gruppenwährung: " + currency, 10);
        writer.line("Zeitraum: " + (from == null && to == null ? "Gesamter Zeitraum"
            : (from == null ? "Beginn" : date(from)) + " bis " + (to == null ? "heute" : date(to))), 10);
        writer.line("Inhalt: " + scopeLabel(scope), 10);
        if (from != null || to != null)
          writer.line("Salden und Vorschläge berücksichtigen nur den ausgewählten Zeitraum.", 10);
        writer.space();
        var total = summary.expenses().stream().map(e -> e.getSettlementAmount())
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        writer.line("Ausgaben gesamt: " + money(total) + " " + currency, 14);
        writer.line(summary.expenses().size() + (summary.expenses().size() == 1 ? " Ausgabe | " : " Ausgaben | ") + summary.members().size() + " Mitglieder", 10);
        if (!scope.equals("expenses")) {
          writer.heading("Offene Beträge (Salden)");
          var rows = new ArrayList<String[]>();
          for (var b : summary.balances())
            rows.add(new String[] {b.name(), b.label(), money(b.amount().abs())});
          writer.table(new String[] {"Person", "Stand", "Betrag (" + currency + ")"}, rows,
              new float[] {200, 170, 129});
          writer.heading("Ausgleichsvorschläge - Wer zahlt wem?");
          if (summary.settlements().isEmpty()) writer.line("Alles ausgeglichen. Es sind keine Zahlungen offen.", 11);
          else {
            rows = new ArrayList<>();
            for (var payment : summary.settlements())
              rows.add(new String[] {payment.from(), payment.to(), money(payment.amount())});
            writer.table(new String[] {"Von", "An", "Betrag (" + currency + ")"}, rows,
                new float[] {185, 185, 129});
          }
        }
        if (!scope.equals("open")) {
          writer.reserve(220);
          writer.heading("Ausgaben und Kostenanteile");
          if (summary.expenses().isEmpty()) writer.line("Keine Ausgaben im gewählten Zeitraum.", 11);
          for (var e : summary.expenses()) {
            writer.reserve(145);
            writer.line(e.getDescription(), 12);
            writer.line(date(e.getExpenseDate()) + " | Bezahlt von " + e.getPaidBy().getName(), 10);
            writer.line("Betrag: " + money(e.getSettlementAmount()) + " " + currency, 11);
            if (!e.getOriginalCurrency().equals(currency)) {
              writer.line("Ursprünglich: " + money(e.getOriginalAmount()) + " " + e.getOriginalCurrency(), 10);
              if (e.getExchangeRate() != null)
                writer.line("Umrechnung: 1 " + e.getOriginalCurrency() + " = "
                    + e.getExchangeRate().stripTrailingZeros().toPlainString().replace('.', ',') + " " + currency
                    + (e.getRateDate() == null ? "" : " | Kurs vom " + date(e.getRateDate())), 10);
            }
            if (e.getCategory() != null) writer.line("Kategorie: " + e.getCategory().getName(), 10);
            var shares = new ArrayList<String[]>();
            for (var share : e.getShares())
              shares.add(new String[] {share.getUser().getName(), money(share.getShareAmount())});
            writer.table(new String[] {"Person", "Kostenanteil (" + currency + ")"}, shares,
                new float[] {330, 169});
            writer.space();
          }
        }
        if (scope.equals("all")) {
          writer.heading("Erfasste Rückzahlungen");
          if (summary.repayments().isEmpty()) writer.line("Noch keine Rückzahlungen erfasst.", 11);
          for (var payment : summary.repayments()) {
            writer.reserve(100);
            writer.line(date(payment.getPaymentDate()) + " | " + money(payment.getAmount()) + " " + currency
                + (payment.isCancelled() ? " | Storniert" : " | Erfasst"), 12);
            writer.line(payment.getSender().getName() + " an " + payment.getRecipient().getName(), 11);
            writer.line("Erfasst von " + payment.getRecordedBy().getName(), 10);
            if (payment.getExpenseDescription() != null)
              writer.line("Für Ausgabe: " + payment.getExpenseDescription(), 10);
            if (payment.isCancelled()) {
              writer.line("Nicht in den offenen Beträgen berücksichtigt.", 10);
              writer.line("Storniert am " + java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm")
                  .withZone(ZoneId.of("Europe/Berlin")).format(payment.getCancelledAt())
                  + (payment.getCancelledBy() == null ? "" : " von " + payment.getCancelledBy().getName()), 10);
              if (payment.getCancellationReason() != null)
                writer.line("Grund: " + payment.getCancellationReason(), 10);
            }
            writer.space();
          }
        }
        writer.space();
        writer.line("Zahlungen finden außerhalb von CampusSplit statt.", 10);
      }
      document.save(out);
      return out.toByteArray();
    }
  }

  private static String date(LocalDate value) {
    return value.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  }

  private static String scopeLabel(String scope) {
    return switch (scope) {
      case "open" -> "Offene Beträge";
      case "expenses" -> "Nur Ausgaben";
      default -> "Gesamte Abrechnung";
    };
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
    }

    void heading(String s) throws IOException {
      reserve(110);
      space();
      stream.setNonStrokingColor(new java.awt.Color(237, 246, 240));
      stream.addRect(42, y - 9, 511, 28);
      stream.fill();
      line(s, 13);
      y -= 8;
    }

    void table(String[] headers, List<String[]> rows, float[] widths) throws IOException {
      reserve(62);
      tableRow(headers, widths);
      for (var row : rows) {
        var wrapped = new ArrayList<List<String>>();
        int lines = 1;
        for (int i = 0; i < row.length; i++) {
          var parts = wrap(row[i], 10, widths[i] - 20);
          wrapped.add(parts);
          lines = Math.max(lines, parts.size());
        }
        int offset = 0;
        while (offset < lines) {
          if (y < 92) {
            newPage();
            tableRow(headers, widths);
          }
          int count = Math.min(lines - offset, Math.max(1, (int) ((y - 65) / 16)));
          float x = 48;
          for (int i = 0; i < row.length; i++) {
            for (int n = 0; n < count && offset + n < wrapped.get(i).size(); n++) {
              String text = wrapped.get(i).get(offset + n);
              float left = i == row.length - 1
                  ? x + widths[i] - 10 - font.getStringWidth(text) / 1000 * 10 : x + 10;
              textAt(text, left, y - 14 - n * 16, 10);
            }
            x += widths[i];
          }
          y -= count * 16 + 10;
          stream.setStrokingColor(new java.awt.Color(220, 229, 223));
          stream.moveTo(48, y + 3); stream.lineTo(547, y + 3); stream.stroke();
          offset += count;
        }
      }
      space();
    }

    private void tableRow(String[] cells, float[] widths) throws IOException {
      stream.setNonStrokingColor(new java.awt.Color(237, 246, 240));
      stream.addRect(48, y - 27, 499, 27); stream.fill();
      float x = 48;
      for (int i = 0; i < cells.length; i++) {
        float left = i == cells.length - 1
            ? x + widths[i] - 10 - font.getStringWidth(cells[i]) / 1000 * 10 : x + 10;
        textAt(cells[i], left, y - 17, 10);
        x += widths[i];
      }
      y -= 29;
    }

    private void textAt(String text, float x, float baseline, int size) throws IOException {
      stream.setNonStrokingColor(new java.awt.Color(16, 43, 83));
      stream.beginText(); stream.setFont(font, size); stream.newLineAtOffset(x, baseline);
      stream.showText(text); stream.endText();
    }

    private List<String> wrap(String raw, int size, float width) throws IOException {
      var result = new ArrayList<String>();
      var current = new StringBuilder();
      for (int cp : raw.replaceAll("[\\p{Cntrl}]", " ").codePoints().toArray()) {
        String ch = new String(Character.toChars(cp));
        try { font.getStringWidth(ch); } catch (IllegalArgumentException ex) { ch = "?"; }
        if (font.getStringWidth(current.toString() + ch) / 1000 * size > width) {
          int boundary = current.lastIndexOf(" ");
          if (boundary > 0) {
            result.add(current.substring(0, boundary)); current.delete(0, boundary + 1);
          } else { result.add(current.toString()); current.setLength(0); }
        }
        current.append(ch);
      }
      result.add(current.toString());
      return result;
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
      for (int i = 0; i < doc.getNumberOfPages(); i++) {
        try (var footer = new PDPageContentStream(doc, doc.getPage(i), PDPageContentStream.AppendMode.APPEND, true, true)) {
          footer.setNonStrokingColor(new java.awt.Color(95, 111, 103));
          footer.beginText(); footer.setFont(font, 9); footer.newLineAtOffset(48, 28);
          footer.showText("CampusSplit | Seite " + (i + 1) + " von " + doc.getNumberOfPages());
          footer.endText();
        }
      }
    }
  }
}
