# B3 – Druck- und Exportausgaben

## B3.1 Zweck und Geltungsbereich

Dieser Baustein beschreibt die von CampusSplit erzeugten Exportausgaben. Exporte dienen dazu, den aktuellen Abrechnungsstand einer Gruppe lesbar oder maschinell weiterverarbeitbar bereitzustellen.

CampusSplit unterstützt zwei Exportformate:

- **PDF** als lesbare Gruppenabrechnung,
- **CSV** als ZIP-Archiv mit fachlich getrennten CSV-Dateien.

Ein Export gehört immer zu genau einer Gruppe. Optional kann ein Zeitraum eingeschränkt und ein Exportinhalt gewählt werden.

Der Export stellt eine Momentaufnahme des zum Erstellungszeitpunkt berechneten Datenstands dar. Das Erstellen eines Exports verändert keine gespeicherten Fachdaten.

Passwörter, Passwort-Hashes, Sitzungsdaten und andere nicht für die Abrechnung benötigte sicherheitsrelevante Daten dürfen nicht exportiert werden.

## B3.2 Exportparameter und Exportumfänge

Der Export wird für eine Gruppe erzeugt. Dabei werden folgende Parameter berücksichtigt:

| Parameter | Bedeutung |
|---|---|
| Gruppe | Gruppe, deren Abrechnung exportiert wird |
| Format | `pdf` oder `csv` |
| Von | optionales Anfangsdatum |
| Bis | optionales Enddatum |
| Inhalt | `all`, `open` oder `expenses` |

Für den Exportinhalt gelten drei fachliche Umfänge:

| Technischer Wert | Anzeige | Inhalt |
|---|---|---|
| `all` | Gesamte Abrechnung | Ausgaben und Kostenanteile, offene Beträge/Salden, Ausgleichsvorschläge sowie erfasste Rückzahlungen |
| `open` | Offene Beträge | offene Beträge/Salden und Ausgleichsvorschläge |
| `expenses` | Nur Ausgaben | Ausgaben und Kostenanteile |

Ist kein Exportumfang angegeben, wird die **gesamte Abrechnung** verwendet.

Ungültige Exportformate oder Exportumfänge werden abgewiesen. Kann der Export technisch nicht erstellt werden, erhält der Benutzer eine verständliche Fehlermeldung.

## B3.3 PDF-Export

Der PDF-Export ist eine lesbare Zusammenfassung der Gruppenabrechnung. Die konkrete Zusammenstellung richtet sich nach dem gewählten Exportumfang.

### B3.3.1 Kopf- und Übersichtsbereich

Der PDF-Export enthält unabhängig vom Exportumfang mindestens:

- Kennzeichnung als Gruppenabrechnung,
- Gruppenname,
- Erstellungsdatum,
- Gruppenwährung,
- gewählten Zeitraum beziehungsweise „Gesamter Zeitraum“,
- gewählten Exportinhalt,
- Summe der Ausgaben im berücksichtigten Datenbestand,
- Anzahl der Ausgaben,
- Anzahl der Gruppenmitglieder.

Wurde ein Zeitraum eingeschränkt, wird zusätzlich darauf hingewiesen, dass Salden und Ausgleichsvorschläge nur den ausgewählten Zeitraum berücksichtigen.

### B3.3.2 Offene Beträge und Ausgleichsvorschläge

Bei den Exportumfängen **Gesamte Abrechnung** und **Offene Beträge** enthält das PDF einen Bereich „Offene Beträge (Salden)“.

Pro Person werden dargestellt:

- Name,
- fachlicher Stand,
- Betrag in Gruppenwährung.

Zusätzlich werden Ausgleichsvorschläge unter „Wer zahlt wem?“ ausgegeben. Ein Vorschlag enthält:

- zahlende Person,
- empfangende Person,
- Betrag in Gruppenwährung.

Sind keine Zahlungen mehr offen, wird dies ausdrücklich als ausgeglichener Zustand dargestellt.

### B3.3.3 Ausgaben und Kostenanteile

Bei den Exportumfängen **Gesamte Abrechnung** und **Nur Ausgaben** enthält das PDF die Ausgaben und ihre Kostenanteile.

Für eine Ausgabe werden, soweit vorhanden beziehungsweise fachlich erforderlich, dargestellt:

- Beschreibung,
- Ausgabedatum,
- zahlende Person,
- Abrechnungsbetrag in Gruppenwährung,
- Kategorie,
- beteiligte Personen,
- Kostenanteil je Person in Gruppenwährung.

Bei einer Ausgabe in Fremdwährung werden zusätzlich dargestellt:

