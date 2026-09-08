# P1 — Ziele und Rahmenbedingungen

Grundlagenbaustein der CampusSplit-Spezifikation nach Siedersleben. Dieser Baustein beschreibt, warum das System entwickelt wird, für wen es gedacht ist und welche Rahmenbedingungen den Lösungsraum eingrenzen.

---

## P1.1 Mission

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Nutzer können Gruppen für Wohngemeinschaften, Reisen oder studentische Projekte erstellen, Ausgaben erfassen und Kosten auf Gruppenmitglieder aufteilen.

Das System soll den Aufwand verringern, der bei gemeinsamen Ausgaben oft durch Chatnachrichten, Tabellen oder manuelle Berechnungen entsteht. Nutzer sollen schnell sehen können, welche Ausgaben es gibt und wer wem noch Geld schuldet.

CampusSplit ersetzt keine echte Zahlungsabwicklung. Die Anwendung dokumentiert gemeinsame Kosten, berechnet offene Beträge und ermöglicht den Export von Ausgabenübersichten. Die fachlichen Abläufe werden in [F1 — Geschäftsprozesse](F1-geschaeftsprozesse.md) und [F2 — Anwendungsfälle](F2-anwendungsf%C3%A4lle.md) genauer beschrieben.

---

## P1.2 Ziele des Systems

| ID | Ziel |
|---|---|
| Z-01 | Nutzer können sich registrieren und anmelden. |
| Z-02 | Nutzer können Gruppen erstellen und verwalten. |
| Z-03 | Mitglieder können einer Gruppe hinzugefügt werden. |
| Z-04 | Ausgaben können erfasst und gespeichert werden. |
| Z-05 | Kosten können auf Gruppenmitglieder aufgeteilt werden. |
| Z-06 | Offene Beträge werden automatisch berechnet. |
| Z-07 | Nutzer können sehen, wer wem Geld schuldet. |
| Z-08 | Ausgabenübersichten können exportiert werden. |
| Z-09 | Die Anwendung soll einfach bedienbar sein. |

Die genaue Beschreibung der Anwendungsfälle steht in [F2 — Anwendungsfälle](F2-anwendungsf%C3%A4lle.md). Die Berechnung offener Beträge wird in [F3 — Anwendungsfunktionen](F3-anwendungsfunktionen.md) beschrieben.

---

## P1.3 Stakeholder und Benutzergruppen

| Rolle | Beschreibung | Interesse |
|---|---|---|
| Nutzer | Person, die CampusSplit verwendet. | Gruppen und Ausgaben verwalten. |
| Gruppenmitglied | Mitglied einer Gruppe. | Ausgaben und offene Beträge ansehen. |
| Gruppenersteller | Erstellt eine Gruppe. | Mitglieder hinzufügen und Gruppe verwalten. |
| Entwicklungsteam | Fünf Studierende. | Anwendung planen, umsetzen und dokumentieren. |
| Dozent / Prüfer | Bewertet das Projekt. | Nachvollziehbare Spezifikation prüfen. |

Eine komplexe Rollenverwaltung ist für die erste Version nicht geplant. Es reicht aus, wenn Nutzer Gruppen erstellen, Mitglieder verwalten und Ausgaben eintragen können. Weitere Regeln zu Rollen und Zugriff können in [N2 — Querschnittskonzepte](N2_Querschnittskonzepte_%28ZO%29.md) beschrieben werden.

---

## P1.4 Projektumfang

Zum geplanten Umfang gehören die wichtigsten Funktionen, die für die Verwaltung gemeinsamer Ausgaben notwendig sind:

- Registrierung von Nutzern
- Anmeldung von Nutzern
- Speicherung des angemeldeten Zustands während der Nutzung
- Erstellung von Gruppen
- Verwaltung von Gruppenmitgliedern
- Erfassen gemeinsamer Ausgaben
- Zuordnung einer Ausgabe zu einer Gruppe
- Angabe, wer eine Ausgabe bezahlt hat
- Aufteilung der Kosten auf Gruppenmitglieder
- automatische Berechnung offener Beträge
- Anzeige einer Übersicht über Ausgaben und Schulden
- Export einer Ausgabenübersicht

Die Dialoge der Anwendung werden in [B1 — Dialogspezifikation](B1_Dialogspezifikation.md) beschrieben. Die Exportausgaben werden in [B3 — Druck- und Exportausgaben](B3_Druckausgaben.md) festgelegt.

---

## P1.5 Nichtziele

Einige Funktionen werden in der ersten Version bewusst nicht umgesetzt. Dadurch bleibt der Projektumfang für ein Semester realistisch.

| ID | Nichtziel | Begründung |
|---|---|---|
| NZ-01 | Direkte Zahlungsabwicklung | Zahlungen erfolgen außerhalb der Anwendung. |
| NZ-02 | Bankanbindung | Für die Grundfunktion nicht notwendig. |
| NZ-03 | Mobile App | Eine Webanwendung reicht aus. |
| NZ-04 | Chatfunktion | Kommunikation ist nicht der Schwerpunkt. |
| NZ-05 | Mehrere Währungen | Die erste Version nutzt nur Euro. |
| NZ-06 | OCR-Erkennung von Belegen | Würde den Umfang zu stark erhöhen. |
| NZ-07 | Datenmigration | CampusSplit wird neu entwickelt. |

Die Abgrenzung zu externen Systemen wird in [P2 — Architekturüberblick](P2-Architekturueberblick.md) und [S1 — Nachbarsysteme](S1_Nachbarsysteme.md) genauer beschrieben.

