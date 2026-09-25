# 9 Architekturentscheidungen

Dieses Kapitel dokumentiert die wichtigsten Architekturentscheidungen für CampusSplit. Jede Entscheidung beschreibt den Kontext, betrachtete Optionen und die Begründung der gewählten Lösung.

Die Entscheidungen verbinden Spezifikation, Architektur und spätere Implementierung. Sie sind nicht als endgültige Implementierungsdetails zu verstehen, sondern als verbindliche Leitplanken für die Umsetzung.

---

## ADR-001: Spring Boot mit Thymeleaf als Frontend-Technologie

**Status:** Akzeptiert

### Kontext

CampusSplit benötigt eine browserbasierte Oberfläche für Registrierung, Anmeldung, Dashboard, Gruppen, Ausgaben, Salden und Export. Die Dialoge aus B1 sollen sowohl auf Desktop als auch auf mobilen Browsern nutzbar sein. Ursprünglich war hierfür React mit TypeScript als eigenständiges Frontend vorgesehen. Nach Rücksprache im Team wurde entschieden, stattdessen den einfacheren Stack Spring Boot + Thymeleaf zu verwenden, um den Implementierungsaufwand für den Projektumfang realistisch zu halten.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Reines HTML mit serverseitigem Rendering | Backend rendert alle Seiten ohne Templating-Engine. | Einfachster Einstieg. | Wenig Struktur für wiederkehrende Layout-Elemente. |
| B — React mit TypeScript | Eigenständiges Frontend mit Komponenten, kommuniziert über REST mit dem Backend. | Gute Komponentenstruktur, moderne Interaktivität. | Zwei Deployables, eigenes Routing/Auth im Frontend, deutlich höherer Aufwand für ein Studienprojekt. |
| C — Spring Boot mit Thymeleaf | Serverseitiges Rendering über Spring MVC, Formulare und Navigation direkt im Backend. | Ein Deployable, ein Routing-System, geringerer Aufwand, für den Projektumfang ausreichend. | Kein SPA-Gefühl, weniger clientseitige Interaktivität. |

### Entscheidung

Option C — Spring Boot mit Thymeleaf.

### Begründung

CampusSplit ist ein studentisches Projekt mit begrenztem Zeitrahmen und überschaubarem Funktionsumfang (Gruppen, Ausgaben, Salden, Export). Ein separates React-Frontend mit eigener REST-API wäre für diesen Umfang unverhältnismäßig aufwendig. Thymeleaf-Templates decken die in B1 beschriebenen Dialoge vollständig ab und laufen im selben Deployable wie das Backend. Diese Entscheidung ersetzt die frühere Festlegung auf React; ADR-003 wird entsprechend angepasst.

---

## ADR-002: Spring Boot als Anwendungsplattform

**Status:** Akzeptiert

### Kontext

Die Anwendung muss Formulare verarbeiten, Eingaben validieren, Benutzer authentifizieren, Gruppenzugriffe prüfen, Daten speichern, Geldbeträge berechnen, Wechselkurse abrufen und Exporte erzeugen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Java mit Spring Boot | Java-Anwendung mit Spring MVC, Validation, Security, Data JPA und Thymeleaf. | Gute Unterstützung für Formularverarbeitung, Validierung, Datenbankzugriff und Tests. | Etwas mehr Struktur und Konfiguration nötig. |
| B — Node.js/Express | JavaScript/TypeScript im Backend. | Einfache Umsetzung. | Nicht so passend zur Java-Rahmenbedingung. |
| C — PHP/Laravel | Klassische Webplattform. | Produktiv für Webanwendungen. | Passt weniger zu den gewählten Projekttechnologien. |

### Entscheidung

Option A — Java 21 mit Spring Boot.

### Begründung

Spring Boot erfüllt die Anforderungen des Projekts sehr gut: Formularverarbeitung über Spring MVC + Thymeleaf, Validierung, Security, JPA, Transaktionen und Tests sind direkt unterstützt. Außerdem passt Java zur geplanten technischen Ausrichtung des Projekts.

---

## ADR-003: Spring Boot Monolith mit Thymeleaf statt getrenntem REST-Frontend

**Status:** Akzeptiert

### Kontext

