# N1 - Nichtfunktionale Anforderungen

In N1 geht es um die messbaren Qualitätsanforderungen an CampusSplit, also nicht um einzelne fachliche Funktionen, sondern um Eigenschaften, die das System als Ganzes erfüllen soll.

Davon abzugrenzen sind die Projektziele und Rahmenbedingungen, die bereits in [P1](P1_Ziele_und_Rahmenbedingungen.md) festgehalten wurden. N1 baut darauf auf und konkretisiert, was das für Benutzbarkeit, Performance, Zuverlässigkeit, Wartbarkeit, Sicherheit und Konformität bedeutet.

Zu jeder Anforderung gehört außerdem ein Fit Criterion - daran lässt sich später prüfen, ob sie wirklich erfüllt wurde.

> **Hinweis zur Gliederung:** Die Nummerierung der folgenden Abschnitte (10 bis 17) orientiert sich am **Volere-Requirements-Schema**, einem etablierten Rahmenwerk zur Kategorisierung nichtfunktionaler Anforderungen. Die Kategorien 10 bis 17 entsprechen den dort vorgesehenen Bereichen Look-and-Feel, Benutzbarkeit, Performance/Zuverlässigkeit, Betrieb/Umgebung, Wartbarkeit/Erweiterbarkeit, Sicherheit, Sprache und Konformität.

## 10\. Look-and-Feel-Anforderungen

### 10a. Erscheinungsbild

#### NFR-10a-01: Nutzbar auf Handy und Desktop

CampusSplit soll sowohl am Desktop als auch auf dem Smartphone vernünftig nutzbar sein.

Das heißt konkret: Die Anwendung passt sich an unterschiedliche Bildschirmgrößen an, und zentrale Funktionen wie Anmeldung, Gruppenauswahl, Ausgabenerfassung, Saldenanzeige und Export lassen sich bedienen, ohne dass horizontal gescrollt werden muss.

**Fit Criterion:**  
Alle zentralen Dialoge aus [B1](B1_Dialogspezifikation.md) sind auf einem Smartphone-Viewport ab 375px Breite und auf einem Desktop-Viewport ab 1366px Breite vollständig bedienbar.

#### NFR-10a-02: Einheitliches Erscheinungsbild in allen Dialogen

CampusSplit muss über alle Dialoge hinweg eine konsistente Oberfläche verwenden.

Wiederkehrende Elemente wie Navigation, Buttons, Fehlermeldungen, Formulare, Tabellen und Karten sollen einheitlich dargestellt werden.

**Fit Criterion:**  
Alle Dialoge verwenden einheitliche Bezeichnungen, einheitliche Navigationsmuster und gleichartige Darstellung für Eingabefelder, Aktionen und Fehlermeldungen.

## 11\. Benutzbarkeit und Bedienbarkeit

### 11a. Einfache Bedienung

#### NFR-11a-01: Ausgabe schnell erfassen können

Das Erfassen einer neuen Ausgabe soll für Gruppenmitglieder schnell und verständlich möglich sein.

Vom Öffnen einer Gruppe bis zum Speichern einer Ausgabe sollen nur wenige Interaktionen erforderlich sein.

**Fit Criterion:**  
Ein angemeldeter Benutzer kann von der Gruppendetailseite aus eine einfache Ausgabe mit gleichmäßiger Aufteilung in höchstens fünf fachlichen Schritten erfassen:

- „Ausgabe hinzufügen" wählen

- Ausgabendaten eingeben

- Beteiligte prüfen oder auswählen

- Aufteilungsart bestätigen

- Speichern

#### NFR-11a-02: Salden auf einen Blick verstehen

Die Saldenübersicht muss für Benutzer:innen ohne zusätzliche Erklärung verständlich sein.

Auf einen Blick muss klar sein, wer Geld bekommt und wer noch etwas schuldet, ebenso wie hoch der offene Betrag ist. Außerdem sollte deutlich werden, dass die eigentliche Zahlung außerhalb von CampusSplit stattfindet.

