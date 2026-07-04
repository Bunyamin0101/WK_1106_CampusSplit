# E1 — Leseanleitung

E1 beschreibt, wie die Spezifikation von CampusSplit zu lesen ist. Der Baustein erklärt Aufbau, Zielgruppe, Nummerierung, Begriffe, Querverweise und den empfohlenen Lesepfad. Die Leseanleitung richtet sich an alle Personen, welche die Spezifikation verstehen, prüfen oder weiterverwenden möchten. Dazu gehören insbesondere das Entwicklungsteam, Betreuer:innen, Prüfer:innen und spätere Mitwirkende am Projekt.

## E1.1 Zweck der Spezifikation

Diese Spezifikation beschreibt CampusSplit fachlich und systematisch.

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Benutzer:innen können Gruppen erstellen, Ausgaben erfassen, Kosten auf Mitglieder verteilen, offene Salden anzeigen und Ausgabenübersichten exportieren. Die Spezifikation dient dazu,

- den fachlichen Umfang des Systems festzulegen,

- Anforderungen nachvollziehbar zu dokumentieren,

- Daten, Funktionen und Dialoge konsistent zu beschreiben,
- die spätere Architektur und Implementierung vorzubereiten,

- Missverständnisse innerhalb des Teams zu vermeiden.

## E1.2 Zielgruppen

| Zielgruppe | Interesse an der Spezifikation |
| --- | --- |
| Entwicklungsteam | Grundlage für Architektur, Implementierung und Tests |
| Projektleitung | Überblick über Umfang, Ziele, Fortschritt und Abgrenzung |
| Prüfer:innen und Betreuer:innen | Bewertung der fachlichen Vollständigkeit und Konsistenz |
| Tester:innen | Ableitung von Testfällen aus Use Cases, Validierungsregeln und Qualitätsanforderungen |
| Neue Teammitglieder | Schneller Einstieg in Zweck, Struktur und Begriffe des Projekts |

## E1.3 Aufbau der Spezifikation

Die Spezifikation ist in Bausteine gegliedert. Jeder Baustein behandelt einen bestimmten Aspekt des Systems.

| Bereich | Baustein | Inhalt |
| --- | --- | --- |
| Projektgrundlagen | P1 | Ziele, Rahmenbedingungen, Umfang und Nichtziele |
| Projektgrundlagen | P2 | Architekturüberblick und Systemkontext |
| Funktionen | F1 | Geschäftsprozesse |
| Funktionen | F2 | Anwendungsfälle |
| Funktionen | F3 | Anwendungsfunktionen |
| Daten | D1 | Datenmodell |
| Daten | D2 | Datentypenverzeichnis |
| Benutzerschnittstelle | B1 | Dialogspezifikation |
| Benutzerschnittstelle | B2 | Batch-Prozesse |
| Benutzerschnittstelle | B3 | Druck- und Exportausgaben |
| Schnittstellen | S1 | Nachbarsysteme |
| Schnittstellen | S2 | Datenmigration |
| Schnittstellen | S3 | Inbetriebnahme |
| Übergreifendes | N1 | Nichtfunktionale Anforderungen |
| Übergreifendes | N2 | Querschnittskonzepte |
| Ergänzendes | E1 | Leseanleitung |
| Ergänzendes | E2 | Glossar |

## E1.4 Empfohlene Lesereihenfolge

Die Spezifikation kann vollständig oder gezielt gelesen werden.

Für einen vollständigen fachlichen Überblick wird folgende Reihenfolge empfohlen:

1. **P1 Ziele und Rahmenbedingungen**
   Gibt einen Überblick über Zweck, Zielgruppe, Umfang und Nichtziele.

2. **P2 Architekturüberblick**
   Zeigt CampusSplit im Systemkontext mit Browser, Datenbank und Exportdateien.

3. **F1 Geschäftsprozesse**
   Beschreibt den fachlichen Ablauf der gemeinsamen Ausgabenverwaltung.

4. **F2 Anwendungsfälle**
   Beschreibt, welche Aktionen Benutzer:innen mit CampusSplit ausführen können.

5. **F3 Anwendungsfunktionen**
   Beschreibt zentrale fachliche Funktionen wie Kostenaufteilung und Saldenberechnung.

6. **D1 und D2**
   Beschreiben Datenobjekte und fachliche Datentypen.

7. **B1 und B3**
   Beschreiben Dialoge und Exportausgaben.

8. **S1 und S3**
   Beschreiben Nachbarsysteme und Inbetriebnahme.

9. **N1 und N2**
   Beschreiben Qualitätsanforderungen und systemweite Konzepte.

10. **E2 Glossar**
   Erklärt zentrale Begriffe.

## E1.5 Nummerierung und Bezeichner

Die Spezifikation verwendet stabile Bezeichner.

Diese Bezeichner dienen dazu, Inhalte eindeutig zu referenzieren.

