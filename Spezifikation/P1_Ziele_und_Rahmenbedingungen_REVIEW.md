# P1 — Ziele und Rahmenbedingungen (Review-Fassung)

Diese Datei ergänzt die bereits vorhandene Projektgrundlagen-Datei und trennt P1 sauber von P2. Die ältere kombinierte Datei bleibt unverändert bestehen.

P1 beschreibt, warum CampusSplit entwickelt wird, für wen das System gedacht ist, welche Ziele verfolgt werden und welche Grenzen für den Projektumfang gelten.

---

## P1.1 Ausgangssituation und Motivation

Gemeinsame Ausgaben entstehen häufig in Wohngemeinschaften, Reisegruppen oder studentischen Projektgruppen. Oft bezahlt zunächst eine Person für mehrere Beteiligte. Später muss nachvollziehbar geklärt werden, wer welchen Anteil trägt und welche Beträge noch offen sind.

Ohne geeignetes System werden solche Abrechnungen häufig über Chatnachrichten, Tabellen oder mündliche Absprachen organisiert. Dadurch entstehen schnell Unübersichtlichkeit, Rechenfehler und Missverständnisse.

CampusSplit unterstützt diesen Prozess durch eine Webanwendung, mit der Gruppen angelegt, Ausgaben erfasst, Kostenanteile berechnet, Salden angezeigt und Übersichten exportiert werden können.

---

## P1.2 Systemziele

| ID | Ziel | Messbare/prüfbare Wirkung |
|---|---|---|
| Z-01 | Gemeinsame Ausgaben zentral erfassen | Ausgaben können pro Gruppe gespeichert und angezeigt werden. |
| Z-02 | Kosten nachvollziehbar auf Mitglieder aufteilen | Jede Ausgabe besitzt einen Zahler und Kostenanteile. |
| Z-03 | Offene Forderungen und Verbindlichkeiten anzeigen | Salden zeigen, wer Geld bekommt und wer Geld schuldet. |
| Z-04 | Abrechnung vereinfachen | Manuelle Tabellen oder Chatabrechnungen werden reduziert. |
| Z-05 | Desktop- und mobile Nutzung ermöglichen | Die Anwendung ist im Browser responsiv nutzbar. |
| Z-06 | Ausgabenübersichten exportieren | PDF- und CSV-Export können aus einer Gruppe erzeugt werden. |
| Z-07 | Fremdwährungsausgaben nachvollziehbar behandeln | Fremdwährungsbeträge werden über einen Wechselkursdienst in die Gruppenwährung umgerechnet. |

---

## P1.3 Zielgruppen und Stakeholder

| Rolle | Beschreibung | Interesse am System |
|------|--------------|---------------------|
| Gruppenmitglied | Registrierte Person innerhalb einer Gruppe | Ausgaben erfassen, Salden sehen, Export nutzen |
| Gruppenadministrator | Mitglied mit Verwaltungsrechten | Gruppe anlegen und Mitglieder hinzufügen |
| Entwicklungsteam | Projektteam im Hochschulprojekt | System spezifizieren, entwickeln, testen und präsentieren |
| Prüfer/Betreuer | Bewertende Person im Modul | Nachvollziehbarkeit von Spezifikation, Architektur und Umsetzung |
| Externer Wechselkursdienst | Nachbarsystem für Fremdwährungen | Liefert Kurse für Umrechnung in die Gruppenwährung |

Die Arbeitsaufteilung innerhalb des Teams ist nicht Bestandteil von P1. Rollen im Projektteam werden außerhalb dieses Bausteins dokumentiert.

---

## P1.4 Projektumfang

| Bereich | Enthalten | Nicht enthalten |
|--------|-----------|-----------------|
| Benutzerverwaltung | Registrierung, Anmeldung, Abmeldung | Social Login, Zwei-Faktor-Authentifizierung |
| Gruppenverwaltung | Gruppen erstellen, Gruppen anzeigen, Mitglieder hinzufügen | Organisationsverwaltung, Mandantenmodell |
| Ausgabenverwaltung | Ausgaben erfassen, bearbeiten, löschen | Automatischer Import von Bankumsätzen |
| Kostenaufteilung | Gleichmäßige Aufteilung, individuelle Beträge | Komplexe Vertrags- oder Rechtslogik |
| Salden | Forderungen, Verbindlichkeiten, Ausgleichsvorschläge | Ausführung echter Zahlungen |
| Export | PDF- und CSV-Ausgabe | E-Mail-Versand, Cloud-Ablage |
| Währungen | Gruppenwährung und Umrechnung über Wechselkursdienst | Bankintegration oder Zahlungsabwicklung |
| Plattform | Responsive Webanwendung | Native Android- oder iOS-App |
| Datenübernahme | Neue Daten im System | Migration aus Altsystemen |

---

## P1.5 Nichtziele

