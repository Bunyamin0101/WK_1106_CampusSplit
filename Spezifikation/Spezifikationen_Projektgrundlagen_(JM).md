# Spezifikation zur Webanwendung „CampusSplit“

## Projektgrundlagen

Gruppe 16

Bearbeiterin: Julia Muntean


## P1 - Ziele und Rahmenbedingungen

Grundlagenbaustein der CampusSplit-Spezifikation nach Siedersleben. Beantwortet die Fragen: Warum wird das System entwickelt, für wen ist es gedacht und welche Rahmenbedingungen beeinflussen die Lösung?

### P1.1 Ausgangssituation und Motivation

Ob in Wohngemeinschaften, auf Reisen oder bei Projekten unter Freunden: Gemeinsame Ausgaben entstehen schnell und müssen fair aufgeteilt werden. Oft zahlt zunächst eine Person für mehrere, zum Beispiel für Einkäufe, Fahr- oder Eintrittskarten oder eine Unterkunft. Später muss dann geklärt werden, wer welchen Anteil übernimmt. In der Praxis wird das häufig über Chatnachrichten, Excel Tabellen, handschriftliche Notizen oder persönliche Absprachen geregelt. Das funktioniert zwar grundsätzlich, wird aber schnell unübersichtlich, sobald mehrere Personen beteiligt sind oder viele Ausgaben zusammenkommen.

CampusSplit soll genau bei diesem Prozess helfen. Mit der Webanwendung können Nutzer: innen Gruppen anlegen, gemeinsame Ausgaben eintragen, verfolgen und die Kosten auf die beteiligten Personen aufteilen. So bleibt übersichtlich, welche Beträge bereits bezahlt wurden und welche noch offen sind, ohne dass alles manuell festgehalten oder berechnet werden muss.

### P1.2 Ziel(e) des Systems

| ID   | Ziel                                                                                                                |
|------|---------------------------------------------------------------------------------------------------------------------|
| Z-01 | Gemeinsame Ausgaben zentral erfassen und verwalten.                                                                 |
| Z-02 | Kosten automatisch und nachvollziehbar auf Gruppenmitglieder aufteilen.                                             |
| Z-03 | Offene Forderungen und Verbindlichkeiten jederzeit transparent darstellen.                                          |
| Z-04 | Den organisatorischen Aufwand für Wohngemeinschaften, Reisen und Projektgruppen reduzieren.                         |
| Z-05 | Eine benutzerfreundliche Webanwendung bereitstellen, die sowohl auf Desktop- als auch auf Mobilgeräten nutzbar ist. |
| Z-06 | Ausgabenübersichten als PDF oder CSV exportierbar machen.                                                           |


### P1.3 Benutzer und Stakeholder

| Rolle                | Beschreibung                                 | Interaktion mit CampusSplit                                        |
|----------------------|----------------------------------------------|--------------------------------------------------------------------|
| Benutzer/Mitglied    | Standardnutzer der Anwendung.                | Erstellt Gruppen, erfasst Ausgaben, betrachtet Salden und Exporte. |
| Gruppenadministrator | Besitzer einer Gruppe.                       | Verwaltet Gruppenmitglieder und Gruppeneinstellungen.              |
| Entwicklungsteam     | Entwickler & Betreiber des Systems.          | Entwickelt, testet und wartet die Anwendung.                       |
| Datenbank            | Persistente Speicherung der Anwendungsdaten. | Speichert Benutzer, Gruppen, Ausgaben und Salden.                  |

Mehrmandantenbetrieb oder organisatorische Rollenmodelle über Gruppenadministratoren hinaus sind nicht vorgesehen.

### P1.4 Projektumfang

#### Im Projektumfang enthalten

- Benutzerregistrierung und Anmeldung
- Erstellung und Verwaltung von Gruppen
- Verwaltung von Gruppenmitgliedern
- Erfassung gemeinsamer Ausgaben
- Gleichmäßige Aufteilung von Kosten
- Berechnung von Forderungen und Verbindlichkeiten
- Anzeige von Gruppensalden
- Export von Ausgabenübersichten als PDF oder CSV
- Browserbasierte Nutzung auf Desktop- und Mobilgeräten

#### Nicht im Projektumfang enthalten