| Präfix | Bedeutung | Beispiel |
| --- | --- | --- |
| P | Projektgrundlagen | P1 |
| F | Fachliche Funktionen | F2 |
| D | Daten | D1 |
| B | Benutzerschnittstelle | B1 |
| S | Schnittstellen und Inbetriebnahme | S3 |
| N | Nichtfunktionales und Querschnittliches | N2 |
| E | Ergänzende Bausteine | E2 |
| UC | Use Case | UC-08 Ausgabe erfassen |
| AF | Anwendungsfunktion | AF-02 Gruppensalden berechnen |
| DLG | Dialog | DLG-07 Ausgabe erfassen |
| NFR | Nichtfunktionale Anforderung | NFR-12c-01 |
| INV | Datenmodell-Invariante | INV-05 |
| EXP | Exportregel | EXP-01 |

Bezeichner werden nach Möglichkeit nicht nachträglich umnummeriert. Dadurch bleiben Querverweise zwischen Bausteinen stabil.

## E1.6 Sprach- und Schreibkonventionen

Die Spezifikation ist in deutscher Sprache verfasst. Fachbegriffe werden möglichst einheitlich verwendet. Zentrale Begriffe werden in E2 Glossar erläutert.

### Schreibweise

In der Spezifikation werden fachliche Begriffe einheitlich verwendet. Technische Begriffe werden nur genutzt, wenn sie für das Verständnis notwendig sind. Beispiele sollen Inhalte verständlicher machen, ersetzen aber keine verbindlichen Regeln. Muss-Formulierungen beschreiben verbindliche Anforderungen. Soll-Formulierungen beschreiben gewünschtes Verhalten. Kann-Formulierungen beschreiben optionale Funktionen oder mögliche spätere Erweiterungen.

### Beispiele

| Formulierung | Bedeutung |
| --- | --- |
| „muss“ | verbindliche Anforderung |
| „soll“ | gewünschtes Verhalten |
| „kann“ | optionale Möglichkeit |
| „nicht Bestandteil“ | bewusst außerhalb des Projektumfangs |
| „nicht anwendbar“ | Baustein oder Thema passt fachlich nicht zum Projekt |

## E1.7 Umgang mit „nicht anwendbar“

Einige Bausteine oder Themen sind für CampusSplit nicht oder nur eingeschränkt relevant. Solche Inhalte werden nicht einfach so weggelassen, sondern ausdrücklich als „nicht anwendbar“ gekennzeichnet.

Für CampusSplit betrifft das insbesondere:

| Baustein oder Thema | Status | Begründung |
| --- | --- | --- |
| B2 Batch | nicht anwendbar | CampusSplit verwendet keine zeitgesteuerten Batch-Prozesse. |
| S2 Datenmigration | nicht anwendbar | CampusSplit ist ein Greenfield-Projekt ohne Altsystem und ohne Altdatenmigration. |
| Zahlungsabwicklung | nicht Bestandteil | CampusSplit berechnet Salden, führt aber keine Zahlungen aus. |
| Bankintegration | nicht Bestandteil | Es werden keine Bankdaten verarbeitet. |
| OCR oder KI-Erkennung von Belegen | nicht Bestandteil | Belege werden nicht automatisch erkannt. |
| Mehrwährungen | nicht Bestandteil der ersten Version | Die erste Version verwendet ausschließlich Euro. |

Diese explizite Kennzeichnung soll zeigen, dass die Themen geprüft wurden und bewusst außerhalb des Umfangs liegen.

## E1.8 Konsistenzregeln

Die Bausteine der Spezifikation bauen aufeinander auf.

Folgende Konsistenzregeln gelten:

| Regel | Beschreibung |
| --- | --- |
| KR-01 | Ziele und Nichtziele aus P1 begrenzen alle weiteren Bausteine. |
| KR-02 | Jeder zentrale Use Case aus F2 muss durch mindestens einen Dialog in B1 unterstützt werden. |
| KR-03 | Daten, die in F2 oder B1 verwendet werden, müssen in D1 oder D2 beschrieben sein. |
| KR-04 | Fachliche Berechnungen aus F3 müssen zu Datenmodell und Datentypen passen. |
| KR-05 | Exportinhalte aus B3 müssen aus D1-Daten ableitbar sein. |
| KR-06 | Qualitätsanforderungen aus N1 müssen in N2, B1, S1 oder S3 berücksichtigt werden. |
| KR-07 | Begriffe müssen einheitlich verwendet und bei Bedarf in E2 erklärt werden. |

## E1.9 Abgrenzung zwischen Spezifikation, Architektur und Implementierung

Diese Spezifikation beschreibt vor allem das „Was“ des Systems. Sie beschreibt nicht vollständig das „Wie“ der technischen Umsetzung.

| Ebene | Inhalt |
| --- | --- |
| Spezifikation | fachliche Anforderungen, Use Cases, Daten, Dialoge, Qualitätsanforderungen |
| Architektur | technische Struktur, Komponenten, Technologien, Schnittstellen, Deployment |
| Implementierung | Quellcode, Tests, konkrete Frameworks, Datenbankschema, Build-Dateien |

Beispiel:

- Die Spezifikation sagt: Eine Ausgabe muss Kostenanteile besitzen.

- Die Architektur beschreibt: Welche Backend-Komponente diese Logik verarbeitet.

