# 5 Bausteinsicht

Die Bausteinsicht zerlegt CampusSplit schrittweise in fachliche und technische Bausteine. Sie zeigt, welche Teile des Systems zusammenarbeiten und welche Verantwortung jeder Baustein besitzt.

Level 0 ist der Systemkontext aus [A03 — Context and Scope](A03-context-and-scope.md): CampusSplit als Blackbox zwischen Browser, Datenbank, Exportdatei und Wechselkursdienst.

Level 1 öffnet diese Blackbox und zeigt die wichtigsten Architekturbausteine. Level 2 verfeinert die für die Umsetzung wichtigsten Bausteine. Level 3 öffnet den Wechselkursadapter, weil dort die externe API-Anbindung architektonisch relevant ist.

---

## 5.1 Whitebox Gesamtsystem

```mermaid
flowchart LR
    UI[Thymeleaf Views / Frontend]
    CTRL[Spring Boot Controller]
    APP[Application Services]
    DOMAIN[Domain Model und Money Logic]
    PERSIST[Persistence]
    EXPORT[Export Module]
    FX[Currency Integration]
    AUTH[Google OAuth2 Integration]
    DB[(PostgreSQL)]
    FILE[PDF/CSV Datei]
    FRANK[Frankfurter API]
    GOOGLE[Google OAuth2 Provider]

    UI -->|Methodenaufruf / Form Submit| CTRL
    CTRL --> APP
    CTRL -->|Auth / Session| AUTH
    APP --> DOMAIN
    APP --> PERSIST
    APP --> EXPORT
    APP --> FX
    PERSIST --> DB
    EXPORT --> FILE
    FX -->|HTTPS/JSON| FRANK
    AUTH -->|HTTPS/OAuth2| GOOGLE
```

| Whitebox | Inhalt |
|---|---|
| Whitebox von | CampusSplit — die System-Blackbox aus Kapitel 3. |
| Enthaltene Bausteine | Thymeleaf Views, Spring Boot Controller, Application Services, Domain Model/Money Logic, Persistence, Export Module, Currency Integration, Google OAuth2 Integration. |
| Lokale Beziehungen | Views lösen Controller-Aktionen aus; Controller verwalten Auth und delegieren an Services; Services nutzen Domainlogik, Persistenz, Export und Wechselkursadapter. |
| Designentscheidung | Ein Deployable (Spring Boot mit Thymeleaf); Fachlogik liegt im Backend; keine separate REST-API-Schicht notwendig; externe Dienste sind isoliert angebunden.    |
| Verworfene Alternativen | Geldlogik im Frontend, Bank-/Payment-Integration, monolithische Vermischung von UI und Fachlogik. |
| Referenzen | [A04](A04-solution-strategy.md), [A06](A06-runtime-view.md), [D1](../Spezifikation/D1_Datenmodell.md), [D2](../Spezifikation/D2_Datentypenverzeichnis.md), [F3](../Spezifikation/F3-anwendungsfunktionen.md), [N2](../Spezifikation/N2_Querschnittskonzepte.md). |
| Offene Punkte | Konkrete Framework-Konfiguration, Security-Mechanismus und Bibliotheken werden bei der Implementierung finalisiert. |

### Enthaltene Bausteine

