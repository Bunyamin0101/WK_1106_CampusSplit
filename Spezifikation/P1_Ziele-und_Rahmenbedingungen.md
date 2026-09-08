# P1 — Ziele und Rahmenbedingungen

Grundlagenbaustein der CampusSplit-Spezifikation nach Siedersleben. Dieser Baustein beschreibt, warum das System entwickelt wird, für wen es gedacht ist und welche Rahmenbedingungen den Lösungsraum eingrenzen.

---

## P1.1 Mission

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Nutzer können Gruppen für Wohngemeinschaften, Reisen oder studentische Projekte erstellen, Ausgaben erfassen und die Kosten auf die beteiligten Gruppenmitglieder aufteilen.

Das System soll den Aufwand reduzieren, der bei gemeinsamen Ausgaben oft durch Chatnachrichten, Tabellen oder manuelle Berechnungen entsteht. Statt Beträge selbst nachzurechnen, sollen Nutzer in CampusSplit sehen können, welche Ausgaben erfasst wurden und wer wem noch Geld schuldet.

CampusSplit ersetzt keine echte Zahlungsabwicklung. Die Anwendung dient dazu, gemeinsame Kosten übersichtlich zu dokumentieren, offene Beträge automatisch zu berechnen und Ausgabenübersichten bei Bedarf zu exportieren.

---

## P1.2 Ziele des Systems

| ID   | Ziel                                                                                 |
| ---- | ------------------------------------------------------------------------------------ |
| Z-01 | Nutzer können sich registrieren und anmelden.                                        |
| Z-02 | Nutzer können Gruppen erstellen und verwalten.                                       |
| Z-03 | Gruppenmitglieder können einer Gruppe hinzugefügt werden.                            |
| Z-04 | Ausgaben können mit Betrag, Beschreibung, Datum und zahlender Person erfasst werden. |
| Z-05 | Kosten können auf die beteiligten Gruppenmitglieder aufgeteilt werden.               |
| Z-06 | Das System berechnet automatisch offene Beträge innerhalb einer Gruppe.              |
| Z-07 | Nutzer können sehen, wer wem noch Geld schuldet.                                     |
| Z-08 | Ausgabenübersichten können als CSV oder PDF exportiert werden.                       |
| Z-09 | Die Anwendung soll einfach und verständlich bedienbar sein.                          |

---

## P1.3 Stakeholder und Benutzergruppen

| Rolle            | Beschreibung                         | Interesse am System                                                               |
| ---------------- | ------------------------------------ | --------------------------------------------------------------------------------- |
| Nutzer           | Person, die CampusSplit verwendet.   | Möchte eigene Gruppen und Ausgaben verwalten.                                     |
| Gruppenmitglied  | Mitglied einer bestimmten Gruppe.    | Möchte sehen, welche Ausgaben entstanden sind und welche Beträge noch offen sind. |
| Gruppenersteller | Person, die eine Gruppe erstellt.    | Möchte Mitglieder hinzufügen und die Gruppe verwalten.                            |
| Entwicklungsteam | Projektgruppe aus fünf Studierenden. | Plant, entwickelt und dokumentiert die Anwendung im Rahmen des Studiums.          |
| Dozent / Prüfer  | Bewertet die Projektabgabe.          | Erwartet eine nachvollziehbare Spezifikation und eine funktionierende Anwendung.  |

Eine komplexe Rollenverwaltung ist für die erste Version nicht geplant. Es reicht aus, wenn Nutzer Gruppen erstellen, Mitglieder verwalten und Ausgaben eintragen können.

---

## P1.4 Projektumfang

Zum geplanten Umfang der Anwendung gehören folgende Funktionen:

* Registrierung von Nutzern
* Anmeldung von Nutzern
* Speicherung des angemeldeten Zustands während der Nutzung
* Erstellung von Gruppen
* Verwaltung von Gruppenmitgliedern
* Erfassen gemeinsamer Ausgaben
* Zuordnung einer Ausgabe zu einer Gruppe
* Angabe, wer eine Ausgabe bezahlt hat
* Aufteilung der Kosten auf Gruppenmitglieder
* automatische Berechnung offener Beträge
* Anzeige einer Übersicht über Ausgaben und Schulden
* Export einer Ausgabenübersicht

---

## P1.5 Nichtziele

Nicht alle möglichen Funktionen werden in der ersten Version umgesetzt. Einige Themen werden bewusst ausgeschlossen, damit der Projektumfang für ein Semester realistisch bleibt.