**Fit Criterion:**  
Die Saldenübersicht zeigt für jedes Mitglied einen Betrag mit eindeutiger Bedeutung. Positive und negative Salden werden verständlich beschriftet, zum Beispiel „bekommt zurück" und „schuldet".

### 11b. Lernanforderungen

#### NFR-11b-01: Bedienung ohne Anleitung möglich

CampusSplit soll ohne Schulung oder externes Handbuch nutzbar sein.

Die wichtigsten Funktionen müssen durch Beschriftungen, Hinweise und klare Dialogführung verständlich sein.

**Fit Criterion:**  
Ein neuer Benutzer kann ohne Dokumentation ein Konto anlegen, eine Gruppe erstellen, eine Ausgabe erfassen und die Salden anzeigen.

### 11c. Barrierearmut

#### NFR-11c-01: Bedienung per Tastatur

Zentrale Dialoge müssen mit Tastatur bedienbar sein.

Das betrifft vor allem Login, Registrierung, Gruppe erstellen, Ausgabe erfassen und Export erzeugen.

**Fit Criterion:**  
Alle Pflichtfelder und Hauptaktionen der zentralen Formulare sind per Tastatur erreichbar und auslösbar.

## 12\. Performance, Zuverlässigkeit und Datenqualität

### 12a. Geschwindigkeit und Antwortzeiten

#### NFR-12a-01: Ladezeiten im Alltag

Normale Benutzeraktionen sollen ohne spürbare Verzögerung verarbeitet werden.

Dazu gehören:

- Dashboard anzeigen
- Gruppe öffnen
- Ausgabenliste anzeigen
- Salden anzeigen

**Fit Criterion:**  
Bei normaler Projektgröße lädt jede zentrale Ansicht innerhalb von 2 Sekunden, sofern Datenbank und Server verfügbar sind.

#### NFR-12a-02: Wie lange ein Export dauern darf

Die Erzeugung einer PDF- oder CSV-Datei darf für typische Gruppen nicht unverhältnismäßig lange dauern.

**Fit Criterion:**  
Ein Export für eine Gruppe mit bis zu 100 Ausgaben wird innerhalb von 5 Sekunden erzeugt oder es wird eine verständliche Fehlermeldung angezeigt.

### 12b. Kapazitätsanforderungen

#### NFR-12b-01: Wie groß eine Gruppe werden darf

CampusSplit ist für kleine bis mittlere Gruppen ausgelegt.

Typische Gruppen sind Wohngemeinschaften, Reisegruppen oder studentische Projektgruppen.

**Fit Criterion:**  
Das System unterstützt pro Gruppe mindestens:

- 20 Mitglieder
- 500 Ausgaben
- 2.000 Kostenanteile

ohne Änderung der fachlichen Bedienlogik.

### 12c. Genauigkeitsanforderungen

#### NFR-12c-01: Geldbeträge müssen exakt stimmen

Geldbeträge müssen in CampusSplit centgenau verarbeitet werden - alles andere führt früher oder später zu Problemen.

Konkret heißt das: Ungenaue Gleitkommarechnungen dürfen nicht dazu führen, dass Salden am Ende nicht mehr stimmen.

**Fit Criterion:**  
Für jede Ausgabe gilt: Die Summe aller Kostenanteile entspricht exakt dem Abrechnungsbetrag (settlementAmount) der Ausgabe in Gruppenwährung. Für jede Gruppe gilt: Die Summe aller Salden ergibt exakt 0,00 in der Gruppenwährung.

#### NFR-12c-02: Rundung bei krummen Beträgen

Bei gleichmäßiger Aufteilung nicht glatt teilbarer Beträge muss die Rundung deterministisch erfolgen.