| Nr. | Baustein | Geplante Code-Artefakte | Verantwortung |
|---|---|---|---|
| [5.1.1](#511-blackbox-thymeleaf-views--frontend) | Thymeleaf Views / Frontend | `src/main/resources/templates/`, `static/` | Dialoge als HTML-Templates rendern, Formulardaten erfassen, Ergebnisse darstellen. |
| [5.1.2](#512-blackbox-spring-boot-controller) | Spring Boot Controller | `.../controller/`, `.../dto/` | HTTP-Anfragen entgegennehmen, Formular-Inputs validieren, View-Namen liefern, Model befüllen. |
| [5.1.3](#513-blackbox-application-services) | Application Services | `.../service/` | Use-Case-nahe Abläufe koordinieren. |
| [5.1.4](#514-blackbox-domain-model-und-money-logic) | Domain Model und Money Logic | `.../domain/`, `.../money/`, `.../balance/` | Fachliche Regeln, Kostenaufteilung, Salden, Ausgleichsvorschläge. |
| [5.1.5](#515-blackbox-persistence) | Persistence | `.../entity/`, `.../repository/`, `backend/src/main/resources/db/` | Dauerhafte Speicherung über PostgreSQL. |
| [5.1.6](#516-blackbox-export-module) | Export Module | `.../export/` | PDF- und CSV-Export erzeugen. |
| [5.1.7](#517-blackbox-currency-integration) | Currency Integration | `.../currency/` | Frankfurter API anbinden und Wechselkurse kapseln. |
| [5.1.8](#518-blackbox-google-oauth2-integration) | Google OAuth2 Integration | `.../security/`, `.../auth/` | Authentifizierung und Login-Abwicklung über Google OAuth2 bereitstellen. |

### Lokale Beziehungen

| Beziehung | Vertrag |
|---|---|
| Thymeleaf Views → Controller | Formularübermittlungen (POST) und Seitenaufrufe (GET) innerhalb desselben Spring Boot Deployables. |
| Controller → Application Services | Controller validieren Eingaben und delegieren an Use-Case-Services. |
| Controller → Google OAuth2 | Spring Security wickelt den Authentifizierungs-Handshake mit Google ab. |
| Application Services → Domain Model | Services verwenden Fachlogik für Kostenanteile, Salden und Ausgleichsvorschläge. |
| Application Services → Persistence | Services laden und speichern Entitäten über Spring Data JPA Repositories. |
| Application Services → Export Module | Export wird auf Benutzeranforderung im Backend erzeugt und als Stream/Download bereitgestellt. |
| Application Services → Currency Integration | Wechselkurs wird nur bei Fremdwährungsausgaben benötigt. |
| Currency Integration → Frankfurter API | Ausgehender HTTPS/JSON-Aufruf ohne personenbezogene Daten. |

---

## 5.1.1 Blackbox Thymeleaf Views / Frontend

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Umsetzung der UI-Dialoge aus B1 über serverseitig gerenderte HTML-Templates. Zeigt Gruppen, Ausgaben, Salden, Fehler und Exporteinstiege an. |
| Bereitgestellte Schnittstelle | Grafische Benutzeroberfläche (HTML/CSS) für Gast, Nutzer, Gruppenmitglied und Administrator im Browser. |
| Benötigte Schnittstellen | Spring Boot Controller / Spring Model; keine direkten Datenbank- oder Drittanbieterzugriffe aus dem Browser heraus. |
| Qualität | Responsive Bedienung, verständliche Fehlermeldungen, serverseitig geschützte rendering-Abläufe. |
| Abhängigkeiten | Moderner Webbrowser, HTML5, CSS3, Thymeleaf Layout Dialect.    |
| Geplante Code-Artefakte | `src/main/resources/templates/pages/`, `templates/fragments/`, `templates/layouts/`, `static/css/`. |
| Erfüllte Anforderungen | B1-Dialoge, N1-Bedienbarkeit, N2-Fehlerdarstellung. |
| Variabilität | Neue Dialoge können als neue Thymeleaf-Templates/Fragmente ergänzt werden. |
| Tests | View-Integrationstests mit @WebMvcTest oder Spring Security Test tooling; manuelle UI-Tests. |
| Offene Punkte | Konkretes CSS-Framework (z. B. Tailwind oder Bootstrap) final auswählen. |
| Verfeinert in | [5.2.1](#521-whitebox-thymeleaf-views--frontend). |

---

## 5.1.2 Blackbox Spring Boot Controller

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Nimmt HTTP-Anfragen entgegen, prüft Formular-/Requestdaten, ermittelt den OAuth2-Benutzerkontext und ruft Application Services auf. |
| Bereitgestellte Schnittstelle | MVC-Controller für Auth, Groups, Members, Expenses, Balances, Settlements, Export und Currency. |
| Benötigte Schnittstellen | Application Services, Validierung via BindingResult, Spring Security Kontext. |
| Qualität | Verständliche Fehlermeldungen direkt im HTML-Formular, verlässlicher Zugriffsschutz auf Routen. |
| Abhängigkeiten | Spring Web MVC, Spring Validation, Spring Security OAuth2 Client. |
| Geplante Code-Artefakte | `controller/`, `dto/form/`, `exception/`. |
| Erfüllte Anforderungen | F2-Use-Cases, B1-Aktionen, N2-Validierung und Fehlerbehandlung. |
| Variabilität | Neue Use Cases erhalten eigene Controller-Klassen oder Methoden. |
| Tests | Controller-Tests mit MockMvc und simuliertem OAuth2-User. |
| Offene Punkte | Rollenmodell für Administrator-Sonderfunktionen mit OAuth2 abgleichen. |
| Verfeinert in | [5.2.2](#522-whitebox-backend). |

---

## 5.1.3 Blackbox Application Services

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Koordiniert fachliche Abläufe, z. B. Gruppe erstellen, Mitglied hinzufügen, Ausgabe speichern, Salden anzeigen, Export erzeugen. |
| Bereitgestellte Schnittstelle | Methoden für Use-Case-nahe Operationen. |
| Benötigte Schnittstellen | Domainlogik, Repositories, Export Module, Currency Integration. |
| Qualität | Transaktional, nachvollziehbar, testbar. |
| Abhängigkeiten | Spring Services (@Service), @Transactional, Repositories. |
| Geplante Code-Artefakte | `GroupService`, `MembershipService`, `ExpenseService`, `BalanceApplicationService`, `ExportApplicationService`. |
| Erfüllte Anforderungen | UC-04 bis UC-12, F3-Anwendungsfunktionen. |
| Variabilität | Neue fachliche Abläufe können als weitere Services ergänzt werden. |
| Tests | Service-Tests mit Testdaten für Gruppen, Ausgaben und Salden. |
| Offene Punkte | Granularität einzelner Services während Implementierung prüfen. |
| Verfeinert in | [5.2.2](#522-whitebox-backend). |

---

## 5.1.4 Blackbox Domain Model und Money Logic

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Bildet fachliche Regeln ab: MoneyAmount, Currency, ExchangeRate, SplitMethod, Kostenanteile, Salden, Debitor/Kreditor und Ausgleichsvorschläge. |
| Bereitgestellte Schnittstelle | Rechenfunktionen und fachliche Modelle für Split, Balance und Settlement. |
| Benötigte Schnittstellen | Keine externen Systeme; arbeitet mit fachlichen Eingaben. |
| Qualität | Centgenau, deterministisch, unabhängig von UI und Datenbank testbar. |
| Abhängigkeiten | Java `BigDecimal` oder centbasierte Integer-Darstellung (`MoneyAmountDT`); keine Gleitkommazahlen. |
| Geplante Code-Artefakte | `Money`, `ExchangeRate`, `SplitService`, `BalanceService`, `SettlementService`. |
| Erfüllte Anforderungen | F3, D1, D2, N2-Geldbetragsverarbeitung. |
| Variabilität | Weitere Aufteilungsarten können ergänzt werden, ohne UI und Persistenz komplett umzubauen. |
| Tests | Unit-Tests für Rundung, Equal Split, Custom Split, Salden und Ausgleichsvorschläge. |
| Offene Punkte | Exakte technische Umsetzung von `MoneyAmountDT` festlegen. |
| Verfeinert in | [5.2.3](#523-whitebox-domain-model-und-money-logic). |

---

## 5.1.5 Blackbox Persistence

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Speichert Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile (`ExpenseShare`), Kategorien und Wechselkursinformationen dauerhaft. |
| Bereitgestellte Schnittstelle | Repository-API für Application Services. |
| Benötigte Schnittstellen | PostgreSQL-Datenbank. |
| Qualität | Konsistente Datenhaltung, keine unvollständigen fachlichen Zustände. |
| Abhängigkeiten | Spring Data JPA, PostgreSQL, Datenbankverbindung. |
| Geplante Code-Artefakte | `entity`, `repository`, Migrations-/Schema-Dateien. |
| Erfüllte Anforderungen | D1-Datenmodell, S3-Inbetriebnahme, N1-Datenkonsistenz. |
| Variabilität | Schemaänderungen erfolgen kontrolliert über Migrationen. |
| Tests | Repository-Tests und Integrationstests gegen Testdatenbank. |
| Offene Punkte |Flyway-Migrationsskripte für initiale Entitäten erstellen. |
| Verfeinert in | Nicht weiter verfeinert; Datenmodell ist in D1/D2 fachlich beschrieben. |

---

## 5.1.6 Blackbox Export Module

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Erzeugt PDF- und CSV-Dateien aus Gruppendaten, Ausgaben, Kostenanteilen, Salden und Ausgleichsvorschlägen. |
| Bereitgestellte Schnittstelle | Exportfunktion für PDF und CSV. |
| Benötigte Schnittstellen | Exportdaten aus Application Services und Domainlogik. |
| Qualität | Keine sensiblen Daten im Export, Beträge korrekt formatiert, Export entspricht aktueller Saldenberechnung. |
| Abhängigkeiten | Bibliothek für PDF-Erzeugung und CSV-Ausgabe. |
| Geplante Code-Artefakte | `ExportService`, `PdfExportWriter`, `CsvExportWriter`, `ExportData`. |
| Erfüllte Anforderungen | B3, UC-12, N2-Exportsicherheit. |
| Variabilität | Weitere Exportformate können später ergänzt werden. |
| Tests | Exportdaten-Tests, CSV-Strukturtests, ggf. PDF-Smoke-Test. |
| Offene Punkte | Konkrete PDF-Bibliothek final auswählen. |
| Verfeinert in | [5.2.4](#524-whitebox-export-und-integration). |

---

## 5.1.7 Blackbox Currency Integration

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Ruft Wechselkurse beim Frankfurter Wechselkursdienst ab und kapselt technische Details der externen REST-API. |
| Bereitgestellte Schnittstelle | Methode zur Ermittlung eines Wechselkurses für Ausgangswährung, Zielwährung und Datum. |
| Benötigte Schnittstellen | HTTPS-Zugriff auf Frankfurter API. |
| Qualität | Keine personenbezogenen Daten nach außen, keine erfundenen Kurse, kontrollierte Fehlerbehandlung. |
| Abhängigkeiten | HTTP-Client des Backends, Erreichbarkeit des Wechselkursdienstes. |
| Geplante Code-Artefakte | `CurrencyRateClient`, `FrankfurterCurrencyRateClient`, `ExchangeRateResponse`. |
| Erfüllte Anforderungen | S1, D2 ExchangeRateDT, N2 Fehlerbehandlung. |
| Variabilität | Wechselkursanbieter kann später durch anderen Adapter ersetzt werden. |
| Tests | Adaptertests mit Mock-HTTP-Server oder Testdouble. |
| Offene Punkte | Timeout- und Cache-Strategie final festlegen. |
| Verfeinert in | [5.3.1](#531-whitebox-currency-integration). |

---

## 5.1.8 Blackbox Google OAuth2 Integration

| Blackbox | Inhalt |
|---|---|
| Zweck / Verantwortung | Übernimmt die Authentifizierung der Benutzer über den Google OAuth2 Dienst und verknüpft OAuth2-Profile mit lokalen Benutzerdaten. |
| Bereitgestellte Schnittstelle | Login- und Callback-Endpunkte für Google-Authentifizierung. |
| Benötigte Schnittstellen | Google OAuth2 Identity Provider via HTTPS. |
| Qualität | Sichere Abwicklung des OAuth2-Flows, Session-Management über Spring Security. |
| Abhängigkeiten | `spring-boot-starter-oauth2-client`, Google Cloud Console Client credentials. |
| Geplante Code-Artefakte | `SecurityConfig`, `CustomOAuth2UserService`, `OAuth2UserBinding`. |
| Erfüllte Anforderungen | Externe API/Auth, N2-Authentifizierung. |
| Variabilität | Weitere OAuth2-Provider könnten später ergänzt werden. |
| Tests | Security-Tests für geschützte Routen mit Mock-OAuth2-User. |
| Offene Punkte | OAuth2 Client-ID und Secret über Umgebungsvariablen bereitstellen. |
| Verfeinert in | Nicht weiter verfeinert; wird durch Standard-Spring-Security abgedeckt. |

---

## 5.2 Level 2

Level 2 öffnet die wichtigsten Level-1-Bausteine. Die Verfeinerung endet dort, wo weitere Details nur noch konkrete Implementierung wiederholen würden.

---

## 5.2.1 Whitebox Thymeleaf Views / Frontend

```mermaid
flowchart TD
    LAYOUTS[Layout Templates]
    PAGES[Page Templates]
    FRAGMENTS[UI Fragments]
    FORMS[Form Binding Models]
    ASSETS[Static Assets CSS/JS]

    LAYOUTS --> PAGES
    PAGES --> FRAGMENTS
    PAGES --> FORMS
    PAGES --> ASSETS
```

| Baustein | Verantwortung |
|---|---|
| `layouts/` | Basis-HTML-Gerüst mit Navigation, Header, Footer und Flash-Message-Bereich. |
| `pages/` | Ansichten für Dashboard, Gruppendetails, Ausgaben-Formulare, Saldenübersicht und Exporteinstiege. |
| `fragments/` | Wiederverwendbare UI-Komponenten (z. B. Formular-Input-Fehler, Tabellenzeilen, Saldenkarten). |
| Form Binding Models | Objekte zur Bindung von HTML-Formulareingaben an Spring-Controller (`@ModelAttribute`) |
| Static Assets | CSS-Styling und minimale JavaScript-Hilfsfunktionen zur Unterstützung der Bedienbarkeit. |

Lokale Beziehungen:

| Beziehung | Vertrag |
|---|---|
| Pages → Layouts | Seiten nutzen `thymeleaf-layout-dialect` zur Einbettung. |
| Pages → Fragments | Seiten binden Fragmente für konsistente UI-Elemente ein. |
| Pages → Controller | Formulare senden Daten per POST an Controller-Aktionen. |

---

## 5.2.2 Whitebox Backend

```mermaid
flowchart TD
    CTRL[Controller]
    FORM[Form / Model DTOs]
    AUTH[Security / OAuth2]
    APP[Application Services]
    DOMAIN[Domain Services]
    REPO[Repositories]
    ENTITY[Entities]
    EXC[Global Exception Handler]

    CTRL --> FORM
    CTRL --> AUTH
    CTRL --> APP
    APP --> DOMAIN
    APP --> REPO
    REPO --> ENTITY
    CTRL --> EXC
    APP --> EXC
```

| Baustein | Verantwortung |
|---|---|
| Controller | Web-Endpunkte zur Abwicklung von HTML-Anfragen, Validierung und Model-Präparation. |
| Form / Model DTOs | Struktur für Formulareingaben und Datenbereitstellung im Thymeleaf Model.    |
| Security / OAuth2 | Absicherung der Pfade, Session-Verwaltung und Extraktion des aktuellen Nutzers. |
| Application Services | Use-Case-nahe Abläufe und Transaktionssteuerung. |
| Domain Services | Rechenlogik für Kostenanteile, Salden und Ausgleich. |
| Repositories | Spring Data JPA Datenzugriffsschicht. |
| Entities | Persistente Abbildung des Datenmodells (User, Group, Expense, ExpenseShare etc.). |
| Global Exception Handler | `@ControllerAdvice` zur Anzeige verständlicher Fehlerseiten bei Systemfehlern. |

Lokale Beziehungen:

| Beziehung | Vertrag |
|---|---|
| Controller → Form DTO | Eingaben werden typisiert gebunden und per `@Valid` geprüft. |
| Controller → Application Service | Controller enthalten keine Fachlogik, sondern steuern nur den Navigationsfluss. |
| Application Service → Repository | Laden und Speichern erfolgt über JPA Repositories. |
| Application Service → Domain Service | Fachliche Berechnung bleibt unabhängig von Web und UI. |

---

## 5.2.3 Whitebox Domain Model und Money Logic

```mermaid
flowchart LR
    MONEY[MoneyAmountDT]
    SPLIT[SplitService]
    BALANCE[BalanceService]
    SETTLEMENT[SettlementService]
    RATE[ExchangeRate]

    SPLIT --> MONEY
    BALANCE --> MONEY
    SETTLEMENT --> MONEY
    RATE --> MONEY
    BALANCE --> SETTLEMENT
```

| Baustein | Schnittstelle | Verantwortung |
|---|---|---|
| `MoneyAmountDT` | Betrag + Währung | Fachliche Darstellung eines Geldbetrags. |
| `ExchangeRate` | fromCurrency, toCurrency, rate, date | Wechselkurswert für Fremdwährungsausgaben. |
| `SplitService` | `calculateShares(...)` | Berechnet Kostenanteile für `EQUAL` und `CUSTOM_AMOUNT`. |
| `BalanceService` | `calculateBalances(groupId)` | Berechnet Salden pro Gruppenmitglied. |
| `SettlementService` | `calculateSettlements(balances)` | Erzeugt Ausgleichsvorschläge zwischen Debitoren und Kreditoren. |

Wichtige Regeln:

| Regel | Umsetzung |
|---|---|
| Keine Gleitkommazahlen für Geld | `BigDecimal` oder centbasierte Integer-Darstellung(`MoneyAmountDT`). |
| EQUAL Split muss exakt aufgehen | Rundungsreste werden deterministisch verteilt. |
| CUSTOM_AMOUNT muss Summe treffen | Speichern nur, wenn Summe exakt dem Abrechnungsbetrag entspricht. |
| Salden müssen auf 0 summieren | Unit-Test für jede zentrale Berechnungsvariante. |
| Debitor/Kreditor eindeutig | Negativer Saldo = Debitor, positiver Saldo = Kreditor. |

---

## 5.2.4 Whitebox Export und Integration

```mermaid
flowchart TD
    EXPORTSERVICE[ExportApplicationService]
    EXPORTDATA[ExportDataAssembler]
    PDF[PdfExportWriter]
    CSV[CsvExportWriter]
    CURRENCY[CurrencyRateClient]

    EXPORTSERVICE --> EXPORTDATA
    EXPORTSERVICE --> PDF
    EXPORTSERVICE --> CSV
    CURRENCY --> FRANK[Frankfurter API]
```

| Baustein | Verantwortung |
|---|---|
| `ExportApplicationService` | Koordiniert Exporterzeugung für PDF oder CSV. |
| `ExportDataAssembler` | Stellt Gruppendaten, Ausgaben, Anteile, Salden und Vorschläge zusammen. |
| `PdfExportWriter` | Erzeugt lesbare PDF-Datei für den HTTP-Response-Download. |
| `CsvExportWriter` | Erzeugt tabellarische CSV-Datei für den HTTP-Response-Download |
| `CurrencyRateClient` | Liefert Wechselkursdaten für Fremdwährungsausgaben. |

---

## 5.3 Level 3

Level 3 öffnet den Wechselkursadapter, weil er die wichtigste externe technische Schnittstelle von CampusSplit darstellt.

## 5.3.1 Whitebox Currency Integration

```mermaid
flowchart LR
    SERVICE[ExpenseService]
    PORT[CurrencyRatePort]
    CLIENT[FrankfurterCurrencyRateClient]
    HTTP[HTTP Client]
    MAP[Response Mapper]
    ERR[Error Mapper]
    API[Frankfurter API]

    SERVICE --> PORT
    PORT --> CLIENT
    CLIENT --> HTTP
    HTTP --> API
    API --> HTTP
    HTTP --> MAP
    HTTP --> ERR
```

| Baustein | Realisierung | Vertrag |
|---|---|---|
| CurrencyRatePort | Interface | Fachliche Schnittstelle: `getRate(from, to, date)`. |
| FrankfurterCurrencyRateClient | Adapter | Kennt URL-Struktur, Requestparameter und Antwortformat der Frankfurter API. |
| HTTP Client | Spring RestClient oder WebClient | Führt synchronen HTTPS-Aufruf aus. |
| Response Mapper | Mapping-Komponente | Wandelt JSON-Antwort in `ExchangeRate` um. |
| Error Mapper | Fehlerkomponente | Wandelt technische Fehler in fachlich verständliche Fehler um. |
