# 10 Glossar

Dieses Glossar definiert Begriffe, die in der Architekturdokumentation von CampusSplit verwendet werden. Fachliche Domänenbegriffe wie Ausgabe, Gruppe, Mitglied, Saldo, Debitor, Kreditor, Kostenanteil oder Ausgleichsvorschlag sind verbindlich im Spezifikationsglossar [`E2 — Glossar`](../Spezifikation/E2_Glossar.md) beschrieben. Dieses Kapitel ergänzt vor allem Architektur- und Implementierungsbegriffe.

---

| Begriff | Definition |
|---|---|
| **Adapter** | Architekturbaustein, der eine externe Schnittstelle kapselt. Bei CampusSplit betrifft das vor allem den Wechselkursadapter zur Frankfurter API. |
| **Application Service** | Service-Schicht, die Use-Case-nahe Abläufe koordiniert, z. B. Ausgabe erfassen, Salden berechnen oder Export erzeugen. |
| **Backend** | Serverseitiger Teil von CampusSplit. Verantwortlich für REST-API, Validierung, Authentifizierung, Autorisierung, Fachlogik, Persistenz, Export und Wechselkursanbindung. |
| **Controller** | Teil des Backends, der HTTP-Anfragen entgegennimmt, validiert und an passende Services weiterleitet. |
| **DTO** | Data Transfer Object. Objekt für den Datenaustausch zwischen Frontend und Backend oder zwischen Services, ohne direkt die Datenbankstruktur offenzulegen. |
| **Entity** | Persistierbares Domänenobjekt, das über JPA auf eine Datenbanktabelle abgebildet wird, z. B. `UserEntity`, `GroupEntity` oder `ExpenseEntity`. |
| **Frontend** | Browserseitiger Teil von CampusSplit. Verantwortlich für Dialoge, Formulare, Navigation und Anzeige der vom Backend gelieferten Ergebnisse. |
| **HTTP-only Cookie** | Cookie, auf den JavaScript nicht direkt zugreifen kann. Wird für eine sichere Session-basierte Authentifizierung empfohlen. |
| **JPA** | Java Persistence API. Standard zur Abbildung von Java-Objekten auf relationale Datenbanktabellen. In CampusSplit über Spring Data JPA vorgesehen. |
| **Repository** | Persistenzschnittstelle, über die Services fachliche Daten lesen und speichern, ohne SQL direkt in der Fachlogik zu verteilen. |
| **REST API** | HTTP-basierte Schnittstelle zwischen Frontend und Backend. Das Frontend ruft über diese Schnittstelle Funktionen wie Login, Gruppen, Ausgaben, Salden und Export auf. |
| **Spring Boot** | Java-Framework zur Umsetzung des CampusSplit-Backends. Unterstützt REST-Endpunkte, Validierung, Security, Datenbankzugriff und Tests. |
| **Spring Security** | Sicherheitsframework im Backend für Authentifizierung, Sitzungsschutz und Zugriffskontrolle. |
| **Transaktion** | Zusammengehöriger Speichervorgang, der vollständig erfolgreich sein muss oder komplett zurückgerollt wird. Wichtig beim Speichern einer Ausgabe mit mehreren Kostenanteilen. |
| **Value Object** | Objekt ohne eigene Identität, das einen fachlichen Wert beschreibt. Beispiele sind Geldbetrag, Währungscode oder Wechselkurs. |
| **Money / MoneyAmount** | Architekturbegriff für die technische Umsetzung von `MoneyAmountDT`: Betrag plus Währung, vorzugsweise centgenau gespeichert. |
| **Currency Adapter** | Backend-Komponente, die Wechselkurse über die Frankfurter API abruft und technische API-Details vor der restlichen Anwendung verbirgt. |
| **ExchangeRate** | Architekturbegriff für den verwendeten Wechselkurs zwischen Originalwährung und Gruppenwährung. Entspricht fachlich `ExchangeRateDT`. |
| **Export Writer** | Komponente, die vorbereitete Exportdaten in ein konkretes Ausgabeformat schreibt, z. B. PDF oder CSV. |
| **ExportDataAssembler** | Komponente, die Gruppen-, Ausgaben-, Kostenanteils-, Salden- und Ausgleichsdaten für den Export fachlich zusammenstellt. |
| **SplitService** | Service zur Berechnung der Kostenanteile einer Ausgabe. |
| **BalanceService** | Service zur Berechnung der Salden je Gruppenmitglied. |
| **SettlementService** | Service zur Berechnung von Ausgleichsvorschlägen zwischen Debitoren und Kreditoren. |
| **MembershipGuard** | Architekturbegriff für eine zentrale Prüfung, ob ein Benutzer Mitglied oder Administrator einer Gruppe ist. |
| **Vite** | Build- und Entwicklungswerkzeug für das React-Frontend. Im Entwicklungsmodus stellt Vite die Oberfläche lokal bereit. |
| **PostgreSQL** | Relationale Datenbank für CampusSplit. Speichert Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile und Kategorien. |
| **CORS** | Cross-Origin Resource Sharing. Relevant, wenn Frontend und Backend in der Entwicklung auf unterschiedlichen Ports laufen. |
| **CSRF** | Cross-Site Request Forgery. Angriffsmuster, bei dem ein Browser zu unerwünschten Anfragen verleitet wird. Bei Session-Cookies muss CSRF-Schutz berücksichtigt werden. |
| **OpenAPI / Swagger** | Möglichkeit, REST-Endpunkte maschinenlesbar und als Weboberfläche zu dokumentieren. Für CampusSplit optional, aber für Review und Entwicklung hilfreich. |
| **MVP** | Minimum Viable Product. Kleinster sinnvoller Funktionsumfang: Login, Gruppen, Mitglieder, Ausgaben, Kostenaufteilung, Salden und Export. |
| **Greenfield** | Neuentwicklung ohne Vorgängersystem und ohne Datenmigration. |
| **Synchrone Verarbeitung** | Eine Aktion wird direkt während der Benutzeranfrage verarbeitet. CampusSplit nutzt dies für Ausgaben, Salden, Wechselkursabruf und Export. |
| **Hintergrundjob** | Zeitgesteuerte oder asynchrone Verarbeitung ohne direkte Benutzeraktion. Für CampusSplit im MVP nicht vorgesehen. |
