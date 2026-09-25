# D2 - Datentypenverzeichnis

Dieses Datentypenverzeichnis fasst die fachlichen Datentypen zusammen, die im Datenmodell von CampusSplit eine besondere Rolle spielen. Einfache Standardtypen wie Text, Integer, Boolean, Date, Email, Timestamp oder URL werden hier nicht eigens erläutert, da sie unmittelbar in den Attributtabellen des Datenmodells verwendet werden und keiner zusätzlichen fachlichen Erklärung bedürfen.

Im Mittelpunkt stehen dagegen Wertebereiche, Gleichheits- und Ordnungsregeln sowie Verarbeitungsvorgaben für die komplexeren Typen. Technische Aspekte wie Datenbankspaltentypen, ORM-Mapping, Serialisierung oder konkrete Framework-Implementierungen werden bewusst ausgeklammert; sie gehören in die Architektur- und Implementierungsdokumentation und nicht in diesen Baustein.

# D2.1 Typenkatalog

| Typ          | Art                          | **Verwendung**                                        |
| ---------------- | --------------------------------- | ----------------------------------------------------- |
| Identifier       | Eindeutiger technischer Schlüssel | Identifikation von Entitäten                          |
| MoneyAmountDT    | Zusammengesetzter Geldbetrag (Betrag + Währung) | Ausgaben, Kostenanteile, Salden, Ausgleichsvorschläge |
| CurrencyCodeDT   | Währungscode                      | Bestandteil von MoneyAmountDT                     |
| ExchangeRateDT   | Zusammengesetzter Wechselkurswert | Umrechnung von Fremdwährungsausgaben in die Gruppenwährung (siehe [S1](S1_Nachbarsysteme.md)) |
| MembershipRoleDT | Aufzählung                        | Rolle eines Benutzers innerhalb einer Gruppe          |
| SplitMethodDT    | Aufzählung                        | Art der Kostenaufteilung                              |
| ExportFormatDT   | Aufzählung                        | Format einer Ausgabenübersicht                        |

# D2.2 Identifier

