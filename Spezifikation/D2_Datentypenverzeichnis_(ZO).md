**D2 - Datentypenverzeichnis**

Dieses Datentypenverzeichnis fasst die fachlichen Datentypen zusammen, die im Datenmodell von CampusSplit eine besondere Rolle spielen. Einfache Standardtypen wie Text, Integer, Boolean, Date, Email, Timestamp oder URL werden hier nicht eigens erläutert, da sie unmittelbar in den Attributtabellen des Datenmodells verwendet werden und keiner zusätzlichen fachlichen Erklärung bedürfen.

Im Mittelpunkt stehen dagegen Wertebereiche, Gleichheits- und Ordnungsregeln sowie Verarbeitungsvorgaben für die komplexeren Typen. Technische Aspekte wie Datenbankspaltentypen, ORM-Mapping, Serialisierung oder konkrete Framework-Implementierungen werden bewusst ausgeklammert; sie gehören in die Architektur- und Implementierungsdokumentation und nicht in diesen Baustein.

# D2.1 Typenkatalog

| **Typ**          | **Art**                           | **Verwendung**                                        |
| ---------------- | --------------------------------- | ----------------------------------------------------- |
| Identifier       | Eindeutiger technischer Schlüssel | Identifikation von Entitäten                          |
| MoneyAmountDT    | Geldbetrag                        | Ausgaben, Kostenanteile, Salden, Ausgleichsvorschläge |
| CurrencyCodeDT   | Währungscode                      | Währung von Ausgaben und Beträgen                     |
| MembershipRoleDT | Aufzählung                        | Rolle eines Benutzers innerhalb einer Gruppe          |
| SplitMethodDT    | Aufzählung                        | Art der Kostenaufteilung                              |
| ExportFormatDT   | Aufzählung                        | Format einer Ausgabenübersicht                        |

# D2.2 Identifier

Der Identifier ist der eindeutige technische Schlüssel, über den fachliche Entitäten angesprochen werden. Er kommt unter anderem bei User.id, Group.id, Membership.id, Expense.id, ExpenseShare.id und Category.id zum Einsatz.

### Wertebereich

Ein Identifier ist ein eindeutiger Wert. Wie er intern aufgebaut ist, spielt für die fachliche Spezifikation keine Rolle.

### Erzeugung

Der Identifier entsteht beim Anlegen einer Entität, bleibt über deren gesamte Lebenszeit unverändert und wird nach dem Löschen der Entität nicht erneut vergeben.

### Gleichheit und Ordnung

Zwei Identifier sind genau dann gleich, wenn ihre Werte exakt übereinstimmen. Eine fachliche Sortierung nach Identifier ist nicht vorgesehen - dafür werden Attribute wie createdAt, expenseDate oder name herangezogen.

# D2.3 MoneyAmountDT

MoneyAmountDT bildet einen Geldbetrag ab und wird für den Gesamtbetrag einer Ausgabe, für Kostenanteile, für den berechneten Saldo eines Gruppenmitglieds, für Ausgleichsvorschläge zwischen Schuldner und Gläubiger sowie für exportierte Beträge verwendet.

### Wertebereich

Ein MoneyAmountDT besitzt immer genau zwei Nachkommastellen.

| **Beispielwert** | **Bedeutung**                     |
| ---------------- | --------------------------------- |
| 0.00             | Kein Betrag                       |
| 10.00            | Zehn Euro                         |
| 3.33             | Drei Euro und dreiunddreißig Cent |
| \-5.00           | Negativer Saldo von fünf Euro     |

### Regeln

Beträge werden centgenau verarbeitet. Eine Ausgabe muss größer als 0.00 sein, während Kostenanteile zwar nicht negativ sein dürfen, aber durchaus 0.00 betragen können. Salden hingegen dürfen positiv, negativ oder null sein: Ein positiver Saldo bedeutet, dass ein Mitglied Geld zurückbekommt, ein negativer, dass es Geld schuldet.

Zwei rechnerische Bedingungen sind dabei zentral: Die Summe aller Kostenanteile einer Ausgabe muss exakt dem Gesamtbetrag entsprechen, und die Summe aller Salden innerhalb einer Gruppe muss stets 0.00 ergeben.

### Rundung

Bei einer gleichmäßigen Aufteilung lässt sich eine Rundungsdifferenz nicht immer vermeiden. Werden beispielsweise 10.00 Euro auf drei Personen verteilt, ergibt sich folgendes Bild:

| **Person** | **Anteil** |
| ---------- | ---------- |
| Person A   | 3.34       |
| Person B   | 3.33       |
| Person C   | 3.33       |

Die Rundung muss deterministisch erfolgen, das heißt: Bei identischer Eingabe liefert das System immer dieselbe Verteilung. In jedem Fall muss die Summe der gerundeten Teilbeträge exakt dem Gesamtbetrag entsprechen.

### Gleichheit

Zwei Geldbeträge gelten als gleich, wenn sie denselben centgenauen Wert besitzen.

| **Betrag A** | **Betrag B** | **Ergebnis** |
| ------------ | ------------ | ------------ |
| 10.00        | 10.00        | gleich       |
| 10.00        | 10.01        | nicht gleich |

