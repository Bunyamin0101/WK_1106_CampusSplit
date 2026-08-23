# B3 — Druck- und Exportausgaben

B3 legt fest, welche Inhalte CampusSplit beim Export erzeugt. Der Export wird über [DLG-11 in B1](B1_Dialogspezifikation.md#dlg-11--export) gestartet und gehört zu [UC-12 — Ausgabenübersicht exportieren](F2-anwendungsf%C3%A4lle.md#uc-12--ausgaben%C3%BCbersicht-exportieren).

Eine eigene Druckfunktion gibt es nicht. Der PDF-Export kann über den Browser oder einen PDF-Viewer ausgedruckt werden.

Technische Details wie verwendete PDF-Bibliothek oder konkrete CSV-Kodierung gehören nicht in B3.

## B3.1 Formate

| Format | Wert | Zweck |
|---|---|---|
| PDF | `PDF` | Lesbare Übersicht für Benutzer und zum Ausdrucken |
| CSV | `CSV` | Tabellarische Daten zur Weiterverarbeitung |

Die erlaubten Exportformate sind in [D2.7 — ExportFormatDT](D2_Datentypenverzeichnis_%28ZO%29.md#d27-exportformatdt) definiert.

Ein Export bezieht sich immer auf genau eine Gruppe und auf den Datenstand zum Zeitpunkt der Erstellung. Optional kann in [DLG-11](B1_Dialogspezifikation.md#dlg-11--export) ein Zeitraum gewählt werden.

## B3.2 Mindestinhalte

Jeder Export enthält die Daten, die für eine nachvollziehbare Gruppenabrechnung benötigt werden.

| Bereich | Inhalt |
|---|---|
| Gruppe | Gruppenname |
| Export | Erstellungsdatum und ggf. gewählter Zeitraum |
| Mitglieder | Namen der Gruppenmitglieder |
| Ausgaben | Datum, Beschreibung, Kategorie, Zahler, Originalbetrag und Originalwährung; bei Fremdwährung zusätzlich Wechselkurs und Abrechnungsbetrag |
| Kostenanteile | Beteiligte Mitglieder und deren Anteil an der Ausgabe |
| Salden | aktueller Saldo je Mitglied in der Gruppenwährung |
| Ausgleich | berechnete Ausgleichsvorschläge |

Die fachlichen Datenobjekte sind in [D1 — Datenmodell](D1_Datenmodell_%28ZO%29.md) beschrieben. Die Berechnung von Kostenanteilen, Salden und Ausgleichsvorschlägen erfolgt gemäß [F3 — Anwendungsfunktionen](F3-anwendungsfunktionen.md).

Passwörter, Passwort-Hashes, Sitzungsdaten und andere nicht benötigte sicherheitsrelevante Daten dürfen nicht exportiert werden. Dazu gelten zusätzlich die Regeln aus [N2.8 — Exportsicherheit](N2_Querschnittskonzepte_%28ZO%29.md#n28-exportsicherheit).

## B3.3 PDF-Export

Der PDF-Export ist eine lesbare Zusammenfassung der Gruppe.

### Aufbau

1. **Kopfbereich**
   - Gruppenname
   - Exportdatum
   - gewählter Zeitraum, falls vorhanden

2. **Mitglieder**
   - Liste der Gruppenmitglieder

3. **Ausgaben**
   - Datum
   - Beschreibung
   - Kategorie, falls vorhanden
   - Zahler
   - Originalbetrag und Originalwährung
   - bei Fremdwährung: verwendeter Wechselkurs und Abrechnungsbetrag in Gruppenwährung
   - Beteiligte mit jeweiligem Kostenanteil in Gruppenwährung

4. **Salden**
   - Saldo pro Gruppenmitglied
   - gleiche Bedeutung wie in [DLG-10 — Salden anzeigen](B1_Dialogspezifikation.md#dlg-10--salden-anzeigen):
     - „bekommt zurück“
     - „schuldet“
     - „ausgeglichen“

5. **Ausgleichsvorschläge**
   - wer wem welchen Betrag zahlen sollte

6. **Hinweis**
   - Die eigentliche Zahlung findet außerhalb von CampusSplit statt.

### Beispiel

```text
CampusSplit — Gruppenabrechnung
Gruppe: WG Sonnenallee
Exportdatum: 23.08.2026

Ausgaben
----------------------------------------------------
12.08.2026  Supermarkt   Anna   42,00 EUR
  Anna: 14,00 EUR
  Max:  14,00 EUR
  Lisa: 14,00 EUR

Salden
----------------------------------------------------
Anna   bekommt 28,00 EUR zurück
Max    schuldet 14,00 EUR
Lisa   schuldet 14,00 EUR

Ausgleichsvorschläge
----------------------------------------------------
Max  -> Anna   14,00 EUR
Lisa -> Anna   14,00 EUR
```


Bei einer Fremdwährung muss die Umrechnung nachvollziehbar sein:

```text
Restaurant
Originalbetrag:     30,00 USD
Wechselkurs:        1 USD = 0,86 EUR
Abrechnungsbetrag:  25,80 EUR
```

Das Beispiel legt kein endgültiges Layout fest. Es zeigt nur, welche Informationen im PDF erkennbar sein müssen.

Beträge werden entsprechend [D2.3 — MoneyAmountDT](D2_Datentypenverzeichnis_%28ZO%29.md#d23-moneyamountdt) dargestellt.

## B3.4 CSV-Export

Der CSV-Export dient der Weiterverarbeitung der Daten. Anders als beim PDF steht hier eine klare Tabellenstruktur im Vordergrund.

Der CSV-Export besteht aus **zwei fachlich getrennten Tabellen**:

1. Ausgaben mit Kostenanteilen
2. Salden mit Ausgleichsinformationen

Die konkrete technische Bereitstellung der beiden CSV-Dateien wird später in der Architektur festgelegt. Fachlich sind die beiden folgenden Strukturen verbindlich.

### B3.4.1 Ausgaben

Eine Zeile steht für den Kostenanteil eines Mitglieds an einer Ausgabe. Dadurch bleibt die Datei tabellarisch und es werden keine variablen Spalten pro Gruppenmitglied benötigt.

| Spalte | Inhalt |
|---|---|
| `expense_date` | Datum der Ausgabe |
| `description` | Beschreibung |
| `category` | Kategorie, falls vorhanden |
| `payer` | Name des Zahlers |
| `original_amount` | ursprünglicher Betrag der Ausgabe |
| `original_currency` | Währung der Ausgabe |
| `exchange_rate` | verwendeter Wechselkurs; leer, wenn keine Umrechnung nötig war |
| `settlement_amount` | umgerechneter Gesamtbetrag in Gruppenwährung |
| `settlement_currency` | Gruppenwährung |
| `participant` | beteiligtes Mitglied |
| `share` | Kostenanteil dieses Mitglieds in Gruppenwährung |

Beispiel:

```text
expense_date,description,category,payer,original_amount,original_currency,exchange_rate,settlement_amount,settlement_currency,participant,share
2026-08-12,Restaurant,Essen,Anna,30.00,USD,0.86,25.80,EUR,Anna,12.90
2026-08-12,Restaurant,Essen,Anna,30.00,USD,0.86,25.80,EUR,Max,12.90
```

Das verwendete Trennzeichen und die Zeichenkodierung sind technische Entscheidungen und werden hier nicht festgelegt.

### B3.4.2 Salden

Eine Zeile steht für ein Gruppenmitglied.

| Spalte | Inhalt |
|---|---|
| `member` | Name des Mitglieds |
| `balance` | berechneter Saldo |
| `status` | `CREDIT`, `DEBT` oder `BALANCED` |

Beispiel:

```text
member,balance,status
Anna,28.00,CREDIT
Max,-14.00,DEBT
Lisa,-14.00,DEBT
```

Ausgleichsvorschläge müssen ebenfalls im CSV-Export enthalten sein. Sie können als zusätzliche Tabelle mit den Spalten `from`, `to` und `amount` ausgegeben werden.

## B3.5 Verhalten bei Sonderfällen

| Fall | Erwartetes Verhalten |
|---|---|
| Gruppe hat keine Ausgaben | Export kann erstellt werden; Ausgabenbereich ist leer, Salden sind ausgeglichen |
| Zeitraum enthält keine Ausgaben | Export wird mit leerem Ausgabenbereich und Hinweis auf den gewählten Zeitraum erzeugt |
| Kategorie fehlt | Feld bleibt leer; Export darf nicht fehlschlagen |
| Export kann nicht erstellt werden | Benutzer erhält in DLG-11 eine verständliche Fehlermeldung |
| Daten ändern sich nach dem Export | Bereits erzeugter Export bleibt eine Momentaufnahme des damaligen Datenstands |

Fehlermeldungen werden nach den allgemeinen Regeln aus [N2.6 — Fehlerbehandlung](N2_Querschnittskonzepte_%28ZO%29.md#n26-fehlerbehandlung) behandelt.

## B3.6 Regeln

| ID | Regel |
|---|---|
| EXP-01 | Ein Export gehört immer zu genau einer Gruppe. |
| EXP-02 | Der Export verwendet den Datenstand zum Zeitpunkt der Erstellung. |
| EXP-03 | Ein optional gewählter Zeitraum filtert die enthaltenen Ausgaben. |
| EXP-04 | Kostenanteile, Salden und Ausgleichsvorschläge müssen mit den aktuellen Berechnungen aus F3 übereinstimmen. |
| EXP-05 | Der PDF-Export enthält die Bereiche aus B3.3. |
| EXP-06 | Der CSV-Export enthält Ausgaben, Kostenanteile, Salden und Ausgleichsvorschläge gemäß B3.4. |
| EXP-07 | Das Erstellen eines Exports verändert keine gespeicherten Daten. |
| EXP-08 | Geldbeträge werden gemäß D2 und N2.5 verarbeitet und dargestellt. |
| EXP-09 | Sicherheitsregeln aus N2.8 gelten für jeden Export. |
| EXP-10 | Nicht vorhandene optionale Werte führen nicht zum Abbruch des Exports. |
| EXP-11 | Bei Fremdwährung werden Originalbetrag, Originalwährung, verwendeter Wechselkurs und Abrechnungsbetrag ausgegeben. |
| EXP-12 | Salden, Kostenanteile und Ausgleichsvorschläge werden in der Gruppenwährung ausgegeben. |

Die Anforderungen an die Exportdauer stehen in [NFR-12a-02 — Wie lange ein Export dauern darf](N1_Nichtfunktionale%20Anforderungen_%28ZO%29.md#nfr-12a-02-wie-lange-ein-export-dauern-darf).

## B3.7 Nicht Bestandteil von B3

Nicht festgelegt werden:

- verwendete PDF-Bibliothek,
- CSV-Trennzeichen und Zeichenkodierung,
- genaue Schriftarten, Farben oder Seitenränder,
- eine eigene Druckfunktion,
- dauerhafte Speicherung der Exportdatei,
- automatischer Versand per E-Mail,
- weitere Formate wie Excel oder JSON.

## B3.8 Querverweise

| Baustein | Relevanz |
|---|---|
| [F2 — Anwendungsfälle](F2-anwendungsf%C3%A4lle.md#f27-export) | UC-12 beschreibt den Export aus Benutzersicht |
| [F3 — Anwendungsfunktionen](F3-anwendungsfunktionen.md#af-04--exportdaten-aufbereiten) | AF-04 bereitet die Exportdaten fachlich auf |
| [D1 — Datenmodell](D1_Datenmodell_%28ZO%29.md) | Datenobjekte, aus denen der Export erzeugt wird |
| [D2 — Datentypenverzeichnis](D2_Datentypenverzeichnis_%28ZO%29.md) | Geldbeträge und Exportformate |
| [B1 — Dialogspezifikation](B1_Dialogspezifikation.md#dlg-11--export) | DLG-11 startet den Export |
| [N1 — Nichtfunktionale Anforderungen](N1_Nichtfunktionale%20Anforderungen_%28ZO%29.md) | Performance- und Qualitätsanforderungen |
| [N2 — Querschnittskonzepte](N2_Querschnittskonzepte_%28ZO%29.md#n28-exportsicherheit) | Exportsicherheit und weitere Querschnittsregeln |
| [S1 — Nachbarsysteme](S1_Nachbarsysteme.md) | Externer Dienst zur Ermittlung von Wechselkursen |

## Eingesetzte KI-Werkzeuge

Claude (Anthropic) und ChatGPT (OpenAI) wurden unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet.

Die fachlichen Inhalte wurden anschließend mit den vorhandenen Spezifikationsbausteinen abgeglichen.