- Originalbetrag,
- Originalwährung,
- verwendeter Wechselkurs,
- gegebenenfalls Datum des verwendeten Wechselkurses.

Damit bleibt nachvollziehbar, wie aus dem Originalbetrag der Abrechnungsbetrag in Gruppenwährung entstanden ist.

Enthält der gewählte Zeitraum keine Ausgaben, wird im Ausgabenbereich ein entsprechender Hinweis ausgegeben.

### B3.3.4 Rückzahlungen

Nur der Exportumfang **Gesamte Abrechnung** enthält den Bereich „Erfasste Rückzahlungen“.

Für eine Rückzahlung werden dargestellt:

- Zahlungsdatum,
- Betrag in Gruppenwährung,
- Status „Erfasst“ oder „Storniert“,
- Sender,
- Empfänger,
- Person, die die Rückzahlung erfasst hat.

Bei einer stornierten Rückzahlung werden zusätzlich, soweit vorhanden, dargestellt:

- Hinweis, dass sie nicht in den offenen Beträgen berücksichtigt wird,
- Stornierungszeitpunkt,
- Person, die storniert hat,
- Stornierungsgrund.

Sind noch keine Rückzahlungen erfasst, wird dies im PDF kenntlich gemacht.

### B3.3.5 Zahlungshinweis

Der PDF-Export weist darauf hin, dass Zahlungen außerhalb von CampusSplit stattfinden.

## B3.4 CSV-/ZIP-Export

Wird als Format CSV gewählt, stellt CampusSplit nicht nur eine einzelne CSV-Datei bereit. Die fachlich getrennten CSV-Daten werden in einem **ZIP-Archiv** zusammengefasst.

Der Download wird als ZIP-Datei bereitgestellt. Die enthaltenen CSV-Dateien sind UTF-8-kodiert.

Welche Dateien enthalten sind, hängt vom Exportumfang ab:

| Datei | `all` | `open` | `expenses` |
|---|:---:|:---:|:---:|
| `gruppe.csv` | ja | ja | ja |
| `ausgaben.csv` | ja | nein | ja |
| `salden.csv` | ja | ja | nein |
| `ausgleich.csv` | ja | ja | nein |
| `rueckzahlungen.csv` | ja | nein | nein |

### B3.4.1 `gruppe.csv`

`gruppe.csv` enthält Metadaten zum Export. Dazu gehören:

- Gruppenname,
- Gruppenwährung,
- Exportzeitpunkt,
- Zeitraum von,
- Zeitraum bis,
- gewählter Exportinhalt.

Diese Datei ist in jedem CSV-/ZIP-Export enthalten.

### B3.4.2 `ausgaben.csv`

`ausgaben.csv` enthält die exportierten Ausgaben einschließlich der für die Abrechnung relevanten Angaben und Kostenanteile.

Die Datei ist bei **Gesamte Abrechnung** und **Nur Ausgaben** enthalten.

Die Daten müssen insbesondere nachvollziehbar machen:

- wann die Ausgabe angefallen ist,
- wofür sie angefallen ist,
- wer bezahlt hat,
- welcher Betrag abgerechnet wird,
- welche Währung beziehungsweise Fremdwährungsinformationen gelten,
- welche Personen beteiligt sind,
- welcher Kostenanteil auf die jeweilige Person entfällt.

Bei Fremdwährungen müssen Originalbetrag, Originalwährung und die für die Abrechnung gespeicherten Umrechnungsinformationen nachvollziehbar bleiben.

### B3.4.3 `salden.csv`

`salden.csv` enthält die berechneten offenen Beträge beziehungsweise Salden der Gruppenmitglieder.

Die Datei ist bei **Gesamte Abrechnung** und **Offene Beträge** enthalten.

Die Salden müssen mit dem zum Exportzeitpunkt berechneten Abrechnungsstand übereinstimmen.

### B3.4.4 `ausgleich.csv`

`ausgleich.csv` enthält die berechneten Ausgleichsvorschläge.

Ein Ausgleichsvorschlag beschreibt fachlich:

- wer zahlt,
- an wen gezahlt wird,
- welchen Betrag die Zahlung umfasst.

Die Datei ist bei **Gesamte Abrechnung** und **Offene Beträge** enthalten.

### B3.4.5 `rueckzahlungen.csv`

`rueckzahlungen.csv` dokumentiert erfasste Rückzahlungen. Sie ist ausschließlich bei **Gesamte Abrechnung** enthalten.

