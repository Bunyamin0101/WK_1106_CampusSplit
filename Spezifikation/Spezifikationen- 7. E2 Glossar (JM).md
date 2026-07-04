# E2 - Glossar

E2 definiert zentrale fachliche und technische Begriffe, die in der Spezifikation von CampusSplit verwendet werden.

Das Glossar dient dazu, Begriffe einheitlich zu verwenden und Missverständnisse innerhalb der Spezifikation zu vermeiden. Die Begriffe sind alphabetisch sortiert.

### Administrator

Administrator bezeichnet einen Benutzer, der innerhalb einer Gruppe erweiterte Rechte besitzt. Ein Administrator kann nicht nur die gemeinsamen Ausgaben verwalten, sondern auch organisatorische Aufgaben übernehmen, zum Beispiel neue Mitglieder hinzufügen oder bestehende Mitglieder verwalten. In CampusSplit ist ein Administrator also ein Gruppenmitglied mit zusätzlichen Berechtigungen gegenüber einem normalen Mitglied.

Siehe auch: Gruppenadministrator.

## ADMIN

ADMIN ist eine Rolle innerhalb einer Gruppe.Ein Benutzer mit der Rolle ADMIN darf Gruppendaten sehen, Ausgaben erfassen, Salden anzeigen und Exporte erzeugen. Zusätzlich darf er neue Mitglieder zur Gruppe hinzufügen und bestehende Gruppenmitglieder verwalten.

Siehe auch: Gruppenmitglied, MEMBER.

### Anmeldung

Vorgang, bei dem sich ein registrierter Benutzer mit E-Mail-Adresse und Passwort bei CampusSplit authentifiziert.Nach erfolgreicher Anmeldung erhält der Benutzer Zugriff auf geschützte Funktionen wie Dashboard, Gruppen, Ausgaben, Salden und Export.

Siehe auch: Authentifizierung, Sitzung.

### Anwendungsfall

Ein Anwendungsfall beschreibt eine fachliche Aktion, die ein Benutzer mit CampusSplit durchführen kann.

Beispiele:

- Registrieren
- Anmelden
- Gruppe erstellen
- Ausgabe erfassen
- Salden anzeigen
- Ausgabenübersicht exportieren

Anwendungsfälle werden in F2 beschrieben.

Siehe auch: Use Case.

### Anwendungsfunktion

Eine Anwendungsfunktion beschreibt eine fachliche Funktion, die von mehreren Use Cases genutzt werden kann.

Beispiele:

- Kostenanteile berechnen
- Gruppensalden berechnen
- Ausgleichsvorschläge berechnen
- Exportdaten aufbereiten

Anwendungsfunktionen werden in F3 beschrieben.

### Aufteilungsart

Die Aufteilungsart beschreibt, wie eine Ausgabe auf beteiligte Gruppenmitglieder verteilt wird.

CampusSplit unterstützt in der ersten Version:

- EQUAL: gleichmäßige Aufteilung
- CUSTOM_AMOUNT: individuelle Beträge pro Mitglied

Siehe auch: SplitMethodDT, Kostenanteil, Ausgabe.

### Ausgabe

Eine Ausgabe ist ein gemeinsamer Geldbetrag, der innerhalb einer Gruppe erfasst wird.

Beispiele:

- Einkauf
- Restaurantbesuch
- Unterkunft
- Fahrtkosten
- Projektmaterial

Eine Ausgabe besitzt mindestens:

- Beschreibung
- Betrag
- Datum
- Zahler
- Gruppe
- Kostenanteile

Im Datenmodell entspricht dies der Entität Expense.

Siehe auch: Expense, Kostenanteil, Zahler.

### Ausgabenübersicht

Eine Ausgabenübersicht ist eine Zusammenfassung der Ausgaben einer Gruppe. Sie kann als PDF oder CSV exportiert werden und enthält je nach Format:

- Gruppenname
- Mitglieder
- Ausgaben
- Kostenanteile
- Salden
- Ausgleichsvorschläge

