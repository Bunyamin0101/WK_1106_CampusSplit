package de.thm.campussplit.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Readable presentation of historical audit text; the stored record stays unchanged. */
public record ActivityDetails(String title, List<Field> fields) {
  public record Field(String label, String value) {}

  public static ActivityDetails from(String details) {
    String[] lines = details.split("\\n", -1);
    String title = lines[0];
    if (title.startsWith("Beleg hinzugefügt:") || title.startsWith("Beleg entfernt:")) {
      title = title.replaceFirst(" \\(#\\d+\\)$", "");
    }
    boolean created = title.equals("Ausgabe erstellt");
    var fields = new ArrayList<Field>();
    for (int i = 1; i < lines.length; i++) {
      int colon = lines[i].indexOf(": ");
      if (colon < 0) {
        if (!lines[i].isBlank()) fields.add(new Field("", lines[i]));
        continue;
      }
      String label = lines[i].substring(0, colon);
      String value = lines[i].substring(colon + 2);
      if (created && value.startsWith("— → ")) value = value.substring(4);
      if (label.equals("Bezahlt von") || label.equals("Beteiligte und Kostenanteile")) {
        value = value.replaceAll(" \\(#\\d+\\)(?=:| →|$)", "");
      }
      if (label.equals("Betrag") || label.equals("Beteiligte und Kostenanteile")) {
        value = value.replaceAll("(\\d+)\\.(\\d{2})(?= [A-Z]{3}\\b)", "$1,$2");
      }
      if (label.equals("Beteiligte und Kostenanteile")) {
        label = "Kostenanteile";
        value = value.replaceAll("(?<=[A-Z]{3}), ", "\n");
      }
      if (label.equals("Aufteilung")) {
        value = value.replace("CUSTOM_AMOUNT", "Individuelle Beträge").replace("EQUAL", "Gleichmäßig");
      }
      if (label.equals("Datum")) {
        var date = java.util.regex.Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b").matcher(value);
        value = date.replaceAll(match -> LocalDate.parse(match.group()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
      }
      fields.add(new Field(label, value));
    }
    return new ActivityDetails(title, List.copyOf(fields));
  }
}
