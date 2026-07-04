# F2 - Anwendungsfälle

Anwendungsfälle beschreiben konkrete Interaktionsszenarien zwischen Benutzer:innen und CampusSplit. Jeder Anwendungsfall verfolgt ein fachlich sinnvolles Ziel und endet in einem stabilen Systemzustand.

F2 ist der systemgestützte Teil des in F1 beschriebenen Geschäftsprozesses. Aktivitäten außerhalb der Systemgrenze, zum Beispiel das tatsächliche Bezahlen offener Beträge, werden hier nicht als Anwendungsfälle modelliert.

Systeminterne Berechnungen, wie die Saldenberechnung oder Validierung von Eingaben, werden nicht als eigene Use Cases beschrieben. Sie werden in F3 als Anwendungsfunktionen dokumentiert.

## F2.1 Use-Case-Index

| ID                                              | Anwendungsfall                 | Gruppe             | Bezug zu F1-Aktivität    |
| ----------------------------------------------- | ------------------------------ | ------------------ | ------------------------ |
| [UC-01](#uc-01--registrieren)                   | Registrieren                   | Zugriff            | Voraussetzung für A3     |
| [UC-02](#uc-02--anmelden)                       | Anmelden                       | Zugriff            | Voraussetzung für A3     |
| [UC-03](#uc-03--abmelden)                       | Abmelden                       | Zugriff            | Nachgelagerte Aktion     |
| [UC-04](#uc-04--dashboard-anzeigen)             | Dashboard anzeigen             | Übersicht          | Einstieg in A3           |
| [UC-05](#uc-05--gruppe-erstellen)               | Gruppe erstellen               | Gruppenverwaltung  | A1 systemseitig abbilden |
| [UC-06](#uc-06--gruppe-anzeigen)                | Gruppe anzeigen                | Gruppenverwaltung  | A4                       |
| [UC-07](#uc-07--mitglied-zur-gruppe-hinzufügen) | Mitglied zur Gruppe hinzufügen | Gruppenverwaltung  | A1 / A4                  |
| [UC-08](#uc-08--ausgabe-erfassen)               | Ausgabe erfassen               | Ausgabenverwaltung | A5 + A6 + A7             |
| [UC-09](#uc-09--ausgabe-bearbeiten)             | Ausgabe bearbeiten             | Ausgabenverwaltung | Korrekturfall zu A5-A7   |
| [UC-10](#uc-10--ausgabe-löschen)                | Ausgabe löschen                | Ausgabenverwaltung | Korrekturfall zu A5-A7   |
| [UC-11](#uc-11--salden-anzeigen)                | Salden anzeigen                | Saldenverwaltung   | A8 + A9                  |
| [UC-12](#uc-12--ausgabenübersicht-exportieren)  | Ausgabenübersicht exportieren  | Export             | A10                      |

##

## F2.2 Zugriff

### UC-01 - Registrieren

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                                |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-01                                                                                                                                                                                                                                                                                 |
| **Name**                | Registrieren                                                                                                                                                                                                                                                                          |
| **Beschreibung**        | Ein Gast erstellt ein Benutzerkonto, um CampusSplit nutzen zu können.                                                                                                                                                                                                                 |
| **Auslöser**            | Ein Gast möchte CampusSplit erstmalig verwenden.                                                                                                                                                                                                                                      |
| **Akteure**             | Gast                                                                                                                                                                                                                                                                                  |
| **Vorbedingung**        | Der Gast besitzt noch kein Benutzerkonto mit derselben E-Mail-Adresse.                                                                                                                                                                                                                |
| **Nachbedingung**       | Ein neues Benutzerkonto wurde angelegt.                                                                                                                                                                                                                                               |
| **Hauptszenario**       | 1\. Gast öffnet die Registrierungsseite.2. System zeigt das Registrierungsformular an.3. Gast gibt Name, E-Mail-Adresse und Passwort ein.4. System validiert die Eingaben.5. System speichert den Benutzer mit gehashtem Passwort.6. System bestätigt die erfolgreiche Registrierung. |
| **Alternativszenarien** | Der Gast bricht die Registrierung ab; es wird kein Konto angelegt.                                                                                                                                                                                                                    |
| **Ausnahmeszenarien**   | E-Mail-Adresse ist bereits vergeben; System zeigt eine Fehlermeldung an.Pflichtfelder fehlen; System markiert die betroffenen Felder.Passwort erfüllt die Mindestanforderungen nicht; System fordert eine Korrektur.                                                                  |
| **Qualitätsbezug**      | N1 Sicherheit, N1 Benutzerfreundlichkeit, N2 Validierung                                                                                                                                                                                                                              |

### UC-02 - Anmelden

| Abschnitt             | Inhalt                                                                                                                                                                                                                                                      |
| --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**        | UC-02                                                                                                                                                                                                                                                       |
| **Name**              | Anmelden                                                                                                                                                                                                                                                    |
| **Beschreibung**      | Ein registrierter Benutzer meldet sich mit seinen Zugangsdaten an.                                                                                                                                                                                          |
| **Auslöser**          | Benutzer möchte auf Gruppen, Ausgaben und Salden zugreifen.                                                                                                                                                                                                 |
| **Akteure**           | Benutzer                                                                                                                                                                                                                                                    |
| **Vorbedingung**      | Benutzerkonto existiert.                                                                                                                                                                                                                                    |
| **Nachbedingung**     | Eine authentifizierte Sitzung ist aktiv.                                                                                                                                                                                                                    |
| **Hauptszenario**     | 1\. Benutzer öffnet die Login-Seite.2. System zeigt das Login-Formular an.3. Benutzer gibt E-Mail-Adresse und Passwort ein.4. System prüft die Zugangsdaten.5. System erstellt eine authentifizierte Sitzung.6. Benutzer wird zum Dashboard weitergeleitet. |
| **Ausnahmeszenarien** | Zugangsdaten sind falsch; System zeigt eine allgemeine Fehlermeldung an. Benutzerkonto existiert nicht; System zeigt eine allgemeine Fehlermeldung an.                                                                                                      |
| **Qualitätsbezug**    | N1 Sicherheit, N2 Authentifizierung                                                                                                                                                                                                                         |

### UC-03 - Abmelden

| Abschnitt          | Inhalt                                                                                                            |
| ------------------ | ----------------------------------------------------------------------------------------------------------------- |
| **Identifier**     | UC-03                                                                                                             |
| **Name**           | Abmelden                                                                                                          |
| **Beschreibung**   | Ein angemeldeter Benutzer beendet seine aktive Sitzung.                                                           |
| **Auslöser**       | Benutzer möchte CampusSplit verlassen.                                                                            |
| **Akteure**        | Benutzer                                                                                                          |
| **Vorbedingung**   | Benutzer ist angemeldet.                                                                                          |
| **Nachbedingung**  | Keine authentifizierte Sitzung ist aktiv.                                                                         |
| **Hauptszenario**  | 1\. Benutzer klickt auf „Abmelden".2. System beendet die Sitzung.3. Benutzer wird zur Login-Seite weitergeleitet. |
| **Qualitätsbezug** | N1 Sicherheit                                                                                                     |

## F2.3 Übersicht

### UC-04 - Dashboard anzeigen

| Abschnitt               | Inhalt                                                                                                                                                                                                         |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-04                                                                                                                                                                                                          |
| **Name**                | Dashboard anzeigen                                                                                                                                                                                             |
| **Beschreibung**        | Benutzer erhält eine Übersicht über seine Gruppen und aktuelle offene Salden.                                                                                                                                  |
| **Auslöser**            | Benutzer meldet sich an oder öffnet die Startseite der Anwendung.                                                                                                                                              |
| **Akteure**             | Benutzer                                                                                                                                                                                                       |
| **Vorbedingung**        | Benutzer ist angemeldet.                                                                                                                                                                                       |
| **Nachbedingung**       | Keine Änderung am Datenbestand.                                                                                                                                                                                |
| **Hauptszenario**       | 1\. Benutzer öffnet das Dashboard.2. System lädt alle Gruppen des Benutzers.3. System zeigt Gruppen, offene Salden und Schnellaktionen an.4. Benutzer kann eine Gruppe öffnen oder eine neue Gruppe erstellen. |
| **Alternativszenarien** | Benutzer ist noch in keiner Gruppe; System zeigt einen leeren Zustand mit Hinweis zur Gruppenerstellung.                                                                                                       |
| **Qualitätsbezug**      | N1 Benutzerfreundlichkeit, N1 Performance                                                                                                                                                                      |

## F2.4 Gruppenverwaltung

### UC-05 - Gruppe erstellen

| Abschnitt             | Inhalt                                                                                                                                                                                                                                                                                                              |
| --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**        | UC-05                                                                                                                                                                                                                                                                                                               |
| **Name**              | Gruppe erstellen                                                                                                                                                                                                                                                                                                    |
| **Beschreibung**      | Benutzer erstellt eine neue Gruppe, in der gemeinsame Ausgaben verwaltet werden.                                                                                                                                                                                                                                    |
| **Auslöser**          | Benutzer möchte Ausgaben für eine WG, Reise oder Projektgruppe verwalten.                                                                                                                                                                                                                                           |
| **Akteure**           | Benutzer                                                                                                                                                                                                                                                                                                            |
| **Vorbedingung**      | Benutzer ist angemeldet.                                                                                                                                                                                                                                                                                            |
| **Nachbedingung**     | Eine neue Gruppe wurde erstellt; der Ersteller ist Gruppenadministrator.                                                                                                                                                                                                                                            |
| **Hauptszenario**     | 1\. Benutzer wählt „Neue Gruppe erstellen".2. System zeigt ein Formular für Gruppendaten an.3. Benutzer gibt Gruppenname und optional Beschreibung ein.4. System validiert die Eingaben.5. System speichert die Gruppe.6. System ordnet den Benutzer als Gruppenadministrator zu.7. System öffnet die Gruppenseite. |
| **Ausnahmeszenarien** | Gruppenname fehlt; System fordert eine Eingabe. Gruppenname überschreitet maximale Länge; System zeigt Fehlermeldung.                                                                                                                                                                                               |
| **Qualitätsbezug**    | N1 Benutzerfreundlichkeit, N2 Validierung                                                                                                                                                                                                                                                                           |

### UC-06 - Gruppe anzeigen

| Abschnitt             | Inhalt                                                                                                                                                                  |
| --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**        | UC-06                                                                                                                                                                   |
| **Name**              | Gruppe anzeigen                                                                                                                                                         |
| **Beschreibung**      | Benutzer öffnet eine Gruppe und sieht Mitglieder, Ausgaben und Salden.                                                                                                  |
| **Auslöser**          | Benutzer möchte Details einer Gruppe ansehen.                                                                                                                           |
| **Akteure**           | Benutzer                                                                                                                                                                |
| **Vorbedingung**      | Benutzer ist angemeldet und Mitglied der Gruppe.                                                                                                                        |
| **Nachbedingung**     | Keine Änderung am Datenbestand.                                                                                                                                         |
| **Hauptszenario**     | 1\. Benutzer wählt eine Gruppe aus.2. System prüft die Mitgliedschaft.3. System lädt Gruppendaten, Mitglieder, Ausgaben und Salden.4. System zeigt die Gruppenseite an. |
| **Ausnahmeszenarien** | Benutzer ist kein Mitglied der Gruppe; Zugriff wird verweigert. Gruppe existiert nicht; System zeigt Fehlermeldung.                                                     |
| **Qualitätsbezug**    | N1 Sicherheit, N1 Performance                                                                                                                                           |

### UC-07 - Mitglied zur Gruppe hinzufügen

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                                                                                                 |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Identifier**          | UC-07                                                                                                                                                                                                                                                                                                                                                  |
| **Name**                | Mitglied zur Gruppe hinzufügen                                                                                                                                                                                                                                                                                                                         |
| **Beschreibung**        | Ein Gruppenadministrator fügt ein weiteres Mitglied zu einer Gruppe hinzu.                                                                                                                                                                                                                                                                             |
| **Auslöser**            | Eine weitere Person soll an der Ausgabenverwaltung teilnehmen.                                                                                                                                                                                                                                                                                         |
| **Akteure**             | Gruppenadministrator                                                                                                                                                                                                                                                                                                                                   |
| **Vorbedingung**        | Benutzer ist angemeldet und Gruppenadministrator der Gruppe.                                                                                                                                                                                                                                                                                           |
| **Nachbedingung**       | Das neue Mitglied ist der Gruppe zugeordnet.                                                                                                                                                                                                                                                                                                           |
| **Hauptszenario**       | 1\. Gruppenadministrator öffnet die Mitgliederverwaltung.2. System zeigt bestehende Mitglieder an.3. Gruppenadministrator gibt die E-Mail-Adresse des neuen Mitglieds ein.4. System prüft, ob ein Benutzerkonto mit dieser E-Mail-Adresse existiert.5. System fügt den Benutzer der Gruppe hinzu.6. System zeigt die aktualisierte Mitgliederliste an. |
| **Alternativszenarien** | Das Mitglied ist bereits in der Gruppe; System zeigt einen Hinweis ohne Änderung.                                                                                                                                                                                                                                                                      |
| **Ausnahmeszenarien**   | Benutzerkonto existiert nicht; System zeigt Fehlermeldung. Ausführender Benutzer ist kein Gruppenadministrator; Zugriff wird verweigert.                                                                                                                                                                                                               |
| **Qualitätsbezug**      | N1 Sicherheit, N2 Autorisierung, N2 Validierung                                                                                                                                                                                                                                                                                                        |

## F2.5 Ausgabenverwaltung

### UC-08 - Ausgabe erfassen

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                                                                                                                                                            |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-08                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Name**                | Ausgabe erfassen                                                                                                                                                                                                                                                                                                                                                                                                  |
| **Beschreibung**        | Ein Gruppenmitglied erfasst eine gemeinsame Ausgabe und legt fest, auf welche Mitglieder sie aufgeteilt wird.                                                                                                                                                                                                                                                                                                     |
| **Auslöser**            | Ein Gruppenmitglied hat eine Ausgabe für mehrere Personen bezahlt.                                                                                                                                                                                                                                                                                                                                                |
| **Akteure**             | Gruppenmitglied                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Vorbedingung**        | Benutzer ist angemeldet und Mitglied der Gruppe.                                                                                                                                                                                                                                                                                                                                                                  |
| **Nachbedingung**       | Ausgabe und Kostenanteile wurden gespeichert; Salden können neu berechnet werden.                                                                                                                                                                                                                                                                                                                                 |
| **Hauptszenario**       | 1\. Benutzer öffnet eine Gruppe.2. Benutzer wählt „Ausgabe hinzufügen".3. System zeigt ein Formular für Beschreibung, Betrag, Datum, Kategorie und Zahler an.4. Benutzer wählt die beteiligten Gruppenmitglieder aus.5. Benutzer wählt die Aufteilungsart.6. System validiert Betrag, Zahler, Beteiligte und Aufteilung.7. System speichert Ausgabe und Kostenanteile.8. System aktualisiert die Saldenübersicht. |
| **Alternativszenarien** | Benutzer bricht die Erfassung ab; es wird keine Ausgabe gespeichert. Benutzer wählt nur einen Teil der Gruppenmitglieder aus; Ausgabe wird nur auf diese Personen aufgeteilt.                                                                                                                                                                                                                                     |
| **Ausnahmeszenarien**   | Betrag ist ungültig; System fordert Korrektur. Kein Zahler ausgewählt; System fordert Auswahl. Keine beteiligten Personen ausgewählt; System fordert Auswahl. Aufteilungssumme stimmt nicht mit Gesamtbetrag überein; System zeigt Fehlermeldung.                                                                                                                                                                 |
| **Qualitätsbezug**      | N1 Datenkonsistenz, N2 Validierung, F3 Saldenberechnung                                                                                                                                                                                                                                                                                                                                                           |

### UC-09 - Ausgabe bearbeiten

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                                                                                   |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-09                                                                                                                                                                                                                                                                                                                                    |
| **Name**                | Ausgabe bearbeiten                                                                                                                                                                                                                                                                                                                       |
| **Beschreibung**        | Ein Gruppenmitglied korrigiert eine bereits erfasste Ausgabe.                                                                                                                                                                                                                                                                            |
| **Auslöser**            | Eine Ausgabe wurde falsch oder unvollständig eingetragen.                                                                                                                                                                                                                                                                                |
| **Akteure**             | Gruppenmitglied                                                                                                                                                                                                                                                                                                                          |
| **Vorbedingung**        | Benutzer ist angemeldet, Mitglied der Gruppe und die Ausgabe existiert.                                                                                                                                                                                                                                                                  |
| **Nachbedingung**       | Ausgabe wurde aktualisiert; Salden können neu berechnet werden.                                                                                                                                                                                                                                                                          |
| **Hauptszenario**       | 1\. Benutzer öffnet die Gruppenseite.2. Benutzer wählt eine Ausgabe aus.3. System zeigt die gespeicherten Ausgabendaten an.4. Benutzer ändert Betrag, Beschreibung, Datum, Zahler, Beteiligte oder Aufteilung.5. System validiert die geänderten Eingaben.6. System speichert die Änderungen.7. System aktualisiert die Saldenübersicht. |
| **Alternativszenarien** | Benutzer verlässt die Bearbeitung ohne Speichern; bestehende Ausgabe bleibt unverändert.                                                                                                                                                                                                                                                 |
| **Ausnahmeszenarien**   | Geänderte Daten sind ungültig; System zeigt konkrete Fehlermeldungen. Benutzer ist kein Gruppenmitglied; Zugriff wird verweigert.                                                                                                                                                                                                        |
| **Qualitätsbezug**      | N1 Datenkonsistenz, N2 Validierung                                                                                                                                                                                                                                                                                                       |

### UC-10 - Ausgabe löschen

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                  |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-10                                                                                                                                                                                                                                                                   |
| **Name**                | Ausgabe löschen                                                                                                                                                                                                                                                         |
| **Beschreibung**        | Ein Gruppenmitglied löscht eine nicht mehr benötigte oder falsch erfasste Ausgabe.                                                                                                                                                                                      |
| **Auslöser**            | Eine Ausgabe soll aus der Gruppe entfernt werden.                                                                                                                                                                                                                       |
| **Akteure**             | Gruppenmitglied                                                                                                                                                                                                                                                         |
| **Vorbedingung**        | Benutzer ist angemeldet, Mitglied der Gruppe und die Ausgabe existiert.                                                                                                                                                                                                 |
| **Nachbedingung**       | Ausgabe und zugehörige Kostenanteile wurden entfernt; Salden können neu berechnet werden.                                                                                                                                                                               |
| **Hauptszenario**       | 1\. Benutzer öffnet die Gruppenseite.2. Benutzer wählt eine Ausgabe aus.3. Benutzer klickt auf „Löschen".4. System fordert eine Bestätigung an.5. Benutzer bestätigt das Löschen.6. System löscht Ausgabe und Kostenanteile.7. System aktualisiert die Saldenübersicht. |
| **Alternativszenarien** | Benutzer bricht die Bestätigung ab; es wird nichts gelöscht.                                                                                                                                                                                                            |
| **Ausnahmeszenarien**   | Ausgabe existiert nicht mehr; System zeigt Fehlermeldung. Benutzer ist kein Gruppenmitglied; Zugriff wird verweigert.                                                                                                                                                   |
| **Qualitätsbezug**      | N1 Datenkonsistenz, N2 Autorisierung                                                                                                                                                                                                                                    |

## F2.6 Saldenverwaltung

### UC-11 - Salden anzeigen

| Abschnitt               | Inhalt                                                                                                                                                                                                                                |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-11                                                                                                                                                                                                                                 |
| **Name**                | Salden anzeigen                                                                                                                                                                                                                       |
| **Beschreibung**        | Ein Gruppenmitglied sieht, welche Personen innerhalb einer Gruppe offene Forderungen oder Verbindlichkeiten haben.                                                                                                                    |
| **Auslöser**            | Benutzer möchte wissen, wer wem welchen Betrag schuldet.                                                                                                                                                                              |
| **Akteure**             | Gruppenmitglied                                                                                                                                                                                                                       |
| **Vorbedingung**        | Benutzer ist angemeldet und Mitglied der Gruppe.                                                                                                                                                                                      |
| **Nachbedingung**       | Keine Änderung am Datenbestand.                                                                                                                                                                                                       |
| **Hauptszenario**       | 1\. Benutzer öffnet eine Gruppe.2. Benutzer wählt die Saldenübersicht.3. System lädt alle relevanten Ausgaben und Kostenanteile.4. System berechnet die aktuellen Salden.5. System zeigt offene Forderungen und Verbindlichkeiten an. |
| **Alternativszenarien** | Es existieren keine Ausgaben; System zeigt einen leeren Zustand. Alle Salden sind ausgeglichen; System zeigt einen entsprechenden Hinweis.                                                                                            |
| **Ausnahmeszenarien**   | Benutzer ist kein Gruppenmitglied; Zugriff wird verweigert.                                                                                                                                                                           |
| **Qualitätsbezug**      | N1 Performance, N1 Datenkonsistenz, F3 Saldenberechnung                                                                                                                                                                               |

## F2.7 Export

### UC-12 - Ausgabenübersicht exportieren

| Abschnitt               | Inhalt                                                                                                                                                                                                                                                                             |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Identifier**          | UC-12                                                                                                                                                                                                                                                                              |
| **Name**                | Ausgabenübersicht exportieren                                                                                                                                                                                                                                                      |
| **Beschreibung**        | Ein Gruppenmitglied exportiert eine Übersicht über Ausgaben und Salden einer Gruppe.                                                                                                                                                                                               |
| **Auslöser**            | Benutzer möchte die Ausgabenübersicht außerhalb von CampusSplit speichern oder teilen.                                                                                                                                                                                             |
| **Akteure**             | Gruppenmitglied                                                                                                                                                                                                                                                                    |
| **Vorbedingung**        | Benutzer ist angemeldet und Mitglied der Gruppe.                                                                                                                                                                                                                                   |
| **Nachbedingung**       | Eine Exportdatei wurde erzeugt und zum Download bereitgestellt.                                                                                                                                                                                                                    |
| **Hauptszenario**       | 1\. Benutzer öffnet eine Gruppe.2. Benutzer wählt „Exportieren".3. System bietet Exportformat PDF oder CSV an.4. Benutzer wählt ein Format.5. System erzeugt eine Ausgabenübersicht mit Gruppenname, Zeitraum, Ausgaben und Salden.6. System stellt die Datei zum Download bereit. |
| **Alternativszenarien** | Benutzer bricht den Export ab; es wird keine Datei erzeugt.                                                                                                                                                                                                                        |
| **Ausnahmeszenarien**   | Export kann nicht erzeugt werden; System zeigt Fehlermeldung. Benutzer ist kein Gruppenmitglied; Zugriff wird verweigert.                                                                                                                                                          |
| **Qualitätsbezug**      | B3 Druckausgaben, N1 Benutzerfreundlichkeit                                                                                                                                                                                                                                        |

## F2.8 Use-Case-Diagramm

Das folgende Diagramm zeigt die wichtigsten Anwendungsfälle von CampusSplit aus Sicht der Benutzer:innen.

![Use-Case-Diagramm](images/Use-Case-Diagramm.png)

## F2.9 Nicht Bestandteil von F2

Folgende Punkte sind bewusst keine eigenen Use Cases:

| Thema                                     | Begründung                                                                         |
| ----------------------------------------- | ---------------------------------------------------------------------------------- |
| Saldenberechnung als Algorithmus          | Systeminterne Berechnung ohne eigenständiges Benutzerziel; wird in F3 beschrieben. |
| Validierung von Eingaben                  | Querschnittliche Anwendungsfunktion; wird in F3/N2 beschrieben.                    |
| Speicherung in der Datenbank              | Technische Funktion, keine Benutzerinteraktion.                                    |
| Tatsächliche Zahlung offener Beträge      | Erfolgt außerhalb von CampusSplit und ist nicht Teil des Systems.                  |
| Bank- oder Zahlungsintegration            | Nicht im Projektumfang enthalten.                                                  |
| PDF-/CSV-Erzeugung als technischer Ablauf | Bestandteil von UC-12 und B3, aber kein eigenständiger Benutzerprozess.            |

## F2.10 Querverweise

| Baustein | Relevanz für F2                                                                                                  |
| -------- | ---------------------------------------------------------------------------------------------------------------- |
| P1       | Ziele, Umfang, Nichtziele und Rahmenbedingungen bestimmen die Auswahl der Use Cases.                             |
| P2       | Webbrowser, CampusSplit, Datenbank und Exportdateien bilden die relevanten Nachbarsysteme.                       |
| F1       | Die Aktivitäten A3 bis A10 werden durch die Use Cases UC-04 bis UC-12 unterstützt.                               |
| F3       | Validierung, Kostenaufteilung, Saldenberechnung und Exporterzeugung werden als Anwendungsfunktionen beschrieben. |
| D1       | Benutzer, Gruppen, Mitgliedschaften, Ausgaben und Kostenanteile bilden die zentralen Datenobjekte der Use Cases. |
| D2       | Datentypen und Attribute werden in den Use Cases verwendet und später präzisiert.                                |
| B1       | Dialoge und Screens konkretisieren die Benutzerinteraktion der Use Cases.                                        |
| B3       | Exportausgaben werden durch UC-12 ausgelöst.                                                                     |
| S1       | Schnittstellen zu Browser, Datenbank und Exportmechanismus werden dort beschrieben.                              |
| N1       | Sicherheit, Performance, Benutzbarkeit und Datenkonsistenz wirken auf alle Use Cases.                            |
| N2       | Authentifizierung, Autorisierung, Validierung und Fehlerbehandlung wirken quer über mehrere Use Cases.           |
