# S2 — Datenmigration

S2 beschreibt die Übernahme von Daten aus einem bestehenden Altsystem in ein neues System. Für CampusSplit ist dieser Baustein fachlich geprüft, aber nicht anwendbar.

---

## S2.1 Einordnung

CampusSplit ist ein Greenfield-Projekt. Das bedeutet, dass die Anwendung neu entwickelt wird und ohne bestehendes Vorgängersystem startet.

| Thema | Bewertung für CampusSplit |
|------|-----------------------------|
| Vorgängersystem | nicht vorhanden |
| Altdatenbank | nicht vorhanden |
| Altdatenübernahme | nicht erforderlich |
| Parallelbetrieb | nicht vorgesehen |
| Cut-over-Termin | nicht erforderlich |
| Datenbereinigung vor Migration | nicht erforderlich |

---

## S2.2 Begründung der Nichtanwendbarkeit

Eine Datenmigration wäre erforderlich, wenn CampusSplit bestehende Daten aus einem anderen System übernehmen müsste.

Beispiele für typische Migrationsquellen wären:

| Migrationsquelle | Relevanz für CampusSplit |
|------------------|---------------------------|
| Alte Datenbank | nicht vorhanden |
| Excel-Abrechnungen | nicht Bestandteil des Projektumfangs |
| Andere Split-App | nicht vorhanden |
| Bankdaten | ausgeschlossen |
| Zahlungsanbieter-Daten | ausgeschlossen |
| Bestehende Benutzerkonten | nicht vorhanden |

Da keine dieser Quellen Teil des Projekts ist, startet CampusSplit mit einer leeren Datenbank. Benutzer:innen, Gruppen, Ausgaben und Kostenanteile entstehen erst durch spätere Nutzung der Anwendung.

---

## S2.3 Startzustand der Daten

Bei der Erstinbetriebnahme ist die Datenbank leer oder enthält nur technisch notwendige Initialdaten.

| Datenobjekt | Startzustand |
|------------|--------------|
| Benutzer | keine Benutzer vorhanden |
| Gruppen | keine Gruppen vorhanden |
| Mitgliedschaften | keine Mitgliedschaften vorhanden |
| Ausgaben | keine Ausgaben vorhanden |
| Kostenanteile | keine Kostenanteile vorhanden |
| Kategorien | optional vordefinierte Standardkategorien |

Optionale Standardkategorien wie „Lebensmittel“, „Unterkunft“, „Fahrtkosten“, „Freizeit“ oder „Sonstiges“ sind keine Datenmigration. Sie sind Initialdaten der Anwendung.

---

## S2.4 Abgrenzung zu Datenbankmigrationen

S2 meint die fachliche Übernahme von Altdaten aus einem Vorgängersystem.

Davon zu unterscheiden sind technische Datenbankmigrationen während der Entwicklung oder bei späteren Releases.

| Begriff | Bedeutung | Relevanz |
|--------|-----------|----------|
| Datenmigration aus Altsystem | Übernahme bestehender fachlicher Daten | nicht anwendbar |
| Datenbankmigration | Änderung des Datenbankschemas bei Releases | relevant für S3 und Architektur |
| Initialdaten | optionale Startwerte wie Kategorien | möglich, aber keine Migration |

---

## S2.5 Ergebnis

S2 wird für CampusSplit ausdrücklich als **nicht anwendbar** markiert.

Die Nichtanwendbarkeit bedeutet nicht, dass der Baustein vergessen wurde. Der Baustein wurde geprüft und ausgeschlossen, weil CampusSplit ohne Vorgängersystem und ohne Altdaten startet.

---

## S2.6 Querverweise

| Baustein | Relevanz für S2 |
|----------|-----------------|
| P1 | Beschreibt CampusSplit als neu entwickeltes System mit begrenztem Projektumfang. |
| P2 | Systemkontext enthält kein Vorgängersystem. |
| D1 | Daten entstehen erst durch Nutzung der Anwendung. |
| D2 | Datentypen gelten für neu erfasste Daten, nicht für migrierte Altdaten. |
| S3 | Erstinbetriebnahme startet mit leerer Datenbank; technische Schemaänderungen gehören zu S3. |
| N1 | Datenkonsistenz gilt für neu gespeicherte Daten. |
| E2 | Glossar erklärt Greenfield-Projekt und Datenmigration. |