Ursprünglich war vorgesehen, Frontend und Backend über eine REST-Schnittstelle zu trennen (React ruft Spring Boot über JSON-Endpunkte auf). Mit der Entscheidung für Thymeleaf (ADR-001) entfällt der Bedarf für diese Trennung bei der Browser-Oberfläche.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Frontend und Backend getrennt über REST | React ruft Spring Boot über JSON-Endpunkte auf. | Klare Trennung, gut testbar. | Zwei Deployables, API-Verträge müssen abgestimmt werden, für den Projektumfang unverhältnismäßig. |
| B — Ein Spring-Boot-Deployable mit Thymeleaf | Controller rendern Thymeleaf-Views direkt, Formulare per Standard-HTTP-POST. | Ein Deployable, keine separate API-Schicht nötig, einfacher zu betreiben. | Kein generisches JSON-API für andere Clients. |

### Entscheidung

Option B — ein Spring-Boot-Deployable mit Thymeleaf-Views, kein separates REST-Frontend.

### Begründung

Da die Browser-Oberfläche laut ADR-001 mit Thymeleaf serverseitig gerendert wird, ist eine zusätzliche REST-Schicht für die reine Browser-Nutzung nicht nötig. Das vereinfacht Deployment, Routing und Fehlerbehandlung erheblich, da alles in einer Anwendung läuft. Backend-Funktionen wie Kostenaufteilung, Saldenberechnung und Export bleiben trotzdem in einer eigenen Service-Schicht gekapselt und sind unabhängig von den Controllern testbar (siehe ADR-005).

---

## ADR-004: PostgreSQL als relationale Datenbank

**Status:** Akzeptiert

### Kontext

CampusSplit verarbeitet stark relationale Daten: Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile und Kategorien. Diese Daten besitzen klare Beziehungen und Integritätsregeln.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — PostgreSQL | Relationale Serverdatenbank. | Gute Unterstützung für Beziehungen, Constraints, Transaktionen und SQL. | Zusätzlicher Datenbankdienst nötig. |
| B — SQLite | Eingebettete Datenbankdatei. | Sehr leichtgewichtig. | Weniger geeignet für Team-/Serverbetrieb und parallele Nutzung. |
| C — JSON-Dateien | Speicherung in Dateien. | Sehr einfach am Anfang. | Keine sauberen Transaktionen, Constraints oder Abfragen. |

### Entscheidung

Option A — PostgreSQL.

### Begründung

PostgreSQL passt sehr gut zum Datenmodell aus D1. Besonders Membership und ExpenseShare lösen n:m-Beziehungen sauber auf. Transaktionen unterstützen konsistente Speichervorgänge, z. B. beim Anlegen einer Ausgabe mit mehreren Kostenanteilen.

**Wichtige Klarstellung zur Systemgrenze:** Gemäß [S1 — Nachbarsysteme](../Spezifikation/S1_Nachbarsysteme.md) ist PostgreSQL Teil der internen Persistenz von CampusSplit und **kein** externes Nachbarsystem. Das Kontext- und Systemgrenzendiagramm in A03 muss entsprechend korrigiert werden: PostgreSQL gehört innerhalb der CampusSplit-Systemgrenze, nicht als NB-02 außerhalb. Nur der Frankfurter Wechselkursdienst (siehe ADR-007) bleibt als externes Nachbarsystem (NB-01) außerhalb der Systemgrenze. Ebenso sollte "Benutzer / Webbrowser" in A03 aufgeteilt werden: Benutzer ist der externe Akteur, der Browser nur dessen technischer Zugangsweg.

---

## ADR-005: Geldlogik zentral im Backend

**Status:** Akzeptiert

### Kontext

Die wichtigste Fachlogik von CampusSplit ist die Berechnung von Kostenanteilen, Salden und Ausgleichsvorschlägen. Fehler in dieser Logik würden direkt zu falschen Forderungen und Verbindlichkeiten führen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Berechnung in der Präsentationsschicht | Templates/Client-Skripte berechnen Kostenanteile und Salden. | Schnelle UI-Reaktion. | Manipulationsanfällig, schwerer zentral zu testen, Risiko unterschiedlicher Logik. |
| B — Berechnung im Backend | Spring Boot berechnet Kostenanteile, Salden und Vorschläge in einer eigenen Service-Schicht. | Zentrale Regeln, testbar, konsistent für Anzeige und Export. | — |
| C — Berechnung in der Datenbank | SQL berechnet Salden direkt. | Datennahe Berechnung. | Fachlogik wird schwerer lesbar und testbar. |

### Entscheidung

Option B — Geldlogik zentral im Backend, in einer von den Controllern getrennten Service-Schicht.

### Begründung