Der Identifier ist der eindeutige technische Schlüssel, über den fachliche Entitäten angesprochen werden. Er kommt unter anderem bei [User](D1_Datenmodell.md#user).id, [Group](D1_Datenmodell.md#group).id, [Membership](D1_Datenmodell.md#membership).id, [Expense](D1_Datenmodell.md#expense).id, [ExpenseShare](D1_Datenmodell.md#expenseshare).id und [Category](D1_Datenmodell.md#category).id zum Einsatz.

### Wertebereich

Ein Identifier ist ein eindeutiger Wert. Wie er intern aufgebaut ist, spielt für die fachliche Spezifikation keine Rolle.

### Interne Struktur (fachliche Sicht)

Auch wenn die konkrete technische Umsetzung nicht Teil dieses Bausteins ist, gilt fachlich Folgendes für die Struktur eines Identifiers:

| Merkmal | Beschreibung |
| ------- | ------------ |
| Eindeutigkeit | Kein Identifier wird innerhalb desselben Entitätstyps zweimal vergeben. |
| Unveränderlichkeit | Der Wert ändert sich nach der Erzeugung nie mehr. |
| Undurchsichtigkeit | Der Wert selbst trägt keine fachliche Bedeutung (z. B. kein Rückschluss auf Reihenfolge oder Kategorie). |
| Typischer Aufbau | In der Praxis meist eine zufällige, eindeutige Zeichenkette (z. B. UUID); die konkrete Wahl ist eine Implementierungsentscheidung. |

### Erzeugung

Der Identifier entsteht beim Anlegen einer Entität, bleibt über deren gesamte Lebenszeit unverändert und wird nach dem Löschen der Entität nicht erneut vergeben.

### Gleichheit und Ordnung

Zwei Identifier sind genau dann gleich, wenn ihre Werte exakt übereinstimmen. Eine fachliche Sortierung nach Identifier ist nicht vorgesehen - dafür werden Attribute wie createdAt, expenseDate oder name herangezogen.

# D2.3 MoneyAmountDT

MoneyAmountDT bildet einen Geldbetrag ab und wird für den Originalbetrag und den Abrechnungsbetrag einer [Expense](D1_Datenmodell.md#expense), für [ExpenseShare](D1_Datenmodell.md#expenseshare)s, für den berechneten Saldo eines Gruppenmitglieds sowie für Ausgleichsvorschläge zwischen Schuldner und Gläubiger verwendet.

### Struktur

MoneyAmountDT ist kein einzelner Wert, sondern setzt sich aus zwei Bestandteilen zusammen:

| Bestandteil | Typ | Beschreibung |
| ----------- | --- | ------------ |
| value | Dezimalwert mit zwei Nachkommastellen | Der eigentliche Betrag. |
| currency | [CurrencyCodeDT](#d24-currencycodedt) | Die Währung, in der der Betrag angegeben ist. |

Betrag und Währung gehören damit fachlich untrennbar zusammen: Ein Geldbetrag ohne Währungsangabe ist fachlich nicht sinnvoll auswertbar.

### Wertebereich

Der value-Bestandteil eines MoneyAmountDT besitzt immer genau zwei Nachkommastellen.

| **Beispielwert** | **Bedeutung**                     |
| ---------------- | --------------------------------- |
| 0.00 EUR             | Kein Betrag                       |
| 10.00 EUR            | Zehn Euro                         |
| 30.00 USD            | Dreißig US-Dollar (Originalbetrag einer Fremdwährungsausgabe) |
| \-5.00 EUR           | Negativer Saldo von fünf Euro     |

### Regeln

Beträge werden centgenau verarbeitet. Eine Ausgabe muss größer als 0.00 sein, während Kostenanteile zwar nicht negativ sein dürfen, aber durchaus 0.00 betragen können. Salden hingegen dürfen positiv, negativ oder null sein: Ein positiver Saldo bedeutet, dass ein Mitglied Geld zurückbekommt, ein negativer, dass es Geld schuldet.

Zwei rechnerische Bedingungen sind dabei zentral: Die Summe aller Kostenanteile einer Ausgabe muss exakt dem **Abrechnungsbetrag in Gruppenwährung** entsprechen, und die Summe aller Salden innerhalb einer Gruppe muss stets 0.00 ergeben.

### Rundung

Bei einer gleichmäßigen Aufteilung lässt sich eine Rundungsdifferenz nicht immer vermeiden. Werden beispielsweise 10.00 Euro auf drei Personen verteilt, ergibt sich folgendes Bild:

| **Person** | **Anteil** |
| ---------- | ---------- |
| Person A   | 3.34       |
| Person B   | 3.33       |
| Person C   | 3.33       |

Die Rundung muss deterministisch erfolgen, das heißt: Bei identischer Eingabe liefert das System immer dieselbe Verteilung. In jedem Fall muss die Summe der gerundeten Teilbeträge exakt dem Gesamtbetrag entsprechen.

### Gleichheit

Zwei Geldbeträge gelten als gleich, wenn ihr value denselben centgenauen Wert besitzt UND ihre currency identisch ist. Beträge in unterschiedlichen Währungen sind ohne Umrechnung nicht direkt vergleichbar.

| **Betrag A** | **Betrag B** | **Ergebnis** |
| ------------ | ------------ | ------------ |
| 10.00 EUR    | 10.00 EUR    | gleich       |
| 10.00 EUR    | 10.01 EUR    | nicht gleich |
| 10.00 EUR    | 10.00 USD    | nicht direkt vergleichbar (siehe [ExchangeRateDT](#d24a-exchangeratedt)) |

### Ordnung

Geldbeträge derselben Währung sind numerisch sortierbar, zum Beispiel: -5.00 EUR < 0.00 EUR < 10.00 EUR. Beträge unterschiedlicher Währungen besitzen ohne Umrechnung keine fachliche Ordnung zueinander.

### Verarbeitungshinweis

Geldbeträge werden mit `BigDecimal` und zwei Nachkommastellen verarbeitet und gespeichert. Für die gleichmäßige Aufteilung verwendet `SplitService` vorübergehend ganze Centbeträge, um Rundungsreste deterministisch zu verteilen. Wechselkurse besitzen eine höhere Dezimalpräzision. `double` und `float` werden nicht für Geldbeträge verwendet.

# D2.4 CurrencyCodeDT

CurrencyCodeDT beschreibt die Währung eines Geldbetrags und ist Bestandteil von [MoneyAmountDT](#d23-moneyamountdt).

### Wertebereich

CampusSplit erlaubt ausschließlich `EUR` (Euro) und `USD` (US-Dollar), sowohl für Gruppen als auch für Ausgaben. Das Backend prüft diese feste Auswahl unabhängig von den weiteren Währungen des Wechselkursdienstes.

Jede Gruppe besitzt eine feste **Gruppenwährung** (siehe [Group.currency](D1_Datenmodell.md#group)). Eine Ausgabe kann in einer anderen Währung erfasst werden als die Gruppenwährung; in diesem Fall wird über den Wechselkursdienst ein Wechselkurs ermittelt und der Betrag in die Gruppenwährung umgerechnet (siehe [ExchangeRateDT](#d24a-exchangeratedt) und S1).

| **Beispielwert** | **Bedeutung** |
| -------- | ------------- |
| EUR      | Euro          |
| USD      | US-Dollar     |

### Regeln

Eine Ausgabe kann in ihrer Originalwährung von der Gruppenwährung abweichen. Salden und Ausgleichsvorschläge werden immer ausschließlich in der Gruppenwährung angegeben; sie enthalten keine unterschiedlichen Währungen gemischt.

### Gleichheit und Ordnung

Zwei Währungscodes sind gleich, wenn ihr Code identisch ist. Eine fachliche Sortierung von Währungscodes ist nicht vorgesehen.

# D2.4a ExchangeRateDT

ExchangeRateDT bildet einen Wechselkurs ab, der zur Umrechnung einer Fremdwährungsausgabe in die Gruppenwährung verwendet wird. Er kommt bei [Expense](D1_Datenmodell.md#expense).exchangeRate zum Einsatz, sofern die Ausgabe in einer anderen Währung als der Gruppenwährung erfasst wurde.

### Struktur

| Bestandteil | Typ | Beschreibung |
| ----------- | --- | ------------ |
| fromCurrency | [CurrencyCodeDT](#d24-currencycodedt) | Ausgangswährung (Originalwährung der Ausgabe) |
| toCurrency | [CurrencyCodeDT](#d24-currencycodedt) | Zielwährung (Gruppenwährung) |
| rate | Dezimalwert | Umrechnungsfaktor von fromCurrency nach toCurrency |
| date | Date | Datum, für das der Kurs ermittelt wurde (i. d. R. das Ausgabedatum) |

### Regeln

- Ein ExchangeRateDT wird nur benötigt, wenn Originalwährung und Gruppenwährung voneinander abweichen (siehe FX-01 in S1.3).
- Der Kurs wird beim externen Wechselkursdienst ermittelt und nicht selbst erfunden oder angenommen (siehe FX-05 in S1.3).
- CampusSplit führt die Multiplikation von Originalbetrag und Kurs sowie die anschließende Rundung selbst durch (siehe FX-03 in S1.3).
- Originalbetrag, Originalwährung und der verwendete Kurs bleiben für die Nachvollziehbarkeit dauerhaft mit der Ausgabe verknüpft (siehe FX-07 in S1.3).

### Gleichheit und Ordnung

Zwei ExchangeRateDT-Werte sind gleich, wenn fromCurrency, toCurrency, rate und date identisch sind. Eine fachliche Ordnung ist nicht vorgesehen.

# D2.5 MembershipRoleDT

MembershipRoleDT legt die Rolle eines Benutzers innerhalb einer Gruppe fest und wird bei [Membership](D1_Datenmodell.md#membership).role verwendet.

| **Wert** | **Bedeutung**                                           |
| -------- | ------------------------------------------------------- |
| ADMIN    | Gruppenadministrator mit erweiterten Verwaltungsrechten |
| MEMBER   | Normales Gruppenmitglied                                |

### Rechte je Rolle

| Recht | ADMIN | MEMBER |
| ----- | :---: | :----: |
| Gruppendaten und Ausgaben ansehen | Ja | Ja |
| Ausgaben erfassen, bearbeiten, löschen | Ja | Ja |
| Salden anzeigen | Ja | Ja |
| Ausgabenübersichten exportieren | Ja | Ja |
| Neue Mitglieder hinzufügen | Ja | Nein |

### Regeln

- Der Ersteller einer Gruppe erhält automatisch die Rolle ADMIN.
- Jede Gruppe muss mindestens einen ADMIN besitzen.
- Ein Benutzer besitzt innerhalb derselben Gruppe genau eine Rolle und darf ihr nicht mehrfach zugeordnet sein.
- Rollen wirken jeweils nur innerhalb der Gruppe, in der sie vergeben wurden.

### Gleichheit und Ordnung

Zwei Rollen sind gleich, wenn ihr Rollenwert identisch ist. Eine natürliche Ordnung der Rollen ist nicht vorgesehen; Berechtigungen werden stattdessen durch eine explizite Rollenprüfung bestimmt.

# D2.6 SplitMethodDT

SplitMethodDT beschreibt, auf welche Weise eine Ausgabe auf die beteiligten Gruppenmitglieder verteilt wird, und kommt bei der Erfassung und Bearbeitung von [Expense](D1_Datenmodell.md#expense)n zum Einsatz. Die Aufteilung erfolgt immer in der Gruppenwährung, also nach einer eventuellen Umrechnung über [ExchangeRateDT](#d24a-exchangeratedt).

| **Wert**      | **Bedeutung**                                                              |
| ------------- | -------------------------------------------------------------------------- |
| EQUAL         | Der Gesamtbetrag wird gleichmäßig auf alle beteiligten Mitglieder verteilt |
| CUSTOM_AMOUNT | Für jedes beteiligte Mitglied wird ein individueller Betrag angegeben      |

### Beispiel EQUAL

Gesamtbetrag: 30.00 EUR, gleichmäßig auf drei Personen verteilt:

| Person | Anteil |
| ------ | ------ |
| Person A | 10.00 |
| Person B | 10.00 |
| Person C | 10.00 |

Ist der Betrag nicht glatt teilbar, wird centgenau verteilt, zum Beispiel 10.00 Euro auf drei Personen:

| Person | Anteil |
| ------ | ------ |
| Person A | 3.34 |
| Person B | 3.33 |
| Person C | 3.33 |

In jedem Fall muss die Summe aller Anteile exakt dem Gesamtbetrag entsprechen.

### Beispiel CUSTOM_AMOUNT

Gesamtbetrag: 30.00 EUR, individuell festgelegt:

| Person | Anteil |
| ------ | ------ |
| Person A | 5.00 |
| Person B | 15.00 |
| Person C | 10.00 |

Auch hier muss die Summe der individuellen Beträge exakt dem Gesamtbetrag entsprechen.

### Allgemeine Regeln

- Mindestens ein Gruppenmitglied muss an einer Ausgabe beteiligt sein.
- Nur Mitglieder der jeweiligen Gruppe dürfen beteiligt werden, und auch der Zahler muss Mitglied dieser Gruppe sein.
- Kostenanteile dürfen nicht negativ sein.
- Die Aufteilung einer Ausgabe muss vollständig und widerspruchsfrei sein.

### Gleichheit und Ordnung

Zwei Aufteilungsarten sind gleich, wenn ihr Wert identisch ist. Eine fachliche Ordnung der Aufteilungsarten ist nicht vorgesehen.

### Erweiterbarkeit

Weitere Aufteilungsarten, etwa eine prozentuale Aufteilung, sind nicht Bestandteil der ersten Version. Eine spätere Erweiterung müsste mindestens [F2](F2-anwendungsfälle.md), [F3](F3-anwendungsfunktionen.md), [D1](D1_Datenmodell.md), D2, [B1](B1_Dialogspezifikation.md) und die Berechnungslogik anpassen.

# D2.7 ExportFormatDT

ExportFormatDT legt das gewünschte Format einer Ausgabenübersicht fest und wird im Use Case „Ausgabenübersicht exportieren" verwendet.

| **Wert** | **Bedeutung**                                         |
| -------- | ----------------------------------------------------- |
| PDF      | Export als lesbares Dokument                          |
| CSV      | Export als tabellarische Datei zur Weiterverarbeitung |

### Regeln

Ein Export bezieht sich stets auf genau eine Gruppe und darf keine Passwörter oder sonstigen sicherheitsrelevanten Informationen enthalten. Salden werden in der Gruppenwährung dargestellt, Originalausgaben zeigen zusätzlich Originalbetrag, Originalwährung und den verwendeten Wechselkurs, sofern eine Umrechnung stattgefunden hat. Zudem dürfen Exporte ausschließlich für Gruppen erzeugt werden, in denen der Benutzer Mitglied ist.

### Inhalt eines Exports

| Bestandteil | Beschreibung |
| ----------- | ------------ |
| Gruppenname | Name der exportierten Gruppe |
| Exportdatum | Zeitpunkt der Erzeugung des Exports |
| Zeitraum | Betrachteter Zeitraum der Ausgaben |
| Mitgliederliste | Liste der Gruppenmitglieder |
| Ausgabenliste | Liste aller enthaltenen Ausgaben mit Originalbetrag/-währung und Abrechnungsbetrag |
| Zahler je Ausgabe | Wer welche Ausgabe bezahlt hat |
| Kostenanteile | Aufteilung je Ausgabe in Gruppenwährung |
| Saldenübersicht | Aktueller Saldo je Mitglied in Gruppenwährung |
| Ausgleichsvorschläge | Vorgeschlagene Ausgleichszahlungen in Gruppenwährung |

### Gleichheit und Ordnung

Zwei Exportformate sind gleich, wenn ihr Formatwert identisch ist. Eine fachliche Ordnung der Exportformate ist nicht vorgesehen.

# D2.8 Notationskonventionen

Die folgenden Notationen werden in D1 und D2 einheitlich verwendet:

| **Notation**  | **Bedeutung**                       |
| ------------- | ------------------------------------ |
| T             | Genau ein Wert vom Typ T            |
| T \[0..1\]    | Kein oder ein Wert                  |
| T \[n..m\]    | Zwischen n und m Werte              |
| Set&lt;T&gt;  | Ungeordnete Sammlung ohne Duplikate |
| List&lt;T&gt; | Geordnete Sammlung                  |

# D2.9 Nicht Bestandteil von D2

Bewusst ausgeklammert bleiben folgende Themen:

| **Thema**                              | **Begründung**                                   |
| --------------------------------------- | -------------------------------------------------- |
| Datenbankspaltentypen                  | Gehören zur Architektur und Implementierung      |
| ORM-Mapping                            | Technische Umsetzung, kein fachlicher Datentyp   |
| REST-DTOs                              | Gehören zu Schnittstellen und Implementierung    |
| UI-Formularfelder                      | Werden in der Dialogspezifikation B1 beschrieben |
| Passwortregeln im Detail               | Gehören zu Sicherheit und Querschnittskonzepten  |
| Physische Speicherung von Geldbeträgen | Architektur- und Implementierungsentscheidung    |
| Cache-Strategie für Wechselkurse       | Technische Umsetzung, siehe S1                   |
| Zahlungsstatus echter Zahlungen        | CampusSplit verarbeitet keine Zahlungen          |

# D2.10 Querverweise

| **Baustein** | **Relevanz für D2**                                                                                                       |
| ------------ | --------------------------------------------------------------------------------------------------------------------------- |
| [P1](P1_Ziele_und_Rahmenbedingungen.md)           | Definiert Ziele, Umfang, Nichtziele und Rahmenbedingungen des Projekts                                                    |
| [P2](P2_Architekturueberblick.md)           | Beschreibt CampusSplit, Browser, Datenbank und Exportdateien als relevante Systeme                                        |
| [F1](F1-geschaeftsprozesse.md)           | Beschreibt den Geschäftsprozess der gemeinsamen Ausgabenverwaltung                                                        |
| [F2](F2-anwendungsfälle.md)           | Nutzt die Datentypen bei Registrierung, Gruppenerstellung, Ausgabenerfassung, Saldenanzeige und Export                    |
| [F3](F3-anwendungsfunktionen.md)           | Verwendet MoneyAmountDT, CurrencyCodeDT, ExchangeRateDT und SplitMethodDT für Kostenaufteilung und Saldenberechnung        |
| [D1](D1_Datenmodell.md)           | Verwendet die hier beschriebenen Datentypen in Entitäten wie User, Group, Expense und ExpenseShare                        |
| [B1](B1_Dialogspezifikation.md)           | Dialoge verwenden diese Datentypen in Eingabefeldern, Auswahllisten und Anzeigen                                          |
| [B3](B3_Druckausgaben.md)           | Druck- und Exportausgaben verwenden ExportFormatDT, MoneyAmountDT und ExchangeRateDT                                      |
| [S1](S1_Nachbarsysteme.md)           | Externer Wechselkursdienst liefert die Werte für ExchangeRateDT                                                            |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)           | Anforderungen an Sicherheit, Datenkonsistenz, Performance und Benutzbarkeit beeinflussen den Umgang mit diesen Datentypen |
| [N2](N2_Querschnittskonzepte.md)           | Validierung, Autorisierung und Fehlerbehandlung nutzen insbesondere MembershipRoleDT, MoneyAmountDT und SplitMethodDT     |
| [E2](E2_Glossar.md)           | Das Glossar definiert Begriffe wie Geldbetrag, Währung, Rolle, Aufteilung, Saldo, Schuldner und Gläubiger                 |