**Fit Criterion:**  
Die gleiche Eingabe führt immer zur gleichen Aufteilung. Beispiel: 10,00 € auf drei Personen ergibt immer dieselbe centgenaue Verteilung, zum Beispiel 3,34 €, 3,33 €, 3,33 €.

### 12d. Zuverlässigkeit und Fehlerverhalten

#### NFR-12d-01: Kein halbes Speichern bei Fehlern

Beim Speichern einer Ausgabe dürfen keine unvollständigen Daten entstehen.

Wenn eine Ausgabe nicht vollständig gespeichert werden kann, dürfen auch die zugehörigen Kostenanteile nicht teilweise gespeichert bleiben. Das gilt auch, wenn bei einer Fremdwährungsausgabe kein Wechselkurs ermittelt werden konnte.

**Fit Criterion:**  
Wenn beim Speichern einer Ausgabe ein Fehler auftritt, existiert danach entweder die vollständige Ausgabe mit allen Kostenanteilen oder gar keine neue Ausgabe.

#### NFR-12d-02: Fehlermeldungen, die man versteht

Fehler müssen für Benutzer:innen verständlich angezeigt werden.

Technische Details sollen nicht ungefiltert angezeigt werden.

**Fit Criterion:**  
Bei Fehlern wie ungültiger Eingabe, fehlender Berechtigung, nicht gefundener Gruppe, nicht erreichbarem Wechselkursdienst oder fehlgeschlagenem Export zeigt CampusSplit eine verständliche Fehlermeldung und ermöglicht die Rückkehr zu einem stabilen Dialog.

## 13\. Betriebs- und Umgebungsanforderungen

### 13a. Erwartete Nutzungsumgebung

#### NFR-13a-01: Unterstützte Browser

CampusSplit muss in aktuellen Desktop- und mobilen Browsern nutzbar sein.

**Fit Criterion:**  
Die Anwendung ist in aktuellen Versionen von Chrome, Firefox, Safari und Edge für zentrale Funktionen nutzbar.

### 13b. Technologische Umgebung

#### NFR-13b-01: Aufbau als Webanwendung

CampusSplit wird als Webanwendung mit getrenntem Frontend, Backend und Datenbank betrieben.

**Fit Criterion:**  
Frontend, Backend und Datenbank können getrennt gestartet und gemeinsam betrieben werden. Die Anwendung ist über den Browser erreichbar.

#### NFR-13b-02: Abhängigkeit von externen Diensten auf das Nötige beschränken

CampusSplit darf in der ersten Version nicht von externen Zahlungs-, Bank-, OCR-, KI- oder E-Mail-Diensten abhängig sein. Ausgenommen ist der externe Wechselkursdienst, der ausschließlich zur Umrechnung von Fremdwährungsausgaben in die Gruppenwährung verwendet wird (siehe [S1](S1_Nachbarsysteme.md)).

**Fit Criterion:**  
Alle Kernfunktionen wie Registrierung, Gruppenverwaltung, Ausgabenerfassung in Gruppenwährung, Saldenberechnung und Export funktionieren ohne externe Drittanbieter-APIs. Einzige Ausnahme ist die Umrechnung von Fremdwährungsausgaben, die den in S1 beschriebenen Wechselkursdienst benötigt.

## 14\. Wartbarkeit und Erweiterbarkeit

### 14a. Wartbarkeit

#### NFR-14a-01: Fachlogik getrennt von der Oberfläche

Fachliche Berechnungen dürfen nicht ausschließlich in der Benutzeroberfläche implementiert werden.

Insbesondere Kostenaufteilung, Saldenberechnung und Ausgleichsvorschläge müssen unabhängig von der konkreten Darstellung testbar sein.

**Fit Criterion:**  
Die Anwendungsfunktionen aus [F3](F3-anwendungsfunktionen.md) sind durch automatisierte Tests prüfbar, ohne dass ein Browserdialog ausgeführt werden muss.

#### NFR-14a-02: Klare Struktur im Repository