Neben den Zahlungsinformationen müssen auch vorhandene Stornierungsinformationen nachvollziehbar bleiben. Stornierte Rückzahlungen dürfen nicht so dargestellt werden, als würden sie weiterhin die offenen Beträge reduzieren.

## B3.5 Zeitraumfilter

Der Export kann optional durch ein Anfangsdatum und/oder ein Enddatum eingeschränkt werden.

Dabei gelten folgende Regeln:

- ohne Anfangs- und Enddatum wird der gesamte verfügbare Zeitraum betrachtet,
- nur mit Anfangsdatum beginnt der Export mit diesem Datum,
- nur mit Enddatum endet der Export mit diesem Datum,
- mit beiden Angaben wird der eingeschlossene Zeitraum verwendet.

Der gewählte Zeitraum wird im Export kenntlich gemacht.

Salden und Ausgleichsvorschläge eines zeitlich eingeschränkten Exports beziehen sich auf den für diesen Export berechneten Zeitraum. Das PDF weist darauf ausdrücklich hin.

## B3.6 Fremdwährungen und Geldbeträge

Die Gruppenwährung ist die maßgebliche Abrechnungswährung für:

- Kostenanteile,
- Salden,
- Ausgleichsvorschläge,
- Rückzahlungen.

Wurde eine Ausgabe ursprünglich in einer anderen Währung erfasst, muss der Export die Umrechnung nachvollziehbar darstellen. Dazu gehören, soweit gespeichert:

- Originalbetrag,
- Originalwährung,
- Wechselkurs,
- Kursdatum,
- Abrechnungsbetrag in Gruppenwährung.

Die Exportfunktion berechnet keinen neuen historischen Wechselkurs allein zum Zweck des Exports. Maßgeblich sind die für die Ausgabe und die Abrechnung vorliegenden Daten.

## B3.7 Verhalten bei Sonderfällen

| Fall | Erwartetes Verhalten |
|---|---|
| Gruppe hat keine Ausgaben | Export kann erstellt werden; der fehlende Ausgabenbestand wird angemessen dargestellt |
| Zeitraum enthält keine Ausgaben | Export kann erstellt werden; der Ausgabenbereich bleibt leer beziehungsweise enthält einen entsprechenden Hinweis |
| Keine offenen Zahlungen | PDF weist auf den ausgeglichenen Zustand hin; Ausgleichsdaten können entsprechend leer sein |
| Keine Rückzahlungen vorhanden | Bei `all` wird im PDF darauf hingewiesen, dass noch keine Rückzahlungen erfasst wurden |
| Rückzahlung wurde storniert | Stornierungsstatus und vorhandene Stornierungsinformationen werden dokumentiert; sie wird nicht als wirksame Rückzahlung für offene Beträge behandelt |
| Kategorie fehlt | Der Export darf deswegen nicht fehlschlagen |
| Optionaler Wert fehlt | Der Export darf deswegen nicht fehlschlagen, sofern der Wert fachlich optional ist |
| Ungültiges Format | Export wird mit verständlicher Fehlermeldung abgewiesen |
| Ungültiger Exportumfang | Export wird mit verständlicher Fehlermeldung abgewiesen |
| Export kann technisch nicht erstellt werden | Benutzer erhält eine verständliche Fehlermeldung |
| Daten ändern sich nach dem Export | Bereits erzeugter Export bleibt eine Momentaufnahme des damaligen Datenstands |

## B3.8 Regeln