### Ordnung

Geldbeträge sind numerisch sortierbar, zum Beispiel: -5.00 < 0.00 < 10.00.

### Verarbeitungshinweis

Geldbeträge dürfen nicht über ungenaue Gleitkommazahlen verarbeitet werden. Die Implementierung muss stattdessen eine exakte Darstellung wählen, etwa einen Dezimaltyp oder eine centbasierte Speicherung.

# D2.4 CurrencyCodeDT

CurrencyCodeDT beschreibt die Währung eines Geldbetrags. Für die erste Version von CampusSplit ist ausschließlich Euro vorgesehen.

| **Wert** | **Bedeutung** |
| -------- | ------------- |
| EUR      | Euro          |

### Regeln

Jede Ausgabe wird in Euro erfasst, und alle zugehörigen Kostenanteile übernehmen automatisch dieselbe Währung. Auch Salden und Ausgleichsvorschläge werden ausschließlich in Euro angegeben; eine Umrechnung zwischen unterschiedlichen Währungen ist nicht vorgesehen.

### Gleichheit und Ordnung

Zwei Währungscodes sind gleich, wenn ihr Code identisch ist. Eine fachliche Sortierung von Währungscodes ist nicht vorgesehen.

### Erweiterbarkeit

Die Unterstützung weiterer Währungen ist nicht Bestandteil der ersten Version. Eine spätere Mehrwährungsunterstützung müsste zusätzlich in Spezifikation, Datenmodell, Berechnungslogik, Oberfläche und Architektur berücksichtigt werden.

# D2.5 MembershipRoleDT

MembershipRoleDT legt die Rolle eines Benutzers innerhalb einer Gruppe fest und wird bei Membership.role verwendet.

| **Wert** | **Bedeutung**                                           |
| -------- | ------------------------------------------------------- |
| ADMIN    | Gruppenadministrator mit erweiterten Verwaltungsrechten |
| MEMBER   | Normales Gruppenmitglied                                |

### Rechte der Rolle ADMIN

Ein ADMIN darf Gruppendaten und Ausgaben ansehen, Ausgaben erfassen, bearbeiten und löschen, Mitglieder hinzufügen, Salden anzeigen sowie Ausgabenübersichten exportieren.

### Rechte der Rolle MEMBER

Ein MEMBER verfügt über dieselben Rechte wie ein ADMIN, mit einer Ausnahme: Das Hinzufügen neuer Gruppenmitglieder bleibt Gruppenadministrator:innen vorbehalten.

### Regeln

- Der Ersteller einer Gruppe erhält automatisch die Rolle ADMIN.
- Jede Gruppe muss mindestens einen ADMIN besitzen.
- Ein Benutzer besitzt innerhalb derselben Gruppe genau eine Rolle und darf ihr nicht mehrfach zugeordnet sein.
- Rollen wirken jeweils nur innerhalb der Gruppe, in der sie vergeben wurden.

### Gleichheit und Ordnung

Zwei Rollen sind gleich, wenn ihr Rollenwert identisch ist. Eine natürliche Ordnung der Rollen ist nicht vorgesehen; Berechtigungen werden stattdessen durch eine explizite Rollenprüfung bestimmt.

# D2.6 SplitMethodDT

SplitMethodDT beschreibt, auf welche Weise eine Ausgabe auf die beteiligten Gruppenmitglieder verteilt wird, und kommt bei der Erfassung und Bearbeitung von Ausgaben zum Einsatz.

| **Wert**      | **Bedeutung**                                                              |
| ------------- | -------------------------------------------------------------------------- |
| EQUAL         | Der Gesamtbetrag wird gleichmäßig auf alle beteiligten Mitglieder verteilt |
| CUSTOM_AMOUNT | Für jedes beteiligte Mitglied wird ein individueller Betrag angegeben      |

### EQUAL

Bei EQUAL wird der Gesamtbetrag gleichmäßig auf alle beteiligten Mitglieder verteilt, etwa 30.00 Euro auf drei Personen zu je 10.00 Euro. Ist der Betrag nicht glatt teilbar, wird centgenau verteilt - 10.00 Euro auf drei Personen ergeben dann 3.34, 3.33 und 3.33 Euro. In jedem Fall muss die Summe aller Anteile exakt dem Gesamtbetrag entsprechen.

### CUSTOM_AMOUNT

Bei CUSTOM_AMOUNT werden die Kostenanteile manuell festgelegt, zum Beispiel 5.00 Euro für Person A, 15.00 Euro für Person B und 10.00 Euro für Person C bei einer Gesamtsumme von 30.00 Euro. Auch hier muss die Summe der individuellen Beträge exakt dem Gesamtbetrag entsprechen.

### Allgemeine Regeln

- Mindestens ein Gruppenmitglied muss an einer Ausgabe beteiligt sein.
- Nur Mitglieder der jeweiligen Gruppe dürfen beteiligt werden, und auch der Zahler muss Mitglied dieser Gruppe sein.
- Kostenanteile dürfen nicht negativ sein.
- Die Aufteilung einer Ausgabe muss vollständig und widerspruchsfrei sein.