| ID    | Nichtziel                                         | Begründung                                                                                               |
| ----- | ------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| NZ-01 | Direkte Zahlungsabwicklung                        | CampusSplit zeigt nur an, wer wem Geld schuldet. Zahlungen werden nicht über die Anwendung durchgeführt. |
| NZ-02 | Anbindung an Bankkonten                           | Das wäre für das Projekt zu aufwendig und ist für die Grundfunktion nicht notwendig.                     |
| NZ-03 | Mobile App für Android oder iOS                   | Eine Webanwendung reicht für dieses Projekt aus.                                                         |
| NZ-04 | Chatfunktion innerhalb der Gruppen                | Kommunikation ist nicht der Schwerpunkt der Anwendung.                                                   |
| NZ-05 | Mehrere Währungen                                 | In der ersten Version wird nur mit Euro gerechnet.                                                       |
| NZ-06 | Automatische Rechnungserkennung per Foto oder OCR | Das würde den Umfang des Projekts zu stark erhöhen.                                                      |
| NZ-07 | Migration bestehender Daten                       | CampusSplit wird neu entwickelt und übernimmt keine alten Datenbestände.                                 |

---

## P1.6 Rahmenbedingungen

CampusSplit wird im Rahmen eines Hochschulprojekts im vierten Semester des Studiengangs Wirtschaftsinformatik entwickelt. Das Projektteam besteht aus fünf Personen.

Die Anwendung soll mit Java umgesetzt werden. Als Entwicklungsumgebung wird Visual Studio Code verwendet. Da CampusSplit eine Webanwendung ist, soll das System über einen Browser nutzbar sein.

Für die Anwendung müssen Daten dauerhaft gespeichert werden. Dazu gehören zum Beispiel:

* Benutzerkonten
* Login-Daten
* Gruppen
* Gruppenmitglieder
* Ausgaben
* Berechnungsgrundlagen für offene Beträge

Die genaue technische Umsetzung, zum Beispiel die konkrete Datenbank oder bestimmte Frameworks, kann im weiteren Projektverlauf festgelegt werden. Wichtig ist, dass die Daten gespeichert und den richtigen Nutzern und Gruppen zugeordnet werden können.

| ID    | Rahmenbedingung                                               |
| ----- | ------------------------------------------------------------- |
| RB-01 | Das Projekt wird von fünf Studierenden umgesetzt.             |
| RB-02 | Die Anwendung wird als Webanwendung entwickelt.               |
| RB-03 | Die Entwicklung erfolgt mit Java und Visual Studio Code.      |
| RB-04 | Die Anwendung benötigt eine dauerhafte Speicherung von Daten. |
| RB-05 | Nutzer müssen sich registrieren und anmelden können.          |
| RB-06 | Der Projektumfang muss für ein Semester realistisch bleiben.  |
| RB-07 | Die Anwendung soll einfach zu bedienen sein.                  |

---

## P1.7 Erfolgskriterien

Das Projekt gilt als erfolgreich, wenn die wichtigsten Funktionen umgesetzt wurden. Dazu gehören vor allem Registrierung, Anmeldung, Gruppenverwaltung, Ausgabenerfassung und die Berechnung offener Beträge.

| ID    | Erfolgskriterium                                         |
| ----- | -------------------------------------------------------- |
| EK-01 | Nutzer können ein Konto erstellen.                       |
| EK-02 | Nutzer können sich anmelden.                             |
| EK-03 | Gruppen können erstellt und angezeigt werden.            |
| EK-04 | Gruppenmitglieder können einer Gruppe zugeordnet werden. |
| EK-05 | Ausgaben können erfasst und gespeichert werden.          |
| EK-06 | Das System berechnet offene Beträge korrekt.             |
| EK-07 | Die offenen Beträge werden verständlich angezeigt.       |
| EK-08 | Eine Ausgabenübersicht kann exportiert werden.           |
| EK-09 | Die Anwendung ist über einen Browser nutzbar.            |

---

## P1.8 Annahmen

Für die erste Version von CampusSplit werden folgende Annahmen getroffen:

| ID   | Annahme                                                                |
| ---- | ---------------------------------------------------------------------- |
| A-01 | Nutzer besitzen ein Gerät mit Internetzugang und aktuellem Webbrowser. |
| A-02 | Die Gruppen bestehen aus einer überschaubaren Anzahl an Personen.      |
| A-03 | Es wird zunächst nur mit Euro gerechnet.                               |
| A-04 | Alle Ausgaben werden manuell eingetragen.                              |
| A-05 | Tatsächliche Zahlungen finden außerhalb der Anwendung statt.           |

---

## P1.9 Risiken

| ID   | Risiko                                                                   | Mögliche Gegenmaßnahme                                                |
| ---- | ------------------------------------------------------------------------ | --------------------------------------------------------------------- |
| R-01 | Die Berechnung der offenen Beträge ist fehlerhaft.                       | Die Berechnungslogik wird früh getestet und mit Beispielen überprüft. |
| R-02 | Der Projektumfang wird zu groß.                                          | Das Team konzentriert sich zuerst auf die wichtigsten Funktionen.     |
| R-03 | Es gibt Probleme bei der Speicherung von Nutzern, Gruppen oder Ausgaben. | Das Datenmodell wird früh geplant und möglichst einfach gehalten.     |
| R-04 | Frontend und Backend passen nicht gut zusammen.                          | Die Schnittstellen werden früh abgestimmt.                            |
| R-05 | Es entstehen Zeitprobleme im Team.                                       | Aufgaben werden klar verteilt und regelmäßig besprochen.              |
