# S3 — Inbetriebnahme

S3 beschreibt, wie CampusSplit in Betrieb genommen wird und welche Voraussetzungen dafür erfüllt sein müssen. Der Baustein beschreibt die Betriebsumgebung, dauerhaft zu erhaltende Datenbereiche, Erstinbetriebnahme, spätere Releases und grundlegende Prüfungen nach der Bereitstellung.

S3 ist keine Schritt-für-Schritt-Installationsanleitung mit konkreten Befehlen. Technische Detailbefehle, Dateipfade, Docker-Konfigurationen oder CI/CD-Skripte gehören in die Architektur- und Betriebsdokumentation.

---

## S3.1 Einordnung

CampusSplit ist eine Webanwendung mit Frontend, Backend und relationaler Datenbank.

| Aspekt | Einordnung für CampusSplit |
|-------|-----------------------------|
| Projekttyp | Greenfield-Projekt |
| Vorgängersystem | nicht vorhanden |
| Betrieb | Webanwendung im Browser |
| Backend | serverseitige Fachlogik und Spring MVC Web-Controller |
| Frontend | serverseitig gerenderte HTML-Views (Thymeleaf) im Browser |
| Datenbank | relationale Datenbank, z. B. PostgreSQL |
| Externe Pflichtdienste | Frankfurter Wechselkursdienst, Google OAuth2 (keine Bank-, Zahlungs- oder OCR-Dienste) |
| Batch-Prozesse | nicht erforderlich |

---

## S3.2 Voraussetzungen der Betriebsumgebung

Vor der Inbetriebnahme müssen folgende Voraussetzungen erfüllt sein.

| Bereich | Voraussetzung | Zweck |
|--------|---------------|------|
| Browser | aktueller Webbrowser | Nutzung der Dialoge aus B1 |
| Anwendungs-Laufzeit | lauffähige Spring Boot Anwendung (Java 21) | Serverseitiges Rendering der Views, Verarbeitung von Use Cases und Fachlogik |
| HTTP-Kommunikation | Browser kann Anwendung über HTTP/HTTPS erreichen | Aufruf von Seiten und Übermittlung von Formulardaten |
| Datenbank | erreichbare relationale Datenbank (PostgreSQL) | Speicherung von Benutzern, Gruppen und Ausgaben |
| Konfiguration | gültige Umgebungswerte (z. B. OAuth2 Secrets, DB-Credentials) | Verbindung zu Datenbank und externen Diensten |
| Sicherheit | Geheimnisse außerhalb des Quellcodes | Schutz von Passwörtern und OAuth2 Zugangsdaten |
| Exportfähigkeit | PDF-/CSV-Erzeugung möglich | Bereitstellung von Ausgabenübersichten |

---

## S3.3 Konfigurationsdaten

CampusSplit benötigt Konfigurationsdaten, die abhängig von der Betriebsumgebung bereitgestellt werden.

| Konfiguration | Beschreibung |
|---------------|--------------|
| Datenbank-URL | Adresse der Datenbank |
| Datenbankbenutzer | Benutzerkonto für den Datenbankzugriff |
| Datenbankpasswort | Passwort für den Datenbankzugriff |
| Backend-Port | Port, über den das Backend erreichbar ist |
| Frontend-URL | Herkunft, von der Browseranfragen zugelassen werden |
| Session-Konfiguration | Einstellungen für Anmeldung und Sitzung |
| Export-Konfiguration | Einstellungen für PDF- und CSV-Erzeugung |

Sicherheitsrelevante Konfigurationen dürfen nicht öffentlich im Repository gespeichert werden.

Nicht ins Repository gehören insbesondere:

- Datenbankpasswörter
- Tokens
- private Schlüssel
- produktive `.env`-Dateien mit Geheimnissen
- Session-Geheimnisse

---

## S3.4 Persistente Datenbereiche

Persistente Daten müssen über Neustarts und spätere Releases hinweg erhalten bleiben.

| Datenbereich | Inhalt | Darf bei Release überschrieben werden? |
|-------------|--------|------------------------------------------|
| Anwendungsdatenbank | Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile, Kategorien | nein |
| Konfigurationsdaten | Datenbankverbindung, Sicherheitskonfiguration, Umgebungswerte | nein |
| Logdaten | technische Ereignisse und Fehler | nein, sofern betrieblich benötigt |
| Exportdateien | heruntergeladene PDF-/CSV-Dateien | liegen nach Download bei Benutzer:innen |

Exportdateien sind keine dauerhaft gespeicherten Fachobjekte von CampusSplit. Sie werden auf Anforderung erzeugt und anschließend heruntergeladen.

---

## S3.5 Erstinbetriebnahme

Die Erstinbetriebnahme erfolgt einmalig, wenn CampusSplit erstmals in einer lauffähigen Umgebung bereitgestellt wird.

| Schritt | Aktivität | Ergebnis |
|--------|-----------|----------|
| 1 | Betriebsumgebung bereitstellen | Frontend, Backend und Datenbank sind vorhanden. |
| 2 | Konfiguration bereitstellen | Backend kennt Datenbankverbindung, Frontend-URL und Sicherheitseinstellungen. |
| 3 | Datenbank initialisieren | Leeres Schema für User, Group, Membership, Expense, ExpenseShare und Category ist vorhanden. |
| 4 | Backend starten | Backend ist erreichbar und kann mit der Datenbank kommunizieren. |
| 5 | Frontend bereitstellen | Anwendung ist im Browser aufrufbar. |
| 6 | Funktionstest durchführen | Zentrale Use Cases funktionieren in der Umgebung. |

Wenn eine Voraussetzung fehlt, ist die Inbetriebnahme nicht abgeschlossen.

---

## S3.6 Funktionstest nach Erstinbetriebnahme