| ID    | Nicht-Ziel                          | Begründung                                                                  |
|-------|-------------------------------------|-----------------------------------------------------------------------------|
| NZ-01 | Integration von Bankkonten          | Erhöht Komplexität erheblich und ist für den Projekterfolg nicht notwendig. |
| NZ-02 | Direkte Zahlungsabwicklung          | CampusSplit verwaltet Ausgaben, verarbeitet jedoch keine Zahlungen.         |
| NZ-03 | Echtzeit-Chat zwischen Benutzern    | Kein Kernbestandteil der Anwendung.                                         |
| NZ-04 | Mobile Apps für Android oder iOS    | Eine responsive Webanwendung ist ausreichend.                               |
| NZ-05 | Mehrwährungsmanagement              | Fokus liegt auf einer einzelnen Währung (Euro).                             |
| NZ-06 | OCR-Rechnungserkennung              | Würde zusätzliche KI- und Bildverarbeitungskomponenten erfordern.           |
| NZ-07 | Migration bestehender Datenbestände | Greenfield-Projekt ohne Altdaten.                                           |


### P1.5 Rahmenbedingungen 

| ID    | Rahmenbedingungen                                                               |
|-------|---------------------------------------------------------------------------------|
| RB-01 | Umsetzung als Webanwendung.                                                     |
| RB-02 | Nutzung einer relationalen Datenbank zur Persistenz.                            |
| RB-03 | Frontend und Backend sind getrennte Komponenten.                                |
| RB-04 | Die Anwendung muss auf Desktop- und Mobilgeräten nutzbar sein.                  |
| RB-05 | Verwendung von Java 21 und Spring Boot im Backend.                              |
| RB-06 | Verwendung von React und TypeScript im Frontend.                                |
| RB-07 | Speicherung der Daten in PostgreSQL.                                            |
| RB-08 | Entwicklung im Rahmen eines Hochschulprojekts durch ein Team aus fünf Personen. |

Technologieentscheidungen werden im Architekturteil (arc42) detailliert dokumentiert und durch ADRs begründet.

### P1.6 Erfolgskriterien

| ID   | Kriterium                                                        |
|------|------------------------------------------------------------------|
| K-01 | Gruppen können erfolgreich erstellt und verwaltet werden.        |
| K-02 | Ausgaben können erfasst und mehreren Personen zugeordnet werden. |
| K-03 | Die Anwendung rechnet korrekt und nachvollziehbar.               |
| K-04 | Benutzer können Ausgabenübersichten exportieren.                 |
| K-05 | Die Anwendung ist auf Desktop und Smartphone nutzbar.            |
| K-06 | Kernfunktionen werden durch automatisierte Tests abgesichert.    |


### P1.7 Annahmen

| ID    | Annahme                                                                               |
|-------|---------------------------------------------------------------------------------------|
| AN-01 | Benutzer verfügen über einen aktuellen Webbrowser u.o. Smartphone mit Internetzugang. |
| AN-02 | Alle Benutzer verwenden dieselbe Währung (Euro).                                      |
| AN-03 | Während der Nutzung besteht eine Internetverbindung.                                  |
| AN-04 | Gruppen bestehen aus einer überschaubaren Anzahl von Mitgliedern.                     |

### P1.8 Risiken

| ID   | Risiko                                                                   | Gegenmaßnahme                                                     |
|------|--------------------------------------------------------------------------|-------------------------------------------------------------------|
| R-01 | Fehlerhafte Saldenberechnung führt zu falschen Ergebnissen.              | Automatisierte Unit-Tests für alle Berechnungsalgorithmen.        |
| R-02 | Verlust von Daten durch Systemfehler.                                    | Persistente Speicherung und regelmäßige Backups.                  |
| R-03 | Zeitliche Überlastung des Entwicklungsteams.                             | Frühe MVP-Definition und klare Rollenverteilung.                  |
| R-04 | Technische Schwierigkeiten bei der Integration von Frontend und Backend. | Frühzeitige Definition und Dokumentation der REST-Schnittstellen. |

## P2 – Architekturüberblick

