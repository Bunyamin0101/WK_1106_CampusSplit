# 9 Architekturentscheidungen

Dieses Kapitel dokumentiert die wichtigsten Architekturentscheidungen für CampusSplit. Jede Entscheidung beschreibt den Kontext, betrachtete Optionen und die Begründung der gewählten Lösung.

Die Entscheidungen verbinden Spezifikation, Architektur und spätere Implementierung. Sie sind nicht als endgültige Implementierungsdetails zu verstehen, sondern als verbindliche Leitplanken für die Umsetzung.

---

## ADR-001: React mit TypeScript als Frontend

**Status:** Akzeptiert

### Kontext

CampusSplit benötigt eine browserbasierte Oberfläche für Registrierung, Anmeldung, Dashboard, Gruppen, Ausgaben, Salden und Export. Die Dialoge aus B1 sollen sowohl auf Desktop als auch auf mobilen Browsern nutzbar sein.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Reines HTML mit serverseitigem Rendering | Backend rendert alle Seiten. | Einfacher Einstieg, weniger Frontend-Tooling. | Weniger geeignet für dynamische Formulare, Beteiligtenauswahl und Live-Validierung. |
| B — React mit TypeScript | Eigenständiges Frontend mit Komponenten und typisierten API-Daten. | Gute Komponentenstruktur, geeignet für responsive Dialoge, klare Trennung vom Backend. | Zusätzlicher Build-Schritt, API-Integration nötig. |
| C — Vue oder Angular | Alternative SPA-Frameworks. | Technisch ebenfalls möglich. | Kein klarer Vorteil gegenüber React im Projektteam. |

### Entscheidung

Option B — React mit TypeScript.

### Begründung

React mit TypeScript passt zu einer komponentenbasierten Weboberfläche. Die Dialoge aus B1 können als Seiten und wiederverwendbare Komponenten umgesetzt werden. TypeScript hilft dabei, Request- und Response-Strukturen der REST-API konsistent zu verwenden.

---

## ADR-002: Spring Boot als Backend-Plattform

**Status:** Akzeptiert

### Kontext

Das Backend muss REST-Endpunkte bereitstellen, Eingaben validieren, Benutzer authentifizieren, Gruppenzugriffe prüfen, Daten speichern, Geldbeträge berechnen, Wechselkurse abrufen und Exporte erzeugen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Java mit Spring Boot | Java-Backend mit Spring Web, Validation, Security und Data JPA. | Gute Unterstützung für REST, Validierung, Datenbankzugriff und Tests. | Etwas mehr Struktur und Konfiguration nötig. |
| B — Node.js/Express | JavaScript/TypeScript im Backend. | Einfache REST-API, gleiche Sprache wie Frontend möglich. | Nicht so passend zur Java-Rahmenbedingung. |
| C — PHP/Laravel | Klassische Webplattform. | Produktiv für Webanwendungen. | Passt weniger zu den gewählten Projekttechnologien. |

### Entscheidung

Option A — Java 21 mit Spring Boot.

### Begründung

Spring Boot erfüllt die Anforderungen des Projekts sehr gut: REST-API, Validierung, Security, JPA, Transaktionen und Tests sind direkt unterstützt. Außerdem passt Java zur geplanten technischen Ausrichtung des Projekts.

---

## ADR-003: Getrenntes Frontend und Backend über REST

**Status:** Akzeptiert

### Kontext

Die Spezifikation sieht eine Webanwendung mit getrennter Benutzeroberfläche und Anwendungslogik vor. Das Frontend soll Dialoge anzeigen, während das Backend Fachlogik und Persistenz verantwortet.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Frontend und Backend getrennt über REST | React ruft Spring Boot über JSON-Endpunkte auf. | Klare Trennung, gut testbar, gut dokumentierbar. | API-Verträge müssen abgestimmt werden. |
| B — Serverseitige Templates | Backend rendert HTML direkt. | Weniger API-Aufwand. | Vermischt UI und Backend stärker. |
| C — Alles im Frontend mit lokaler Speicherung | Browser speichert Daten lokal. | Sehr einfacher Start. | Keine sichere Mehrbenutzerfähigkeit und keine verlässliche Autorisierung. |

### Entscheidung

Option A — getrenntes Frontend und Backend über REST.

### Begründung

Die REST-Schnittstelle macht die Systemgrenze zwischen Frontend und Backend klar. Das unterstützt die Review-Notizen zu API, Nachbarsystemen und Datenflüssen. Außerdem können Backend-Funktionen wie Kostenaufteilung, Saldenberechnung und Export unabhängig vom Frontend getestet werden.

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

---

## ADR-005: Geldlogik zentral im Backend

**Status:** Akzeptiert

### Kontext

Die wichtigste Fachlogik von CampusSplit ist die Berechnung von Kostenanteilen, Salden und Ausgleichsvorschlägen. Fehler in dieser Logik würden direkt zu falschen Forderungen und Verbindlichkeiten führen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Berechnung im Frontend | React berechnet Kostenanteile und Salden. | Schnelle UI-Reaktion. | Manipulationsanfällig, schwerer zentral zu testen, Risiko unterschiedlicher Logik. |
| B — Berechnung im Backend | Spring Boot berechnet Kostenanteile, Salden und Vorschläge. | Zentrale Regeln, testbar, konsistent für Anzeige und Export. | Frontend muss Ergebnisse vom Backend abfragen. |
| C — Berechnung in der Datenbank | SQL berechnet Salden direkt. | Datennahe Berechnung. | Fachlogik wird schwerer lesbar und testbar. |

### Entscheidung

Option B — Geldlogik zentral im Backend.

