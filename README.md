# CampusSplit

CampusSplit ist das Softwareentwicklungsprojekt der Gruppe 16 im Modul **Projekt I (Softwaretechnik), WK_1106**. Die Webanwendung hilft Wohngemeinschaften, Reisegruppen und anderen Gruppen, gemeinsame Ausgaben aufzuteilen und offene Beträge nachzuvollziehen.

**Online-Version:** https://wk1106campussplit-production.up.railway.app/

Die Projektinstanz läuft auf Railway. Ihre Verfügbarkeit hängt vom Hostingtarif und dessen Laufzeit ab. Für eine unabhängige Vorführung lässt sich die Anwendung lokal starten.

## Funktionen

- Registrierung und Anmeldung mit E-Mail und Passwort; optional Google-Anmeldung und Google-Registrierung.
- Bestehende Konten können nach Bestätigung des Passworts im Profil mit Google verknüpft werden. Konten mit derselben E-Mail werden nicht automatisch zusammengeführt.
- Gruppen erstellen, Mitglieder hinzufügen und gemeinsame Ausgaben verwalten.
- Gruppenwährungen EUR und USD; gleichmäßige oder individuell festgelegte Kostenanteile.
- Salden und Rückzahlungsvorschläge sowie Erfassung und Stornierung von Rückzahlungen.
- Belege als JPEG, PNG oder PDF: bis zu 10 MB pro Datei und fünf Belege pro Ausgabe.
- Aktivitäten zu Ausgaben, Belegen und Zahlungen.
- Abrechnungen als PDF oder CSV-Dateien im ZIP-Archiv, mit auswählbarem Inhalt.
- Gruppen durch Administratoren archivieren, wiederherstellen oder nach Namensbestätigung endgültig löschen.
- Übersicht nach Währung und ergänzende Umrechnung in Euro mit Wechselkursen des Frankfurter-Dienstes.

CampusSplit dokumentiert Zahlungen, überweist aber kein Geld. Die Euroübersicht ersetzt keine tatsächliche Verrechnung zwischen Gruppen oder Währungen.

## Technischer Aufbau

| Bereich | Umsetzung |
| --- | --- |
| Laufzeit | Java 21 |
| Backend | Spring Boot 3.5.16, Spring MVC |
| Oberfläche | Thymeleaf, HTML und CSS; serverseitig gerendert |
| Anmeldung | Spring Security, BCrypt, optional Google OAuth2/OpenID Connect |
| Persistenz | PostgreSQL, Spring Data JPA, Flyway-Migrationen |
| Exporte | Apache PDFBox, CSV im ZIP-Archiv |
| Tests | JUnit, Spring Boot Test, Spring Security Test, H2 im PostgreSQL-Modus |
| Build | Maven mit Maven Wrapper |

Ein separater Frontend-Build oder Node.js ist zum Starten nicht erforderlich.

## Lokal starten

### 1. Voraussetzungen

- JDK 21; `java -version` muss Java 21 anzeigen.
- Eine erreichbare PostgreSQL-Datenbank und ein Benutzer mit Rechten auf diese Datenbank.
- Git und beim ersten Build Internetzugang für Maven-Abhängigkeiten.

```sh
git clone https://github.com/Bunyamin0101/WK_1106_CampusSplit.git
cd WK_1106_CampusSplit
```

### 2. Datenbank vorbereiten

Beispiel in `psql` als PostgreSQL-Administrator:

```sql
CREATE ROLE campussplit LOGIN;
\password campussplit
CREATE DATABASE campussplit OWNER campussplit;
```

Der Befehl `\password` fragt das selbst gewählte Passwort interaktiv ab. Eine bereits vorhandene Datenbank kann stattdessen verwendet werden. Flyway legt beim Anwendungsstart die Tabellen an und führt vorhandene Migrationen aus.

### 3. Umgebung konfigurieren

Die Datei [.env.example](.env.example) zeigt die verfügbaren Variablen. **Maven lädt eine `.env`-Datei nicht automatisch.** Die Werte müssen dem gestarteten Prozess als Umgebungsvariablen übergeben werden.

Beispiel für macOS/Linux:

```sh
export DB_URL='jdbc:postgresql://localhost:5432/campussplit'
export DB_USER='campussplit'
export DB_PASSWORD='DEIN_LOKALES_DATENBANKPASSWORT'
export SERVER_PORT=8088
export GOOGLE_LOGIN_ENABLED=false
sh ./mvnw spring-boot:run
```

