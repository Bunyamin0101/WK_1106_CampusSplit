# 6 Laufzeitsicht

Die Laufzeitsicht zeigt, wie die Bausteine aus [A05 — Building Block View](A05-building-block-view.md) in wichtigen Szenarien zusammenarbeiten. Es werden nicht alle CRUD-Abläufe vollständig wiederholt, sondern nur die Abläufe, die architektonisch besonders relevant sind.

Ausgewählt wurden Szenarien, die zentrale Architekturentscheidungen sichtbar machen: Authentifizierung, Gruppenzugriff, Kostenaufteilung, Fremdwährungsumrechnung, Saldenberechnung und Export.

CampusSplit wird als serverseitig gerenderte Spring-Boot-Webanwendung mit Thymeleaf umgesetzt. Benutzeraktionen werden über HTTP-Anfragen an Spring-MVC-Controller verarbeitet. Nach erfolgreichen schreibenden Aktionen wird nach Möglichkeit das Muster **POST → Redirect → GET** verwendet.

---

## 6.0 Übersicht der Szenarien

| Szenario | Use Case | Warum architektonisch relevant? |
|---|---|---|
| [6.1](#61-registrierung-und-anmeldung) Registrierung und Anmeldung | UC-01, UC-02 | Öffentlicher Zugriff, Passwortschutz, Start einer serverseitigen Sitzung. |
| [6.2](#62-gruppe-erstellen-und-mitglied-hinzufügen) Gruppe erstellen und Mitglied hinzufügen | UC-05, UC-07 | Membership, Rollen, Adminrechte und Zugriffsschutz. |
| [6.3](#63-ausgabe-in-gruppenwährung-erfassen) Ausgabe in Gruppenwährung erfassen | UC-08 | Validierung, Kostenanteile, transaktionales Speichern. |
| [6.4](#64-fremdwährungsausgabe-erfassen) Fremdwährungsausgabe erfassen | UC-08, S1 | Externe API, Wechselkurs, Fehlerfall ohne unvollständige Speicherung. |
| [6.5](#65-salden-und-ausgleichsvorschläge-anzeigen) Salden und Ausgleichsvorschläge anzeigen | UC-11 | Backendseitige Geldlogik, Debitor/Kreditor, deterministische Berechnung. |
| [6.6](#66-pdf-oder-csv-export-erzeugen) PDF- oder CSV-Export erzeugen | UC-12 | Exportdaten, Exportsicherheit, Datei als Datenfluss. |

Alle fachlichen Aktionen werden durch Benutzerinteraktionen ausgelöst. Es gibt keine Batch-Verarbeitung, keine Queue und keine Hintergrundjobs.

---

## 6.1 Registrierung und Anmeldung

```mermaid
sequenceDiagram
    actor Gast
    participant Browser
    participant Auth as Spring Security / AuthController
    participant Service as AuthService
    participant Repo as UserRepository
    participant DB as PostgreSQL
    participant View as Thymeleaf Template

    Gast->>Browser: Registrierungsformular ausfüllen
    Browser->>Auth: POST /register
    Auth->>Auth: Request validieren
    Auth->>Service: register(name, email, password)
    Service->>Repo: existsByEmail(email)
    Repo->>DB: E-Mail prüfen
    DB-->>Repo: Ergebnis
    Service->>Service: Passwort hashen
    Service->>Repo: User speichern
    Repo->>DB: INSERT User
    DB-->>Repo: gespeicherter User
    Repo-->>Service: User
    Service-->>Auth: Registrierung erfolgreich
    Auth-->>Browser: Redirect /login

    Gast->>Browser: E-Mail und Passwort eingeben
    Browser->>Auth: POST /login
    Auth->>Repo: Benutzer laden und Passwort prüfen
    Repo->>DB: SELECT User
    DB-->>Repo: User
    Auth->>Auth: serverseitige Session erzeugen
    Auth-->>Browser: Redirect /dashboard + HTTP-only Session-Cookie
    Browser->>Auth: GET /dashboard
    Auth->>View: Model + Template
    View-->>Browser: HTML
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Öffentliche Endpunkte | Registrierung und Anmeldung sind ohne vorherige Sitzung erreichbar. |
| Passwortschutz | Klartextpasswörter werden nie gespeichert; nur Hashwerte liegen in der Datenbank. |
| Generische Loginfehler | Bei falschen Zugangsdaten wird nicht verraten, ob E-Mail oder Passwort falsch war. |
| Serverseitige Sitzung | Nach erfolgreicher Anmeldung verwaltet Spring Security die Session; der Browser erhält ein HTTP-only Session-Cookie. |
| Startpunkt | Nach erfolgreicher Anmeldung ist das Dashboard der Einstieg in die Anwendung. |

Fehlerfälle:

| Fehler | Verhalten |
|---|---|
| E-Mail ungültig | Formular wird erneut angezeigt und enthält eine verständliche Validierungsmeldung. |
| E-Mail bereits vergeben | Registrierung wird abgelehnt und das Formular zeigt einen Hinweis. |
| Passwort falsch | Allgemeine Fehlermeldung auf der Login-Seite. |
| Datenbank nicht erreichbar | Technischer Fehler, kein Login und keine halbe Registrierung. |

---

## 6.2 Gruppe erstellen und Mitglied hinzufügen

```mermaid
sequenceDiagram
    actor Nutzer
    participant Browser
    participant Controller as GroupController / MembershipController
    participant GroupSvc as GroupService
    participant MemberSvc as MembershipService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Nutzer->>Browser: Neue Gruppe anlegen
    Browser->>Controller: POST /groups
    Controller->>Controller: Authentifizierung prüfen
    Controller->>Controller: Gruppenname und Währung validieren
    Controller->>GroupSvc: createGroup(currentUser, request)
    GroupSvc->>Repo: Group speichern
    Repo->>DB: INSERT Group
    GroupSvc->>Repo: Membership ADMIN speichern
    Repo->>DB: INSERT Membership
    DB-->>Repo: gespeicherte Daten
    GroupSvc-->>Controller: Gruppe mit ADMIN-Mitgliedschaft
    Controller-->>Browser: Redirect /groups/{groupId}
    Browser-->>Nutzer: Gruppendetail anzeigen

    Nutzer->>Browser: Mitglied per E-Mail hinzufügen
    Browser->>Controller: POST /groups/{groupId}/members
    Controller->>MemberSvc: addMember(groupId, email, currentUser)
    MemberSvc->>Repo: aktuelle Membership prüfen
    Repo->>DB: Rolle des aktuellen Nutzers laden
    DB-->>Repo: ADMIN oder MEMBER
    MemberSvc->>MemberSvc: ADMIN-Recht prüfen
    MemberSvc->>Repo: User anhand E-Mail suchen
    Repo->>DB: SELECT User
    MemberSvc->>Repo: neue Membership speichern
    Repo->>DB: INSERT Membership MEMBER
    MemberSvc-->>Controller: Mitglied hinzugefügt
    Controller-->>Browser: Redirect /groups/{groupId}
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Automatische ADMIN-Rolle | Der Gruppenersteller wird direkt Mitglied und erhält ADMIN. |
| Membership als Zugriffsbasis | Zugriff auf Gruppen wird immer über Membership geprüft. |
| Adminpflicht | Nur ADMIN darf neue Mitglieder hinzufügen. |
| Keine Doppelmitgliedschaft | Ein Benutzer darf in derselben Gruppe nicht mehrfach Mitglied sein. |

Fehlerfälle:

| Fehler | Verhalten |
|---|---|
| Nicht angemeldet | Anfrage wird abgelehnt. |
| Gruppenname leer | Gruppe wird nicht gespeichert. |
| Nutzer ist kein ADMIN | Mitglied wird nicht hinzugefügt. |
| E-Mail existiert nicht | Verständliche Fehlermeldung. |
| Person ist bereits Mitglied | Keine doppelte Membership. |

---

## 6.3 Ausgabe in Gruppenwährung erfassen

```mermaid
sequenceDiagram
    actor Mitglied
    participant Browser
    participant Controller as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant Split as SplitService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Mitglied->>Browser: Ausgabe erfassen
    Browser->>Controller: POST /groups/{groupId}/expenses
    Controller->>Controller: Authentifizierung prüfen
    Controller->>Controller: Requestdaten validieren
    Controller->>ExpenseSvc: createExpense(groupId, request, currentUser)
    ExpenseSvc->>Repo: Membership des Nutzers prüfen
    Repo->>DB: SELECT Membership
    DB-->>Repo: Membership vorhanden
    ExpenseSvc->>Repo: Gruppe und Mitglieder laden
    Repo->>DB: SELECT Group, Memberships
    DB-->>Repo: Gruppendaten
    ExpenseSvc->>ExpenseSvc: Zahler und Beteiligte prüfen
    ExpenseSvc->>Split: calculateShares(settlementAmount, participants, splitMethod)
    Split-->>ExpenseSvc: Kostenanteile
    ExpenseSvc->>Repo: Expense speichern
    Repo->>DB: INSERT Expense
    ExpenseSvc->>Repo: ExpenseShares speichern
    Repo->>DB: INSERT ExpenseShare*
    DB-->>Repo: gespeichert
    ExpenseSvc-->>Controller: gespeicherte Ausgabe
    Controller-->>Browser: Redirect /groups/{groupId}
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Backend entscheidet verbindlich | Thymeleaf-Formulare liefern Eingaben; das Backend prüft und berechnet verbindlich. |
| Keine Fremdwährungs-API nötig | Wenn Originalwährung = Gruppenwährung, wird kein Wechselkursdienst aufgerufen. |
| Kostenanteile entstehen im Backend | Equal Split und Custom Amount werden zentral berechnet/geprüft. |
| Transaktionales Speichern | Expense und ExpenseShares müssen zusammen gespeichert werden. |

Fehlerfälle:

| Fehler | Verhalten |
|---|---|
| Zahler ist kein Gruppenmitglied | Ausgabe wird abgelehnt. |
| Keine beteiligte Person | Ausgabe wird abgelehnt. |
| Betrag <= 0 | Ausgabe wird abgelehnt. |
| Custom-Anteile passen nicht zur Summe | Ausgabe wird nicht gespeichert. |
| Datenbankfehler nach Teiloperation | Transaktion wird zurückgerollt. |

---

## 6.4 Fremdwährungsausgabe erfassen

```mermaid
sequenceDiagram
    actor Mitglied
    participant Browser
    participant Controller as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant FX as CurrencyRateClient
    participant Frank as Frankfurter API
    participant Split as SplitService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Mitglied->>Browser: Ausgabe z. B. 30 USD erfassen
    Browser->>Controller: POST /groups/{groupId}/expenses
    Controller->>ExpenseSvc: createExpense(...)
    ExpenseSvc->>Repo: Gruppe laden
    Repo->>DB: SELECT Group
    DB-->>Repo: Gruppenwährung z. B. EUR
    ExpenseSvc->>ExpenseSvc: Originalwährung != Gruppenwährung erkennen
    ExpenseSvc->>FX: getRate(USD, EUR, expenseDate)
    FX->>Frank: HTTPS GET Wechselkurs
    Frank-->>FX: JSON mit Kurs
    FX-->>ExpenseSvc: ExchangeRate
    ExpenseSvc->>ExpenseSvc: settlementAmount berechnen und runden
    ExpenseSvc->>Split: calculateShares(settlementAmount, participants, splitMethod)
    Split-->>ExpenseSvc: Kostenanteile in Gruppenwährung
    ExpenseSvc->>Repo: Expense mit originalAmount, settlementAmount, exchangeRate speichern
    Repo->>DB: INSERT Expense + ExpenseShares
    DB-->>Repo: gespeichert
    ExpenseSvc-->>Controller: gespeicherte Ausgabe
    Controller-->>Browser: Redirect /groups/{groupId}
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| API-Aufruf nur bei Bedarf | Frankfurter API wird nur verwendet, wenn Originalwährung und Gruppenwährung abweichen. |
| Keine personenbezogenen Daten | An den Dienst gehen nur Ausgangswährung, Zielwährung und Datum. |
| Originalbetrag bleibt erhalten | Die Ausgabe speichert Originalbetrag, Originalwährung, Kurs und Abrechnungsbetrag. |
| Salden in Gruppenwährung | Kostenanteile, Salden und Ausgleichsvorschläge werden in Gruppenwährung berechnet. |
| Kein erfundener Kurs | Ohne gültigen Kurs wird keine Fremdwährungsausgabe gespeichert. |

Fehlerfall: Wechselkursdienst nicht erreichbar

```mermaid
sequenceDiagram
    actor Mitglied
    participant Browser
    participant Controller as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant FX as CurrencyRateClient
    participant Frank as Frankfurter API
    participant DB as PostgreSQL

    Mitglied->>Browser: Fremdwährungsausgabe speichern
    Browser->>Controller: POST /groups/{groupId}/expenses
    Controller->>ExpenseSvc: createExpense(...)
    ExpenseSvc->>FX: getRate(...)
    FX->>Frank: HTTPS GET
    Frank--xFX: Fehler / Timeout / ungültige Antwort
    FX--xExpenseSvc: CurrencyRateException
    ExpenseSvc--xController: fachlicher Fehler
    Controller-->>Browser: Thymeleaf-Formular mit verständlicher Fehlermeldung
    Note over DB: Es wird keine unvollständige Ausgabe gespeichert.
```

---

## 6.5 Salden und Ausgleichsvorschläge anzeigen

```mermaid
sequenceDiagram
    actor Mitglied
    participant Browser
    participant Controller as BalanceController
    participant BalanceSvc as BalanceApplicationService
    participant Repo as Repositories
    participant Domain as BalanceService / SettlementService
    participant DB as PostgreSQL

    Mitglied->>Browser: Saldenübersicht öffnen
    Browser->>Controller: GET /groups/{groupId}/balances
    Controller->>Controller: Authentifizierung prüfen
    Controller->>BalanceSvc: getBalances(groupId, currentUser)
    BalanceSvc->>Repo: Membership prüfen
    Repo->>DB: SELECT Membership
    DB-->>Repo: Zugriff erlaubt
    BalanceSvc->>Repo: Ausgaben und Kostenanteile laden
    Repo->>DB: SELECT Expenses, ExpenseShares, Members
    DB-->>Repo: Daten
    BalanceSvc->>Domain: calculateBalances(expenses, shares, members)
    Domain-->>BalanceSvc: Salden
    BalanceSvc->>Domain: calculateSettlements(balances)
    Domain-->>BalanceSvc: Ausgleichsvorschläge
    BalanceSvc-->>Controller: Salden + Vorschläge
    Controller-->>Browser: gerenderte Thymeleaf-Seite mit Salden und Vorschlägen
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Berechnung bei Anzeige | Salden werden aus aktuellen Ausgaben und Kostenanteilen berechnet. |
| Keine echten Zahlungen | Ausgleichsvorschläge sind nur Empfehlungen. |
| Debitor/Kreditor | Negativer Saldo = schuldet Geld; positiver Saldo = bekommt Geld. |
| Summe 0,00 | Die Summe aller Gruppensalden muss 0,00 ergeben. |
| Backend als Quelle | Thymeleaf stellt Ergebnisse nur dar; die verbindliche Berechnung erfolgt im Backend. |

Beispiel:

| Mitglied | Gezahlt | Eigener Anteil | Saldo | Rolle |
|---|---:|---:|---:|---|
| Anna | 30,00 EUR | 10,00 EUR | +20,00 EUR | Kreditor |
| Max | 0,00 EUR | 10,00 EUR | -10,00 EUR | Debitor |
| Lisa | 0,00 EUR | 10,00 EUR | -10,00 EUR | Debitor |

Ausgleichsvorschläge:

| Debitor | Kreditor | Betrag |
|---|---|---:|
| Max | Anna | 10,00 EUR |
| Lisa | Anna | 10,00 EUR |

---

## 6.6 PDF- oder CSV-Export erzeugen

```mermaid
sequenceDiagram
    actor Mitglied
    participant Browser
    participant Controller as ExportController
    participant ExportSvc as ExportApplicationService
    participant BalanceSvc as BalanceService / SettlementService
    participant Repo as Repositories
    participant Writer as PdfExportWriter / CsvExportWriter
    participant DB as PostgreSQL

    Mitglied->>Browser: Export anfordern
    Browser->>Controller: GET /groups/{groupId}/export?format=pdf
    Controller->>Controller: Authentifizierung und Format prüfen
    Controller->>ExportSvc: exportGroup(groupId, format, currentUser)
    ExportSvc->>Repo: Membership prüfen
    Repo->>DB: SELECT Membership
    DB-->>Repo: Zugriff erlaubt
    ExportSvc->>Repo: Gruppendaten, Mitglieder, Ausgaben laden
    Repo->>DB: SELECT Exportdaten
    DB-->>Repo: Daten
    ExportSvc->>BalanceSvc: Salden und Vorschläge berechnen
    BalanceSvc-->>ExportSvc: Salden + Ausgleichsvorschläge
    ExportSvc->>ExportSvc: ExportData aufbereiten
    ExportSvc->>Writer: Datei erzeugen
    Writer-->>ExportSvc: Byte-Stream / Dateiinhalt
    ExportSvc-->>Controller: Exportergebnis
    Controller-->>Browser: HTTP-Dateidownload
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Export als Datenfluss | PDF/CSV ist keine aktive externe Anwendung, sondern eine erzeugte Datei. |
| Zugriffsschutz | Nur Gruppenmitglieder dürfen einen Export erzeugen. |
| Aktuelle Berechnung | Exportierte Salden müssen zur aktuellen Saldenberechnung passen. |
| Keine sensiblen Daten | Passwort-Hashes, Sessions und technische Interna werden nie exportiert. |
| Formatabhängigkeit | PDF ist lesbar für Menschen, CSV für Tabellenkalkulation geeignet. |

Fehlerfälle:

| Fehler | Verhalten |
|---|---|
| Nutzer ist kein Gruppenmitglied | Export wird verweigert. |
| Format nicht `pdf` oder `csv` | Anfrage wird abgelehnt. |
| Exportbibliothek erzeugt Fehler | Verständliche Fehlermeldung; Daten bleiben unverändert. |
| Keine Ausgaben vorhanden | Export kann eine leere Übersicht mit Hinweis erzeugen oder Benutzer erhält einen Hinweis. |

---

## 6.7 Gemeinsame Laufzeitregeln

| Regel | Gilt für |
|---|---|
| Authentifizierung vor geschützten Aktionen | Gruppen, Mitglieder, Ausgaben, Salden, Export. |
| POST → Redirect → GET | Erfolgreiche schreibende Formularaktionen verwenden nach Möglichkeit einen Redirect. |
| Membership-Prüfung bei Gruppenbezug | Jede Anfrage mit `groupId`. |
| Validierung vor Speicherung | Registrierung, Gruppen, Mitglieder, Ausgaben, Export. |
| Keine Teilzustände | Expense und ExpenseShares werden zusammen gespeichert oder gar nicht. |
| Serverseitige Fachlogik | Verbindliche Berechnungen passieren in Services und Domain-Komponenten, nicht in Thymeleaf. |
| Verständliche Fehler | Technische Details werden nicht ungefiltert an Benutzer ausgegeben. |
| Keine sensiblen Daten nach außen | Exporte und Wechselkurs-API erhalten keine Passwörter, Sessions oder privaten Gruppendetails. |

