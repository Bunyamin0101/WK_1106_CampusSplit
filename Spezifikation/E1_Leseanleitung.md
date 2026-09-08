# E1 — Leseanleitung

E1 erklärt, wie die Spezifikation von CampusSplit aufgebaut ist und wie die einzelnen Bausteine gelesen werden sollen. Die Leseanleitung richtet sich vor allem an das Entwicklungsteam, Prüfer, Betreuer und neue Mitwirkende.

Die Spezifikation beschreibt fachlich, was CampusSplit leisten soll. Technische Details werden nur dort erwähnt, wo sie für das Verständnis notwendig sind.

---

## E1.1 Zweck der Spezifikation

Die Spezifikation beschreibt die Webanwendung CampusSplit fachlich und nachvollziehbar. Sie legt fest, welche Funktionen zum System gehören und welche bewusst nicht umgesetzt werden.

CampusSplit dient zur Verwaltung gemeinsamer Ausgaben in Gruppen. Nutzer können Gruppen erstellen, Ausgaben erfassen, Kosten auf Gruppenmitglieder verteilen, offene Salden anzeigen und Ausgabenübersichten exportieren.

Die Spezifikation soll:

- den fachlichen Umfang festlegen,
- Anforderungen nachvollziehbar dokumentieren,
- Begriffe einheitlich verwenden,
- die spätere Umsetzung vorbereiten,
- Missverständnisse im Team vermeiden.

---

## E1.2 Zielgruppen

| Zielgruppe | Interesse |
|---|---|
| Entwicklungsteam | Grundlage für Umsetzung und Tests |
| Prüfer und Betreuer | Prüfung von Struktur und Vollständigkeit |
| Tester | Ableitung von Testfällen |
| Neue Mitwirkende | Schneller Einstieg in das Projekt |

---

## E1.3 Aufbau der Spezifikation

Die Spezifikation ist nach Bausteinen gegliedert. Jeder Baustein behandelt einen bestimmten Teil des Systems.

| Bereich | Baustein | Inhalt |
|---|---|---|
| Projektgrundlagen | [P1](P1_Ziele_und_Rahmenbedingungen.md) | Ziele, Umfang, Nichtziele und Rahmenbedingungen |
| Projektgrundlagen | [P2](P2_Architekturueberblick.md) | Architekturüberblick und Systemkontext |
| Funktionen | [F1](F1-geschaeftsprozesse.md) | Geschäftsprozesse |
| Funktionen | [F2](F2-anwendungsf%C3%A4lle.md) | Anwendungsfälle |
| Funktionen | [F3](F3-anwendungsfunktionen.md) | Anwendungsfunktionen |
| Daten | [D1](D1_Datenmodell.md) | Datenmodell |
| Daten | [D2](D2_Datentypenverzeichnis.md) | Datentypenverzeichnis |
| Benutzerschnittstelle | [B1](B1_Dialogspezifikation.md) | Dialogspezifikation |
| Benutzerschnittstelle | [B2](B2_Batch.md) | Batch-Prozesse |
| Benutzerschnittstelle | [B3](B3_Druckausgaben.md) | Druck- und Exportausgaben |
| Schnittstellen | [S1](S1_Nachbarsysteme.md) | Nachbarsysteme |
| Schnittstellen | [S2](S2_Datenmigration.md) | Datenmigration |
| Schnittstellen | [S3](S3_Inbetriebnahme.md) | Inbetriebnahme |
| Übergreifendes | [N1](N1_Nichtfunktionale%20Anforderungen.md) | Nichtfunktionale Anforderungen |
| Übergreifendes | [N2](N2_Querschnittskonzepte.md) | Querschnittskonzepte |
| Ergänzendes | E1 | Leseanleitung |
| Ergänzendes | [E2](E2_Glossar.md) | Glossar |

---

## E1.4 Empfohlene Lesereihenfolge

Für einen vollständigen Überblick wird folgende Reihenfolge empfohlen:

1. [P1](P1_Ziele_und_Rahmenbedingungen.md) und [P2](P2_Architekturueberblick.md) für Ziele, Umfang und Systemkontext
2. [F1](F1-geschaeftsprozesse.md), [F2](F2-anwendungsf%C3%A4lle.md) und [F3](F3-anwendungsfunktionen.md) für Abläufe, Use Cases und Funktionen
3. [D1](D1_Datenmodell.md) und [D2](D2_Datentypenverzeichnis.md) für Daten und Datentypen
4. [B1](B1_Dialogspezifikation.md), [B2](B2_Batch.md) und [B3](B3_Druckausgaben.md) für Dialoge, Batch-Einordnung und Exporte
5. [S1](S1_Nachbarsysteme.md), [S2](S2_Datenmigration.md) und [S3](S3_Inbetriebnahme.md) für Schnittstellen, Migration und Inbetriebnahme
6. [N1](N1_Nichtfunktionale%20Anforderungen.md) und [N2](N2_Querschnittskonzepte.md) für Qualitätsanforderungen und übergreifende Regeln
7. [E2](E2_Glossar.md) für zentrale Begriffe

Einzelne Bausteine können auch gezielt gelesen werden, wenn nur ein bestimmtes Thema relevant ist.

---

## E1.5 Nummerierung und Bezeichner

Die Spezifikation verwendet feste Bezeichner. Dadurch können Inhalte eindeutig referenziert werden.

| Präfix | Bedeutung | Beispiel |
|---|---|---|
| P | Projektgrundlagen | P1 |
| F | Funktionen | F2 |
| D | Daten | D1 |
| B | Benutzerschnittstelle | B1 |
| S | Schnittstellen | S1 |
| N | Nichtfunktionales und Querschnittliches | N2 |
| E | Ergänzende Bausteine | E2 |
| UC | Use Case | UC-08 |
| AF | Anwendungsfunktion | AF-02 |
| DLG | Dialog | DLG-07 |
| NFR | Nichtfunktionale Anforderung | NFR-12a-01 |
| INV | Invariante | INV-05 |
| EXP | Exportregel | EXP-01 |

Bezeichner sollen möglichst nicht nachträglich geändert werden, damit Links und Querverweise stabil bleiben.

---

## E1.6 Sprach- und Schreibkonventionen

Die Spezifikation ist in deutscher Sprache verfasst. Fachliche Begriffe sollen einheitlich verwendet werden. Zentrale Begriffe werden im [Glossar](E2_Glossar.md) erklärt.

| Formulierung | Bedeutung |
|---|---|
| muss | verbindliche Anforderung |
| soll | gewünschtes Verhalten |
| kann | optionale Möglichkeit |
| nicht Bestandteil | bewusst außerhalb des Projektumfangs |
| nicht anwendbar | passt fachlich nicht zum Projekt |

Technische Begriffe werden nur verwendet, wenn sie für das Verständnis notwendig sind.

---

## E1.7 Umgang mit „nicht anwendbar“

Einige Bausteine oder Themen passen nicht zur ersten Version von CampusSplit. Diese Inhalte werden nicht einfach weggelassen, sondern als **nicht anwendbar** oder **nicht Bestandteil** gekennzeichnet.

| Thema | Status | Grund |
|---|---|---|
| [B2 Batch-Prozesse](B2_Batch.md) | nicht anwendbar | keine zeitgesteuerten Batch-Prozesse geplant |
| [S2 Datenmigration](S2_Datenmigration.md) | nicht anwendbar | neues Projekt ohne Altdaten |
| Zahlungsabwicklung | nicht Bestandteil | Zahlungen erfolgen außerhalb der Anwendung |
| Bankintegration | nicht Bestandteil | keine Verarbeitung von Bankdaten |
| OCR oder KI-Erkennung | nicht Bestandteil | Belege werden nicht automatisch erkannt |
| Mehrwährungen | nicht Bestandteil der ersten Version | erste Version verwendet Euro |

---

## E1.8 Konsistenzregeln

Die Bausteine hängen inhaltlich zusammen. Deshalb müssen Änderungen an einer Stelle auch in den betroffenen anderen Bausteinen geprüft werden.