Backendseitige Berechnung stellt sicher, dass Anzeige, Speicherung und Export dieselben Regeln verwenden. Die Trennung von Controller- und Service-Schicht (unabhängig davon, ob über Thymeleaf oder eine API angesprochen) ermöglicht Unit-Tests für Rundung, Kostenanteile, Salden und Ausgleichsvorschläge ohne HTTP-Layer.

---

## ADR-006: Geldbetrag-Repräsentation — Cent-Integer für Beträge, BigDecimal für Wechselkurse

**Status:** Akzeptiert

### Kontext

Geldbeträge müssen exakt und nachvollziehbar verarbeitet werden. Gleitkommazahlen können zu Rundungsfehlern führen und sind deshalb für Geldlogik ungeeignet. Für den Wechselkurs (ADR-007) wird zusätzlich eine präzise Dezimaldarstellung benötigt, die kein ganzzahliges Cent-Raster hat.

Die vorherige Fassung dieses ADRs war widersprüchlich: Der Status war "Akzeptiert", die Entscheidung lautete aber nur vage "Cent-Integer bevorzugt, BigDecimal alternativ erlaubt" — das ist keine eindeutige Entscheidung.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — `double`/`float` überall | Geld als Gleitkommazahl. | Einfach zu schreiben. | Rundungsfehler, ungeeignet für Geld. |
| B — `BigDecimal` überall | Auch Beträge als Dezimalzahl mit fester Skalierung. | Präzise, in Java üblich. | Größerer Aufwand und Speicherbedarf für einfache Centbeträge unnötig. |
| C — Centbasierter `long`-Wert für Beträge, `BigDecimal` für den Wechselkurs | Beträge (originalAmount, settlementAmount, shareAmount) als `long` in Cent; der Wechselkurs selbst als `BigDecimal`. | Beträge exakt und schnell prüfbar, Wechselkurs behält nötige Nachkommapräzision. | Zwei Repräsentationen, muss sauber dokumentiert werden. |

### Entscheidung

Option B — `BigDecimal` überall für alle Geldbeträge (`MoneyAmountDT`: `originalAmount`, `settlementAmount`, `shareAmount`) sowie für Wechselkurse (`ExchangeRateDT.rate`).

### Begründung

Centbasierte `long`-Werte machen die wichtigsten Regeln einfach prüfbar: Kostenanteile summieren sich exakt zum Abrechnungsbetrag, und Gruppensalden summieren sich exakt zu 0,00. Ein Wechselkurs wie `0.86` ist dagegen kein glattes Centraster und braucht echte Nachkommastellen-Präzision, daher `BigDecimal` nur an dieser einen Stelle. Die Umrechnung `originalAmount × rate = settlementAmount` erfolgt im Backend; das Ergebnis wird anschließend deterministisch auf volle Cent gerundet. Für Anzeige und Export wird der Centwert in eine Darstellung mit zwei Nachkommastellen umgewandelt.

---

## ADR-007: Frankfurter API hinter eigenem Wechselkursadapter

**Status:** Akzeptiert

### Kontext

Die Spezifikation sieht einen externen Wechselkursdienst für Fremdwährungsausgaben vor. Dieser Dienst darf keine personenbezogenen Daten erhalten und darf die Fachlogik nicht direkt dominieren.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Kein Fremdwährungssupport | Nur Gruppenwährung, keine externe API. | Einfachster Umfang. | Review-Punkt API/Schnittstelle wäre schwächer abgedeckt. |
| B — Direkter API-Aufruf aus der Präsentationsschicht | Client ruft Wechselkursdienst auf. | Schnell umzusetzen. | API-Logik verteilt sich, Fehlerbehandlung schlechter kontrollierbar. |
| C — Backend-Adapter zur Frankfurter API | Backend kapselt den externen Dienst über einen eigenen `ExchangeRateService`. | Saubere Schnittstelle, keine personenbezogenen Daten, austauschbar. | Zusätzlicher Backend-Code nötig. |

### Entscheidung

Option C — Frankfurter API über einen eigenen Backend-Adapter (`ExchangeRateService`).

### Begründung

Der Wechselkursadapter begrenzt die Abhängigkeit zur externen API auf eine Stelle. CampusSplit überträgt nur Ausgangswährung, Zielwährung und Datum. Die Umrechnung, Rundung (siehe ADR-006) und Speicherung des Ergebnisses erfolgen im Backend.

**Ergänzung zu Stakeholdern:** Der Frankfurter Wechselkursdienst und PostgreSQL sind technische Nachbarsysteme bzw. interne Persistenz, keine Stakeholder im Sinne von A01, Abschnitt 1.3. Diese sollten dort entfernt und nur im Kontextdiagramm (A03) geführt werden.