Damit sich im Repository jeder zurechtfindet, braucht es eine nachvollziehbare Struktur.

Dokumentation, Frontend, Backend und Tests sollten dafür klar voneinander getrennt liegen.

**Fit Criterion:**  
Das Repository enthält klar erkennbare Bereiche für Dokumentation, Frontend, Backend und Tests. Neue Teammitglieder können anhand der Struktur erkennen, wo Spezifikation, Architektur und Quellcode liegen.

### 14b. Testbarkeit

#### NFR-14b-01: Tests für die wichtigste Fachlogik

Die zentrale Fachlogik muss automatisiert getestet werden.

Besonders relevant sind:

- Kostenanteile berechnen
- Gruppensalden berechnen
- Ausgleichsvorschläge berechnen
- Validierung von Ausgaben
- Umrechnung von Fremdwährungsausgaben

**Fit Criterion:**  
Für die Anwendungsfunktionen AF-01, AF-02 und AF-03 existieren automatisierte Tests mit Normalfällen, Grenzfällen, Rundungsfällen und Fremdwährungsfällen.

### 14c. Erweiterbarkeit

#### NFR-14c-01: Später weitere Aufteilungsarten ergänzen können

Die Architektur und Fachlogik sollen spätere Erweiterungen der Aufteilungsarten ermöglichen.

Die erste Version unterstützt EQUAL und CUSTOM_AMOUNT.

**Fit Criterion:**  
Eine spätere Aufteilungsart, zum Beispiel prozentuale Aufteilung, kann ergänzt werden, ohne bestehende gespeicherte Ausgaben zu beschädigen.

#### NFR-14c-02: Später weitere Exportformate ergänzen können

CampusSplit unterstützt in der ersten Version PDF und CSV.

Die Struktur soll spätere Exportformate ermöglichen.

**Fit Criterion:**  
Ein weiteres Exportformat kann ergänzt werden, ohne die bestehenden Formate PDF und CSV fachlich zu verändern.

## 15\. Sicherheitsanforderungen

### 15a. Zugriffsschutz

#### NFR-15a-01: Anmeldung für geschützte Funktionen nötig

Alle fachlichen Funktionen außerhalb von Registrierung und Anmeldung erfordern eine authentifizierte Sitzung.

**Fit Criterion:**  
Ein nicht angemeldeter Benutzer kann keine Gruppen, Ausgaben, Salden oder Exporte abrufen oder verändern.

#### NFR-15a-02: Passwörter sicher speichern

Aus Sicherheitsgründen dürfen Passwörter nie im Klartext gespeichert werden.

**Fit Criterion:**  
In der Datenhaltung ist ausschließlich ein Passwort-Hash gespeichert. Das Klartextpasswort ist nach der Registrierung oder Anmeldung nicht mehr abrufbar.

### 15b. Autorisierung

#### NFR-15b-01: Nur eigene Gruppen einsehbar

Benutzer:innen dürfen nur Gruppen sehen und bearbeiten, in denen sie Mitglied sind.

**Fit Criterion:**  
Der Zugriff auf eine Gruppe wird verweigert, wenn der Benutzer keine Mitgliedschaft in dieser Gruppe besitzt.

#### NFR-15b-02: Mitglieder verwalten nur als Admin

Nur Gruppenadministrator:innen dürfen neue Mitglieder zu einer Gruppe hinzufügen.

**Fit Criterion:**  
Ein Benutzer mit der Rolle MEMBER kann die Mitgliederverwaltung nicht erfolgreich ausführen.

### 15c. Integrität

#### NFR-15c-01: Keine ungültigen Daten im System

CampusSplit muss verhindern, dass fachlich ungültige Daten gespeichert werden.

**Fit Criterion:**  
Das System lehnt Ausgaben ab, wenn:

- der Betrag kleiner oder gleich 0,00 ist
- kein Zahler ausgewählt wurde
- der Zahler kein Gruppenmitglied ist
- keine beteiligte Person ausgewählt wurde
- die Summe der Kostenanteile nicht dem Abrechnungsbetrag entspricht
- bei einer Fremdwährungsausgabe kein Wechselkurs ermittelt werden konnte

#### NFR-15c-02: Keine sensiblen Daten im Export

Exportdateien dürfen keine sicherheitsrelevanten Informationen enthalten.

**Fit Criterion:**  
PDF- und CSV-Exporte enthalten keine Passwörter, Passwort-Hashes, Sessiondaten oder sicherheitsrelevanten Konfigurationswerte.

### 15d. Datenschutz

#### NFR-15d-01: Nur notwendige personenbezogene Daten

CampusSplit verarbeitet nur personenbezogene Daten, die für die Nutzung der Anwendung erforderlich sind.

Dazu zählen insbesondere Name, E-Mail-Adresse, Gruppenmitgliedschaften und die erfassten Ausgaben.

**Fit Criterion:**  
Die Registrierung erfordert keine Daten, die für die Nutzung der Anwendung nicht notwendig sind, zum Beispiel Adresse, Telefonnummer oder Bankdaten. An den Wechselkursdienst werden keine personenbezogenen Daten übertragen.

#### NFR-15d-02: Keine Bankdaten im System

CampusSplit darf keine Bankdaten speichern oder verarbeiten.

**Fit Criterion:**  
Im Datenmodell und in den Dialogen existieren keine Felder für IBAN, Kreditkartendaten, PayPal-Konto oder andere Zahlungsinformationen.

### 15e. Protokollierung

#### NFR-15e-01: Logs ohne sensible Daten

Logausgaben dürfen keine sensiblen Informationen enthalten.

**Fit Criterion:**  
Logs enthalten keine Passwörter, Passwort-Hashes, Sessiontokens oder vollständigen Zugangsdaten.

## 16\. Sprach- und Kulturvorgaben

### 16a. Sprache

#### NFR-16a-01: Oberfläche einheitlich auf Deutsch

Die Benutzeroberfläche soll eine einheitliche Sprache verwenden.

Für CampusSplit ist Deutsch als Sprache der Benutzeroberfläche vorgesehen.

**Fit Criterion:**  
Dialogtitel, Buttons, Fehlermeldungen und Hinweise sind in deutscher Sprache formuliert und innerhalb der Anwendung konsistent.

### 16b. Dokumentationssprache

#### NFR-16b-01: Dokumentation auf Deutsch

Die Spezifikation und begleitende Projektdokumentation werden in deutscher Sprache erstellt.

**Fit Criterion:**  
Die Dokumentationsbausteine der Spezifikation sind auf Deutsch formuliert. Technische Begriffe dürfen verwendet werden, wenn sie im Glossar erklärt werden.

## 17\. Konformitätsanforderungen

### 17a. Git-Konventionen

#### NFR-17a-01: Conventional Commits

Git-Commit-Nachrichten müssen dem Conventional-Commits-Schema folgen.

Beispiele:

feat(expense): add expense creation  
fix(balance): correct rounding difference  
docs(spec): add use case descriptions  
test(balance): add settlement calculation tests

**Fit Criterion:**  
Commit-Nachrichten folgen dem Muster type(scope): description.

### 17b. Dokumentationskonformität

#### NFR-17b-01: Spezifikation, Architektur und Code passen zusammen

Die Artefakte müssen nachvollziehbar aufeinander aufbauen.

Use Cases aus [F2](F2-anwendungsfälle.md) müssen in der Architektur und im Code wiederauffindbar sein.

**Fit Criterion:**  
Für zentrale Use Cases wie „Ausgabe erfassen", „Salden anzeigen" und „Export erzeugen" kann gezeigt werden, welche Datenobjekte, Anwendungsfunktionen, Architekturkomponenten und Codebereiche beteiligt sind.

