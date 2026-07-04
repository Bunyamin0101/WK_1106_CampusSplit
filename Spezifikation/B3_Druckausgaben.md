# B3 — Druck- und Exportausgaben

B3 legt fest, was in der Datei steht, die CampusSplit beim Export erzeugt (UC-12, ausgelöst über DLG-11). Der technische Aufbau (PDF-Bibliothek, CSV-Zeichenkodierung), wird in der Architektur beschrieben.

Eine eigene Druckfunktion gibt es nicht. Der PDF-Export eignet sich zum Ausdrucken, aber gedruckt wird über den Browser oder PDF-Viewer und nicht über eine CampusSplit-eigene Funktion.

## B3.1 Formate

| Format | Wert | Zweck |
|---|---|---|
| PDF | `PDF` | Lesbare Übersicht, auch zum Ausdrucken |
| CSV | `CSV` | Tabellarisch, zur Weiterverarbeitung |

Ein Export bezieht sich immer auf genau eine Gruppe und auf den aktuellen Datenstand.

## B3.2 Kern Elemente

Wichtige Kernelemente sind:

- Gruppenname und Exportdatum, ggf. gewählter Zeitraum
- Mitgliederliste
- Ausgabenliste (Datum, Beschreibung, Kategorie, Zahler, Betrag)
- Kostenanteile je Ausgabe
- Saldenübersicht je Mitglied
- Ausgleichsvorschläge

Keine Passwörter, Passwort-Hashes oder Sitzungsdaten , da es von  N2.8 (Exportsicherheit) gereglt wird.

## B3.3 PDF-Export

Aufbau als lesbares Dokument:

1. Kopf: Gruppenname, Exportdatum, ggf. Zeitraum
2. Mitgliederliste
3. Ausgabenliste
4. Saldenübersicht : Gleiche Beschriftung wie in DLG-10 („bekommt zurück" / „schuldet" / „ausgeglichen")
5. Ausgleichsvorschläge
6. Fußzeile mit Hinweis: Das ist eine berechnete Übersicht, die eigentliche Zahlung läuft außerhalb von CampusSplit

Beträge immer in Euro mit zwei Nachkommastellen.

## B3.4 CSV-Export

Zwei Teile:

**Ausgaben**, eine Zeile pro Ausgabe: Datum, Beschreibung, Kategorie, Zahler, Betrag, dazu der Anteil jedes beteiligten Mitglieds.

**Salden**, eine Zeile pro Mitglied: Name, Saldo (positiv, negativ oder 0,00).

Ob beides in einer Datei mit zwei Abschnitten oder als zwei separate Dateien kommt, entscheidet die Architektur. Fachlich müssen einfach beide Teile vollständig drin sein.

## B3.5 Regeln

| ID | Regel |
|---|---|
| EXP-01 | Export enthält immer die Mindestinhalte aus B3.2. |
| EXP-02 | Export bezieht sich auf genau eine Gruppe. |
| EXP-03 | Beträge in Euro, zwei Nachkommastellen. |
| EXP-04 | Salden und Ausgleichsvorschläge im Export stimmen mit der aktuellen Berechnung überein. |
| EXP-05 | PDF ist wie in B3.3 gegliedert. |
| EXP-06 | CSV enthält Ausgaben- und Saldenteil wie in B3.4. |
| EXP-07 | Ein Export verändert keine gespeicherten Daten. |
| EXP-08 | Sicherheitsregeln aus N2.8 (EXP-SEC-01 bis 07) gelten zusätzlich. |

## B3.6 Nicht Bestandteil von B3

PDF-Bibliothek, CSV-Trennzeichen und -Kodierung, Layout/Design, eine eigene Druckfunktion, dauerhafte Speicherung der Exportdatei, automatischer Versand (z. B. per Mail), weitere Formate wie Excel oder JSON.

## B3.7 Querverweise

| Baustein | Relevanz |
|---|---|
| F2 | UC-12 löst den Export aus. |
| F3 | Berechnet die Werte, die exportiert werden. |
| D1 / D2 | Liefern Datenobjekte und Datentypen für den Export. |
| B1 | DLG-11 startet den Export. |
| N2 | N2.8 regelt die Exportsicherheit. |

## Eingesetzte KI-Werkzeuge

Claude (Anthropic): Für die Verbindung und Expandierung unterschiedliche Ideen und Bausteine sowie die saubere Formulierung

Entwurf geprüft von Momosan009.
