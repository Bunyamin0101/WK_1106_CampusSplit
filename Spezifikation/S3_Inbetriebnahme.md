# S3 — Inbetriebnahme

S3 beschreibt, unter welchen fachlichen und betrieblichen Voraussetzungen CampusSplit in Betrieb genommen wird. Der Baustein regelt die Betriebsumgebung, dauerhaft zu erhaltende Datenbereiche, die Erstinbetriebnahme, spätere Releases sowie grundlegende Funktionsprüfungen nach der Bereitstellung.

Konkrete Installationsbefehle, Server- und Containerkonfigurationen, Dateipfade sowie technische Abläufe der Auslieferung sind nicht Bestandteil der Spezifikation, sondern werden in der Architekturdokumentation und der Betriebsanleitung beschrieben.

---

## S3.1 Einordnung

CampusSplit ist eine webbasierte Mehrbenutzeranwendung mit zentraler Datenhaltung und Anbindung an externe Dienste.

| Aspekt | Einordnung für CampusSplit |
|-------|-----------------------------|
| Projekttyp | Greenfield-Projekt |
| Vorgängersystem | nicht vorhanden |
| Nutzungskanal | Webbrowser |
| Anwendungsbetrieb | Zentrale Webanwendung mit serverseitiger Verarbeitung |
| Datenhaltung | Relationale Datenbank (PostgreSQL) |
| Externe Dienste | Wechselkursdienst (Frankfurter-API) für Umrechnungen zwischen EUR und USD; optionaler externer Identitätsanbieter (Google) bei aktivierter Google-Anmeldung |
| Hintergrundläufe (Batch) | nicht erforderlich |

---

## S3.2 Voraussetzungen der Betriebsumgebung

Vor der Inbetriebnahme müssen folgende Voraussetzungen erfüllt sein.

| Bereich | Voraussetzung | Zweck |
|--------|---------------|------|
| Client-Zugang | Aktueller Webbrowser mit aktiviertem JavaScript und Cookies | Darstellung der Dialoge aus B1 und Aufrechterhaltung der Benutzersitzung |
| Anwendungsbetrieb | Lauffähige Laufzeitumgebung für die Webanwendung | Bereitstellung der Benutzeroberfläche sowie Ausführung der Use Cases(F2) und Anwendungsfunktionen(F3) |
| Netzwerkerreichbarkeit | Erreichbarkeit der Anwendung über HTTPS sowie ausgehende HTTPS-Verbindung zu den externen Diensten | Kommunikation mit Benutzer:innen, Abruf von Wechselkursen und optionale Google-Anmeldung |
| Datenhaltung | Erreichbare PostgreSQL-Datenbank mit eingerichteten Zugriffsrechten | Dauerhafte Speicherung aller fachlichen Daten(D1) |
| Umgebungskonfiguration | Vollständig gesetzte Konfigurationsparameter (Datenbankzugang, Anwendungsadresse, Dienstendpunkte) | Trennung von Anwendungscode und umgebungsspezifischen Einstellungen |
| Geheimnisverwaltung | Zugangsdaten und Schlüssel außerhalb des Quellcode-Repositorys | Schutz von Datenbankzugängen, Sitzungsgeheimnissen und Anmeldeschlüsseln |
| Dokumentenerzeugung | Verfügbarkeit der erforderlichen Schriftarten und Exportfunktionen | Erzeugung der PDF- und CSV-Abrechnungen(B3) |

---

## S3.3 Konfigurationsdaten

CampusSplit benötigt Konfigurationsdaten, die abhängig von der Betriebsumgebung bereitgestellt werden.

| Konfiguration | Beschreibung |
|---------------|--------------|
| Datenbankverbindung | Adresse, Datenbankname sowie Benutzerkennung und Passwort für den Zugriff auf PostgreSQL |
| Anwendungsport und Basisadresse | Netzwerkport und öffentliche Adresse, unter der die Webanwendung erreichbar ist (inkl. Rücksprungadresse für die Google-Anmeldung) |
| Wechselkursdienst-Adresse | Basisadresse des externen Wechselkursdienstes (Standard: Frankfurter-API) |
| Google-Anmeldung (optional) | Aktivierungsschalter sowie Client-Kennung und Geheimnis des externen Identitätsanbieters |
| Sitzungs- und Sicherheitseinstellungen | Vorgaben zur Gültigkeit von Benutzersitzungen und zum Schutz gegen unbefugte Formularanfragen |

Sicherheitsrelevante Konfigurationswerte (insbesondere Datenbankpasswörter, Sitzungsgeheimnisse und Schlüssel für externe Dienste) dürfen nicht im Quellcode-Repository abgelegt werden.

---

## S3.4 Persistente Datenbereiche