Unter Windows PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/campussplit'
$env:DB_USER = 'campussplit'
$env:DB_PASSWORD = 'DEIN_LOKALES_DATENBANKPASSWORT'
$env:SERVER_PORT = '8088'
$env:GOOGLE_LOGIN_ENABLED = 'false'
.\mvnw.cmd spring-boot:run
```

Die Passwortangabe ist ein Platzhalter und muss lokal ersetzt werden. Zugangsdaten gehören nicht in Git.

Anschließend **http://localhost:8088/** öffnen und ein eigenes Konto erstellen. Der normale Start benötigt keine vorgegebenen Demo-Zugangsdaten. Ohne `SERVER_PORT` verwendet die Anwendung Port 8080.

### Konfiguration

| Variable | Bedeutung |
| --- | --- |
| `DB_URL` | JDBC-Adresse der PostgreSQL-Datenbank |
| `DB_USER` / `DB_PASSWORD` | Datenbankzugang |
| `SERVER_PORT` | Anwendungsport, Standard 8080 |
| `FRANKFURTER_BASE_URL` | Wechselkursdienst, Standard `https://api.frankfurter.dev` |
| `GOOGLE_LOGIN_ENABLED` | Google-Anmeldung einschalten, Standard `false` |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Zugangsdaten des Google-Webclients; bei aktivierter Google-Anmeldung erforderlich |

### Google-Anmeldung

Einen OAuth-Webclient in der Google Auth Platform einrichten. Für den lokalen Start mit Port 8088 lautet die autorisierte Weiterleitungsadresse:

```text
http://localhost:8088/login/oauth2/code/google
```

Für die Projektinstanz:

```text
https://wk1106campussplit-production.up.railway.app/login/oauth2/code/google
```

Danach die Client-ID und den Clientschlüssel ausschließlich in der jeweiligen Laufzeitumgebung setzen und `GOOGLE_LOGIN_ENABLED=true` verwenden. Ohne gültige Zugangsdaten darf Google-Login nicht aktiviert werden. Für reine lokale Tests kann die Option deaktiviert bleiben; die Anmeldung mit E-Mail und Passwort ist davon unabhängig.

## Tests und Build

```sh
sh ./mvnw test
sh ./mvnw clean package
```

Unter Windows entsprechend `mvnw.cmd` verwenden. Die automatisierten Tests verwenden eine H2-Testdatenbank; sie benötigen keine produktiven Datenbank- oder Google-Zugangsdaten. Ein vollständiger Google-Anmeldetest erfolgt zusätzlich im Browser mit einem eigenen Konto.

Das ausführbare JAR liegt nach dem Build unter `target/`:

```sh
java -jar target/campussplit-0.1.0-SNAPSHOT.jar
```

Auch beim JAR-Start müssen die Datenbankvariablen gesetzt sein. H2-Tests ersetzen keine Prüfung des PostgreSQL-Betriebs oder externer Dienste.

## Onlinebetrieb

Die Projektinstanz besteht aus einem Railway-App-Dienst und PostgreSQL. Änderungen am verbundenen GitHub-Branch `main` lösen eine erneute Bereitstellung aus. Ein erfolgreicher Push allein bestätigt noch kein erfolgreiches Deployment; anschließend den Railway-Status und die Webseite prüfen.

Bei HTTPS hinter dem Railway-Proxy sind insbesondere `SERVER_FORWARD_HEADERS_STRATEGY=framework` und `SERVER_SERVLET_SESSION_COOKIE_SECURE=true` konfiguriert. Der konfigurierte Anwendungsport muss zur Railway-Portzuordnung passen. Datenbankpasswörter und Google-Schlüssel werden als Servicevariablen verwaltet.

Eine manuelle Datenbanksicherung und ein Wiederherstellungstest wurden im Projekt durchgeführt. Automatische Backups sind damit nicht eingerichtet. Vor der Abgabe bzw. Vorführung sollten Laufzeit, Zugriff und eine aktuelle Sicherung geprüft werden.

## Projektstruktur und Dokumentation

- [Team und Projektidee](TEAMINFO.md)
- [KI-Offenlegung](KI_DISCLOSURE.md)
- [Spezifikation](Spezifikation/) – Anforderungen, Dialoge, Datenmodell und Druckausgaben
- [Architektur](Architektur/) – Architekturentscheidungen und Systemaufbau
- `src/main/java/de/thm/campussplit/` – Fachmodell, Dienste, Persistenz, Webcontroller und Konfiguration
- `src/main/resources/templates/` und `static/` – Oberfläche
- `src/main/resources/db/migration/` – Datenbankmigrationen
- `src/test/` – automatisierte Tests und Testkonfiguration

## Projektgrenzen

Die Anwendung ist ein Hochschulprojekt. Betreiberangaben und die öffentlichen Datenschutz- und Nutzungsinformationen für einen dauerhaften öffentlichen Betrieb sind noch zu klären. Die Google-Konfiguration befindet sich derzeit im Test-Veröffentlichungsstatus. Eine vollständige Produktions- oder Sicherheitsfreigabe wird durch diese README nicht behauptet.
