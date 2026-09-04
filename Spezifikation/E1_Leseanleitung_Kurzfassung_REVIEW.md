# E1 — Leseanleitung (Kurzfassung nach Review)

Diese Kurzfassung setzt die Review-Hinweise zu E1 um. Die vorhandene ausführliche Datei bleibt unverändert bestehen.

Ziel dieser Datei ist eine knappe Orientierung ohne lange Lesebeispiele und ohne überflüssige Querverweise.

---

## E1.1 Zweck

Diese Spezifikation beschreibt CampusSplit fachlich nach Siedersleben-Bausteinen. Sie dient als Grundlage für Architektur, Implementierung, Tests und Review.

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Benutzer:innen können Gruppen erstellen, Ausgaben erfassen, Kosten aufteilen, Salden anzeigen und Exporte erzeugen.

---

## E1.2 Aufbau

| Block | Bausteine | Inhalt |
|---|---|---|
| Projektgrundlagen | P1, P2 | Ziele, Rahmenbedingungen, Systemkontext |
| Abläufe und Funktionen | F1, F2, F3 | Geschäftsprozess, Use Cases, Anwendungsfunktionen |
| Daten | D1, D2 | Datenmodell und Datentypen |
| Benutzerschnittstelle | B1, B2, B3 | Dialoge, Batch-Einordnung, Exporte |
| Schnittstellen | S1, S2, S3 | Nachbarsysteme, Datenmigration, Inbetriebnahme |
| Übergreifendes | N1, N2 | Qualitätsanforderungen und Querschnittskonzepte |
| Ergänzendes | E1, E2 | Leseanleitung und Glossar |

---

## E1.3 Lesereihenfolge

Für einen schnellen Überblick wird folgende Reihenfolge empfohlen:

1. P1 — Ziele und Rahmenbedingungen
2. P2 — Architekturüberblick
3. F2 — Anwendungsfälle
4. D1/D2 — Datenmodell und Datentypen
5. B1/B3 — Dialoge und Exporte
6. S1/S3 — Schnittstellen und Inbetriebnahme
7. N1/N2 — Qualitätsanforderungen und Querschnittskonzepte
8. E2 — Glossar

F1 und F3 sollten ergänzend gelesen werden, wenn Geschäftsprozess oder Berechnungslogik im Detail geprüft werden.

---

## E1.4 Kennzeichnungen

| Kennzeichnung | Bedeutung |
|---|---|
| muss | verbindliche Anforderung |
| soll | gewünschtes Verhalten |
| kann | optionale oder spätere Erweiterung |
| nicht Bestandteil | bewusst außerhalb des Projektumfangs |
| nicht anwendbar | Baustein wurde geprüft, passt aber fachlich nicht zum Projekt |

---

## E1.5 Nicht anwendbare Bausteine

| Baustein | Einordnung | Begründung |
|---|---|---|
| B2 Batch | nicht anwendbar | CampusSplit verwendet keine zeitgesteuerten Batch-Prozesse. |
| S2 Datenmigration | nicht anwendbar | CampusSplit ist ein Greenfield-Projekt ohne Altdatenmigration. |

---

## E1.6 Änderungshinweis

Diese Kurzfassung ersetzt keine fachlichen Bausteine. Sie dient nur als Einstieg. Fachliche Regeln stehen in den jeweiligen Hauptbausteinen.

Änderungen an Use Cases, Datenmodell, Geldbeträgen, Währungen oder Exporten müssen immer in den betroffenen Bausteinen geprüft werden.