---

## P1.6 Rahmenbedingungen

CampusSplit wird im Rahmen eines Hochschulprojekts im vierten Semester des Studiengangs Wirtschaftsinformatik entwickelt. Das Projektteam besteht aus fünf Personen.

Die Anwendung soll mit Java umgesetzt werden. Als Entwicklungsumgebung wird Visual Studio Code verwendet. Da CampusSplit eine Webanwendung ist, soll das System über einen Browser nutzbar sein.

Für die Anwendung müssen Daten dauerhaft gespeichert werden. Dazu gehören zum Beispiel Benutzerkonten, Gruppen, Mitglieder, Ausgaben und Berechnungsgrundlagen für offene Beträge. Das Datenmodell wird in [D1 — Datenmodell](D1_Datenmodell_%28ZO%29.md) und [D2 — Datentypenverzeichnis](D2_Datentypenverzeichnis_%28ZO%29.md) genauer beschrieben.

| ID | Rahmenbedingung |
|---|---|
| RB-01 | Umsetzung durch fünf Studierende. |
| RB-02 | Umsetzung als Webanwendung. |
| RB-03 | Entwicklung mit Java und Visual Studio Code. |
| RB-04 | Dauerhafte Speicherung von Daten. |
| RB-05 | Registrierung und Anmeldung sind erforderlich. |
| RB-06 | Der Umfang muss für ein Semester realistisch bleiben. |
| RB-07 | Die Anwendung soll einfach zu bedienen sein. |

---

## P1.7 Erfolgskriterien

Das Projekt gilt als erfolgreich, wenn die wichtigsten Funktionen umgesetzt wurden und nachvollziehbar funktionieren.

| ID | Erfolgskriterium |
|---|---|
| EK-01 | Nutzer können ein Konto erstellen. |
| EK-02 | Nutzer können sich anmelden. |
| EK-03 | Gruppen können erstellt und angezeigt werden. |
| EK-04 | Mitglieder können Gruppen zugeordnet werden. |
| EK-05 | Ausgaben können erfasst und gespeichert werden. |
| EK-06 | Offene Beträge werden korrekt berechnet. |
| EK-07 | Die Ergebnisse werden verständlich angezeigt. |
| EK-08 | Eine Ausgabenübersicht kann exportiert werden. |
| EK-09 | Die Anwendung ist im Browser nutzbar. |

Nichtfunktionale Anforderungen, zum Beispiel Bedienbarkeit und Sicherheit, werden in [N1 — Nichtfunktionale Anforderungen](N1_Nichtfunktionale%20Anforderungen_%28ZO%29.md) beschrieben.

---

## P1.8 Annahmen

Für die erste Version von CampusSplit gelten folgende Annahmen:

| ID | Annahme |
|---|---|
| A-01 | Nutzer besitzen ein Gerät mit aktuellem Webbrowser. |
| A-02 | Gruppen bestehen aus einer überschaubaren Anzahl an Personen. |
| A-03 | Es wird zunächst nur mit Euro gerechnet. |
| A-04 | Ausgaben werden manuell eingetragen. |
| A-05 | Zahlungen finden außerhalb der Anwendung statt. |

---

## P1.9 Risiken

| ID | Risiko | Gegenmaßnahme |
|---|---|---|
| R-01 | Offene Beträge werden falsch berechnet. | Berechnung früh mit Beispielen testen. |
| R-02 | Der Projektumfang wird zu groß. | Zuerst auf Kernfunktionen konzentrieren. |
| R-03 | Daten werden unpassend gespeichert. | Datenmodell früh planen. |
| R-04 | Frontend und Backend passen nicht zusammen. | Schnittstellen früh abstimmen. |
| R-05 | Zeitprobleme im Team. | Aufgaben klar verteilen. |

---

## P1.10 Querverweise

| Baustein | Relevanz |
|---|---|
| [P2 — Architekturüberblick](P2-Architekturueberblick.md) | Systemkontext und grobe Struktur. |
| [F1 — Geschäftsprozesse](F1-geschaeftsprozesse.md) | Fachliche Abläufe. |
| [F2 — Anwendungsfälle](F2-anwendungsf%C3%A4lle.md) | Aktionen der Nutzer. |
| [F3 — Anwendungsfunktionen](F3-anwendungsfunktionen.md) | Berechnungen und Funktionen. |
| [D1 — Datenmodell](D1_Datenmodell_%28ZO%29.md) | Gespeicherte Datenobjekte. |
| [D2 — Datentypenverzeichnis](D2_Datentypenverzeichnis_%28ZO%29.md) | Fachliche Datentypen. |
| [B1 — Dialogspezifikation](B1_Dialogspezifikation.md) | Dialoge der Anwendung. |
| [B3 — Druck- und Exportausgaben](B3_Druckausgaben.md) | CSV- und PDF-Export. |
| [S1 — Nachbarsysteme](S1_Nachbarsysteme.md) | Externe Systeme und Schnittstellen. |
| [N1 — Nichtfunktionale Anforderungen](N1_Nichtfunktionale%20Anforderungen_%28ZO%29.md) | Qualitätsanforderungen. |
| [N2 — Querschnittskonzepte](N2_Querschnittskonzepte_%28ZO%29.md) | Zugriff, Validierung und allgemeine Regeln. |

## Eingesetzte KI-Werkzeuge

ChatGPT (OpenAI) wurde unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet. Die fachlichen Inhalte wurden anschließend mit dem Projektkontext und den übrigen Spezifikationsbausteinen abgeglichen.