| Regel | Beschreibung |
|---|---|
| KR-01 | Ziele und Nichtziele aus [P1](P1_Ziele_und_Rahmenbedingungen.md) begrenzen den Umfang. |
| KR-02 | Use Cases aus [F2](F2-anwendungsf%C3%A4lle.md) sollen durch passende Dialoge in [B1](B1_Dialogspezifikation.md) unterstützt werden. |
| KR-03 | Daten aus Use Cases oder Dialogen sollen in [D1](D1_Datenmodell.md) oder [D2](D2_Datentypenverzeichnis.md) beschrieben sein. |
| KR-04 | Berechnungen aus [F3](F3-anwendungsfunktionen.md) müssen zum Datenmodell passen. |
| KR-05 | Exportinhalte aus [B3](B3_Druckausgaben.md) müssen aus gespeicherten Daten ableitbar sein. |
| KR-06 | Begriffe sollen einheitlich verwendet und bei Bedarf in [E2](E2_Glossar.md) erklärt werden. |

---

## E1.9 Abgrenzung zwischen Spezifikation, Architektur und Implementierung

Die Spezifikation beschreibt hauptsächlich, **was** CampusSplit leisten soll. Die genaue technische Umsetzung wird nicht vollständig in der Spezifikation festgelegt.

| Ebene | Inhalt |
|---|---|
| Spezifikation | Anforderungen, Use Cases, Daten, Dialoge und Qualitätsanforderungen |
| Architektur | Struktur, Komponenten, Schnittstellen und Deployment |
| Implementierung | Quellcode, Tests, Frameworks und konkrete Datenbankstruktur |

---

## E1.10 Umgang mit Änderungen

Änderungen an der Spezifikation sollen nur gemacht werden, wenn die betroffenen Bausteine mitgeprüft werden. Besonders wichtig ist, dass Nichtziele aus [P1](P1_Ziele_und_Rahmenbedingungen.md) nicht unbeabsichtigt aufgehoben werden.

Bei Änderungen sollen vor allem diese Fragen geprüft werden:

- Passt die Änderung noch zum Projektumfang?
- Sind Use Cases, Datenmodell und Dialoge betroffen?
- Müssen Begriffe im Glossar angepasst werden?
- Entstehen neue Anforderungen an Sicherheit, Export oder Inbetriebnahme?

---

## E1.11 Nicht Bestandteil von E1

E1 ist nur eine Leseanleitung. Fachliche Details stehen in den jeweiligen Bausteinen.

| Thema | Zuständiger Baustein |
|---|---|
| Ziele und Umfang | [P1](P1_Ziele_und_Rahmenbedingungen.md) |
| Systemkontext | [P2](P2_Architekturueberblick.md) |
| Geschäftsprozesse | [F1](F1-geschaeftsprozesse.md) |
| Anwendungsfälle | [F2](F2-anwendungsf%C3%A4lle.md) |
| Anwendungsfunktionen | [F3](F3-anwendungsfunktionen.md) |
| Datenmodell | [D1](D1_Datenmodell.md) |
| Datentypen | [D2](D2_Datentypenverzeichnis.md) |
| Dialoge | [B1](B1_Dialogspezifikation.md) |
| Exporte | [B3](B3_Druckausgaben.md) |
| Nachbarsysteme | [S1](S1_Nachbarsysteme.md) |
| Nichtfunktionale Anforderungen | [N1](N1_Nichtfunktionale%20Anforderungen.md) |
| Querschnittskonzepte | [N2](N2_Querschnittskonzepte.md) |
| Begriffe | [E2](E2_Glossar.md) |

---

## E1.12 Querverweise

| Baustein | Relevanz |
|---|---|
| [P1 — Ziele und Rahmenbedingungen](P1_Ziele_und_Rahmenbedingungen.md) | Einstieg in Ziele und Umfang |
| [P2 — Architekturüberblick](P2_Architekturueberblick.md) | Systemkontext und grobe Struktur |
| [F2 — Anwendungsfälle](F2-anwendungsf%C3%A4lle.md) | zentrale Nutzeraktionen |
| [B1 — Dialogspezifikation](B1_Dialogspezifikation.md) | Bedienung der Anwendung |
| [D1 — Datenmodell](D1_Datenmodell.md) | gespeicherte Datenobjekte |
| [E2 — Glossar](E2_Glossar.md) | zentrale Begriffe |

---

## Eingesetzte KI-Werkzeuge

ChatGPT wurde unterstützend für Kürzung, Strukturierung und Formulierung verwendet. Die Inhalte wurden anschließend fachlich geprüft und an die übrigen Spezifikationsbausteine angepasst.
