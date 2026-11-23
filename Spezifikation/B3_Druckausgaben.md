# B3 Druckausgaben

CampusSplit erzeugt genau eine Druckausgabe: die Abrechnung einer Gruppe, als PDF (DR-01) oder als CSV-Dateien in einem ZIP-Archiv (DR-02). Niemand gibt darin etwas ein.

| | |
|---|---|
| Anlass | UC-18 aus DLG-06; es gibt keinen Batch ([E1.3](E1-leseanleitung.md#e13-nicht-anwendbare-bausteine)) |
| Empfänger | das Gruppenmitglied, das sie herunterlädt |
| Verbleib | Die Datei wird beim Herunterladen erzeugt und in CampusSplit nicht gespeichert. Was der Benutzer damit macht, liegt außerhalb von CampusSplit. |
| Zeitraum | immer die ganze Gruppe; die Oberfläche bietet keinen Zeitraumfilter |
| Dateiname | `campussplit-` + interne Nummer der Gruppe + `.pdf` bzw. `.zip` |
| Datenstand | Salden und Vorschläge werden beim Herunterladen berechnet (AF-02, AF-03) und stimmen mit DLG-06 überein. |

## B3.1 Abrechnungsinhalt

| Teil | Gesamte Abrechnung | Offene Beträge | Nur Ausgaben |
|---|---|---|---|
| Kopf | ✓ | ✓ | ✓ |
| Salden und Ausgleichsvorschläge | ✓ | ✓ | — |
| Ausgaben und Kostenanteile | ✓ | — | ✓ |
| Rückzahlungen | ✓ | — | — |

## B3.2 DR-01 Abrechnung als PDF

![Musterseite DR-01](diagramme/b3-pdf-musterseite.svg)

Die Musterseite zeigt „Gesamte Abrechnung“ mit Beispieldaten. Beträge stehen im deutschen Format mit der Gruppenwährung. Die Spalte „Herkunft“ der Feldliste unterscheidet die Feldarten: **Text** ist fester Text, ein Attribut aus D1 ist ein Anzeigefeld, AF-02/AF-03 und „Summe“ sind berechnete Felder. Eingabefelder und Bedienelemente gibt es nicht.

| Teil | Feld | Herkunft | Regel |
|---|---|---|---|
| Kopf | Titel | Text | „Gruppenabrechnung“ |
| | Gruppenname | Group.name | — |
| | Erstellt am, Gruppenwährung | Tagesdatum, Group.currency | „Erstellt am TT.MM.JJJJ \| Gruppenwährung: EUR“ |
| | Zeitraum | Text | „Gesamter Zeitraum“ |
| | Inhalt | Abrechnungsinhalt | — |
| | Ausgaben gesamt | Summe der Expense.settlementAmount | — |
| | Anzahl | Zahl der Ausgaben und Mitglieder | „1 Ausgabe \| 3 Mitglieder“ |
| Offene Beträge (Salden) | Person, Stand, Betrag | AF-02 | Betrag ohne Vorzeichen; der Stand sagt die Richtung |
| Ausgleichsvorschläge – Wer zahlt wem? | Von, An, Betrag | AF-03 | ohne Vorschläge: „Alles ausgeglichen. Es sind keine Zahlungen offen.“ |
| Ausgaben und Kostenanteile (je Ausgabe) | Beschreibung; Datum \| Bezahlt von; Betrag | Expense | Betrag ist der Abrechnungsbetrag; ohne Ausgaben: „Keine Ausgaben im gewählten Zeitraum.“ |
| | Ursprünglich; Umrechnung | Expense.originalAmount, exchangeRate, rateDate | nur bei Fremdwährung: „Umrechnung: 1 USD = 0,9 EUR \| Kurs vom TT.MM.JJJJ“ |
| | Kategorie | Expense.category | nur wenn vorhanden |
| | Person, Kostenanteil | ExpenseShare | — |
| Erfasste Rückzahlungen (je Rückzahlung) | Datum \| Betrag \| Status; Sender an Empfänger; Erfasst von | Repayment | Status „Erfasst“ oder „Storniert“; ohne Rückzahlungen: „Noch keine Rückzahlungen erfasst.“ |
| | Für Ausgabe | Repayment.expenseDescription | nur bei Zuordnung |
| | Stornierung | Repayment.cancelledAt, cancelledBy, cancellationReason | nur bei Status „Storniert“: „Nicht in den offenen Beträgen berücksichtigt.“, „Storniert am TT.MM.JJJJ um HH:MM von …“, „Grund: …“ |
| Fuß | Zahlungshinweis | Text | „Zahlungen finden außerhalb von CampusSplit statt.“ |

## B3.3 DR-02 Abrechnung als CSV-Dateien im ZIP

Jede Datei ist UTF-8-kodiert, durch Kommas getrennt, jeder Wert steht in Anführungszeichen. Beträge haben einen Punkt als Dezimaltrennzeichen, Daten das Format JJJJ-MM-TT, Zeitpunkte das Format JJJJ-MM-TTThh:mm:ssZ in Weltzeit (UTC). Ein Wert, der mit `=`, `+`, `-` oder `@` beginnt, erhält ein vorangestelltes `'`, damit eine Tabellenkalkulation ihn nicht als Formel ausführt. Ausgenommen sind die Saldo-Beträge in `salden.csv`; sie bleiben Zahlen mit Vorzeichen.

| Datei | enthalten bei | Eine Zeile je | Spalten |
|---|---|---|---|
| `gruppe.csv` | allen Inhalten | Gruppe | Gruppe, Währung, Exportiert am, Zeitraum von, Zeitraum bis, Inhalt |
| `ausgaben.csv` | Gesamte Abrechnung, Nur Ausgaben | Kostenanteil | Datum, Beschreibung, Kategorie, Bezahlt von, Originalbetrag, Originalwährung, Wechselkurs, Ausgabenbetrag (nicht je Person summieren), Gruppenwährung, Beteiligte Person, Kostenanteil |
| `salden.csv` | Gesamte Abrechnung, Offene Beträge | Mitglied | Person, Saldo, Status |
| `ausgleich.csv` | Gesamte Abrechnung, Offene Beträge | Vorschlag | Von, An, Betrag |
| `rueckzahlungen.csv` | Gesamte Abrechnung | Rückzahlung | Datum, Von, An, Betrag, Währung, Erfasst von, Status, Storniert am, Storniert von, Stornogrund, Zugeordnete Ausgabe |

In `ausgaben.csv` steht der Abrechnungsbetrag einer Ausgabe in jeder ihrer Zeilen; die Spaltenüberschrift warnt davor, ihn zu summieren. In `gruppe.csv` bleiben „Zeitraum von“ und „Zeitraum bis“ leer, weil immer der ganze Zeitraum exportiert wird. „Saldo“ in `salden.csv` ist positiv für Gläubiger und negativ für Schuldner.

## B3.4 Was nie in einer Abrechnung steht

E-Mail-Adressen, Passwörter, Google-Kennungen und Belegdateien ([NFR-12](N1-nichtfunktionale-anforderungen.md#n16-sicherheit)).