Persistente Daten müssen über Neustarts und spätere Releases hinweg erhalten bleiben.

| Datenbereich | Inhalt | Darf bei Release überschrieben werden? |
|-------------|--------|------------------------------------------|
| Fachliche Anwendungsdaten | Benutzerkonten, Google-Kontoverknüpfungen, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile, Kategorien, Belege, Rückzahlungen und Änderungshistorie | nein |
| Umgebungskonfiguration | Datenbankzugangsdaten, Schlüssel externer Dienste und betriebliche Parameter | nein |
| Betriebs- und Fehlerprotokolle | Technische Ereignisse und Fehlerausgaben zur Betriebsanalyse | nein, sofern betrieblich aufbewahrt |
| Exportierte Abrechnungen | Erzeugte PDF-Dateien und CSV-ZIP-Archive (werden nur temporär erzeugt und lokal bei Benutzer:innen gespeichert) | nicht zutreffend (keine dauerhafte Speicherung in CampusSplit) |

---

## S3.5 Erstinbetriebnahme

Die Erstinbetriebnahme erfolgt einmalig, wenn CampusSplit erstmals in einer lauffähigen Umgebung bereitgestellt wird.

| Schritt | Aktivität | Ergebnis |
|--------|-----------|----------|
| 1 | Betriebsumgebung einrichten | Laufzeitumgebung für die Webanwendung und PostgreSQL-Datenbank sind bereitgestellt und erreichbar. |
| 2 | Konfiguration hinterlegen | Datenbankzugang, Basisadresse, Wechselkursdienst und ggf. Google-Anmeldedaten sind sicher konfiguriert. |
| 3 | Datenstrukturen initialisieren | Alle erforderlichen Tabellen, Schlüssel, Integritätsregeln (D1) und Standardkategorien sind in der Datenbank angelegt. |
| 4 | Webanwendung starten | Die Anwendung verbindet sich erfolgreich mit der Datenbank, prüft die Datenstruktur und nimmt Anfragen entgegen. |
| 5 | Funktionsprüfung durchführen | Zentrale Anwendungsfälle aus F2 werden erfolgreich in der Zielumgebung geprüft. |

Fehlt eine dieser Voraussetzungen oder schlägt ein Schritt fehl, gilt die Erstinbetriebnahme als nicht abgeschlossen.

---

## S3.6 Funktionstest nach Erstinbetriebnahme

Nach der Erstinbetriebnahme wird ein kurzer Funktionstest durchgeführt.

| Prüfpunkt | Erwartetes Ergebnis |
|----------|---------------------|
| Start- und Anmeldeseite aufrufen | Die Anwendung antwortet im Browser und zeigt die Anmelde- bzw. Startseite an. |
| Registrierung und Anmeldung | Ein neues Benutzerkonto lässt sich anlegen und anmelden (bei aktivierter Option auch per Google-Anmeldung). |
| Gruppe anlegen und Mitglied aufnehmen | Eine Gruppe wird erstellt und ein weiteres Mitglied hinzugefügt. |
| Ausgabe mit Fremdwährung und Beleg erfassen | Eine Ausgabe (z. B. in USD innerhalb einer EUR-Gruppe) wird erfasst, über den Wechselkursdienst umgerechnet, aufgeteilt und mit einem Beleg versehen. |
| Salden, Ausgleichsvorschläge und Rückzahlung prüfen | Salden und Ausgleichsvorschläge werden korrekt angezeigt; eine Rückzahlung lässt sich erfassen und stornieren. |
| Export erzeugen | Die Gruppenabrechnung wird vollständig als PDF-Datei und als CSV-ZIP-Archiv heruntergeladen. |
| Gruppe archivieren und Abmeldung | Die Archivierung einer Gruppe und das Beenden der Benutzersitzung funktionieren fehlerfrei. |

---

## S3.7 Spätere Releases

Spätere Releases aktualisieren die Anwendung nach der Erstinbetriebnahme.

| Schritt | Aktivität | Regel |
|--------|-----------|-------|
| 1 | Stand prüfen und testen | Alle fachlichen Anforderungen und automatisierten Prüfungen sind erfolgreich durchlaufen. |
| 2 | Datensicherung erstellen | Vor Änderungen an der Datenstruktur wird eine vollständige Sicherung der PostgreSQL-Datenbank erstellt. |
| 3 | Neue Anwendungsversion bereitstellen | Die aktualisierte Anwendung ersetzt die bisherige Version, ohne persistente Daten oder Konfigurationen zu überschreiben. |
| 4 | Datenbankschema kontrolliert anpassen | Notwendige Erweiterungen der Datenstruktur werden schrittweise auf den bestehenden Datenbestand angewendet. |
| 5 | Funktionsprüfung wiederholen | Die zentralen Abläufe aus S3.6 werden nach dem Release erneut verifiziert. |

