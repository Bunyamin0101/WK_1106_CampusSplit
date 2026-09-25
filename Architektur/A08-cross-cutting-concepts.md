# 8 Querschnittskonzepte

Dieses Kapitel beschreibt Architekturkonzepte, die mehrere Bausteine gleichzeitig betreffen. Die fachlichen Strategien stehen in [`N2 — Querschnittskonzepte`](../Spezifikation/N2_Querschnittskonzepte.md). Dieses Kapitel übersetzt diese Strategien in eine geplante technische Realisierung für CampusSplit.

Da die Implementierung noch nicht vollständig vorliegt, beschreibt dieses Kapitel Zielstruktur, Verantwortlichkeiten und Regeln, die beim Programmieren einzuhalten sind. Konkrete Klassennamen sind als Architekturvorschlag zu verstehen und sollen bei der Umsetzung möglichst beibehalten werden.

CampusSplit wird als serverseitig gerenderte Spring-Boot-Webanwendung mit Thymeleaf umgesetzt. Thymeleaf ist für die Darstellung zuständig; verbindliche Fachlogik verbleibt in Services und Domain-Komponenten.

---

## 8.1 Überblick

| Abschnitt | Konzept | Bezug zur Spezifikation |
|---|---|---|
| [8.2](#82-domänenmodell-und-persistenz) | Domänenmodell und Persistenz | D1, D2 |
| [8.3](#83-authentifizierung-und-sitzung) | Authentifizierung und Sitzung | N2.2 |
| [8.4](#84-autorisierung-und-gruppenrechte) | Autorisierung und Gruppenrechte | N2.3 |
| [8.5](#85-validierung) | Validierung | N2.4 |
| [8.6](#86-geldbetragsverarbeitung) | Geldbetragsverarbeitung | F3, D2, N2.5 |
| [8.7](#87-fremdwährung-und-wechselkursdienst) | Fremdwährung und Wechselkursdienst | S1, D1, D2 |
| [8.8](#88-rückzahlungen-belege-historie-und-archivierung) | Rückzahlungen, Belege, Historie und Archivierung | erweiterter Funktionsumfang |
| [8.9](#89-fehlerbehandlung) | Fehlerbehandlung | N2.6 |
| [8.10](#810-logging) | Logging | N2.7 |
| [8.11](#811-exportsicherheit) | Exportsicherheit | B3, N2.8 |
| [8.12](#812-ui-architektur) | UI-Architektur | B1, N1 |
| [8.13](#813-testkonzept) | Testkonzept | F3, N1 |

---

## 8.2 Domänenmodell und Persistenz

Das fachliche Datenmodell aus D1 wird technisch durch JPA-Entities und PostgreSQL-Tabellen abgebildet. Salden und Ausgleichsvorschläge werden nicht als dauerhafte Stammdaten gespeichert, sondern aus Ausgaben und Kostenanteilen berechnet.

```mermaid
flowchart LR
    D1[D1 Fachliches Datenmodell]
    ENT[JPA Entities]
    DB[(PostgreSQL Tabellen)]
    REPO[Spring Data Repositories]
    SERV[Application Services]

    D1 --> ENT
    ENT --> DB
    REPO --> DB
    SERV --> REPO
```

| Fachliche Entität | Technische Realisierung | Bemerkung |
|---|---|---|
| User | `UserEntity` / Tabelle `users` | Enthält E-Mail, Anzeigename und Passwort-Hash. |
| Group | `GroupEntity` / Tabelle `groups` | Enthält Name, Beschreibung, Gruppenwährung und Owner. |
| Membership | `MembershipEntity` / Tabelle `memberships` | Löst die n:m-Beziehung zwischen User und Group auf. |
| Expense | `ExpenseEntity` / Tabelle `expenses` | Enthält Zahler, Ersteller, Originalbetrag, Abrechnungsbetrag und Datum. |
| ExpenseShare | `ExpenseShareEntity` / Tabelle `expense_shares` | Enthält Kostenanteil je beteiligtem Mitglied. |
| Category | `CategoryEntity` / Tabelle `categories` | Optionale Klassifikation von Ausgaben. |
| Repayment | `Repayment` / Persistenz über `RepaymentRepository` | Dokumentiert Rückzahlungen zwischen Gruppenmitgliedern; Stornierung bleibt nachvollziehbar. |
| Receipt | `Receipt` / Persistenz über `ReceiptRepository` | Beleg, der einer Ausgabe zugeordnet ist. |
| ExpenseChange | `ExpenseChange` / Persistenz über `ExpenseChangeRepository` | Fachliche Änderungshistorie für Ausgaben und Belegvorgänge. |
| Balance | DTO / berechnetes Ergebnis | Wird aus Expenses und ExpenseShares berechnet. |
| SettlementProposal | DTO / berechnetes Ergebnis | Wird aus Salden berechnet. |

### Persistenzregeln

| ID | Regel |
|---|---|
| PER-01 | Datenbankzugriffe erfolgen über Repositories oder klar abgegrenzte Persistenzkomponenten. |
| PER-02 | Fachliche Transaktionen, z. B. Ausgabe plus Kostenanteile, werden atomar gespeichert. |
| PER-03 | Salden werden nicht manuell gespeichert, damit keine widersprüchlichen Werte entstehen. |
| PER-04 | Fremdschlüssel sichern Beziehungen zwischen User, Group, Membership, Expense und ExpenseShare. |
| PER-05 | E-Mail-Adressen müssen eindeutig sein. |
| PER-06 | Ein Benutzer darf pro Gruppe höchstens eine Membership besitzen. |

---

## 8.3 Authentifizierung und Sitzung

CampusSplit schützt alle fachlichen Funktionen außer Registrierung und Anmeldung. Die technische Umsetzung erfolgt mit Spring Security und einer serverseitigen HTTP-Session. Als Anmeldewege stehen das klassische Form-Login mit E-Mail und Passwort sowie – bei aktivierter Konfiguration – Google OAuth2/OpenID Connect zur Verfügung.

Nach erfolgreicher Anmeldung verwaltet das Backend unabhängig vom Anmeldeweg die Sitzung. Der Browser erhält ein HTTP-only Session-Cookie. Authentifizierungsdaten werden nicht im Local Storage gespeichert. Ein separates JWT-Konzept ist für den MVP nicht vorgesehen.

```mermaid
sequenceDiagram
    actor Nutzer
    participant Browser
    participant Security as Spring Security
    participant DB as PostgreSQL
    participant View as Thymeleaf Template

    Nutzer->>Browser: E-Mail + Passwort eingeben
    Browser->>Security: POST /login
    Security->>DB: Benutzer und Passwort-Hash prüfen
    DB-->>Security: Benutzer gefunden
    Security->>Security: serverseitige Session erzeugen
    Security-->>Browser: Redirect /dashboard + HTTP-only Session-Cookie
    Browser->>Security: GET /dashboard
    Security->>View: Model + Template
    View-->>Browser: HTML
```

| Regel | Umsetzung |
|---|---|
| AUTH-01 | Registrierung und Anmeldung sind öffentlich erreichbar. |
| AUTH-02 | Alle anderen geschützten Routen benötigen eine gültige serverseitige Session. |
| AUTH-03 | Passwörter werden nie im Klartext gespeichert. |
| AUTH-04 | Login-Fehler bleiben allgemein, z. B. „Anmeldedaten ungültig“. |
| AUTH-05 | Logout beendet die serverseitige Sitzung. |
| AUTH-06 | Das Session-Cookie wird HTTP-only gesetzt; Authentifizierungsdaten werden nicht im Local Storage verwaltet. |

---

### Google OAuth2 / OpenID Connect

Google wird nur für die externe Authentifizierung verwendet. Die Zuordnung zur lokalen CampusSplit-Identität erfolgt im Backend. Spring Security validiert den OIDC-Ablauf; fachliche Rollen und Gruppenrechte bleiben vollständig in CampusSplit.

```mermaid
flowchart LR
    B[Browser] --> SS[Spring Security]
    SS --> G[Google OIDC]
    G --> SS
    SS --> GA[GoogleOidcUserService]
    GA --> U[(lokaler CampusSplit User)]
    U --> S[serverseitige Session]
```

Ein bestehendes lokales Konto kann über das Profil mit Google verknüpft werden. Vor Beginn der Verknüpfung wird das lokale Passwort erneut geprüft. Ein kurzlebiger Link-Intent in der Session bindet den Vorgang an den bereits angemeldeten Benutzer. Konflikte bei der Zuordnung führen zu einem kontrollierten Fehler statt zu einer automatischen Kontoübernahme.

Zusätzliche Regeln:

| Regel | Umsetzung |
|---|---|
| AUTH-07 | Google-Login ist optional und kann per Konfiguration deaktiviert werden. |
| AUTH-08 | Nach Google-Login wird dieselbe serverseitige Session-Architektur wie beim Form-Login verwendet. |
| AUTH-09 | Gruppenrollen und Autorisierung werden nicht von Google übernommen. |
| AUTH-10 | Eine Google-Kontoverknüpfung erfordert eine erneute lokale Bestätigung und einen kurzlebigen Verknüpfungsvorgang. |
| AUTH-11 | Account-Konflikte werden geschlossen behandelt; Konten werden nicht stillschweigend zusammengeführt. |


## 8.4 Autorisierung und Gruppenrechte

Autorisierung wird nicht der Benutzeroberfläche überlassen. Thymeleaf darf Buttons abhängig von Rechten ausblenden, aber das Backend entscheidet verbindlich, ob eine Aktion erlaubt ist.

| Anfrageart | Backend-Prüfung |
|---|---|
| Gruppe anzeigen | Ist der aktuelle Benutzer Mitglied der Gruppe? |
| Mitglied hinzufügen | Ist der aktuelle Benutzer ADMIN dieser Gruppe? |
| Ausgabe erfassen | Ist der aktuelle Benutzer Mitglied der Gruppe? |
| Ausgabe bearbeiten/löschen | Ist der aktuelle Benutzer Mitglied der Gruppe? |
| Salden anzeigen | Ist der aktuelle Benutzer Mitglied der Gruppe? |
| Export erzeugen | Ist der aktuelle Benutzer Mitglied der Gruppe? |
| Beleg hoch-/herunterladen/löschen | Hat der aktuelle Benutzer Zugriff auf die zugehörige Gruppe und Ausgabe? |
| Rückzahlung erfassen/stornieren | Ist der aktuelle Benutzer berechtigtes Gruppenmitglied und sind Sender/Empfänger gültig? |
| Gruppe archivieren/wiederherstellen | Besitzt der aktuelle Benutzer die erforderliche Gruppenberechtigung? |

Empfohlener Service:

```text
MembershipGuard
├── requireMember(userId, groupId)
└── requireAdmin(userId, groupId)
```

### Architekturregel

Jeder gruppenbezogene Controller ruft vor der eigentlichen Fachlogik eine Membership-Prüfung auf. Dadurch wird verhindert, dass ein Benutzer über direkt aufgerufene URLs oder manipulierte Formularanfragen auf fremde Gruppendaten zugreift.

---

## 8.5 Validierung

Validierung findet auf mehreren Ebenen statt. Browser und Thymeleaf helfen durch verständliche Formularhinweise, aber nur das Backend ist verbindlich.

| Ebene | Aufgabe | Beispiel |
|---|---|---|
| Browser / Thymeleaf | Bedienhinweise und Darstellung von Fehlern | Pflichtfelder markieren, vorhandene Feldfehler anzeigen. |
| Spring MVC Form Object / DTO | Formale Eingabeprüfung | `@NotBlank`, `@Email`, `@Positive`, `@NotNull`. |
| Service | Fachliche Prüfung | Zahler muss Gruppenmitglied sein. |
| Datenbank | Integritätsprüfung | Unique Constraints, Foreign Keys. |

### Pflichtfelder je Bereich

| Bereich | Pflichtfelder |
|---|---|
| Registrierung | Name, E-Mail, Passwort |
| Anmeldung | E-Mail, Passwort |
| Gruppe erstellen | Gruppenname, Gruppenwährung |
| Mitglied hinzufügen | E-Mail-Adresse des Mitglieds |
| Ausgabe erfassen | Beschreibung, Betrag, Währung, Datum, Zahler, Beteiligte, Aufteilungsart |
| Ausgabe bearbeiten | Zu ändernde Felder plus gültige Ausgabe-ID |
| Export | Gruppe, Exportformat |

### Fehlerprinzip

Ungültige Eingaben erzeugen verständliche Fehlermeldungen. Bei Formularfehlern wird das entsprechende Thymeleaf-Template mit den eingegebenen Werten und Validierungsmeldungen erneut angezeigt. Es wird nichts teilweise gespeichert, wenn eine fachliche Prüfung fehlschlägt.

---

## 8.6 Geldbetragsverarbeitung

Geldlogik ist der kritischste Teil von CampusSplit. Deshalb liegt sie zentral im Backend und nicht in Thymeleaf oder clientseitigem JavaScript.

| Regel | Umsetzung |
|---|---|
| MONEY-01 | Kein `double` oder `float` für Geldbeträge. |
| MONEY-02 | Geldbeträge werden centgenau verarbeitet. |
| MONEY-03 | `MoneyAmountDT` besteht fachlich aus Betrag und Währung. |
| MONEY-04 | Kostenanteile müssen in Summe exakt dem Abrechnungsbetrag entsprechen. |
| MONEY-05 | Salden innerhalb einer Gruppe müssen in Summe 0,00 ergeben. |
| MONEY-06 | Rundungsdifferenzen werden deterministisch verteilt. |

Empfohlene technische Umsetzung:

```text
Money
├── amount: BigDecimal
└── currency: CurrencyCode
```

Geldbeträge werden im Backend durchgehend als `BigDecimal` mit fixer Skalierung (2 Nachkommastellen) verarbeitet.

### Verantwortliche Services

| Service | Aufgabe |
|---|---|
| `SplitService` | Berechnet Kostenanteile einer Ausgabe. |
| `BalanceService` | Berechnet Salden je Gruppenmitglied. |
| `SettlementService` | Berechnet Ausgleichsvorschläge zwischen Debitoren und Kreditoren. |

---

## 8.7 Fremdwährung und Wechselkursdienst

Fremdwährungsausgaben werden über den Wechselkursdienst aus S1 behandelt. Die externe API wird ausschließlich vom Backend aufgerufen.

```mermaid
sequenceDiagram
    participant ExpenseService
    participant CurrencyAdapter
    participant Frankfurter as Frankfurter API

    ExpenseService->>CurrencyAdapter: getRate(from, to, date)
    CurrencyAdapter->>Frankfurter: HTTPS GET Wechselkurs
    Frankfurter-->>CurrencyAdapter: Kursdaten
    CurrencyAdapter-->>ExpenseService: ExchangeRateDT
    ExpenseService->>ExpenseService: settlementAmount berechnen
```

| Regel | Umsetzung |
|---|---|
| FX-01 | API-Aufruf nur, wenn Originalwährung und Gruppenwährung verschieden sind. |
| FX-02 | Es werden nur Währungscodes und Datum übertragen, keine personenbezogenen Daten. |
| FX-03 | Ohne gültigen Kurs wird keine Fremdwährungsausgabe gespeichert. |
| FX-04 | Originalbetrag, Originalwährung, Kurs und Abrechnungsbetrag bleiben nachvollziehbar. |
| FX-05 | Die eigentliche Umrechnung und Rundung erfolgt in CampusSplit. |

Empfohlener Adapter:

```text
CurrencyRateClient
└── getRate(fromCurrency, toCurrency, date): ExchangeRate
```

---


## 8.8 Rückzahlungen, Belege, Historie und Archivierung

Diese Funktionen sind im aktuellen Projektstand als zusätzliche fachliche Bausteine angelegt. Sie werden getrennt von der eigentlichen Ausgabenberechnung gehalten, greifen aber auf dieselben Membership- und Sicherheitsregeln zurück.

### Rückzahlungen

`Repayment` dokumentiert einen bereits erfolgten Ausgleich zwischen Sender und Empfänger. Der Betrag wird in der Gruppenwährung gespeichert. Eine Rückzahlung kann mit Begründung storniert werden; sie wird nicht einfach gelöscht. Eine `requestId` dient dazu, doppelte Erfassung desselben Vorgangs zu vermeiden.

Wichtig: CampusSplit führt keine Banktransaktion aus. Es dokumentiert die Rückzahlung und berücksichtigt sie bei den offenen Salden.

### Belege

`Receipt` ordnet einer Ausgabe einen hochgeladenen Beleg zu. Upload, Download und Löschen werden über einen eigenen Controller bzw. Service gekapselt. Beim Download werden Sicherheitsheader gesetzt; der Browser soll den Inhalt als Datei behandeln und nicht unkontrolliert inline ausführen.

### Änderungshistorie

`ExpenseChange` hält fachlich relevante Änderungen an einer Ausgabe bzw. zugehörigen Belegvorgängen fest. Diese Historie ist von technischem Logging zu unterscheiden: Sie ist Teil der fachlichen Nachvollziehbarkeit und kann in der Gruppenansicht angezeigt werden.

### Gruppenarchivierung

Archivierte Gruppen bleiben gespeichert und einsehbar. Archivierung ist deshalb ein Statuswechsel und kein physisches Löschen. Eine archivierte Gruppe kann wiederhergestellt werden.


## 8.9 Fehlerbehandlung

Fehler werden einheitlich behandelt, damit Benutzer keine technischen Details sehen und Daten konsistent bleiben.

| Fehlerart | Beispiel | Verhalten |
|---|---|---|
| Validierungsfehler | Betrag fehlt oder ist negativ | 400/422 mit verständlicher Feldmeldung. |
| Authentifizierungsfehler | Nicht angemeldet | Weiterleitung zur Anmeldung oder Zugriff wird durch Spring Security verweigert. |
| Autorisierungsfehler | Zugriff auf fremde Gruppe | Zugriff wird verweigert; fremde Daten werden nicht angezeigt. |
| Nicht gefunden | Gruppe existiert nicht | Fehlerseite oder passende 404-Behandlung. |
| Externe API nicht erreichbar | Wechselkursdienst antwortet nicht | Verständliche Fehlermeldung im Formular, keine Speicherung mit erfundenem Kurs. |
| Technischer Fehler | Datenbankfehler | Allgemeine Fehlerseite bzw. Fehlermeldung, technische Details nur im Log. |
| Google-Anmeldung fehlgeschlagen | OAuth2/OIDC-Fehler oder Kontokonflikt | Kontrollierte Rückkehr zur Login-Seite; keine automatische falsche Kontoverknüpfung. |
| Ungültiger Beleg | Datei nicht zulässig oder Zugriff fehlt | Upload/Download wird abgelehnt; bestehende Ausgabendaten bleiben unverändert. |
| Rückzahlung ungültig | Betrag, Beteiligte oder Stornierung unzulässig | Vorgang wird nicht gespeichert bzw. nicht verändert. |

### Architekturregel

Controller und Fehlerseiten geben keine Stacktraces, SQL-Fehler oder internen Klassennamen an Benutzer aus. Technische Details bleiben im Log.

---

## 8.10 Logging

Logging dient der Fehleranalyse, darf aber keine sensiblen Daten offenlegen.

| Darf geloggt werden | Darf nicht geloggt werden |
|---|---|
| Zeitpunkt eines Fehlers | Klartextpasswörter |
| Fehlerkategorie | Passwort-Hashes |
| aufgerufene Route oder Use Case | Session-IDs oder Session-Cookies |
| technische Ursache in allgemeiner Form | personenbezogene Detaildaten aus Ausgaben |
| Benutzer-ID, wenn für Analyse nötig | vollständige Exportinhalte |

Empfohlene Regel: Logs sollen bei Fehlern helfen, aber nicht zu einer zweiten Datenbank mit sensiblen Inhalten werden.

---

## 8.11 Exportsicherheit

PDF- und CSV-Export sind ausgehende Datenflüsse. Deshalb gelten besondere Regeln.

| Regel | Umsetzung |
|---|---|
| EXP-01 | Export nur für Gruppen, in denen der Benutzer Mitglied ist. |
| EXP-02 | Exportdaten werden aus aktuellen Gruppen-, Ausgaben-, Anteil- und Saldendaten erzeugt. |
| EXP-03 | Export enthält keine Passwörter, Hashes, Sessions oder technischen Interna. |
| EXP-04 | Export erzeugt keine fachliche Zustandsänderung. |
| EXP-05 | PDF ist für menschliches Lesen gedacht, CSV für tabellarische Weiterverarbeitung. |

Empfohlene technische Trennung:

```text
ExportController
└── ExportService
    ├── ExportDataAssembler
    ├── PdfExportWriter
    └── CsvExportWriter
```

---

## 8.12 UI-Architektur

Die Benutzeroberfläche wird mit Thymeleaf serverseitig gerendert. Spring-MVC-Controller bereiten die benötigten Daten vor und übergeben sie über das Model an Templates. Formulare werden als Form Objects bzw. DTOs an Controller gebunden und serverseitig validiert.

Thymeleaf dient ausschließlich der Darstellung. Verbindliche Fachlogik wie Kostenaufteilung, Saldenberechnung, Autorisierung und Währungsumrechnung verbleibt in Services und Domain-Komponenten.

| UI-Bereich | Architekturregel |
|---|---|
| Thymeleaf Templates | Stellen vollständige HTML-Seiten für die Dialoge aus B1 bereit. |
| Fragments | Wiederverwendbare Seitenteile wie Navigation, Fehlermeldungen oder Tabellenbereiche. |
| Spring MVC Controller | Bereiten Model-Daten vor, verarbeiten Formulare und koordinieren Navigation. |
| Model | Übergibt View-Daten vom Controller an Thymeleaf. |
| Form Objects / DTOs | Nehmen Formulareingaben entgegen und werden serverseitig validiert. |
| CSS | Zuständig für Layout und responsive Darstellung. |
| JavaScript | Nur ergänzend für Bedienkomfort; keine verbindliche Fachlogik. |

Empfohlene Struktur:

```text
src/main/resources/
├── templates/
│   ├── auth/
│   ├── groups/
│   ├── expenses/
│   ├── balances/
│   └── fragments/
└── static/
    ├── css/
    └── js/
```

### Navigationsprinzip

Lesende Seiten werden über `GET` geladen. Schreibende Formularaktionen verwenden `POST` und führen bei erfolgreicher Verarbeitung nach Möglichkeit über einen Redirect auf eine passende `GET`-Seite zurück. Dieses **POST → Redirect → GET**-Muster verhindert unbeabsichtigtes erneutes Absenden beim Neuladen der Seite.

---

## 8.13 Testkonzept

Tests konzentrieren sich zuerst auf die fachlich riskanten Stellen.

| Testart | Fokus |
|---|---|
| Unit-Tests | SplitService, BalanceService, SettlementService, Money-Rundung. |
| Service-Tests | Gruppenrechte, Ausgabenerfassung, Fremdwährungslogik und Fehlerfälle. |
| Repository-/Integrationstests | JPA-Zugriffe, Transaktionen und PostgreSQL-nahe Persistenz. |
| MVC-Tests | Controller, Formularbindung, Validierungsfehler, Views und Redirects mit MockMvc. |
| Security-Tests | Login, Logout, geschützte Seiten, Membership- und Adminrechte. |
| Manuelle UI-Tests | Responsive Darstellung, Formulare und Demo-Szenarien für Review und Präsentation. |

### Besonders wichtige Testfälle

| ID | Testfall |
|---|---|
| T-01 | 10,00 auf 3 Personen ergibt 3,34 / 3,33 / 3,33. |
| T-02 | Summe aller Kostenanteile entspricht exakt dem Abrechnungsbetrag. |
| T-03 | Summe aller Salden einer Gruppe ist 0,00. |
| T-04 | Nicht-Mitglieder können keine Gruppendaten sehen. |
| T-05 | MEMBER kann kein neues Mitglied hinzufügen. |
| T-06 | Fremdwährungsausgabe ohne gültigen Kurs wird nicht gespeichert. |
| T-07 | Export enthält keine sensiblen Daten. |
| T-08 | Erfolgreiche schreibende Formulare verwenden einen Redirect und werden beim Neuladen nicht erneut abgesendet. |
| T-09 | Google-Login ordnet eine gültige Google-Identität dem richtigen lokalen Benutzer zu. |
| T-10 | Konflikte bei Google-Konten führen nicht zu einer ungewollten Kontoverknüpfung. |
| T-11 | Rückzahlungen verändern die offenen Salden korrekt; stornierte Rückzahlungen werden entsprechend berücksichtigt. |
| T-12 | Belege sind nur für berechtigte Gruppenmitglieder abrufbar. |
| T-13 | Archivieren löscht keine Gruppendaten und eine Gruppe kann wiederhergestellt werden. |

---

## 8.14 Abgrenzung

Nicht Bestandteil der Querschnittskonzepte sind:

- vollständige Implementierung einzelner Klassen,
- detaillierte SQL-Migrationen,
- konkrete CSS- oder Layoutvorgaben,
- produktive Monitoring-Infrastruktur,
- Zahlungs- oder Bank-Sicherheitskonzepte,
- KI-Sicherheitskonzepte zur Laufzeit.
 
 