---

## ADR-008: Backendseitiger PDF- und CSV-Export

**Status:** Akzeptiert

### Kontext

CampusSplit soll Ausgabenübersichten als PDF oder CSV bereitstellen. Die exportierten Daten müssen mit den aktuellen Salden und Ausgleichsvorschlägen übereinstimmen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Export in der Präsentationsschicht | Client erzeugt PDF/CSV. | Weniger Backend-Code. | Risiko abweichender Berechnungen und schwieriger Exportsicherheit. |
| B — Export im Backend | Backend erzeugt Exportdaten und Datei. | Nutzt gleiche Fachlogik wie Saldenanzeige, sicherheitsprüfbar. | Backend benötigt Exportbibliotheken. |
| C — Externer Exportdienst | Dritter Dienst erzeugt Dokumente. | Auslagerung der Dokumenterzeugung. | Zusätzliche Schnittstelle, Datenschutz- und Betriebsaufwand. |

### Entscheidung

Option B — Export im Backend.

### Begründung

Backendseitiger Export stellt sicher, dass PDF und CSV dieselben Daten und Berechnungen verwenden wie die Anwendung. Außerdem kann das Backend zuverlässig verhindern, dass Passwörter, Tokens oder technische Interna in den Export gelangen.

---

## ADR-009: Keine Hintergrundjobs im MVP

**Status:** Akzeptiert

### Kontext

CampusSplit verarbeitet Benutzeraktionen wie Gruppe erstellen, Ausgabe speichern, Salden anzeigen und Export erzeugen. Die Spezifikation markiert Batch-Prozesse als nicht anwendbar.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Hintergrundjobs/Scheduler | Regelmäßige automatische Verarbeitung. | Sinnvoll bei automatischen Erinnerungen oder Importen. | Nicht erforderlich, erhöht Komplexität. |
| B — Synchrone Verarbeitung auf Benutzeraktion | Verarbeitung startet direkt bei der Anfrage. | Einfach, nachvollziehbar, passend zum MVP. | Benutzer wartet bei Export oder Wechselkurs kurz auf Ergebnis. |

### Entscheidung

Option B — synchrone Verarbeitung auf Benutzeraktion.

### Begründung

CampusSplit benötigt keine regelmäßigen Nachtläufe, keine automatischen Zahlungsprozesse und keine periodischen Imports. Alle relevanten Vorgänge können direkt durch Benutzeraktionen ausgelöst werden.

---

## ADR-010: Spring Security mit serverseitiger Sitzung

**Status:** Akzeptiert

### Kontext

CampusSplit läuft als ein Spring-Boot-Deployable mit Thymeleaf (siehe ADR-001, ADR-003). Die Spezifikation spricht von Anmeldung, Abmeldung und Sitzung (siehe N2.2). Zugangsdaten und Gruppendaten müssen geschützt werden.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — HTTP-only Session-Cookie | Backend verwaltet die Sitzung serverseitig, Browser speichert nur das Session-Cookie. | Keine Tokens im JavaScript, passt zum serverseitigen Rendering mit Thymeleaf, einfacher Logout. | Sitzungsspeicher auf dem Server nötig. |
| B — JWT im Local Storage | Client speichert Token selbst. | Wäre bei getrenntem Frontend/Backend praktisch gewesen. | Höheres Risiko bei XSS, Logout schwieriger, für Thymeleaf unüblich. |
| C — Keine echte Auth im MVP | Nur Mock-Benutzer. | Schnell am Anfang. | Widerspricht Spezifikation und Sicherheitsanforderungen. |

### Entscheidung

Option A — Spring Security mit serverseitiger Sitzung und HTTP-only Session-Cookie.

### Begründung

Diese Lösung passt zur Spezifikation (N2.2) und vermeidet dauerhaft gespeicherte Tokens im Client. Da CampusSplit nach ADR-003 als ein einziges Spring-Boot-Deployable mit Thymeleaf läuft, entfällt außerdem die frühere Sorge um CORS/CSRF zwischen getrennten Frontend-/Backend-Origins — Frontend und Backend laufen unter derselben Origin. Diese Entscheidung muss durchgängig in A05 und A08 nachgezogen werden: dort darf nicht mehr offen "Session-Cookie oder Token" stehen, sondern nur noch diese Session-Variante.

---

## Eingesetzte KI-Werkzeuge

Claude (Anthropic) wurde unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet.

Die fachlichen Inhalte wurden anschließend mit den vorhandenen Spezifikationsbausteinen abgeglichen.