Dieser Baustein zeigt, wie CampusSplit in die Systemlandschaft eingebunden ist. Dabei werden die Nachbarsysteme und die wichtigsten Datenflüsse zwischen diesen Systemen und CampusSplit beschrieben. Die interne Architektur, zum Beispiel Komponenten, Schichtenmodell, Laufzeitsicht und Deployment, wird hier nicht behandelt, sondern separat in der arc42-Architekturdokumentation beschrieben.

### P2.1 Systemkontext

CampusSplit ist eine browserbasierte Webanwendung zur Verwaltung gemeinsamer Gruppenausgaben. Die Nutzer greifen über den Webbrowser auf das System zu. Die Verarbeitung erfolgt im Backend, während die Daten dauerhaft in einer relationalen Datenbank gespeichert werden.

Optional können Ausgabenübersichten als PDF- oder CSV-Datei exportiert und lokal heruntergeladen werden. Da dies kein richtiges Nachbarsystem ist wurde es außenvor gelassen.

```text
+----------------+
|   Benutzer     |
| (Webbrowser)   |
+--------+-------+
         |
         | HTTPS
         v
+----------------+
|  CampusSplit   |
|  Webanwendung  |
+--------+-------+
         |
         | Datenzugriff
         v
+----------------+
|  PostgreSQL    |
|   Datenbank    |
+----------------+

         |
         | Export
         v
+----------------+
| PDF / CSV Datei|
+----------------+
```

### P2.2 Nachbarsysteme 

Vollständige Liste aller Systeme, mit denen CampusSplit kommuniziert. Detaillierte Schnittstellenbeschreibungen werden in S1 (Nachbarsysteme) dokumentiert.

| ID    | System               | Rolle                                         | Richtung      | Kopplung       | Häufigkeit               | Verantwortlich   |
|-------|----------------------|-----------------------------------------------|---------------|----------------|--------------------------|------------------|
| NB-01 | Webbrowser           | Benutzeroberfläche für die Anwendung          | bidirektional | eng (synchron) | bei jeder Benutzeraktion | Benutzer         |
| NB-02 | PostgreSQL Datenbank | Persistente Speicherung aller Anwendungsdaten | bidirektional | eng            | bei nahezu jeder Anfrage | Entwicklungsteam |
| NB-03 | PDF-/CSV-Export      | Bereitstellung von Ausgabenübersichten        | ausgehend     | lose           | auf Anforderung          | Entwicklungsteam |

#### Hinweise zur Systemlandschaft

##### Nachbarsysteme

Die Kommunikation erfolgt ausschließlich zwischen:

- Webbrowser und CampusSplit
- CampusSplit und PostgreSQL
- CampusSplit und Exportdateien (PDF/CSV)

##### Greenfield Projekt

CampusSplit wird als Neuentwicklung umgesetzt. Es existieren keine Altsysteme, die integriert oder migriert werden müssen. Daher ist der Baustein S2 (Datenmigration) nicht anwendbar.

##### Externe Dienste

In der ersten Projektversion werden keine externen Dienste oder APIs verwendet.

Insbesondere sind folgende Integrationen nicht Bestandteil des Systems:

- Bankenschnittstellen
- Zahlungsanbieter
- E-Mail-Dienste
- Cloud-Speicherdienste
- Social-Login-Anbieter

##### Systemgrenzen

Die Systemgrenze von CampusSplit umfasst:

- Frontend
- Backend
- Datenbankzugriffe
- Exportfunktionalitäten

Nicht Teil des Systems sind:

- Browser des Benutzers
- Betriebssystem des Benutzers
- PDF-Reader oder Tabellenkalkulationsprogramme
- Externe Zahlungsdienste

### P2.3 Architekturziele

Der Architekturüberblick verfolgt folgende Ziele:

| ID    | Ziel                                                                            |
|-------|---------------------------------------------------------------------------------|
| AO-01 | Klare Trennung zwischen Benutzeroberfläche, Geschäftslogik und Datenhaltung     |
| AO-02 | Einfache Wartbarkeit und Erweiterbarkeit                                        |
| AO-03 | Plattformunabhängige Nutzung über Webbrowser                                    |
| AO-04 | Nachvollziehbare Speicherung und Verarbeitung von Ausgaben und Salden           |
| AO-05 | Unterstützung zukünftiger Erweiterungen ohne grundlegende Architekturänderungen |
