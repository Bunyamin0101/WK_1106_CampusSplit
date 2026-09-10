# 6 Laufzeitsicht

Die Laufzeitsicht zeigt, wie die Bausteine aus [A05 — Building Block View](A05-building-block-view.md) in wichtigen Szenarien zusammenarbeiten. Es werden nicht alle CRUD-Abläufe vollständig wiederholt, sondern nur die Abläufe, die architektonisch besonders relevant sind.

Ausgewählt wurden Szenarien, die zentrale Architekturentscheidungen sichtbar machen: Authentifizierung, Gruppenzugriff, Kostenaufteilung, Fremdwährungsumrechnung, Saldenberechnung und Export.

---

## 6.0 Übersicht der Szenarien

| Szenario | Use Case | Warum architektonisch relevant? |
|---|---|---|
| [6.1](#61-registrierung-und-anmeldung) Registrierung und Anmeldung | UC-01, UC-02 | Öffentlicher Zugriff, Passwortschutz, Start einer Sitzung. |
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
    participant UI as React Frontend
    participant API as AuthController
    participant Auth as AuthService
    participant Repo as UserRepository
    participant DB as PostgreSQL

    Gast->>UI: Registrierungsdaten eingeben
    UI->>API: POST /api/auth/register
    API->>API: Request validieren
    API->>Auth: register(name, email, password)
    Auth->>Repo: existsByEmail(email)
    Repo->>DB: E-Mail prüfen
    DB-->>Repo: Ergebnis
    Auth->>Auth: Passwort hashen
    Auth->>Repo: User speichern
    Repo->>DB: INSERT User
    DB-->>Repo: gespeicherter User
    Repo-->>Auth: User
    Auth-->>API: Registrierung erfolgreich
    API-->>UI: 201 Created
    UI-->>Gast: Weiter zur Anmeldung

    Gast->>UI: E-Mail und Passwort eingeben
    UI->>API: POST /api/auth/login
    API->>Auth: authenticate(email, password)
    Auth->>Repo: findByEmail(email)
    Repo->>DB: User laden
    DB-->>Repo: User
    Auth->>Auth: Passwort prüfen
    Auth-->>API: Benutzer authentifiziert
    API-->>UI: Session/Token + Benutzerdaten
    UI-->>Gast: Dashboard anzeigen
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Öffentliche Endpunkte | Registrierung und Anmeldung sind ohne vorherige Sitzung erreichbar. |
| Passwortschutz | Klartextpasswörter werden nie gespeichert; nur Hashwerte liegen in der Datenbank. |
| Generische Loginfehler | Bei falschen Zugangsdaten wird nicht verraten, ob E-Mail oder Passwort falsch war. |
| Startpunkt | Nach erfolgreicher Anmeldung ist das Dashboard der Einstieg in die Anwendung. |

Fehlerfälle:

| Fehler | Verhalten |
|---|---|
| E-Mail ungültig | 400/422 mit verständlicher Meldung. |
| E-Mail bereits vergeben | Registrierung wird abgelehnt. |
| Passwort falsch | Allgemeine Fehlermeldung. |
| Datenbank nicht erreichbar | Technischer Fehler, kein Login und keine halbe Registrierung. |

---

## 6.2 Gruppe erstellen und Mitglied hinzufügen

```mermaid
sequenceDiagram
    actor Nutzer
    participant UI as React Frontend
    participant API as GroupController / MembershipController
    participant GroupSvc as GroupService
    participant MemberSvc as MembershipService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Nutzer->>UI: Neue Gruppe anlegen
    UI->>API: POST /api/groups
    API->>API: Authentifizierung prüfen
    API->>API: Gruppenname und Währung validieren
    API->>GroupSvc: createGroup(currentUser, request)
    GroupSvc->>Repo: Group speichern
    Repo->>DB: INSERT Group
    GroupSvc->>Repo: Membership ADMIN speichern
    Repo->>DB: INSERT Membership
    DB-->>Repo: gespeicherte Daten
    GroupSvc-->>API: Gruppe mit ADMIN-Mitgliedschaft
    API-->>UI: 201 Created + Gruppe
    UI-->>Nutzer: Gruppendetail anzeigen

    Nutzer->>UI: Mitglied per E-Mail hinzufügen
    UI->>API: POST /api/groups/{groupId}/members
    API->>MemberSvc: addMember(groupId, email, currentUser)
    MemberSvc->>Repo: aktuelle Membership prüfen
    Repo->>DB: Rolle des aktuellen Nutzers laden
    DB-->>Repo: ADMIN oder MEMBER
    MemberSvc->>MemberSvc: ADMIN-Recht prüfen
    MemberSvc->>Repo: User anhand E-Mail suchen
    Repo->>DB: SELECT User
    MemberSvc->>Repo: neue Membership speichern
    Repo->>DB: INSERT Membership MEMBER
    MemberSvc-->>API: Mitglied hinzugefügt
    API-->>UI: 201 Created
    UI-->>Nutzer: Mitgliederliste aktualisieren
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
    participant UI as React Frontend
    participant API as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant Split as SplitService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Mitglied->>UI: Ausgabe erfassen
    UI->>API: POST /api/groups/{groupId}/expenses
    API->>API: Authentifizierung prüfen
    API->>API: Requestdaten validieren
    API->>ExpenseSvc: createExpense(groupId, request, currentUser)
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
    ExpenseSvc-->>API: gespeicherte Ausgabe
    API-->>UI: 201 Created
    UI-->>Mitglied: Ausgabe und aktualisierte Gruppe anzeigen
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Backend entscheidet verbindlich | Frontend sendet Eingaben; Backend prüft und berechnet verbindlich. |
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
    participant UI as React Frontend
    participant API as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant FX as CurrencyRateClient
    participant Frank as Frankfurter API
    participant Split as SplitService
    participant Repo as Repositories
    participant DB as PostgreSQL

    Mitglied->>UI: Ausgabe z. B. 30 USD erfassen
    UI->>API: POST /api/groups/{groupId}/expenses
    API->>ExpenseSvc: createExpense(...)
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
    ExpenseSvc-->>API: gespeicherte Ausgabe
    API-->>UI: 201 Created
    UI-->>Mitglied: Ausgabe mit Kurs und Abrechnungsbetrag anzeigen
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
    participant UI as React Frontend
    participant API as ExpenseController
    participant ExpenseSvc as ExpenseService
    participant FX as CurrencyRateClient
    participant Frank as Frankfurter API
    participant DB as PostgreSQL

    Mitglied->>UI: Fremdwährungsausgabe speichern
    UI->>API: POST /api/groups/{groupId}/expenses
    API->>ExpenseSvc: createExpense(...)
    ExpenseSvc->>FX: getRate(...)
    FX->>Frank: HTTPS GET
    Frank--xFX: Fehler / Timeout / ungültige Antwort
    FX--xExpenseSvc: CurrencyRateException
    ExpenseSvc--xAPI: fachlicher Fehler
    API-->>UI: 422 oder 503 mit verständlicher Meldung
    UI-->>Mitglied: Kurs konnte nicht ermittelt werden
    Note over DB: Es wird keine unvollständige Ausgabe gespeichert.
```

---

## 6.5 Salden und Ausgleichsvorschläge anzeigen

```mermaid
sequenceDiagram
    actor Mitglied
    participant UI as React Frontend
    participant API as BalanceController
    participant BalanceSvc as BalanceApplicationService
    participant Repo as Repositories
    participant Domain as BalanceService / SettlementService
    participant DB as PostgreSQL

    Mitglied->>UI: Saldenübersicht öffnen
    UI->>API: GET /api/groups/{groupId}/balances
    API->>API: Authentifizierung prüfen
    API->>BalanceSvc: getBalances(groupId, currentUser)
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
    BalanceSvc-->>API: Salden + Vorschläge
    API-->>UI: 200 OK
    UI-->>Mitglied: Debitoren, Kreditoren und Vorschläge anzeigen
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Berechnung bei Anzeige | Salden werden aus aktuellen Ausgaben und Kostenanteilen berechnet. |
| Keine echten Zahlungen | Ausgleichsvorschläge sind nur Empfehlungen. |
| Debitor/Kreditor | Negativer Saldo = schuldet Geld; positiver Saldo = bekommt Geld. |
| Summe 0,00 | Die Summe aller Gruppensalden muss 0,00 ergeben. |
| Backend als Quelle | Frontend stellt nur dar, berechnet aber nicht verbindlich. |

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
    participant UI as React Frontend
    participant API as ExportController
    participant ExportSvc as ExportApplicationService
    participant BalanceSvc as BalanceService / SettlementService
    participant Repo as Repositories
    participant Writer as PdfExportWriter / CsvExportWriter
    participant DB as PostgreSQL

    Mitglied->>UI: Export anfordern
    UI->>API: GET /api/groups/{groupId}/export?format=pdf
    API->>API: Authentifizierung und Format prüfen
    API->>ExportSvc: exportGroup(groupId, format, currentUser)
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
    ExportSvc-->>API: Exportergebnis
    API-->>UI: Datei-Download
    UI-->>Mitglied: PDF/CSV herunterladen
```

Wichtige Aspekte:

| Aspekt | Bedeutung |
|---|---|
| Export als Datenfluss | PDF/CSV ist keine aktive externe Anwendung, sondern eine erzeugte Datei. |
| Zugriffsschutz | Nur Gruppenmitglieder dürfen einen Export erzeugen. |
| Aktuelle Berechnung | Exportierte Salden müssen zur aktuellen Saldenberechnung passen. |
| Keine sensiblen Daten | Passwort-Hashes, Tokens, technische Interna und Sitzungsdaten werden nie exportiert. |
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
| Membership-Prüfung bei Gruppenbezug | Jede Anfrage mit `groupId`. |
| Validierung vor Speicherung | Registrierung, Gruppen, Mitglieder, Ausgaben, Export. |
| Keine Teilzustände | Expense und ExpenseShares werden zusammen gespeichert oder gar nicht. |
| Keine Frontend-Fachhoheit | Verbindliche Berechnungen passieren im Backend. |
| Verständliche Fehler | Technische Details werden nicht ungefiltert an Benutzer ausgegeben. |
| Keine sensiblen Daten nach außen | Exporte und Wechselkurs-API erhalten keine Passwörter, Tokens oder privaten Gruppendetails. |