Siehe auch: Export, PDF, CSV.

### Ausgleichsvorschlag

Ein Ausgleichsvorschlag beschreibt, welche Zahlung zwischen Gruppenmitgliedern sinnvoll wäre, um offene Salden auszugleichen.

Beispiel:

Person A zahlt 10,00 € an Person B.

Ein Ausgleichsvorschlag ist keine echte Zahlung. CampusSplit führt keine Zahlungen aus.

Siehe auch: Saldo, Schuldner, Gläubiger.

### Authentifizierung

Authentifizierung ist die Prüfung der Identität eines Benutzers. In CampusSplit erfolgt die Authentifizierung durch Anmeldung mit E-Mail-Adresse und Passwort. Nur authentifizierte Benutzer dürfen geschützte Funktionen verwenden.

Siehe auch: Anmeldung, Sitzung, Passwort-Hash.

### Autorisierung

Autorisierung beschreibt die Prüfung, ob ein angemeldeter Benutzer eine bestimmte Aktion ausführen darf. In CampusSplit hängt die Autorisierung vor allem von der Gruppenmitgliedschaft und der Rolle innerhalb einer Gruppe ab.

Beispiele:

- Nur Gruppenmitglieder dürfen Gruppendaten sehen.
- Nur Gruppenadministrator:innen dürfen neue Mitglieder hinzufügen.

Siehe auch: Membership, Gruppenadministrator, MEMBER, ADMIN.

### Backend

Das Backend ist der serverseitige Teil von CampusSplit. Es verarbeitet fachliche Logik, prüft Berechtigungen, validiert Eingaben, berechnet Kostenanteile und Salden und kommuniziert mit der Datenbank.

Siehe auch: Frontend, Datenbank, Schnittstelle.

### Batch

Ein Batch ist ein zeitgesteuerter oder gesammelt ausgeführter Hintergrundprozess. CampusSplit verwendet in der ersten Version keine Batch-Prozesse. Alle fachlichen Aktionen werden direkt durch Benutzerinteraktion ausgelöst.

### Benutzer

Ein Benutzer ist eine registrierte Person, die CampusSplit verwendet.

Ein Benutzer kann:

- Gruppen erstellen
- Mitglied mehrerer Gruppen sein
- Ausgaben erfassen
- Salden anzeigen
- Exporte erzeugen

Im Datenmodell entspricht dies der Entität User.

Siehe auch: User, Gruppe, Membership.

### Browser

Der Browser ist das Programm, über das Benutzer:innen CampusSplit verwenden.

Beispiele:

- Chrome
- Firefox
- Safari
- Edge

Der Browser ist ein Nachbarsystem von CampusSplit und wird in S1 beschrieben.

### CSV

CSV steht für „Comma-Separated Values". In CampusSplit ist CSV ein Exportformat für tabellarische Ausgabendaten. CSV-Dateien können zum Beispiel in Tabellenkalkulationsprogrammen geöffnet werden.

Siehe auch: Export, ExportFormatDT, PDF.

### Dashboard

Das Dashboard ist die zentrale Übersichtsseite nach der Anmeldung. Es zeigt dem Benutzer seine Gruppen und eine kurze Übersicht über offene Salden oder relevante Gruppeninformationen.

### Datenbank

Die Datenbank speichert die persistenten Daten von CampusSplit.

Dazu gehören:

- Benutzer
- Gruppen
- Mitgliedschaften
- Ausgaben
- Kostenanteile
- Kategorien

In der geplanten technischen Umsetzung wird eine relationale Datenbank verwendet.

Siehe auch: PostgreSQL, Persistenz.

### Datenmigration

Datenmigration beschreibt die Übernahme von Daten aus einem alten System in ein neues System. Für CampusSplit ist Datenmigration nicht anwendbar, weil das Projekt neu entwickelt wird und kein Vorgängersystem existiert.

