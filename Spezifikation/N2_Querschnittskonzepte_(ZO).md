# N2 - Querschnittskonzepte

Manche Themen lassen sich nicht sauber einem einzelnen Baustein oder Use Case zuordnen, weil sie an mehreren Stellen im System gleichzeitig auftauchen. Solche Themen fassen wir hier als Querschnittskonzepte zusammen, damit wir sie nicht bei jedem Use Case neu erfinden müssen.

Bei CampusSplit betrifft das vor allem sieben Bereiche: Authentifizierung, Autorisierung, Validierung, Geldbetragsverarbeitung, Fehlerbehandlung, Logging und Exportsicherheit.

Der Sinn von N2 ist, diese Konzepte einmal festzulegen, statt sie in jedem Kapitel neu und möglicherweise widersprüchlich zu beschreiben.

## N2.1 Konzeptkatalog

| ID                                           | Konzept                         | Kurzbeschreibung                                                       |
| -------------------------------------------- | ------------------------------- | ---------------------------------------------------------------------- |
| [N2.2](#n22-authentifizierung-und-sitzung)   | Authentifizierung und Sitzung   | Einheitlicher Zugriffsschutz für angemeldete Benutzer:innen            |
| [N2.3](#n23-autorisierung-und-gruppenrechte) | Autorisierung und Gruppenrechte | Zugriff auf Gruppen und Aktionen abhängig von Mitgliedschaft und Rolle |
| [N2.4](#n24-validierung)                     | Validierung                     | Einheitliche Prüfung von Eingaben vor Speicherung                      |
| [N2.5](#n25-geldbetragsverarbeitung)         | Geldbetragsverarbeitung         | Centgenaue und konsistente Verarbeitung von Geldbeträgen               |
| [N2.6](#n26-fehlerbehandlung)                | Fehlerbehandlung                | Einheitliches Verhalten bei fachlichen und technischen Fehlern         |
| [N2.7](#n27-logging)                         | Logging                         | Nachvollziehbare Protokollierung ohne sensible Daten                   |
| [N2.8](#n28-exportsicherheit)                | Exportsicherheit                | Sichere und konsistente Erzeugung von PDF- und CSV-Exporten            |

## N2.2 Authentifizierung und Sitzung

### Anliegen

In CampusSplit stecken sensible Informationen: wer in welcher Gruppe ist, wer wieviel ausgegeben hat und wie die Salden aussehen. Zugreifen sollen darauf nur angemeldete Benutzer:innen können.

Daraus folgt direkt: Bis auf Registrierung und Anmeldung braucht jeder fachliche Use Case eine gültige Sitzung.

### Strategie

- Registrierung und Anmeldung sind die einzigen Bereiche, die ohne Login erreichbar sind.
- Für alle übrigen Funktionen ist eine authentifizierte Sitzung Voraussetzung.
- Mit einer erfolgreichen Anmeldung wird automatisch eine Sitzung angelegt.
- Über diese Sitzung weiß das System, welcher Benutzer gerade aktiv ist.
- Meldet sich jemand ab, wird die Sitzung sofort beendet.
- Wer nicht angemeldet ist, landet automatisch auf der Anmeldeseite.
- Passwörter werden zu keinem Zeitpunkt im Klartext gespeichert.
- Gespeichert wird ausschließlich ein Passwort-Hash.

### Betroffene Use Cases

- UC-01 Registrieren
- UC-02 Anmelden
- UC-03 Abmelden
- UC-04 Dashboard anzeigen
- UC-05 Gruppe erstellen
- UC-06 Gruppe anzeigen
- UC-07 Mitglied zur Gruppe hinzufügen
- UC-08 Ausgabe erfassen
- UC-09 Ausgabe bearbeiten
- UC-10 Ausgabe löschen
- UC-11 Salden anzeigen
- UC-12 Ausgabenübersicht exportieren

### Regeln

| ID      | Regel                                                                                                                  |
| ------- | ---------------------------------------------------------------------------------------------------------------------- |
| AUTH-01 | Ohne Anmeldung dürfen nur Registrierung und Anmeldung ausgeführt werden.                                               |
| AUTH-02 | Nach erfolgreicher Anmeldung wird der Benutzer zum Dashboard weitergeleitet.                                           |
| AUTH-03 | Nach Abmeldung ist kein Zugriff auf geschützte Dialoge mehr möglich.                                                   |
| AUTH-04 | Passwörter werden nur als Hash gespeichert.                                                                            |
| AUTH-05 | Fehlgeschlagene Anmeldungen geben keine detaillierten Informationen darüber preis, ob E-Mail oder Passwort falsch war. |

### Querverweise

| Baustein | Relevanz                                                               |
| -------- | ---------------------------------------------------------------------- |
| F2       | Beschreibt Registrierung, Anmeldung und Abmeldung als Use Cases.       |
| B1       | Beschreibt Login-, Registrierungs- und Abmeldedialoge.                 |
| D1       | User enthält E-Mail-Adresse und Passwort-Hash.                         |
| N1       | Sicherheitsanforderungen fordern Authentifizierung und Passwortschutz. |

## N2.3 Autorisierung und Gruppenrechte

### Anliegen

Da CampusSplit von mehreren Personen gleichzeitig genutzt wird, muss klar geregelt sein, wer worauf zugreifen darf: Man sieht grundsätzlich nur die Gruppen, in denen man selbst Mitglied ist.

Innerhalb einer Gruppe gibt es außerdem noch einmal Unterschiede, denn nicht jede Aktion darf jedes Mitglied ausführen. Neue Mitglieder hinzuzufügen ist zum Beispiel den Gruppenadministrator:innen vorbehalten.

### Strategie

- Jede Gruppe besitzt eine oder mehrere Mitgliedschaften.
- Eine Mitgliedschaft verknüpft einen Benutzer mit einer Gruppe.
- Ob jemand auf Gruppendaten zugreifen darf, wird immer über diese Mitgliedschaft geprüft.
- Rollen gelten immer nur innerhalb der jeweiligen Gruppe, nicht gruppenübergreifend.
- Der Ersteller einer Gruppe erhält automatisch die Rolle ADMIN.
- Alle anderen Mitglieder erhalten die Rolle MEMBER.
- Nur ADMIN darf neue Mitglieder hinzufügen.
- MEMBER darf Ausgaben erfassen, bearbeiten, löschen, Salden anzeigen und Exporte erzeugen.

### Rollen

| Rolle  | Bedeutung                                               |
| ------ | ------------------------------------------------------- |
| ADMIN  | Gruppenadministrator mit erweiterten Verwaltungsrechten |
| MEMBER | Normales Gruppenmitglied                                |

### Berechtigungen

| Aktion              | ADMIN | MEMBER |
| ------------------- | ----- | ------ |
| Gruppe anzeigen     | ja    | ja     |
| Ausgaben anzeigen   | ja    | ja     |
| Ausgabe erfassen    | ja    | ja     |
| Ausgabe bearbeiten  | ja    | ja     |
| Ausgabe löschen     | ja    | ja     |
| Salden anzeigen     | ja    | ja     |
| Export erzeugen     | ja    | ja     |
| Mitglied hinzufügen | ja    | nein   |

### Regeln

| ID     | Regel                                                                         |
| ------ | ----------------------------------------------------------------------------- |
| AUT-01 | Ein Benutzer darf eine Gruppe nur sehen, wenn er Mitglied dieser Gruppe ist.  |
| AUT-02 | Ein Benutzer darf Ausgaben nur für Gruppen sehen, in denen er Mitglied ist.   |
| AUT-03 | Ein Benutzer darf nur Ausgaben in Gruppen erfassen, in denen er Mitglied ist. |
| AUT-04 | Nur Gruppenadministrator:innen dürfen Mitglieder hinzufügen.                  |
| AUT-05 | Rollen gelten ausschließlich innerhalb der jeweiligen Gruppe.                 |
| AUT-06 | Jede Gruppe muss mindestens einen Administrator besitzen.                     |

### Querverweise

| Baustein | Relevanz                                                                          |
| -------- | --------------------------------------------------------------------------------- |
| F2       | UC-05 bis UC-12 setzen Mitgliedschaft oder Administratorrechte voraus.            |
| D1       | Membership verbindet User und Group.                                              |
| D2       | MembershipRoleDT definiert ADMIN und MEMBER.                                      |
| B1       | Dialoge blenden Aktionen abhängig von Berechtigungen ein oder aus.                |
| N1       | Zugriffsschutz und Autorisierung werden als Sicherheitsanforderungen beschrieben. |

## N2.4 Validierung

### Anliegen

Überall dort, wo Benutzer:innen Daten eingeben, kann auch etwas Falsches oder Unvollständiges dabei sein. Würden solche Daten ungeprüft gespeichert, hätte das direkte Folgen für Saldenberechnung und Exporte - am Ende kämen einfach falsche Zahlen heraus.

Wichtig ist Validierung deshalb überall dort, wo Nutzer:innen etwas eintragen: bei Registrierung, Gruppenerstellung, Mitgliederverwaltung, Ausgabenerfassung, Ausgabenbearbeitung und Export.

### Strategie

- Eingaben werden immer vor dem Speichern geprüft, nie danach.
- Pflichtfelder dürfen nicht leer bleiben.
- Beträge müssen ein gültiges Geldformat haben.
- E-Mail-Adressen müssen ein gültiges Format haben.
- Wer etwas in einer Gruppe tut, muss auch Mitglied dieser Gruppe sein.
- Auch Zahler und Beteiligte einer Ausgabe müssen Mitglieder der Gruppe sein.
- Die einzelnen Kostenanteile müssen in Summe exakt den Gesamtbetrag der Ausgabe ergeben.
- Fehlermeldungen werden verständlich direkt im betroffenen Dialog angezeigt.
- Ist eine Eingabe ungültig, wird nichts gespeichert.

### Validierungsbereiche

| Bereich              | Beispiele                                                   |
| -------------------- | ----------------------------------------------------------- |
| Registrierung        | Name, E-Mail, Passwort                                      |
| Anmeldung            | E-Mail, Passwort                                            |
| Gruppenerstellung    | Gruppenname                                                 |
| Mitgliederverwaltung | E-Mail-Adresse des neuen Mitglieds                          |
| Ausgabenerfassung    | Beschreibung, Betrag, Datum, Zahler, Beteiligte, Aufteilung |
| Export               | Exportformat, optionaler Zeitraum                           |

### Regeln

| ID     | Regel                                                                  |
| ------ | ---------------------------------------------------------------------- |
| VAL-01 | Pflichtfelder dürfen nicht leer sein.                                  |
| VAL-02 | E-Mail-Adressen müssen formal gültig sein.                             |
| VAL-03 | Eine E-Mail-Adresse darf nur einem Benutzerkonto zugeordnet sein.      |
| VAL-04 | Ein Gruppenname darf nicht leer sein.                                  |
| VAL-05 | Der Betrag einer Ausgabe muss größer als 0.00 sein.                    |
| VAL-06 | Der Zahler einer Ausgabe muss Mitglied der Gruppe sein.                |
| VAL-07 | Jede beteiligte Person einer Ausgabe muss Mitglied der Gruppe sein.    |
| VAL-08 | Mindestens eine beteiligte Person muss ausgewählt sein.                |
| VAL-09 | Die Summe aller Kostenanteile muss exakt dem Gesamtbetrag entsprechen. |
| VAL-10 | Ein Exportformat muss PDF oder CSV sein.                               |

### Fehlerdarstellung

Validierungsfehler werden direkt im jeweiligen Dialog angezeigt.

Beispiele:

| Situation                          | Beispielmeldung                                            |
| ---------------------------------- | ---------------------------------------------------------- |
| Gruppenname fehlt                  | „Bitte geben Sie einen Gruppennamen ein."                  |
| Betrag ist ungültig                | „Der Betrag muss größer als 0,00 € sein."                  |
| Keine beteiligte Person ausgewählt | „Bitte wählen Sie mindestens ein Gruppenmitglied aus."     |
| Aufteilungssumme ist falsch        | „Die Summe der Anteile muss dem Gesamtbetrag entsprechen." |
| Benutzer ist kein Gruppenmitglied  | „Sie haben keinen Zugriff auf diese Gruppe."               |

### Querverweise

| Baustein | Relevanz                                                                      |
| -------- | ----------------------------------------------------------------------------- |
| F2       | Use Cases beschreiben, wann Eingaben erfolgen.                                |
| F3       | Kostenaufteilung und Saldenberechnung setzen gültige Eingaben voraus.         |
| D1       | Datenmodell-Invarianten definieren fachlich erlaubte Zustände.                |
| D2       | Datentypen bestimmen gültige Wertebereiche.                                   |
| B1       | Dialoge zeigen Validierungsfehler an.                                         |
| N1       | Datenkonsistenz und Benutzerfreundlichkeit fordern verständliche Validierung. |

## N2.5 Geldbetragsverarbeitung

### Anliegen

Da es bei CampusSplit im Kern um Geld geht, muss hier besonders sauber gerechnet werden. Schon kleine Rundungsfehler summieren sich über mehrere Ausgaben hinweg und führen am Ende zu Salden, die nicht mehr stimmen. Deshalb legen wir für den gesamten Umgang mit Geldbeträgen eine einheitliche Regel fest, statt das jeder Funktion einzeln zu überlassen.

### Strategie

- Geldbeträge werden centgenau verarbeitet.
- Die erste Version verwendet ausschließlich Euro.
- Beträge werden immer mit zwei Nachkommastellen dargestellt.
- Ausgaben und Kostenanteile können nicht negativ sein.
- Ein Saldo dagegen kann positiv, negativ oder genau null sein.
- Berechnungen laufen deterministisch ab, also bei gleicher Eingabe immer mit demselben Ergebnis.
- Die Kostenanteile einer Ausgabe müssen in Summe dem Gesamtbetrag entsprechen.
- Die Summe aller Salden innerhalb einer Gruppe muss 0.00 ergeben.

### Fachliche Bedeutung von Salden

| Saldo   | Bedeutung                    |
| ------- | ---------------------------- |
| Positiv | Mitglied bekommt Geld zurück |
| Negativ | Mitglied schuldet Geld       |
| 0.00    | Mitglied ist ausgeglichen    |

### Rundungsstrategie

Lässt sich ein Betrag bei gleichmäßiger Aufteilung nicht glatt durch die Anzahl der Personen teilen, wird trotzdem centgenau aufgeteilt - die Differenz von einem Cent bekommt einfach eine der beteiligten Personen ab.

Beispiel:

10.00 € werden auf drei Personen verteilt.

| Person   | Anteil |
| -------- | ------ |
| Person A | 3.34 € |
| Person B | 3.33 € |
| Person C | 3.33 € |

Wichtig ist dabei, dass die Rundung nachvollziehbar und deterministisch bleibt: Bei identischer Eingabe kommt jedes Mal dieselbe Verteilung heraus.

### Regeln

| ID       | Regel                                                                |
| -------- | -------------------------------------------------------------------- |
| MONEY-01 | Geldbeträge werden nicht mit ungenauen Gleitkommazahlen verarbeitet. |
| MONEY-02 | Ausgaben müssen größer als 0.00 sein.                                |
| MONEY-03 | Kostenanteile dürfen nicht negativ sein.                             |
| MONEY-04 | Salden dürfen negativ, positiv oder null sein.                       |
| MONEY-05 | Die Summe der Kostenanteile entspricht exakt dem Gesamtbetrag.       |
| MONEY-06 | Die Summe aller Gruppensalden ergibt exakt 0.00.                     |
| MONEY-07 | Alle Beträge werden in der ersten Version in Euro dargestellt.       |

### Querverweise

| Baustein | Relevanz                                                                         |
| -------- | -------------------------------------------------------------------------------- |
| F3       | AF-01, AF-02 und AF-03 verwenden Geldbeträge fachlich.                           |
| D1       | Expense, ExpenseShare, Balance und SettlementProposal verwenden Geldbeträge.     |
| D2       | MoneyAmountDT und CurrencyCodeDT definieren Wertebereiche und Regeln.            |
| B1       | Dialoge erfassen und zeigen Geldbeträge.                                         |
| B3       | Exportdateien enthalten Geldbeträge und Salden.                                  |
| N1       | Genauigkeits- und Datenkonsistenzanforderungen beziehen sich auf Geldberechnung. |

## N2.6 Fehlerbehandlung

### Anliegen

Fehler lassen sich nie ganz vermeiden - sei es, weil jemand etwas Falsches eingibt, keine Berechtigung hat, die Datenbank gerade nicht erreichbar ist oder ein Export fehlschlägt.

Damit die Anwendung in solchen Fällen trotzdem benutzbar bleibt, muss sie einheitlich reagieren, verständlich informieren und darf dabei keine Daten verlieren.

### Strategie

- Fehlermeldungen werden dort angezeigt, wo Benutzer:innen sie auch verstehen und einordnen können.
- Technische Details werden nicht einfach ungefiltert an die Oberfläche durchgereicht.
- Ungültige Eingaben verhindern grundsätzlich die Speicherung.
- Fehlt eine Berechtigung, gibt es eine klare Zugriff-verweigert-Meldung.
- Schlägt eine Speicheroperation fehl, dürfen keine unvollständigen Daten übrig bleiben.
- Nach einem Fehler kehrt die Anwendung wieder in einen stabilen Zustand zurück.
- Aktionen, die wiederholbar sind, können erneut ausgeführt werden.

### Fehlerarten

| Fehlerart            | Beispiel                              | Verhalten                               |
| -------------------- | ------------------------------------- | --------------------------------------- |
| Validierungsfehler   | Betrag ist ungültig                   | Feldbezogene Fehlermeldung              |
| Autorisierungsfehler | Benutzer ist kein Gruppenmitglied     | Zugriff verweigert                      |
| Nicht gefunden       | Gruppe existiert nicht                | Fehlermeldung und Rückkehrmöglichkeit   |
| Persistenzfehler     | Ausgabe kann nicht gespeichert werden | Keine Teilspeicherung, Fehlermeldung    |
| Exportfehler         | PDF kann nicht erzeugt werden         | Fehlermeldung, erneuter Versuch möglich |
| Sitzungsfehler       | Sitzung abgelaufen                    | Weiterleitung zur Anmeldung             |

### Regeln

| ID     | Regel                                                                            |
| ------ | -------------------------------------------------------------------------------- |
| ERR-01 | Benutzer:innen erhalten verständliche Fehlermeldungen.                           |
| ERR-02 | Technische Fehlermeldungen werden nicht direkt angezeigt.                        |
| ERR-03 | Bei fehlender Berechtigung wird die Aktion abgelehnt.                            |
| ERR-04 | Bei fehlerhafter Ausgabeerfassung entsteht keine Teilspeicherung.                |
| ERR-05 | Nach einem Fehler bleibt ein stabiler Systemzustand erhalten.                    |
| ERR-06 | Wiederholbare Aktionen wie Export oder Speichern können erneut ausgelöst werden. |

### Querverweise

| Baustein | Relevanz                                                                  |
| -------- | ------------------------------------------------------------------------- |
| F2       | Exception-Szenarien beschreiben Fehler in Use Cases.                      |
| B1       | Dialoge zeigen Fehlerzustände und Validierungsfehler.                     |
| S1       | Schnittstellenfehler werden an Use Cases zurückgegeben.                   |
| S3       | Funktionstests prüfen zentrale Fehlerfälle nach Inbetriebnahme.           |
| N1       | Zuverlässigkeit und verständliche Fehlerbehandlung werden dort gefordert. |

## N2.7 Logging

### Anliegen

Damit sich technische Fehler und wichtige Ereignisse im Nachhinein nachvollziehen lassen, braucht CampusSplit ein grundlegendes Logging.

Dabei geht es uns nur um Fehlersuche und Betrieb - Logging ist ausdrücklich kein vollständiges Audit-System und ersetzt auch keine fachliche Historie der Zahlungen.

### Strategie

- Technische Fehler werden protokolliert, damit sie sich später nachvollziehen lassen.
- Auch sicherheitsrelevante Ereignisse können protokolliert werden.
- Passwörter oder Passwort-Hashes tauchen in Logeinträgen nicht auf.
- Sessiontokens werden ebenfalls nicht protokolliert.
- Vollständige sensible Zugangsdaten landen grundsätzlich nicht im Log.
- Fachliche Inhalte werden nur dann protokolliert, wenn sie für die Fehlersuche nötig und gleichzeitig unkritisch sind.
- Benutzer:innen bekommen keine technischen Logdetails zu sehen.

### Mögliche Logereignisse

| Ereignis                            | Zweck                                  |
| ----------------------------------- | -------------------------------------- |
| Fehlgeschlagene Anmeldung           | Erkennen von Zugriffsproblemen         |
| Zugriff verweigert                  | Nachvollziehen unberechtigter Zugriffe |
| Fehler beim Speichern einer Ausgabe | Fehlersuche                            |
| Fehler bei Exporterzeugung          | Fehlersuche                            |
| Fehler beim Datenbankzugriff        | Betrieb und Diagnose                   |
| Anwendung gestartet                 | Betriebsinformation                    |

### Regeln

| ID     | Regel                                                                              |
| ------ | ---------------------------------------------------------------------------------- |
| LOG-01 | Passwörter werden niemals protokolliert.                                           |
| LOG-02 | Passwort-Hashes werden nicht protokolliert.                                        |
| LOG-03 | Sessiontokens werden nicht protokolliert.                                          |
| LOG-04 | Technische Fehler können mit Zeitstempel protokolliert werden.                     |
| LOG-05 | Benutzer:innen sehen verständliche Fehlermeldungen, keine technischen Stacktraces. |
| LOG-06 | Logs dienen nicht als fachliche Zahlungshistorie.                                  |

### Querverweise

| Baustein | Relevanz                                                    |
| -------- | ----------------------------------------------------------- |
| N1       | Sicherheitsanforderungen verbieten sensible Daten in Logs.  |
| S3       | Logdaten werden als betrieblicher Datenbereich beschrieben. |
| F2       | Fehlerfälle in Use Cases können Logeinträge auslösen.       |
| N2.6     | Fehlerbehandlung und Logging wirken zusammen.               |

## N2.8 Exportsicherheit

### Anliegen

CampusSplit kann Gruppendaten, Ausgaben, Kostenanteile und Salden als PDF oder CSV exportieren. Dabei muss sichergestellt sein, dass die Exporte fachlich korrekt sind und keine sensiblen oder unnötigen technischen Informationen enthalten.

### Strategie

- Exporte dürfen nur von Mitgliedern der jeweiligen Gruppe erzeugt werden.
- Ein Export bezieht sich immer auf genau eine Gruppe.
- Die Exportdaten werden jedes Mal aus dem aktuellen Datenbestand berechnet.
- Die exportierten Salden müssen mit der Berechnung aus F3 übereinstimmen.
- Passwörter, Passwort-Hashes und Sessioninformationen tauchen in keiner Exportdatei auf.
- Technische IDs werden nur exportiert, wenn sie fachlich tatsächlich gebraucht werden.
- Exporte verändern keine gespeicherten Daten.
- In der ersten Version werden Exportdateien nicht dauerhaft als eigene fachliche Entität gespeichert.

### Exportformate

| Format | Zweck                                            |
| ------ | ------------------------------------------------ |
| PDF    | Lesbare Übersicht für Dokumentation und Ausdruck |
| CSV    | Tabellarische Weiterverarbeitung                 |

### Regeln

| ID         | Regel                                                                     |
| ---------- | ------------------------------------------------------------------------- |
| EXP-SEC-01 | Nur Gruppenmitglieder dürfen Exporte ihrer Gruppe erzeugen.               |
| EXP-SEC-02 | Exporte enthalten keine Passwörter oder Passwort-Hashes.                  |
| EXP-SEC-03 | Exporte enthalten keine Session- oder Tokeninformationen.                 |
| EXP-SEC-04 | Exportierte Beträge werden in Euro mit zwei Nachkommastellen dargestellt. |
| EXP-SEC-05 | Exportierte Salden stimmen mit der aktuellen Berechnung überein.          |
| EXP-SEC-06 | Exporte führen keine Zahlungen aus.                                       |
| EXP-SEC-07 | Exporte verändern keine gespeicherten Daten.                              |

### Querverweise

| Baustein | Relevanz                                                                   |
| -------- | -------------------------------------------------------------------------- |
| F2       | UC-12 löst den Export aus.                                                 |
| F3       | AF-04 bereitet Exportdaten fachlich auf.                                   |
| D1       | Exportdaten stammen aus Gruppen, Mitgliedern, Ausgaben und Kostenanteilen. |
| D2       | ExportFormatDT, MoneyAmountDT und CurrencyCodeDT bestimmen Exportwerte.    |
| B1       | DLG-11 beschreibt den Exportdialog.                                        |
| B3       | Beschreibt Inhalt und Struktur der PDF- und CSV-Ausgaben.                  |
| N1       | Sicherheits- und Konsistenzanforderungen gelten auch für Exporte.          |

## N2.9 Nicht Bestandteil von N2

Ein paar Querschnittskonzepte, die man sich grundsätzlich vorstellen könnte, haben wir für die erste Version von CampusSplit bewusst ausgeklammert.

| Thema                       | Status           | Begründung                                                                                                                   |
| --------------------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Mehrmandantenfähigkeit      | nicht vorgesehen | CampusSplit unterscheidet nur zwischen Benutzern und Gruppen - Organisationen oder Mandanten gibt es in diesem Modell nicht. |
| Zahlungsabwicklung          | nicht vorgesehen | CampusSplit berechnet zwar Salden, führt aber selbst keine Zahlungen aus.                                                    |
| Bankintegration             | nicht vorgesehen | Es werden keinerlei Bankdaten verarbeitet.                                                                                   |
| Mehrwährungslogik           | nicht vorgesehen | Die erste Version verwendet ausschließlich Euro.                                                                             |
| Vollständige Audit-Historie | nicht vorgesehen | Änderungen an Daten werden nicht als eigene fachliche Historie mitgeschrieben.                                               |
| Echtzeit-Kommunikation      | nicht vorgesehen | Weder Live-Chat noch Echtzeit-Synchronisation gehören zum Projektumfang.                                                     |
| E-Mail-Benachrichtigungen   | nicht vorgesehen | Ein externer E-Mail-Dienst ist nicht vorgesehen.                                                                             |

Wir nennen diese Punkte hier bewusst, damit klar wird: Das Fehlen dieser Funktionen ist keine Lücke, sondern eine bewusste Entscheidung, um den Projektumfang realistisch zu halten.

## N2.10 Querverweise

| Baustein | Relevanz für N2                                                                                                   |
| -------- | ----------------------------------------------------------------------------------------------------------------- |
| P1       | Projektziele, Nichtziele und Rahmenbedingungen begrenzen die Querschnittskonzepte.                                |
| P2       | Systemkontext zeigt, welche Nachbarsysteme von Querschnittskonzepten betroffen sind.                              |
| F1       | Geschäftsprozess zeigt, wo Authentifizierung, Validierung, Geldberechnung und Export relevant werden.             |
| F2       | Use Cases bilden die sichtbare Oberfläche der Querschnittskonzepte.                                               |
| F3       | Anwendungsfunktionen setzen Geldbetragsverarbeitung, Validierung und Exportregeln fachlich um.                    |
| D1       | Datenmodell-Invarianten bilden die Grundlage für Validierung und Autorisierung.                                   |
| D2       | Datentypen wie MoneyAmountDT, MembershipRoleDT, SplitMethodDT und ExportFormatDT stützen die Querschnittsregeln.  |
| B1       | Dialoge zeigen Validierungsfehler, Fehlerzustände und berechtigungsabhängige Aktionen.                            |
| B3       | Exporte folgen den Regeln der Exportsicherheit.                                                                   |
| S1       | Schnittstellen müssen Authentifizierung, Autorisierung, Validierung und Fehlerbehandlung berücksichtigen.         |
| S3       | Inbetriebnahme und Releases müssen Konfiguration, Datenbeständigkeit und Logging berücksichtigen.                 |
| N1       | Nichtfunktionale Anforderungen definieren messbare Qualitätskriterien für die hier beschriebenen Konzepte.        |
| E2       | Glossar definiert zentrale Begriffe wie Authentifizierung, Autorisierung, Saldo, Export und Gruppenadministrator. |