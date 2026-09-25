# 10 Glossar

Dieses Glossar definiert Begriffe, die in der Architekturdokumentation von CampusSplit verwendet werden. Fachliche Domänenbegriffe wie Ausgabe, Gruppe, Mitglied, Saldo, Debitor, Kreditor, Kostenanteil oder Ausgleichsvorschlag sind verbindlich im Spezifikationsglossar [`E2 — Glossar`](../Spezifikation/E2_Glossar.md) beschrieben. Dieses Kapitel ergänzt vor allem Architektur- und Implementierungsbegriffe.

---

| Begriff | Definition |
|---|---|
| **Adapter** | Architekturbaustein, der eine externe Schnittstelle kapselt. Bei CampusSplit betrifft das vor allem den Wechselkursadapter zur Frankfurter API sowie die OAuth2-Sicherheitsanbindung an Google. |
| **Application Service** | Service-Schicht, die Use-Case-nahe Abläufe koordiniert, z. B. Ausgabe erfassen, Salden berechnen oder Export erzeugen. |
| **Backend** | Serverseitiger Teil von CampusSplit, umgesetzt mit Spring Boot. Verantwortlich für Formularverarbeitung, Validierung, Authentifizierung, Autorisierung, Fachlogik, Persistenz, Export und Wechselkursanbindung (siehe ADR-001, ADR-002). |
| **Controller** | Teil des Backends, der eingehende Formulare und Anfragen entgegennimmt, validiert, an passende Services weiterleitet und anschließend die passende Thymeleaf-View rendert. |
| **DTO** | Data Transfer Object. Objekt für den Datenaustausch zwischen Services oder zwischen Service-Schicht und Thymeleaf-Model, ohne direkt die Datenbankstruktur offenzulegen. |
| **Entity** | Persistierbares Domänenobjekt, das über JPA auf eine Datenbanktabelle abgebildet wird, z. B. `UserEntity`, `GroupEntity` oder `ExpenseEntity`. |
| **Thymeleaf-View** | Serverseitig gerendertes HTML-Template, das die Dialoge aus B1 umsetzt. Ersetzt die ursprünglich geplante separate Frontend-Anwendung (siehe ADR-001, ADR-003). |
| **HTTP-only Cookie** | Cookie, auf den JavaScript nicht direkt zugreifen kann. Wird für die Session-basierte Authentifizierung verwendet (siehe ADR-010). |
| **JPA** | Java Persistence API. Standard zur Abbildung von Java-Objekten auf relationale Datenbanktabellen. In CampusSplit über Spring Data JPA vorgesehen. |
| **Repository** | Persistenzschnittstelle, über die Services fachliche Daten lesen und speichern, ohne SQL direkt in der Fachlogik zu verteilen. |
| **Spring Boot** | Java-Framework zur Umsetzung des CampusSplit-Backends. Unterstützt Formularverarbeitung, Validierung, Security, Datenbankzugriff und Tests (siehe ADR-002). |
| **Spring Security** | Sicherheitsframework im Backend für Authentifizierung, Sitzungsschutz und Zugriffskontrolle. |
| **Transaktion** | Zusammengehöriger Speichervorgang, der vollständig erfolgreich sein muss oder komplett zurückgerollt wird. Wichtig beim Speichern einer Ausgabe mit mehreren Kostenanteilen. |
| **Value Object** | Objekt ohne eigene Identität, das einen fachlichen Wert beschreibt. Beispiele sind Geldbetrag, Währungscode oder Wechselkurs. |
| **Money / MoneyAmount** | Architekturbegriff für die technische Umsetzung von `MoneyAmountDT`: Betrag plus Währung, technisch umgesetzt und gespeichert als BigDecimal mit fixer Skalierung (2 Nachkommastellen) für alle Beträge (siehe ADR-006). |
| **Currency Adapter** | Backend-Komponente, die Wechselkurse über die Frankfurter API abruft und technische API-Details vor der restlichen Anwendung verbirgt. |
| **Google OAuth2** | Externer Dienst zur Authentifizierung von Benutzern. CampusSplit nutzt OAuth2 / OpenID Connect für die sichere Anmeldung und Registrierung, sodass keine Passwörter in der eigenen Datenbank gespeichert werden müssen (siehe ADR-008). |
| **ExchangeRate** | Architekturbegriff für den verwendeten Wechselkurs zwischen Originalwährung und Gruppenwährung. Entspricht fachlich `ExchangeRateDT`, technisch als `BigDecimal` umgesetzt (siehe ADR-006). |
| **Export Writer** | Komponente, die vorbereitete Exportdaten in ein konkretes Ausgabeformat schreibt, z. B. PDF oder CSV. |
| **ExportDataAssembler** | Komponente, die Gruppen-, Ausgaben-, Kostenanteils-, Salden- und Ausgleichsdaten für den Export fachlich zusammenstellt. |
| **SplitService** | Service zur Berechnung der Kostenanteile einer Ausgabe. |
| **BalanceService** | Service zur Berechnung der Salden je Gruppenmitglied. |
| **SettlementService** | Service zur Berechnung von Ausgleichsvorschlägen zwischen Debitoren und Kreditoren. |
| **MembershipGuard** | Architekturbegriff für eine zentrale Prüfung, ob ein Benutzer Mitglied oder Administrator einer Gruppe ist. |
| **PostgreSQL** | Relationale Datenbank für CampusSplit. Speichert Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile und Kategorien. Teil der internen Persistenz, kein externes Nachbarsystem (siehe ADR-004). |
| **CSRF** | Cross-Site Request Forgery. Angriffsmuster, bei dem ein Browser zu unerwünschten Anfragen verleitet wird. Bei den in CampusSplit verwendeten Session-Cookies (siehe ADR-011) wird dagegen der Standard-CSRF-Schutz von Spring Security für Thymeleaf-Formulare genutzt. |
| **MVP** | Minimum Viable Product. Kleinster sinnvoller Funktionsumfang: Login, Gruppen, Mitglieder, Ausgaben, Kostenaufteilung, Salden und Export. |
| **Greenfield** | Neuentwicklung ohne Vorgängersystem und ohne Datenmigration. |
| **Synchrone Verarbeitung** | Eine Aktion wird direkt während der Benutzeranfrage verarbeitet. CampusSplit nutzt dies für Ausgaben, Salden, Wechselkursabruf und Export. |
| **Hintergrundjob** | Zeitgesteuerte oder asynchrone Verarbeitung ohne direkte Benutzeraktion. Für CampusSplit im MVP nicht vorgesehen. |