Siehe auch: Greenfield-Projekt, S2 Datenmigration.

### Dialog

Ein Dialog ist eine benutzerseitige Ansicht oder Maske innerhalb von CampusSplit.

Beispiele:

- Anmelden
- Dashboard
- Gruppe erstellen
- Ausgabe erfassen
- Saldenübersicht
- Export

Dialoge werden in B1 beschrieben.

### E-Mail-Adresse

Die E-Mail-Adresse dient in CampusSplit zur Identifikation eines Benutzerkontos bei Registrierung und Anmeldung. Jede E-Mail-Adresse darf nur einem Benutzerkonto zugeordnet sein.

### Export

Ein Export ist die Erzeugung einer Ausgabenübersicht als Datei.

CampusSplit unterstützt in der ersten Version:

- PDF
- CSV

Ein Export verändert keine gespeicherten Daten. Er stellt nur eine Momentaufnahme der Gruppendaten bereit.

Siehe auch: PDF, CSV, Ausgabenübersicht.

### ExportFormatDT

ExportFormatDT ist ein fachlicher Datentyp aus D2. Er beschreibt das gewünschte Exportformat einer Ausgabenübersicht.

Mögliche Werte:

- PDF
- CSV

Siehe auch: Export, PDF, CSV.

### Expense

Expense ist die technische Bezeichnung der Entität Ausgabe im Datenmodell. Eine Expense gehört zu einer Gruppe, besitzt einen Zahler und wird über Kostenanteile auf Gruppenmitglieder verteilt.

Siehe auch: Ausgabe, ExpenseShare.

### ExpenseShare

ExpenseShare ist die technische Bezeichnung der Entität Kostenanteil im Datenmodell.

Ein ExpenseShare beschreibt, welchen Anteil ein Benutzer an einer bestimmten Ausgabe trägt.

Siehe auch: Kostenanteil, Expense.

### Fachliche Invariante

Eine fachliche Invariante ist eine Regel, die im Datenmodell immer gelten muss.

Beispiele:

- Eine Ausgabe muss mindestens einen Kostenanteil besitzen.
- Die Summe aller Kostenanteile muss dem Gesamtbetrag der Ausgabe entsprechen.
- Ein Benutzer darf eine Gruppe nur sehen, wenn er Mitglied dieser Gruppe ist.

Invarianten werden insbesondere in D1 beschrieben.

### Frontend

Das Frontend ist der benutzerseitige Teil von CampusSplit. Es stellt Dialoge im Browser dar und nimmt Benutzereingaben entgegen.

Siehe auch: Backend, Browser, Dialog.

### Geldbetrag

Ein Geldbetrag ist ein centgenauer Betrag in Euro. Geldbeträge werden für Ausgaben, Kostenanteile, Salden und Ausgleichsvorschläge verwendet.

Siehe auch: MoneyAmountDT, Währung, Saldo.

### Gläubiger

Ein Gläubiger ist ein Gruppenmitglied, das laut Saldenberechnung Geld zurückbekommt.

Ein positiver Saldo bedeutet, dass das Mitglied Gläubiger ist.

Siehe auch: Saldo, Schuldner, Ausgleichsvorschlag.

### Greenfield-Projekt

Ein Greenfield-Projekt ist ein neu entwickeltes System ohne bestehendes Vorgängersystem. CampusSplit ist ein Greenfield-Projekt. Deshalb gibt es keine Altdatenmigration.

Siehe auch: Datenmigration, S2 Datenmigration.

### Gruppe

Eine Gruppe ist ein Zusammenschluss von Benutzer:innen, die gemeinsame Ausgaben verwalten.

Beispiele:

- Wohngemeinschaft
- Reisegruppe
- studentische Projektgruppe
- Haushaltsgruppe

Im Datenmodell entspricht dies der Entität Group.

Siehe auch: Group, Membership, Ausgabe.

### Gruppenadministrator