---

## S3.8 Datenbankänderungen bei Releases

Wenn ein Release das Datenmodell verändert, müssen Datenbankänderungen kontrolliert durchgeführt werden.

| Änderungstyp | Fachliche Auswirkung |
|-------------|----------------------|
| Neues Pflichtattribut an bestehender Entität | Bestehende Datensätze benötigen einen gültigen Standardwert oder eine definierte Überführungsregel. |
| Neue Entität (z. B. für zusätzliche Nachweise oder Protokolle) | Neue Tabellen und Beziehungen werden ergänzt, bestehende Ausgaben und Salden bleiben unverändert. |
| Erweiterung zulässiger Wertebereiche (z. B. zusätzliche Währung oder Kategorie) | Prüfregeln (D2) und Stammdaten werden konsistent aktualisiert. |

Vor jeder Schemaänderung ist ein Wiederherstellungspunkt (Backup) der PostgreSQL-Datenbank anzulegen.

---

## S3.9 Rollback

Ein Rollback ist die Rückkehr zu einem früheren Stand der Anwendung.

| Fehlersituation | Vorgehen |
|----------------|----------|
| Fehler in der Anwendungslogik ohne Änderung der Datenstruktur | Die vorherige Version der Webanwendung wird wieder bereitgestellt; der bestehende Datenbestand bleibt nutzbar. |
| Fehlerhafte Umgebungskonfiguration | Die Konfigurationsparameter werden korrigiert und die Anwendung neu gestartet. |
| Fehler in Verbindung mit einer Datenbankänderung | Ein einfaches Zurücksetzen der Anwendung genügt nicht, da die alte Version ggf. nicht mit der geänderten Datenstruktur arbeitet; die Datenbank muss auf die vor dem Release erstellte Sicherung zurückgesetzt werden. |

---

## S3.10 Nicht Bestandteil von S3

| Thema | Begründung |
|------|------------|
| Konkrete Installations- und Startbefehle | Gehören in die Installationsanleitung (`README`) und Betriebsdokumentation. |
| Server-, Container- und Hosting-Konfiguration | Gehört in die Architekturdokumentation. |
| Automatisierte Build- und Auslieferungsabläufe | Gehören in die Entwicklungs- und Architekturdokumentation. |
| Technische Backup- und Wiederherstellungsprozeduren | Gehören in das Betriebskonzept bzw. die Architekturdokumentation. |
| Datenmigration aus einem Altsystem | Nicht anwendbar, da CampusSplit als Neuentwicklung ohne Vorgängersystem entsteht. |

---

## S3.11 Querverweise

| Baustein | Relevanz für S3 |
|----------|-----------------|
| [P1](P1_Ziele_und_Rahmenbedingungen.md) | Definiert Rahmenbedingungen, Projektumfang und Nichtziele für den Betrieb. |
| [P2](P2_Architekturueberblick.md) | Beschreibt die Systemgrenze und die externen Nachbarsysteme im Kontext. |
| [F1](F1-geschaeftsprozesse.md) | Der Geschäftsprozess bildet den fachlichen Rahmen für die Funktionsprüfung. |
| [F2](F2-anwendungsfälle.md) | Die Use Cases liefern die konkreten Prüffälle für S3.6. |
| [F3](F3-anwendungsfunktionen.md) | Die Anwendungsfunktionen müssen nach der Inbetriebnahme fachlich korrekt arbeiten. |
| [D1](D1_Datenmodell.md) | Definiert die dauerhaft zu speichernden Entitäten und Integritätsregeln in PostgreSQL. |
| [D2](D2_Datentypenverzeichnis.md) | Definiert die fachlichen Datentypen und Wertebereiche. |
| [B1](B1_Dialogspezifikation.md) | Die spezifizierten Dialoge müssen über den Webbrowser erreichbar sein. |
| [B3](B3_Druckausgaben.md) | PDF- und CSV-Exporte müssen nach der Inbetriebnahme erzeugbar sein. |
| [S1](S1_Nachbarsysteme.md) | Beschreibt die Schnittstellen zu externen Diensten (Wechselkursdienst, Google-Anmeldung). |
| [N1](N1_Nichtfunktionale_Anforderungen.md) | Qualitätsanforderungen an Sicherheit, Verfügbarkeit und Antwortzeiten bestimmen die Betriebsvoraussetzungen. |
| [N2](N2_Querschnittskonzepte.md) | Querschnittliche Regeln zu Authentifizierung, Autorisierung und Protokollierung wirken auf den Betrieb. |
| [E2](E2_Glossar.md) | Das Glossar definiert zentrale Begriffe. |
