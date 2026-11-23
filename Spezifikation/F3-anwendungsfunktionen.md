# F3 - Anwendungsfunktionen

Anwendungsfunktionen beschreiben wiederverwendbare fachliche Funktionen, die von einem oder mehreren Use Cases verwendet werden. Sie enthalten genug fachliche oder algorithmische Substanz, um außerhalb einzelner Anwendungsfälle beschrieben zu werden.

Für CampusSplit stehen vor allem die Funktionen im Mittelpunkt, die gemeinsame Ausgaben fachlich auswerten: Kostenaufteilung, Währungsumrechnung, Saldenberechnung, Ausgleichsvorschläge, Rückzahlungszuordnung und Exportaufbereitung.

##

## F3.1 Funktionskatalog

| ID                                              | Funktion                       | Zweck                                                                              |
| ----------------------------------------------- | ------------------------------ | ---------------------------------------------------------------------------------- |
| [AF-01](#af-01--kostenanteile-berechnen) | Kostenanteile berechnen | Ermittelt, welchen Anteil jedes beteiligte Gruppenmitglied an einer Ausgabe trägt. |
| [AF-02](#af-02--gruppensalden-berechnen) | Gruppensalden berechnen | Berechnet pro Gruppenmitglied, ob es Geld bekommt oder schuldet. |
| [AF-03](#af-03--ausgleichsvorschläge-berechnen) | Ausgleichsvorschläge berechnen | Ermittelt, welche Zahlungen offene Salden möglichst einfach ausgleichen. |
| [AF-04](#af-04--exportdaten-aufbereiten) | Exportdaten aufbereiten | Bereitet Ausgaben, Salden, Ausgleichsvorschläge und Rückzahlungen für PDF- oder CSV-Export fachlich auf. |
| [AF-05](#af-05--fremdwährungsbetrag-umrechnen) | Fremdwährungsbetrag umrechnen | Rechnet Ausgabenbeträge aus einer Fremdwährung zum Ausgabedatum in die Gruppenwährung um. |
| [AF-06](#af-06--rückzahlung-validieren-und-ausgabe-zuordnen) | Rückzahlung validieren und Ausgabe zuordnen | Prüft die Zulässigkeit einer Rückzahlung und verrechnet sie optional gegen den offenen Anteil einer konkreten Ausgabe. |

##

## F3.2 Funktionsbeschreibungen

### AF-01 — Kostenanteile berechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Zweck**                  | Berechnet für eine Ausgabe die Kostenanteile der beteiligten Gruppenmitglieder in der jeweiligen Gruppenwährung.                                                                                                                                                                                                                                                                                                                                                                      |
| **Eingaben**               | Abrechnungsbetrag in Gruppenwährung, zahlendes Mitglied, beteiligte Mitglieder, gewählte Aufteilungsart (gleichmäßig oder individuell), optional individuelle Einzelbeträge.                                                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Liste von centgenauen Kostenanteilen pro beteiligtem Mitglied.                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Regeln und Invarianten** | Der Abrechnungsbetrag muss größer als 0 sein und auf zwei Nachkommastellen genau vorliegen. Mindestens ein Gruppenmitglied muss beteiligt sein; Zahler und alle Beteiligten müssen Mitglieder der Gruppe sein. Bei gleichmäßiger Aufteilung wird der Gesamtbetrag in Cent ganzzahlig durch die Anzahl der Beteiligten geteilt. Verbleibt ein Restbetrag von <i>r</i> Cent, erhalten die ersten beteiligten Mitglieder jeweils einen zusätzlichen Cent +0,01). Die Reihenfolge richtet sich dabei aufsteigend nach der eindeutigen Benutzerkennung der Mitglieder. Bei individueller Aufteilung muss für jedes beteiligte Mitglied ein Betrag &ge; 0,00 angegeben werden, und die Summe aller Einzelanteile muss exakt dem Abrechnungsbetrag entsprechen. |
| **Verwendet von**          | UC-10 Ausgabe erfassen, UC-11 Ausgabe bearbeiten                                                                                                                                                                                                                                                                                                                                                                                                    |

#### Fachliche Beschreibung

Wenn eine Ausgabe erfasst wird, muss CampusSplit bestimmen, welchen Anteil jedes beteiligte Mitglied trägt.

Beispiel:

10,00 € (1.000 Cent) werden gleichmäßig auf drei Personen verteilt. Bei der ganzzahligen Division entfallen auf jede Person 333 Cent (3,33 €), und es bleibt 1 Cent übrig. Da die Beteiligten aufsteigend nach ihrer eindeutigen Benutzerkennung sortiert werden (Person A vor Person B und Person C), erhält Person A den verbleibenden Cent:

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
| **Eingaben**               | Alle Mitglieder der Gruppe, alle Ausgaben der Gruppe (mit Zahler, Abrechnungsbetrag und Kostenanteilen) sowie alle Rückzahlungen der Gruppe (Sender, Empfänger, Betrag und Status).                                                                                                                                                                                                                          |
| **Ausgaben**               | Saldo pro Gruppenmitglied.                                                                                                                                                                                                                                                                    |
| **Regeln und Invarianten** | Bezahlte Ausgaben erhöhen den Saldo des zahlenden Mitglieds um den Abrechnungsbetrag. Kostenanteile verringern den Saldo des jeweiligen Mitglieds. Gesendete Rückzahlungen erhöhen den Saldo des Senders; empfangene Rückzahlungen verringern den Saldo des Empfängers. Stornierte Rückzahlungen werden nicht berücksichtigt. Ein positiver Saldo bedeutet, dass das Mitglied Geld zurückbekommt; ein negativer Saldo bedeutet, dass es Geld schuldet. Die Summe aller Salden einer Gruppe muss immer 0,00 ergeben. |
| **Verwendet von**          | UC-05 Dashboard anzeigen, UC-07 Gruppe anzeigen, UC-10 Ausgabe erfassen, UC-11 Ausgabe bearbeiten, UC-12 Ausgabe löschen, UC-14 Salden anzeigen, UC-15 Rückzahlung erfassen und stornieren, UC-16 Ausgabenübersicht exportieren                                                                                                                                   |

#### Fachliche Beschreibung

CampusSplit berechnet für jedes Gruppenmitglied:

Saldo = bezahlte Ausgaben - eigene Kostenanteile
        + gesendete Rückzahlungen - empfangene Rückzahlungen

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

Die Summe der Salden ist 0,00 €. Stornierte Rückzahlungen werden nicht berücksichtigt; die optionale Ausgabenzuordnung ist keine zusätzliche Saldenbuchung.

## AF-03 — Ausgleichsvorschläge berechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Zweck**                  | Ermittelt konkrete Zahlungsvorschläge, mit denen offene Gruppensalden ausgeglichen werden können.                                                                                                                                                                                                                                    |
| **Eingaben**               | Berechnete Gruppensalden aus AF-02.                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Liste von vorgeschlagenen Zahlungen (Schuldner, Gläubiger, Betrag in Gruppenwährung).                                                                                                                                                                                                                                                              |
| **Regeln und Invarianten** | Die Summe der eingehenden Salden muss 0,00 ergeben. Mitglieder mit negativem Saldo gelten als Schuldner, Mitglieder mit positivem Saldo gelten als Gläubiger (Mitglieder mit ausgeglichenem Saldo werden ignoriert). Schuldner und Gläubiger werden schrittweise gegeneinander verrechnet, indem jeweils der kleinere der beiden offenen Beträge als Zahlungsvorschlag gebildet wird. Die Vorschläge führen keine echten Zahlungen aus. Nach vollständiger Umsetzung aller Vorschläge wären alle Gruppensalden 0,00. |
| **Verwendet von**          | UC-14 Salden anzeigen, UC-15 Rückzahlung erfassen und stornieren, UC-16 Ausgabenübersicht exportieren                                                                                                                                                                                                                                                                          |

#### Fachliche Beschreibung

Auf Basis der berechneten Salden aus AF-02 schlägt CampusSplit vor, wer wem welchen Betrag zahlen sollte:

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

### AF-04 — Exportdaten aufbereiten

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Zweck**                  | Bereitet die fachlichen Daten einer Gruppe so auf, dass sie als PDF- oder CSV-Datei exportiert werden können.                                                                                                                                                                                                                                                                   |
| **Eingaben**               | Gruppendaten (Gruppenname, Gruppenwährung), Ausgaben (inkl. Originalbetrag, Originalwährung, Wechselkurs, Abrechnungsbetrag und Kostenanteilen), Salden (AF-02), Ausgleichsvorschläge (AF-03), Rückzahlungen (inkl. Stornierungsangaben), optionaler Zeitraumfilter sowie gewünschter Exportumfang (gesamte Übersicht, nur offene Salden oder nur Ausgaben).                                                                                                                                                                                                                                                                                             |
| **Ausgaben**               | Strukturierte Exportdaten für die PDF- oder CSV-Erzeugung.                                                                                                                                                                                                                                                                                                                          |
| **Regeln und Invarianten** | Exportdaten enthalten Gruppenname, Gruppenwährung, Exportdatum, gewählten Zeitraum und Exportumfang. Je nach gewähltem Umfang werden Ausgaben (inkl. Fremdwährungsangaben), Salden, Ausgleichsvorschläge und Rückzahlungen aufbereitet. Beträge werden einheitlich mit zwei Nachkommastellen dargestellt. Die exportierten Salden müssen mit der aktuellen Saldenberechnung aus AF-02 übereinstimmen. Passwörter oder sicherheitsrelevante Kontodaten dürfen nicht enthalten sein. |
| **Verwendet von**          | UC-16 Ausgabenübersicht exportieren, B3 Druckausgaben                                                                                                                                                                                                                                                                                                                          |
### AF-05 — Fremdwährungsbetrag umrechnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Zweck**                  | Rechnet einen in einer Fremdwährung erfassten Ausgabenbetrag in die jeweilige Gruppenwährung (EUR oder USD) um.                                                                                                                                                                                                                                    |
| **Eingaben**               | Ursprungsbetrag, Quellwährung (EUR oder USD), Zielwährung (Gruppenwährung: EUR oder USD), Ausgabedatum; bei Bearbeitung zudem ein bereits gespeicherter Wechselkurs samt Kursdatum.                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Umgerechneter Abrechnungsbetrag in Gruppenwährung (auf zwei Nachkommastellen gerundet), verwendeter Wechselkurs sowie Stichtag des Kurses.                                                                                                                                                                                                                                                              |
| **Regeln und Invarianten** | Ursprungsbetrag und Wechselkurs müssen größer als 0 sein. Stimmt die Quellwährung mit der Gruppenwährung überein, erfolgt keine Umrechnung. Weicht die Quellwährung ab, wird über den externen Wechselkursdienst (Frankfurter-API) der historische Referenzkurs zum Ausgabedatum ermittelt. Liegt für das Ausgabedatum kein Kurs vor (z. B. an Wochenenden oder Feiertagen), wird der zuletzt veröffentlichte Kurs vor diesem Datum verwendet. Die Umrechnung in die Gruppenwährung erfolgt durch Multiplikation und kaufmännische Rundung auf zwei Nachkommastellen. Beim Bearbeiten einer Ausgabe wird der bisherige Wechselkurs beibehalten, solange Quellwährung, Ursprungsbetrag und Ausgabedatum unverändert bleiben. |
| **Verwendet von**          | UC-10 Ausgabe erfassen, UC-11 Ausgabe bearbeiten                                                                                                                                                                                                                                                                      |

#### Fachliche Beschreibung

Wird eine Ausgabe in einer anderen Währung als der Gruppenwährung erfasst (z. B. 50,00 USD in einer EUR-Gruppe), ermittelt CampusSplit den historischen Kurs zum Ausgabedatum und berechnet den Abrechnungsbetrag in Gruppenwährung, der anschließend für die Kostenaufteilung (AF-01) verwendet wird. Originalbetrag, Originalwährung, Wechselkurs und Kursdatum bleiben zur Nachvollziehbarkeit an der Ausgabe erhalten.

### AF-06 — Rückzahlung validieren und Ausgabe zuordnen

| Abschnitt                  | Inhalt                                                                                                                                                                                                                                                                                                                               |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Zweck**                  | Prüft die fachliche Zulässigkeit einer Rückzahlung, verhindert Doppelbuchungen und verrechnet sie optional gegen den noch offenen Anteil einer konkreten Ausgabe.                                                                                                                                                                                                                                    |
| **Eingaben**               | Gruppe, Sender, Empfänger, Rückzahlungsbetrag, eindeutige Vorgangskennung zur Vermeidung von Doppelbuchungen, optionale Auswahl einer konkreten Ausgabe, aktuelle Gruppensalden und Ausgleichsvorschläge (AF-02/AF-03) sowie bisherige Ausgaben und Rückzahlungen der Gruppe.                                                                                                                                                                                                                                                                                                  |
| **Ausgaben**               | Validierte Rückzahlungsbuchung (inkl. optionaler Ausgabenzuordnung) sowie aktualisierte offene Restanteile der zuordenbaren Ausgaben.                                                                                                                                                                                                                                                              |
| **Regeln und Invarianten** | Der Rückzahlungsbetrag muss größer als 0,00 und centgenau sein. Zwischen Sender und Empfänger muss ein offener Ausgleichsvorschlag (AF-03) bestehen, dessen Betrag nicht überschritten werden darf (keine Überzahlung). Jede Vorgangskennung darf nur einmal verarbeitet werden, um versehentliche Doppelbuchungen auszuschließen. Zur Bestimmung der noch offenen Einzelausgaben werden alle vom Empfänger bezahlten Ausgaben mit Beteiligung des Senders chronologisch nach Ausgabedatum (bei gleichem Datum nach Erfassungsreihenfolge) betrachtet. Direkt zugeordnete, Rückzahlungen verringern den offenen Anteil der jeweiligen Ausgabe; nicht zugeordnete Rückzahlungen werden in chronologischer Reihenfolge auf die ältesten noch offenen Ausgabenanteile angerechnet. Wird bei der Erfassung eine konkrete Ausgabe gewählt, darf die Rückzahlung deren verbleibenden offenen Anteil nicht übersteigen. Bei Stornierung einer Rückzahlung (mit verpflichtender Begründung) wird der zugeordnete Anteil wieder freigegeben. |
| **Verwendet von**          | UC-15 Rückzahlung erfassen und stornieren

#### Fachliche Beschreibung

Wenn Person B eine Rückzahlung an Person A dokumentiert, kann sie optional eine konkrete Ausgabe (z. B. „Wocheneinkauf vom 12.08.“) auswählen, um den Ausgleich einer bestimmten Ausgabe zuzuordnen. AF-06 ermittelt vorab, welche Ausgabenanteile nach Abzug früherer Rückzahlungen noch offen sind, und stellt sicher, dass weder der offene Einzelanteil noch der gesamte offene Ausgleichsvorschlag überzahlt wird.                                                                                                                                                                                                                                                               
##

## F3.3 Querverweise

| Baustein | Relevanz für F3                                                                                 |
| -------- | ----------------------------------------------------------------------------------------------- |
| [P1](P1_Ziele_und_Rahmenbedingungen.md) | Definiert das Ziel, gemeinsame Ausgaben transparent und fair zu verwalten. |
| [F1](F1-geschaeftsprozesse.md) | Die Aktivitäten A3 bis A7 motivieren Kostenaufteilung, Währungsumrechnung, Saldenberechnung, Export und Rückzahlungszuordnung. |
| [F2](F2-anwendungsfälle.md) | Die Use Cases zur Ausgaben-, Salden-, Rückzahlungs- und Exportverwaltung verwenden die beschriebenen Anwendungsfunktionen. |
| [D1](D1_Datenmodell.md) | Definiert die Datenobjekte Benutzer, Gruppe, Ausgabe, Kostenanteil, Rückzahlung und Beleg. |
| [D2](D2_Datentypenverzeichnis.md) | Definiert die Datentypen für Betrag, Währung, Wechselkurs, Datum, Aufteilungsart und Exportformat. |
| [B1](B1_Dialogspezifikation.md) | Dialoge lösen die Funktionen über Benutzeraktionen aus. |
| [B3](B3_Druckausgaben.md) | Exportausgaben nutzen die aufbereiteten Exportdaten aus AF-04. |
| [S1](S1_Nachbarsysteme.md) | Schnittstelle zur Frankfurter-Wechselkurs-API (genutzt von AF-05). |
| [N1](N1_Nichtfunktionale_Anforderungen.md) | Anforderungen an Datenkonsistenz, Performance und Sicherheit beeinflussen die Funktionen. |
| [N2](N2_Querschnittskonzepte.md) | Validierung, Fehlerbehandlung und Autorisierung wirken auf die Ausführung der Funktionen. |