Ein Gruppenadministrator ist ein Benutzer mit der Rolle ADMIN innerhalb einer Gruppe.

Ein Gruppenadministrator darf zusätzlich zur normalen Gruppennutzung neue Mitglieder hinzufügen.

Siehe auch: MembershipRoleDT, Autorisierung.

### Gruppenmitglied

Ein Gruppenmitglied ist ein Benutzer, der über eine Mitgliedschaft einer Gruppe zugeordnet ist. Gruppenmitglieder dürfen Gruppendaten, Ausgaben, Salden und Exporte der jeweiligen Gruppe verwenden.

Siehe auch: Membership, MEMBER, Gruppe.

### Group

Group ist die technische Bezeichnung der Entität Gruppe im Datenmodell.

Siehe auch: Gruppe.

### Identifier

Identifier ist ein fachlicher Datentyp aus D2. Er beschreibt einen eindeutigen technischen Schlüssel für Entitäten wie User, Group, Expense oder ExpenseShare.

### Inbetriebnahme

Inbetriebnahme beschreibt das erstmalige Bereitstellen von CampusSplit in einer lauffähigen Umgebung.

Dazu gehören unter anderem:

- Betriebsumgebung vorbereiten
- Datenbank initialisieren
- Anwendung konfigurieren
- Backend und Frontend starten
- Funktionstest durchführen

Die Inbetriebnahme wird in S3 beschrieben.

### Kategorie

Eine Kategorie dient der fachlichen Einordnung einer Ausgabe.

Beispiele:

- Lebensmittel
- Unterkunft
- Fahrtkosten
- Freizeit
- Sonstiges

Kategorien beeinflussen die Saldenberechnung nicht.

### Kostenanteil

Ein Kostenanteil beschreibt, welchen Anteil ein Gruppenmitglied an einer Ausgabe trägt.

Beispiel:

Eine Ausgabe über 30,00 € wird auf drei Mitglieder verteilt. Jeder Kostenanteil beträgt 10,00 €.

Im Datenmodell entspricht dies der Entität ExpenseShare.

Siehe auch: ExpenseShare, Aufteilungsart, Geldbetrag.

### MEMBER

MEMBER ist eine Rolle innerhalb einer Gruppe. Ein Benutzer mit der Rolle MEMBER darf Gruppendaten sehen, Ausgaben erfassen, Salden anzeigen und Exporte erzeugen. Er darf jedoch keine neuen Mitglieder hinzufügen.

Siehe auch: MembershipRoleDT, Gruppenmitglied, ADMIN.

### Membership

Membership ist die technische Bezeichnung der Mitgliedschaft eines Benutzers in einer Gruppe. Sie verbindet User und Group und enthält die Rolle des Benutzers innerhalb der Gruppe.

Siehe auch: Gruppenmitglied, MembershipRoleDT.

### MembershipRoleDT

MembershipRoleDT ist ein fachlicher Datentyp aus D2. Er beschreibt die Rolle eines Benutzers innerhalb einer Gruppe.

Mögliche Werte:

- ADMIN
- MEMBER

Siehe auch: Gruppenadministrator, Gruppenmitglied, Autorisierung.

### MoneyAmountDT

MoneyAmountDT ist ein fachlicher Datentyp aus D2. Er beschreibt einen Geldbetrag mit genau zwei Nachkommastellen.

Der Typ wird verwendet für:

- Ausgabenbeträge
- Kostenanteile
- Salden
- Ausgleichsvorschläge

Siehe auch: Geldbetrag, Saldo, Kostenanteil.

### Nachbarsystem

Ein Nachbarsystem ist ein System oder technischer Kontext außerhalb von CampusSplit, mit dem CampusSplit kommuniziert.

Für CampusSplit sind relevante Nachbarsysteme:

- Webbrowser
- PostgreSQL-Datenbank
- Exportdatei

Nachbarsysteme werden in S1 beschrieben.

### Nichtziel

Ein Nichtziel beschreibt bewusst ausgeschlossene Funktionen oder Eigenschaften.