Nach der Erstinbetriebnahme wird ein kurzer Funktionstest durchgeführt.

| Testfall | Erwartetes Ergebnis |
|---------|---------------------|
| Anwendung im Browser öffnen | Startseite oder Anmeldeseite wird angezeigt. |
| Benutzer registrieren | Benutzerkonto wird gespeichert. |
| Benutzer anmelden | Dashboard wird angezeigt. |
| Gruppe erstellen | Gruppe wird gespeichert und geöffnet. |
| Mitglied hinzufügen | Mitgliedschaft wird gespeichert, sofern Benutzer ADMIN ist. |
| Ausgabe erfassen | Ausgabe und Kostenanteile werden gespeichert. |
| Salden anzeigen | Salden werden korrekt berechnet. |
| Export erzeugen | PDF- oder CSV-Datei wird bereitgestellt. |
| Abmelden | Sitzung wird beendet. |

Der Funktionstest orientiert sich an den Use Cases aus F2 und den Dialogen aus B1.

---

## S3.7 Spätere Releases

Spätere Releases aktualisieren die Anwendung nach der Erstinbetriebnahme.

| Schritt | Aktivität | Regel |
|--------|-----------|-------|
| 1 | Quellstand prüfen | Änderungen müssen zum Projektumfang passen. |
| 2 | Tests ausführen | Zentrale Fachlogik muss geprüft werden. |
| 3 | Anwendung bauen | Frontend und Backend werden aus aktuellem Stand erzeugt. |
| 4 | Artefakte bereitstellen | Neue Anwendung ersetzt alte Anwendung. |
| 5 | Datenbankänderungen prüfen | Schemaänderungen werden kontrolliert durchgeführt. |
| 6 | Funktionstest durchführen | Zentrale Funktionen werden erneut geprüft. |

Persistente Daten dürfen bei einem Release nicht gelöscht oder überschrieben werden.

---

## S3.8 Datenbankänderungen bei Releases

Wenn ein Release das Datenmodell verändert, müssen Datenbankänderungen kontrolliert durchgeführt werden.

| Beispiel | Bedeutung |
|---------|-----------|
| Neues Attribut bei Expense | Datenbankschema muss erweitert werden. |
| Neue Entität für spätere Funktionen | Neue Tabelle oder Struktur kann erforderlich sein. |
| Neue Aufteilungsart | Datenmodell und Validierung müssen angepasst werden. |
| Neues Exportformat | Fachlogik und Exportmodul müssen erweitert werden. |

Vor größeren Datenbankänderungen ist ein Backup sinnvoll. Konkrete Backup- und Migrationsbefehle gehören nicht zu S3, sondern zur technischen Betriebsdokumentation.

---

## S3.9 Rollback

Ein Rollback ist die Rückkehr zu einem früheren Stand der Anwendung.

| Situation | Bewertung |
|----------|-----------|
| Release ohne Datenbankänderung | Vorherige Anwendungsversion kann grundsätzlich wieder bereitgestellt werden. |
| Release mit Datenbankänderung | Rollback ist schwieriger, weil die alte Anwendung eventuell nicht zum neuen Schema passt. |
| Fehlerhafte Konfiguration | Korrektur der Konfiguration erforderlich. |
| Fehlerhafte Exportfunktion | Anwendung kann auf vorherige Version zurückgesetzt werden, falls keine Schemaänderung erfolgt ist. |

Der Zeitpunkt direkt vor einer Datenbankänderung ist ein wichtiger Sicherungspunkt.

---

## S3.10 Nicht Bestandteil von S3

| Thema | Begründung |
|------|------------|
| Exakte Installationsbefehle | Gehören in README oder Betriebsanleitung. |
| Konkrete Dateipfade | Gehören zur technischen Deployment-Dokumentation. |
| Docker-Compose-Dateien | Gehören zur Architektur oder Implementierung. |
| CI/CD-Pipeline im Detail | Gehört zur Entwicklungs- und Architekturdokumentation. |
| Webserver-Konfiguration | Gehört zum Betrieb. |
| Backup-Mechanik im Detail | Gehört zum Betriebskonzept. |
| Datenmigration aus Altsystem | Nicht anwendbar, siehe S2. |
| Zahlungsanbieter-Einrichtung | Nicht Bestandteil des Projektumfangs. |

---

## S3.11 Querverweise

| Baustein | Relevanz für S3 |
|----------|-----------------|
| P1 | Definiert Rahmenbedingungen, Umfang und Nichtziele. |
| P2 | Beschreibt Browser, CampusSplit, Datenbank und Exportdateien im Systemkontext. |
| F1 | Geschäftsprozess liefert Grundlage für Funktionstests. |
| F2 | Use Cases werden nach Inbetriebnahme exemplarisch geprüft. |
| F3 | Kostenaufteilung, Saldenberechnung und Exportaufbereitung müssen funktionieren. |
| D1 | Datenbank enthält die fachlichen Entitäten. |
| D2 | Datentypen beeinflussen Validierung und Tests. |
| B1 | Dialoge müssen im Browser erreichbar sein. |
| B3 | PDF- und CSV-Exporte müssen erzeugbar sein. |
| S1 | Nachbarsysteme wie Browser, Datenbank und Exportdatei müssen erreichbar sein. |
| S2 | Datenmigration ist nicht anwendbar, da CampusSplit ein Greenfield-Projekt ist. |
| N1 | Qualitätsanforderungen beeinflussen Betrieb, Sicherheit und Performance. |
| N2 | Authentifizierung, Autorisierung, Validierung, Fehlerbehandlung und Logging wirken auf den Betrieb. |
| E2 | Glossar erklärt Begriffe wie Inbetriebnahme, Release, Datenbank und Export. |