## N1.1 Nicht anwendbare Volere-Bereiche

Einige nichtfunktionale Anforderungsbereiche sind für CampusSplit in der ersten Version nicht relevant.

| Bereich                                   | Status                               | Begründung                                                              |
| ----------------------------------------- | ------------------------------------ | ----------------------------------------------------------------------- |
| Safety-critical Requirements              | nicht anwendbar                      | CampusSplit ist kein sicherheitskritisches System.                      |
| Zahlungs-Compliance                       | nicht anwendbar                      | CampusSplit verarbeitet keine Zahlungen.                                |
| Bankrechtliche Anforderungen              | nicht anwendbar                      | Es gibt keine Bank- oder Zahlungsintegration.                           |
| Medizinische oder behördliche Regulierung | nicht anwendbar                      | Das Projekt hat keinen entsprechenden Fachkontext.                      |
| Hochverfügbarkeit                         | nicht anwendbar                      | CampusSplit ist ein Hochschulprojekt und kein produktives Massensystem. |
| Mehrsprachigkeit                          | nicht Bestandteil der ersten Version | Die Benutzeroberfläche ist in der ersten Version deutsch.               |

##

## N1.2 Querverweise

| Baustein | Relevanz für N1                                                                                                                  |
| -------- | -------------------------------------------------------------------------------------------------------------------------------- |
| [P1](P1_Ziele_und_Rahmenbedingungen.md)       | Definiert Ziele, Rahmenbedingungen, Umfang und Nichtziele, aus denen Qualitätsanforderungen abgeleitet werden.                   |
| [P2](P2_Architekturueberblick.md)       | Beschreibt die Systemlandschaft mit Browser, CampusSplit, Datenbank und Exportdateien.                                           |
| [F1](F1-geschaeftsprozesse.md)       | Der Geschäftsprozess zeigt, welche Qualitätsanforderungen für Ausgabenerfassung, Saldenanzeige und Export relevant sind.         |
| [F2](F2-anwendungsfälle.md)       | Use Cases konkretisieren, wo Anforderungen an Bedienbarkeit, Sicherheit und Fehlerbehandlung wirken.                             |
| [F3](F3-anwendungsfunktionen.md)       | Kostenaufteilung, Saldenberechnung und Exportaufbereitung müssen korrekt, testbar und nachvollziehbar sein.                      |
| [D1](D1_Datenmodell.md#d15-datenmodell-invarianten)       | Datenmodell-Invarianten stützen Anforderungen an Datenkonsistenz und Zugriffsschutz.                                             |
| [D2](D2_Datentypenverzeichnis.md)       | Fachliche Datentypen wie MoneyAmountDT, CurrencyCodeDT, ExchangeRateDT, SplitMethodDT und ExportFormatDT bestimmen Anforderungen an Genauigkeit und Validierung. |
| [B1](B1_Dialogspezifikation.md)       | Dialoge müssen benutzbar, responsiv und verständlich sein.                                                                       |
| [B3](B3_Druckausgaben.md)       | Exportdateien müssen konsistent, sicher und fachlich korrekt erzeugt werden.                                                     |
| [S1](S1_Nachbarsysteme.md)       | Externer Wechselkursdienst muss zuverlässig und sicher genutzt werden; Ausfälle dürfen keine unvollständigen Daten erzeugen.     |
| [S3](S3_Inbetriebnahme.md)       | Inbetriebnahme muss sicherstellen, dass zentrale Funktionen nach Start und Release prüfbar sind.                                 |
| [N2](N2_Querschnittskonzepte.md)       | Querschnittskonzepte konkretisieren Authentifizierung, Autorisierung, Validierung, Fehlerbehandlung und Logging.                 |
| [E2](E2_Glossar.md)       | Glossar erklärt zentrale Begriffe wie Saldo, Kostenanteil, Gruppe, Export und Administrator.                                     |