Beispiele für CampusSplit:

- keine Bankintegration
- keine Zahlungsabwicklung
- keine OCR-Belegerkennung
- keine Mehrwährungsunterstützung in der ersten Version

Nichtziele begrenzen den Projektumfang.

### Passwort-Hash

Ein Passwort-Hash ist eine nicht direkt rückrechenbare Darstellung eines Passworts.

CampusSplit speichert keine Klartextpasswörter, sondern nur Passwort-Hashes.

Siehe auch: Authentifizierung, Sicherheit.

### PDF

PDF ist ein Exportformat für lesbare Dokumente. In CampusSplit dient PDF zur Ausgabe einer verständlichen Gruppenausgabenübersicht.

Siehe auch: Export, Ausgabenübersicht, CSV.

### Persistenz

Persistenz bedeutet dauerhafte Speicherung von Daten. In CampusSplit werden Daten wie Benutzer, Gruppen, Ausgaben und Kostenanteile dauerhaft in der Datenbank gespeichert.

### PostgreSQL

PostgreSQL ist eine relationale Datenbank. Für CampusSplit ist PostgreSQL als Datenbank vorgesehen, um Benutzer, Gruppen, Ausgaben und Kostenanteile zu speichern.

### Repository

Ein Repository ist ein Speicherort für Quellcode und Projektdokumentation in Git.

Das CampusSplit-Projekt wird in einem GitHub-Repository verwaltet.

### Rolle

Eine Rolle beschreibt die Berechtigung eines Benutzers innerhalb einer Gruppe.

CampusSplit unterscheidet:

- ADMIN
- MEMBER

Siehe auch: MembershipRoleDT, Autorisierung.

### Saldo

Ein Saldo beschreibt, ob ein Gruppenmitglied Geld zurückbekommt oder Geld schuldet.

| Saldo   | Bedeutung                    |
| ------- | ---------------------------- |
| positiv | Mitglied bekommt Geld zurück |
| negativ | Mitglied schuldet Geld       |
| 0,00 €  | Mitglied ist ausgeglichen    |

Der Saldo wird berechnet als:

text id="kao71x" Saldo = gezahlte Beträge - eigene Kostenanteile

Siehe auch: Ausgleichsvorschlag, Gläubiger, Schuldner.

### Schuldner

Ein Schuldner ist ein Gruppenmitglied, das laut Saldenberechnung Geld schuldet. Ein negativer Saldo bedeutet, dass das Mitglied Schuldner ist.

Siehe auch: Saldo, Gläubiger, Ausgleichsvorschlag.

### Schnittstelle

Eine Schnittstelle beschreibt die Grenze zwischen CampusSplit und einem anderen System oder technischen Bestandteil.

Beispiele:

- Browser zu CampusSplit
- CampusSplit zur Datenbank
- CampusSplit zur Exportdatei

Schnittstellen zu Nachbarsystemen werden in S1 beschrieben.

### Sitzung

Eine Sitzung entsteht nach erfolgreicher Anmeldung. Sie ermöglicht dem Benutzer den Zugriff auf geschützte Funktionen, bis er sich abmeldet oder die Sitzung endet.

Siehe auch: Anmeldung, Authentifizierung.

### SplitMethodDT

SplitMethodDT ist ein fachlicher Datentyp aus D2. Er beschreibt die Art der Kostenaufteilung einer Ausgabe.

Mögliche Werte:

- EQUAL
- CUSTOM_AMOUNT

Siehe auch: Aufteilungsart, Kostenanteil.

### Use Case

Englische Bezeichnung für das Wort "Anwendungsfall". In CampusSplit beschreibt ein Use Case eine benutzerrelevante Funktion, zum Beispiel „Ausgabe erfassen" oder „Salden anzeigen".

Siehe auch: Anwendungsfall.

### User

User ist die technische Bezeichnung der Entität Benutzer im Datenmodell.