- Die Implementierung enthält: Den konkreten Code und die Tests.

## E1.10 Umgang mit Änderungen

Änderungen an der Spezifikation sollen konsistent erfolgen. Wenn ein Baustein geändert wird, müssen abhängige Bausteine geprüft werden.

Beispiele:

| Änderung | Zu prüfende Bausteine |
| --- | --- |
| Neue Aufteilungsart | F2, F3, D2, B1, N2 |
| Neues Exportformat | F2, F3, D2, B1, B3, N1 |
| Neue Rolle | D1, D2, F2, B1, N2 |
| Neue Datenattribute bei Ausgaben | D1, D2, B1, B3, S3 |
| Einführung echter Zahlungen | P1, F1, F2, D1, D2, S1, N1, N2 |

Besonders wichtig ist, dass Nichtziele aus P1 nicht unbeabsichtigt aufgehoben werden.

## E1.11 Lesebeispiele

### Beispiel 1: Ausgabe erfassen verstehen

Um den Ablauf „Ausgabe erfassen“ vollständig zu verstehen, sollten folgende Bausteine gelesen werden:

| Baustein | Relevanz |
| --- | --- |
| F1 | Zeigt die Ausgabeerfassung im Geschäftsprozess. |
| F2 | Beschreibt UC-08 Ausgabe erfassen. |
| F3 | Beschreibt Kostenaufteilung und Saldenberechnung. |
| D1 | Beschreibt Expense und ExpenseShare. |
| D2 | Beschreibt MoneyAmountDT und SplitMethodDT. |
| B1 | Beschreibt DLG-07 Ausgabe erfassen. |
| N2 | Beschreibt Validierung und Geldbetragsverarbeitung. |

### Beispiel 2: Export verstehen

Um den Export vollständig zu verstehen, sollten folgende Bausteine gelesen werden:

| Baustein | Relevanz |
| --- | --- |
| F2 | Beschreibt UC-12 Ausgabenübersicht exportieren. |
| F3 | Beschreibt AF-04 Exportdaten aufbereiten. |
| D1 | Beschreibt die Datenbasis für den Export. |
| D2 | Beschreibt ExportFormatDT, MoneyAmountDT und CurrencyCodeDT. |
| B1 | Beschreibt DLG-11 Export. |
| B3 | Beschreibt Inhalt und Struktur von PDF und CSV. |
| N2 | Beschreibt Exportsicherheit. |

### Beispiel 3: Zugriffsschutz verstehen

Um Zugriffsschutz und Berechtigungen zu verstehen, sollten folgende Bausteine gelesen werden:

| Baustein | Relevanz |
| --- | --- |
| F2 | Beschreibt Vorbedingungen der Use Cases. |
| D1 | Beschreibt User, Group und Membership. |
| D2 | Beschreibt MembershipRoleDT. |
| B1 | Beschreibt berechtigungsabhängige Dialogaktionen. |
| N1 | Beschreibt Sicherheitsanforderungen. |
| N2 | Beschreibt Authentifizierung und Autorisierung. |

## E1.12 Nicht Bestandteil von E1

Folgende Inhalte sind nicht Bestandteil der Leseanleitung:

| Thema | Begründung |
| --- | --- |
| Fachliche Detailanforderungen | Werden in P1, F1, F2 und F3 beschrieben. |
| Datenmodell | Wird in D1 beschrieben. |
| Datentypen | Werden in D2 beschrieben. |
| Dialogdetails | Werden in B1 beschrieben. |
| Exportstruktur | Wird in B3 beschrieben. |
| Technische Architektur | Wird in der Architekturdokumentation beschrieben. |
| Implementierung | Gehört zum Quellcode. |
| Testfälle im Detail | Werden aus F2, F3, N1 und N2 abgeleitet. |
| Glossarbegriffe | Werden in E2 erklärt. |

## E1.13 Querverweise

| Baustein | Relevanz für E1 |
| --- | --- |
| P1 | Liefert Ziele, Rahmenbedingungen, Umfang und Nichtziele als Einstiegspunkt. |
| P2 | Erklärt den Systemkontext von CampusSplit. |
| F1 | Beschreibt den fachlichen Geschäftsprozess. |
| F2 | Beschreibt die benutzerrelevanten Use Cases. |
| F3 | Beschreibt zentrale fachliche Anwendungsfunktionen. |
| D1 | Beschreibt das fachliche Datenmodell. |
| D2 | Beschreibt fachliche Datentypen. |
| B1 | Beschreibt die Dialoge der Anwendung. |
| B2 | Wird als nicht anwendbar eingeordnet. |
| B3 | Beschreibt Druck- und Exportausgaben. |
| S1 | Beschreibt Nachbarsysteme. |
| S2 | Wird als nicht anwendbar eingeordnet. |
| S3 | Beschreibt Inbetriebnahme und Releases. |
| N1 | Beschreibt nichtfunktionale Anforderungen. |
| N2 | Beschreibt Querschnittskonzepte. |
| E2 | Enthält das Glossar zentraler Begriffe. |