### Gleichheit und Ordnung

Zwei Aufteilungsarten sind gleich, wenn ihr Wert identisch ist. Eine fachliche Ordnung der Aufteilungsarten ist nicht vorgesehen.

### Erweiterbarkeit

Weitere Aufteilungsarten, etwa eine prozentuale Aufteilung, sind nicht Bestandteil der ersten Version. Eine spätere Erweiterung müsste mindestens F2, F3, D1, D2, B1 und die Berechnungslogik anpassen.

# D2.7 ExportFormatDT

ExportFormatDT legt das gewünschte Format einer Ausgabenübersicht fest und wird im Use Case „Ausgabenübersicht exportieren" verwendet.

| **Wert** | **Bedeutung**                                         |
| -------- | ----------------------------------------------------- |
| PDF      | Export als lesbares Dokument                          |
| CSV      | Export als tabellarische Datei zur Weiterverarbeitung |

### Regeln

Ein Export bezieht sich stets auf genau eine Gruppe und darf keine Passwörter oder sonstigen sicherheitsrelevanten Informationen enthalten. Exportierte Beträge werden in Euro mit zwei Nachkommastellen dargestellt, und exportierte Salden müssen mit der aktuellen Saldenberechnung übereinstimmen. Zudem dürfen Exporte ausschließlich für Gruppen erzeugt werden, in denen der Benutzer Mitglied ist.

### Inhalt eines Exports

Ein Export kann Gruppenname, Exportdatum, Zeitraum, Mitgliederliste, Ausgabenliste, den jeweiligen Zahler je Ausgabe, Kostenanteile, eine Saldenübersicht sowie Ausgleichsvorschläge enthalten.

### Gleichheit und Ordnung

Zwei Exportformate sind gleich, wenn ihr Formatwert identisch ist. Eine fachliche Ordnung der Exportformate ist nicht vorgesehen.

# D2.8 Notationskonventionen

Die folgenden Notationen werden in D1 und D2 einheitlich verwendet:

| **Notation**  | **Bedeutung**                       |
| ------------- | ----------------------------------- |
| T             | Genau ein Wert vom Typ T            |
| T \[0..1\]    | Kein oder ein Wert                  |
| T \[n..m\]    | Zwischen n und m Werte              |
| Set&lt;T&gt;  | Ungeordnete Sammlung ohne Duplikate |
| List&lt;T&gt; | Geordnete Sammlung                  |

# D2.9 Nicht Bestandteil von D2

Bewusst ausgeklammert bleiben folgende Themen:

| **Thema**                              | **Begründung**                                   |
| -------------------------------------- | ------------------------------------------------ |
| Datenbankspaltentypen                  | Gehören zur Architektur und Implementierung      |
| ORM-Mapping                            | Technische Umsetzung, kein fachlicher Datentyp   |
| REST-DTOs                              | Gehören zu Schnittstellen und Implementierung    |
| UI-Formularfelder                      | Werden in der Dialogspezifikation B1 beschrieben |
| Passwortregeln im Detail               | Gehören zu Sicherheit und Querschnittskonzepten  |
| Physische Speicherung von Geldbeträgen | Architektur- und Implementierungsentscheidung    |
| Mehrwährungsumrechnung                 | Nicht Bestandteil der ersten Version             |
| Zahlungsstatus echter Zahlungen        | CampusSplit verarbeitet keine Zahlungen          |

# D2.10 Querverweise

| **Baustein** | **Relevanz für D2**                                                                                                       |
| ------------ | ------------------------------------------------------------------------------------------------------------------------- |
| P1           | Definiert Ziele, Umfang, Nichtziele und Rahmenbedingungen des Projekts                                                    |
| P2           | Beschreibt CampusSplit, Browser, Datenbank und Exportdateien als relevante Systeme                                        |
| F1           | Beschreibt den Geschäftsprozess der gemeinsamen Ausgabenverwaltung                                                        |
| F2           | Nutzt die Datentypen bei Registrierung, Gruppenerstellung, Ausgabenerfassung, Saldenanzeige und Export                    |
| F3           | Verwendet MoneyAmountDT, CurrencyCodeDT und SplitMethodDT für Kostenaufteilung und Saldenberechnung                       |
| D1           | Verwendet die hier beschriebenen Datentypen in Entitäten wie User, Group, Expense und ExpenseShare                        |
| B1           | Dialoge verwenden diese Datentypen in Eingabefeldern, Auswahllisten und Anzeigen                                          |
| B3           | Druck- und Exportausgaben verwenden ExportFormatDT und MoneyAmountDT                                                      |
| N1           | Anforderungen an Sicherheit, Datenkonsistenz, Performance und Benutzbarkeit beeinflussen den Umgang mit diesen Datentypen |
| N2           | Validierung, Autorisierung und Fehlerbehandlung nutzen insbesondere MembershipRoleDT, MoneyAmountDT und SplitMethodDT     |
| E2           | Das Glossar definiert Begriffe wie Geldbetrag, Währung, Rolle, Aufteilung, Saldo, Schuldner und Gläubiger                 |