Siehe auch: Benutzer.

### Validierung

Validierung ist die Prüfung von Eingaben vor der Verarbeitung oder Speicherung.

Beispiele:

- Pflichtfelder dürfen nicht leer sein.
- Beträge müssen größer als 0,00 € sein.
- Zahler müssen Gruppenmitglieder sein.
- Die Summe der Kostenanteile muss dem Gesamtbetrag entsprechen.

Siehe auch: N2 Validierung, Fachliche Invariante.

### Währung

Die Währung beschreibt, in welcher Geldeinheit Beträge angegeben werden. CampusSplit verwendet in der ersten Version ausschließlich Euro.

### Zahlungsabwicklung

Zahlungsabwicklung bedeutet, dass ein System echte Zahlungen ausführt oder Zahlungsdienste integriert. CampusSplit führt keine Zahlungen aus. Das System berechnet nur offene Salden und Ausgleichsvorschläge. Tatsächliche Zahlungen erfolgen außerhalb der Anwendung.

Siehe auch: Ausgleichsvorschlag, Saldo, Nichtziel.

### Zahler

Der Zahler ist das Gruppenmitglied, das eine Ausgabe tatsächlich bezahlt hat. Der Zahler muss Mitglied der Gruppe sein, zu der die Ausgabe gehört.

Siehe auch: Ausgabe, Saldo, Kostenanteil.

## E2.1 Nicht Bestandteil des Glossars

Das Glossar ersetzt keine fachlichen Bausteine der Spezifikation.

| Thema                          | Ort in der Spezifikation |
| ------------------------------ | ------------------------ |
| Ziele und Projektumfang        | P1                       |
| Systemkontext                  | P2                       |
| Geschäftsprozesse              | F1                       |
| Anwendungsfälle                | F2                       |
| Anwendungsfunktionen           | F3                       |
| Datenmodell                    | D1                       |
| Datentypen                     | D2                       |
| Dialoge                        | B1                       |
| Exporte                        | B3                       |
| Nachbarsysteme                 | S1                       |
| Inbetriebnahme                 | S3                       |
| Nichtfunktionale Anforderungen | N1                       |
| Querschnittskonzepte           | N2                       |

## E2.2 Querverweise

| Baustein | Relevanz für E2                                                                                                       |
| -------- | --------------------------------------------------------------------------------------------------------------------- |
| P1       | Liefert zentrale Begriffe wie Ziel, Nichtziel, Projektumfang und Rahmenbedingung.                                     |
| P2       | Liefert Begriffe wie Systemkontext, Browser, Datenbank und Exportdatei.                                               |
| F1       | Liefert Begriffe zum Geschäftsprozess der gemeinsamen Ausgabenverwaltung.                                             |
| F2       | Liefert Begriffe zu Use Cases und Benutzeraktionen.                                                                   |
| F3       | Liefert Begriffe zu Kostenaufteilung, Saldenberechnung und Exportaufbereitung.                                        |
| D1       | Liefert Entitäten wie User, Group, Membership, Expense und ExpenseShare.                                              |
| D2       | Liefert Datentypen wie Identifier, MoneyAmountDT, CurrencyCodeDT, MembershipRoleDT, SplitMethodDT und ExportFormatDT. |
| B1       | Liefert Begriffe zu Dialogen und Benutzeroberflächen.                                                                 |
| B3       | Liefert Begriffe zu PDF-, CSV- und Exportausgaben.                                                                    |
| S1       | Liefert Begriffe zu Nachbarsystemen und Schnittstellen.                                                               |
| S3       | Liefert Begriffe zu Inbetriebnahme, Release und persistenten Daten.                                                   |
| N1       | Liefert Qualitätsbegriffe wie Benutzbarkeit, Sicherheit, Performance und Wartbarkeit.                                 |
| N2       | Liefert Begriffe zu Authentifizierung, Autorisierung, Validierung, Fehlerbehandlung, Logging und Exportsicherheit.    |