| ID | Regel |
|---|---|
| EXP-01 | Ein Export gehört immer zu genau einer Gruppe. |
| EXP-02 | Der Export verwendet den zum Erstellungszeitpunkt für die Anfrage ermittelten Datenstand. |
| EXP-03 | Ein optional gewählter Zeitraum begrenzt den für den Export ermittelten Abrechnungsstand. |
| EXP-04 | Kostenanteile, Salden und Ausgleichsvorschläge müssen mit den für den Export verwendeten aktuellen Berechnungen übereinstimmen. |
| EXP-05 | Zulässige Exportformate sind PDF und CSV/ZIP. |
| EXP-06 | Zulässige Exportumfänge sind Gesamte Abrechnung (`all`), Offene Beträge (`open`) und Nur Ausgaben (`expenses`). |
| EXP-07 | Ohne expliziten Exportumfang wird die gesamte Abrechnung verwendet. |
| EXP-08 | Der PDF-Inhalt richtet sich nach dem gewählten Exportumfang. |
| EXP-09 | Der CSV-Export wird als ZIP-Archiv mit fachlich getrennten CSV-Dateien bereitgestellt. |
| EXP-10 | `gruppe.csv` ist in jedem CSV-/ZIP-Export enthalten. |
| EXP-11 | `ausgaben.csv` ist bei `all` und `expenses` enthalten. |
| EXP-12 | `salden.csv` und `ausgleich.csv` sind bei `all` und `open` enthalten. |
| EXP-13 | `rueckzahlungen.csv` ist nur bei `all` enthalten. |
| EXP-14 | Das Erstellen eines Exports verändert keine gespeicherten Fachdaten. |
| EXP-15 | Nicht für die Abrechnung benötigte sicherheitsrelevante Daten werden nicht exportiert. |
| EXP-16 | Nicht vorhandene optionale Werte führen nicht zum Abbruch des Exports. |
| EXP-17 | Bei Fremdwährungen bleiben Originalbetrag, Originalwährung und vorhandene Umrechnungsinformationen nachvollziehbar. |
| EXP-18 | Salden, Kostenanteile, Ausgleichsvorschläge und Rückzahlungen werden im fachlichen Kontext der Gruppenwährung dargestellt. |
| EXP-19 | Stornierte Rückzahlungen müssen als storniert erkennbar sein und dürfen nicht als wirksame Rückzahlung in offenen Beträgen erscheinen. |
| EXP-20 | Zahlungen selbst erfolgen außerhalb von CampusSplit. |

## B3.9 Nicht Bestandteil von B3

Nicht festgelegt werden:

- konkrete Implementierungsdetails der verwendeten PDF-Bibliothek,
- genaue Schriftarten, Farben oder Seitenränder des PDFs,
- eine eigenständige Druckfunktion des Browsers oder Betriebssystems,
- dauerhafte Speicherung erzeugter Exportdateien,
- automatischer Versand per E-Mail,
- weitere Exportformate wie Excel oder JSON.

## B3.10 Querverweise

Für B3 sind insbesondere folgende Spezifikationsbausteine relevant:

- [B1 – Dialogspezifikation](B1_Dialogspezifikation.md) – Dialoge zum Anzeigen der Gruppe, der Salden und zum Starten des Exports.
- [F2 – Anwendungsfälle](F2-anwendungsfälle.md) – Export aus Benutzersicht.
- [F3 – Anwendungsfunktionen](F3-anwendungsfunktionen.md) – fachliche Funktionen zur Aufbereitung und Berechnung der Exportdaten.
- [D1 – Datenmodell](D1_Datenmodell.md) – Datenobjekte, aus denen die Exportausgabe erzeugt wird.
- [D2 – Datentypenverzeichnis](D2_Datentypenverzeichnis.md) – fachliche Datentypen, insbesondere Geldbeträge und Währungen.
- [N1 – Nichtfunktionale Anforderungen](N1_Nichtfunktionale Anforderungen.md) – Qualitäts- und Performanceanforderungen.
- [N2 – Querschnittskonzepte](N2_Querschnittskonzepte.md) – übergreifende Regeln, insbesondere Fehlerbehandlung, Geldbeträge und Exportsicherheit.
- [S1 – Nachbarsysteme](S1_Nachbarsysteme.md) – externe Systeme, insbesondere der Wechselkursdienst.

Die konkreten Abschnittsanker sollten den tatsächlich vorhandenen Überschriften der jeweiligen Dokumente entsprechen. Es werden keine neuen Use-Case- oder Anforderungs-IDs eingeführt, die in den referenzierten Bausteinen nicht vorhanden sind.

## Eingesetzte KI-Werkzeuge

Claude (Anthropic) und ChatGPT (OpenAI) wurden unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet.

Die fachlichen Inhalte wurden anschließend mit dem aktuellen Implementierungsstand und den vorhandenen Spezifikationsbausteinen abgeglichen.


## Ausgabenzuordnung bei Rückzahlungen

Ist eine Rückzahlung einer einzelnen Ausgabe zugeordnet, zeigt das PDF „Für Ausgabe: …“ mit der gespeicherten Beschreibung. Die Rückzahlungs-CSV enthält zusätzlich die Spalte `Zugeordnete Ausgabe`; bei allgemeinen Rückzahlungen bleibt sie leer. Teilzahlungen erscheinen mit ihrem tatsächlich erfassten Betrag. Eine spätere Änderung oder Löschung der Ausgabe verändert die gespeicherte Beschreibung nicht. Stornierte Zahlungen bleiben entsprechend gekennzeichnet.

Die Zeitraumfilter dieses Dokuments beschreiben die optionalen Parameter des Export-Endpunkts. Der aktuelle Dialog bietet ausschließlich Exportinhalt und Format an, keine Datumsfelder.