### Begründung

Backendseitige Berechnung stellt sicher, dass Anzeige, Speicherung und Export dieselben Regeln verwenden. Dadurch können Unit-Tests für Rundung, Kostenanteile, Salden und Ausgleichsvorschläge gezielt geschrieben werden.

---

## ADR-006: Centbasierte Money-Repräsentation

**Status:** Akzeptiert

### Kontext

Geldbeträge müssen exakt und nachvollziehbar verarbeitet werden. Gleitkommazahlen können zu Rundungsfehlern führen und sind deshalb für Geldlogik ungeeignet.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — `double`/`float` | Geld als Gleitkommazahl. | Einfach zu schreiben. | Rundungsfehler, ungeeignet für Geld. |
| B — `BigDecimal` | Dezimalzahl mit fester Skalierung. | Präzise, in Java üblich. | Disziplin bei Skalierung und Rundung nötig. |
| C — Centbasierter Integer-Wert | Betrag intern als ganze Centzahl. | Sehr einfach exakt zu testen, keine Nachkommaberechnungsfehler. | Anzeigeumrechnung nötig. |

### Entscheidung

Option C als bevorzugte fachliche Repräsentation; `BigDecimal` ist als technische Alternative erlaubt, wenn konsequent mit fester Skalierung gearbeitet wird.

### Begründung

Centbasierte Werte machen die wichtigsten Regeln einfach prüfbar: Anteile summieren sich exakt zum Abrechnungsbetrag und Gruppensalden exakt zu 0,00. Für die Anzeige und den Export wird der Centwert in eine Darstellung mit zwei Nachkommastellen umgewandelt.

---

## ADR-007: Frankfurter API hinter eigenem Wechselkursadapter

**Status:** Akzeptiert

### Kontext

Die Spezifikation sieht einen externen Wechselkursdienst für Fremdwährungsausgaben vor. Dieser Dienst darf keine personenbezogenen Daten erhalten und darf die Fachlogik nicht direkt dominieren.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Kein Fremdwährungssupport | Nur Gruppenwährung, keine externe API. | Einfachster Umfang. | Review-Punkt API/Schnittstelle wäre schwächer abgedeckt. |
| B — Direkter API-Aufruf aus dem Frontend | Browser ruft Wechselkursdienst auf. | Schnell umzusetzen. | API-Logik verteilt sich im Frontend, Fehlerbehandlung schlechter kontrollierbar. |
| C — Backend-Adapter zur Frankfurter API | Backend kapselt den externen Dienst. | Saubere Schnittstelle, keine personenbezogenen Daten, austauschbar. | Zusätzlicher Backend-Code nötig. |

### Entscheidung

Option C — Frankfurter API über einen eigenen Backend-Adapter.

### Begründung

Der Wechselkursadapter begrenzt die Abhängigkeit zur externen API auf eine Stelle. CampusSplit überträgt nur Ausgangswährung, Zielwährung und Datum. Die Umrechnung, Rundung und Speicherung des Ergebnisses erfolgen im Backend.

---

## ADR-008: Backendseitiger PDF- und CSV-Export

**Status:** Akzeptiert

### Kontext

CampusSplit soll Ausgabenübersichten als PDF oder CSV bereitstellen. Die exportierten Daten müssen mit den aktuellen Salden und Ausgleichsvorschlägen übereinstimmen.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — Export im Frontend | Browser erzeugt PDF/CSV. | Weniger Backend-Code. | Risiko abweichender Berechnungen und schwieriger Exportsicherheit. |
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
| B — Synchrone Verarbeitung auf Benutzeraktion | Verarbeitung startet durch REST-Anfrage. | Einfach, nachvollziehbar, passend zum MVP. | Benutzer wartet bei Export oder Wechselkurs kurz auf Ergebnis. |

### Entscheidung

Option B — synchrone Verarbeitung auf Benutzeraktion.

### Begründung

CampusSplit benötigt keine regelmäßigen Nachtläufe, keine automatischen Zahlungsprozesse und keine periodischen Imports. Alle relevanten Vorgänge können direkt durch Benutzeraktionen ausgelöst werden.

---

## ADR-010: Spring Security mit serverseitiger Sitzung

**Status:** Akzeptiert für den MVP, bei Implementierung prüfbar

### Kontext

Das Frontend und Backend sind getrennt, aber die Spezifikation spricht von Anmeldung, Abmeldung und Sitzung. Zugangsdaten und Gruppendaten müssen geschützt werden.

### Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|---|---|---|---|
| A — HTTP-only Session-Cookie | Backend verwaltet Sitzung, Browser speichert nur Cookie. | Keine Tokens im JavaScript, passt zu Logout und Sitzung. | CORS/CSRF-Konfiguration bei getrennten Dev-Ports beachten. |
| B — JWT im Local Storage | Frontend speichert Token selbst. | Einfach bei getrenntem Frontend/Backend. | Höheres Risiko bei XSS, Logout schwieriger. |
| C — Keine echte Auth im MVP | Nur Mock-Benutzer. | Schnell am Anfang. | Widerspricht Spezifikation und Sicherheitsanforderungen. |

### Entscheidung

Option A — Spring Security mit serverseitiger Sitzung und HTTP-only Cookie.

### Begründung

Diese Lösung passt zur Spezifikation und vermeidet dauerhaft gespeicherte Tokens im Frontend. Für die Entwicklung kann ein Vite-Proxy oder eine passende CORS/CSRF-Konfiguration verwendet werden. Die endgültige technische Ausgestaltung kann bei der Implementierung konkretisiert werden, ohne die fachliche Architektur zu ändern.
