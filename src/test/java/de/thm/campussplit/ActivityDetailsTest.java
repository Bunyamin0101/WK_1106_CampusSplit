package de.thm.campussplit;

import static org.assertj.core.api.Assertions.assertThat;
import de.thm.campussplit.domain.*;
import org.junit.jupiter.api.Test;

class ActivityDetailsTest {
  @Test void presentsExistingRecordsWithoutTechnicalIds() {
    String raw = "Ausgabe erstellt\nBeschreibung: — → Einkauf #123\nBetrag: — → 36.00 EUR\nBezahlt von: — → Lena (#34)\nDatum: — → 2026-09-18\nAufteilung: — → EQUAL\nBeteiligte und Kostenanteile: — → Max (#1): 12.00 EUR, Lena (#34): 24.00 EUR";
    var actor = new User();
    actor.setName("Max");
    var change = new ExpenseChange(1L, 2L, actor, raw);
    var fields = change.getDisplayDetails().fields();
    assertThat(change.getDetails()).isEqualTo(raw);
    assertThat(fields).extracting(ActivityDetails.Field::value).containsExactly(
      "Einkauf #123", "36,00 EUR", "Lena", "18.09.2026", "Gleichmäßig", "Max: 12,00 EUR\nLena: 24,00 EUR");
  }
  @Test void keepsBeforeAndAfterValuesForEdits() {
    var result = ActivityDetails.from("Ausgabe geändert\nBezahlt von: Max (#1) → Lena (#34)\nAufteilung: EQUAL → CUSTOM_AMOUNT");
    assertThat(result.fields()).extracting(ActivityDetails.Field::value).containsExactly("Max → Lena", "Gleichmäßig → Individuelle Beträge");
  }
  @Test void removesOnlyGeneratedReceiptSuffix() {
    assertThat(ActivityDetails.from("Beleg hinzugefügt: Rechnung #42.pdf (#5)").title()).isEqualTo("Beleg hinzugefügt: Rechnung #42.pdf");
    assertThat(ActivityDetails.from("Beleg entfernt: Rechnung.pdf (#5)").title()).isEqualTo("Beleg entfernt: Rechnung.pdf");
    assertThat(ActivityDetails.from("Ausgabe gelöscht: Einkauf #42").title()).isEqualTo("Ausgabe gelöscht: Einkauf #42");
  }
}