| ID | Nichtziel | Begründung |
|---|-----------|------------|
| NZ-01 | Direkte Zahlungsabwicklung | CampusSplit berechnet nur Salden; Zahlungen erfolgen außerhalb der Anwendung. |
| NZ-02 | Bankintegration | Bankdaten erhöhen Komplexität, Datenschutzanforderungen und Projektumfang. |
| NZ-03 | OCR-Belegerkennung | Automatische Belegerkennung ist kein Kernbestandteil des Projekts. |
| NZ-04 | Echtzeit-Chat | Kommunikation zwischen Mitgliedern ist nicht Ziel der Anwendung. |
| NZ-05 | Native Mobile Apps | Eine responsive Webanwendung genügt für den Projektumfang. |
| NZ-06 | Datenmigration aus Altsystemen | CampusSplit startet als Greenfield-Projekt ohne Altdaten. |
| NZ-07 | Vollständige Zahlungshistorie | Tatsächliche Zahlungen werden nicht verarbeitet oder bestätigt. |

---

## P1.6 Rahmenbedingungen

| ID | Rahmenbedingung | Bedeutung für CampusSplit |
|---|-----------------|----------------------------|
| RB-01 | Hochschulprojekt mit begrenzter Zeit | Der Umfang bleibt MVP-orientiert. |
| RB-02 | Webanwendung | Nutzung über Browser auf Desktop und Smartphone. |
| RB-03 | Getrenntes Frontend und Backend | Frontend kommuniziert über eine eigene REST-API mit dem Backend. |
| RB-04 | Relationale Datenhaltung | Gruppen, Ausgaben und Kostenanteile werden strukturiert gespeichert. |
| RB-05 | Java 21 / Spring Boot | Backend-Technologie. |
| RB-06 | React / TypeScript | Frontend-Technologie. |
| RB-07 | PostgreSQL | Datenbanktechnologie. |
| RB-08 | GitHub-Repository | Spezifikation, Code und Dokumentation werden versioniert. |
| RB-09 | Externer Wechselkursdienst | Nur für Fremdwährungsausgaben; keine personenbezogenen Daten werden übertragen. |

---

## P1.7 Erfolgskriterien

| ID | Erfolgskriterium |
|---|------------------|
| K-01 | Benutzer können sich registrieren und anmelden. |
| K-02 | Gruppen können erstellt und geöffnet werden. |
| K-03 | Mitglieder können einer Gruppe hinzugefügt werden. |
| K-04 | Ausgaben können mit Zahler, Betrag, Währung, Datum und Beteiligten erfasst werden. |
| K-05 | Kostenanteile werden korrekt und centgenau berechnet. |
| K-06 | Salden zeigen nachvollziehbar Debitoren und Kreditoren. |
| K-07 | Fremdwährungsausgaben werden über einen dokumentierten Wechselkurs nachvollziehbar umgerechnet. |
| K-08 | PDF- und CSV-Exporte können erzeugt werden. |
| K-09 | Zentrale Funktionen sind durch Tests oder Review nachvollziehbar überprüfbar. |

---

## P1.8 Risiken und Gegenmaßnahmen

| Risiko | Auswirkung | Gegenmaßnahme |
|-------|------------|---------------|
| Fehlerhafte Saldenberechnung | Falsche Forderungen und Verbindlichkeiten | Unit-Tests für Kostenanteile, Salden und Rundung |
| Unklare Fremdwährungslogik | Nicht nachvollziehbare Abrechnungsbeträge | Originalbetrag, Währung, Kurs und Gruppenwährung speichern |
| Externe API nicht erreichbar | Fremdwährungsausgabe kann nicht umgerechnet werden | Fehlermeldung; kein erfundener Kurs; erneuter Versuch möglich |
| Zu großer Funktionsumfang | Projekt wird zeitlich nicht fertig | Zahlungsabwicklung, OCR und Chat bleiben ausgeschlossen |
| Uneinheitliche Dokumentation | Review wird erschwert | Bausteine getrennt, Tabellen und Mermaid-Diagramme verwenden |

---

## P1.9 Querverweise

| Baustein | Relevanz |
|---|---|
| P2 | Systemkontext und Nachbarsysteme konkretisieren den Umfang. |
| F2 | Use Cases setzen die Ziele funktional um. |
| F3 | Berechnungen für Kostenanteile, Salden und Währungen stützen die Kernziele. |
| D1/D2 | Datenmodell und Datentypen bilden die Grundlage für Ausgaben, Beträge und Währungen. |
| B1/B3 | Dialoge und Exporte machen die Funktionen für Benutzer sichtbar. |
| S1 | Der Wechselkursdienst und die REST-API werden dort als Schnittstellen beschrieben. |
| N1/N2 | Qualitätsanforderungen und Querschnittskonzepte sichern Bedienbarkeit, Korrektheit und Sicherheit. |
