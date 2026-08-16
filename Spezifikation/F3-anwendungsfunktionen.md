# F3 - Anwendungsfunktionen

Anwendungsfunktionen beschreiben wiederverwendbare fachliche Funktionen, die von einem oder mehreren Use Cases verwendet werden. Sie enthalten genug fachliche oder algorithmische Substanz, um außerhalb einzelner Anwendungsfälle beschrieben zu werden.

F3 beschreibt keine Dialoge, keine Controller, keine Datenbankoperationen und keine technische Architektur. Diese Themen werden in B1, D1/D2 und in der Architekturdokumentation behandelt.

Für CampusSplit stehen vor allem die Funktionen im Mittelpunkt, die gemeinsame Ausgaben fachlich auswerten: Kostenaufteilung, Saldenberechnung, Ausgleichsvorschläge und Exportaufbereitung.

## F3.1 Funktionskatalog

| ID                                              | Funktion                       | Zweck                                                                              |
| ----------------------------------------------- | ------------------------------ | ---------------------------------------------------------------------------------- |
| [AF-01](#af-01--kostenanteile-berechnen)        | Kostenanteile berechnen        | Ermittelt, welchen Anteil jedes beteiligte Gruppenmitglied an einer Ausgabe trägt. |
| [AF-02](#af-02--gruppensalden-berechnen)        | Gruppensalden berechnen        | Berechnet pro Gruppenmitglied, ob es Geld bekommt oder schuldet.                   |
| [AF-03](#af-03--ausgleichsvorschläge-berechnen) | Ausgleichsvorschläge berechnen | Ermittelt, welche Zahlungen offene Salden möglichst einfach ausgleichen.           |
| [AF-04](#af-04--exportdaten-aufbereiten)        | Exportdaten aufbereiten        | Bereitet Ausgaben und Salden für PDF- oder CSV-Export fachlich auf.                |

## F3.2 Funktionsbeschreibungen

### AF-01 — Kostenanteile berechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Zweck**                  | Berechnet für eine Ausgabe die Kostenanteile der beteiligten Gruppenmitglieder.                                                                                                                                                                                                                                                                                                                                                                      |
| **Eingaben**               | Gesamtbetrag der Ausgabe, zahlendes Mitglied, beteiligte Mitglieder, Aufteilungsart, optional individuelle Beträge.                                                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Liste von Kostenanteilen pro beteiligtes Mitglied.                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Regeln und Invarianten** | Der Gesamtbetrag muss größer als 0 sein. Mindestens ein Mitglied muss beteiligt sein. Der Zahler muss Mitglied der Gruppe sein. Bei gleichmäßiger Aufteilung wird der Betrag centgenau auf alle Beteiligten verteilt. Rundungsdifferenzen werden deterministisch verteilt, sodass die Summe aller Kostenanteile exakt dem Gesamtbetrag entspricht. Bei individueller Aufteilung muss die Summe der Einzelanteile exakt dem Gesamtbetrag entsprechen. |
| **Verwendet von**          | UC-08 Ausgabe erfassen, UC-09 Ausgabe bearbeiten.                                                                                                                                                                                                                                                                                                                                                                                                    |

#### Fachliche Beschreibung

Wenn eine Ausgabe erfasst wird, muss CampusSplit bestimmen, welchen Anteil jedes beteiligte Mitglied trägt.

Beispiel:

Eine Ausgabe von 30,00 € wird auf drei Personen gleichmäßig verteilt.

| Mitglied | Kostenanteil |
| -------- | ------------ |
| Person A | 10,00 €      |
| Person B | 10,00 €      |
| Person C | 10,00 €      |

Bei nicht glatt teilbaren Beträgen wird centgenau gerundet.

Beispiel:

10,00 € werden auf drei Personen verteilt.

| Mitglied | Kostenanteil |
| -------- | ------------ |
| Person A | 3,34 €       |
| Person B | 3,33 €       |
| Person C | 3,33 €       |

Die Summe bleibt exakt 10,00 €.

### AF-02 — Gruppensalden berechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                        |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Zweck**                  | Berechnet pro Gruppenmitglied den aktuellen Saldo innerhalb einer Gruppe.                                                                                                                                                                                                                     |
| **Eingaben**               | Alle Ausgaben einer Gruppe, alle Kostenanteile, zahlende Mitglieder.                                                                                                                                                                                                                          |
| **Ausgaben**               | Saldo pro Gruppenmitglied.                                                                                                                                                                                                                                                                    |
| **Regeln und Invarianten** | Zahlungen erhöhen den Saldo des zahlenden Mitglieds. Kostenanteile verringern den Saldo des beteiligten Mitglieds. Ein positiver Saldo bedeutet: Mitglied bekommt Geld zurück. Ein negativer Saldo bedeutet: Mitglied schuldet Geld. Die Summe aller Salden einer Gruppe muss 0,00 € ergeben. |
| **Verwendet von**          | UC-06 Gruppe anzeigen, UC-08 Ausgabe erfassen, UC-09 Ausgabe bearbeiten, UC-10 Ausgabe löschen, UC-11 Salden anzeigen, UC-12 Ausgabenübersicht exportieren.                                                                                                                                   |

#### Fachliche Beschreibung

CampusSplit berechnet für jedes Gruppenmitglied:

Saldo = gezahlte Beträge - eigene Kostenanteile

Beispiel:

| Mitglied | Gezahlt | Eigener Kostenanteil | Saldo     |
| -------- | ------- | -------------------- | --------- |
| Person A | 30,00 € | 10,00 €              | +20,00 €  |
| Person B | 0,00 €  | 10,00 €              | \-10,00 € |
| Person C | 0,00 €  | 10,00 €              | \-10,00 € |

Interpretation:

- Person A bekommt 20,00 € zurück.
- Person B schuldet 10,00 €.
- Person C schuldet 10,00 €.

Die Summe der Salden ist 0,00 €.

### AF-03 — Ausgleichsvorschläge berechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Zweck**                  | Ermittelt konkrete Zahlungsvorschläge, mit denen offene Gruppensalden ausgeglichen werden können.                                                                                                                                                                                                                                    |
| **Eingaben**               | Berechnete Gruppensalden aus AF-02.                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Liste von vorgeschlagenen Zahlungen zwischen Schuldnern und Gläubigern.                                                                                                                                                                                                                                                              |
| **Regeln und Invarianten** | Mitglieder mit negativem Saldo gelten als Schuldner. Mitglieder mit positivem Saldo gelten als Gläubiger. Ein Ausgleichsvorschlag enthält Schuldner, Gläubiger und Betrag. Die Vorschläge verändern keine echten Zahlungen und führen keine Transaktionen aus. Nach vollständiger Anwendung der Vorschläge wären alle Salden 0,00 €. |
| **Verwendet von**          | UC-11 Salden anzeigen, UC-12 Ausgabenübersicht exportieren.                                                                                                                                                                                                                                                                          |

#### Fachliche Beschreibung

CampusSplit schlägt vor, wer wem welchen Betrag zahlen sollte.

Beispiel:

| Mitglied | Saldo     |
| -------- | --------- |
| Person A | +20,00 €  |
| Person B | \-10,00 € |
| Person C | \-10,00 € |

Daraus entstehen folgende Ausgleichsvorschläge:

| Schuldner | Gläubiger | Betrag  |
| --------- | --------- | ------- |
| Person B  | Person A  | 10,00 € |
| Person C  | Person A  | 10,00 € |

CampusSplit führt diese Zahlung nicht selbst aus. Die Zahlung erfolgt außerhalb der Anwendung.

### AF-04 — Exportdaten aufbereiten

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Zweck**                  | Bereitet die fachlichen Daten einer Gruppe so auf, dass sie als PDF- oder CSV-Datei exportiert werden können.                                                                                                                                                                                                                                                                   |
| **Eingaben**               | Gruppendaten, Mitglieder, Ausgaben, Kostenanteile, Salden und Ausgleichsvorschläge.                                                                                                                                                                                                                                                                                             |
| **Ausgaben**               | Strukturierte Exportdaten für PDF- oder CSV-Erzeugung.                                                                                                                                                                                                                                                                                                                          |
| **Regeln und Invarianten** | Exportdaten enthalten Gruppenname, Exportdatum, Ausgabenliste, beteiligte Mitglieder und Saldenübersicht. Beträge werden einheitlich in Euro und mit zwei Nachkommastellen dargestellt. Die exportierten Salden müssen mit der aktuellen Saldenberechnung aus AF-02 übereinstimmen. Die Exportdaten dürfen keine Passwörter oder sicherheitsrelevanten Informationen enthalten. |
| **Verwendet von**          | UC-12 Ausgabenübersicht exportieren, B3 Druckausgaben.                                                                                                                                                                                                                                                                                                                          |

#### Fachliche Beschreibung

CampusSplit erzeugt eine strukturierte Übersicht der Gruppenausgaben.

Ein Export kann enthalten:

- Gruppenname
- Zeitraum
- Liste aller Ausgaben
- Zahler je Ausgabe
- Beteiligte Mitglieder
- Kostenanteile
- Saldenübersicht
- Ausgleichsvorschläge

Die eigentliche technische Erzeugung der Datei wird nicht in F3 beschrieben. F3 beschreibt nur, welche fachlichen Daten für den Export vorbereitet werden.


## F3.3 Querverweise

| Baustein | Relevanz für F3                                                                                 |
| -------- | ----------------------------------------------------------------------------------------------- |
| P1       | Definiert das Ziel, gemeinsame Ausgaben transparent und fair zu verwalten.                      |
| F1       | Die Aktivitäten A5 bis A10 motivieren Kostenaufteilung, Saldenberechnung und Export.            |
| F2       | UC-08 bis UC-12 verwenden die beschriebenen Anwendungsfunktionen.                               |
| D1       | Definiert die Datenobjekte Benutzer, Gruppe, Ausgabe, Kostenanteil und Saldo.                   |
| D2       | Definiert die Datentypen für Betrag, Datum, Aufteilungsart und Exportformat.                    |
| B1       | Dialoge lösen die Funktionen über Benutzeraktionen aus.                                         |
| B3       | Exportausgaben nutzen die aufbereiteten Exportdaten aus AF-04.                                  |
| N1       | Anforderungen an Datenkonsistenz, Performance und Sicherheit beeinflussen die Funktionen.       |
| N2       | Validierung, Fehlerbehandlung und Autorisierung wirken auf die Ausführung der Funktionen.       |
| E2       | Begriffe wie Ausgabe, Kostenanteil, Saldo, Schuldner und Gläubiger werden im Glossar definiert. |
