# S1 — Nachbarsysteme

P2 zeigt schon im Überblick, mit welchen Systemen CampusSplit spricht. S1 geht auf jede dieser Schnittstellen etwas genauer ein: was fließt hin und her, wann, und was passiert bei Störungen. 

## S1.1 Übersicht

| ID | Nachbarsystem | Rolle | Richtung | Häufigkeit |
|---|---|---|---|---|
| NB-01 | Webbrowser | Benutzeroberfläche | bidirektional | bei jeder Aktion |
| NB-02 | PostgreSQL | Datenspeicher | bidirektional | bei fast jeder Anfrage |
| NB-03 | PDF-/CSV-Export | Download-Datei | ausgehend | auf Anforderung (UC-12) |

Externe Dienste wie Zahlungsanbieter, Bank, E-Mail oder Cloud-Login sind bewusst nicht vorgesehen (siehe P1).

## S1.2 NB-01 — Webbrowser

Der Browser ist der einzige Weg, CampusSplit zu nutzen. Er zeigt die Dialoge aus B1 und schickt Eingaben ans System wie Login-Daten, Formulare, Exportanfragen. Umgekehrt bekommt er Gruppen, Ausgaben, Salden und Fehlermeldungen zurück.

Bricht die Verbindung ab, kann es nicht weiter genutzt werden. Das zeigt dann der Browser selbst an und nicht CampusSplit. Sobald die Verbindung wiederhergestellt ist, geht alles ohne Datenverlust weiter.

Alle Funktionen außer Registrierung und Anmeldung setzen eine angemeldete Sitzung voraus (siehe N2.2).

## S1.3 NB-02 — PostgreSQL-Datenbank

Hier liegen alle dauerhaften Daten: Benutzer, Gruppen, Mitgliedschaften, Ausgaben, Kostenanteile. Salden und Ausgleichsvorschläge werden dagegen nicht gespeichert, sondern bei Bedarf aus diesen Daten berechnet (siehe D1.3).

Ist die Datenbank nicht erreichbar, kann CampusSplit nicht zuverlässig arbeiten. Wichtig: Es dürfen dabei nie halbe Datensätze entstehen, und die Fehlermeldung an die Nutzer:innen bleibt verständlich statt technisch (siehe N2.6).

Zugangsdaten zur Datenbank gehören nicht ins Repository (siehe S3.3). Wer welche Gruppendaten sehen darf, regelt N2.3 und zwar unabhängig davon, wie die Datenbankabfrage technisch läuft.

Da CampusSplit ein Neuprojekt ohne Altsystem ist, muss hier nichts migriert werden (Baustein S2 ist deshalb nicht anwendbar).

## S1.4 NB-03 — PDF-/CSV-Export

Diese Schnittstelle hebt folgendes vor :  CampusSplit erzeugt auf Wunsch eine Datei zum Download. Was genau es beinhaltet wird von B3 geführt. Hier geht es nur um die Schnittstelle selbst.

Ausgelöst wird das ausschließlich manuell über UC-12/DLG-11, nie automatisch oder zeitgesteuert.

Sollte die Erzeugung nicht klappen, gibt's eine verständliche Fehlermeldung und dann entsteht einen neuen Verusch. Dabei entsteht keine halbe Datei und es werden keine Daten verändert. Nur Gruppenmitglieder dürfen Exporte ihrer eigenen Gruppe erzeugen (N2.8).

Nach dem Download gehört die Datei der Nutzer:innen. CampusSplit speichert sie nicht dauerhaft und verfolgt nicht aktiv weiter, was damit passiert.

## S1.5 Nicht Bestandteil von S1

Konkrete Protokolle, Datenbankschema, Verbindungspools, PDF-/CSV-Bibliotheken, Anbindung externer Dienste, Datenmigration.

## S1.6 Querverweise

| Baustein | Relevanz |
|---|---|
| P2 | Liefert den Überblick, den S1 vertieft. |
| D1 | Beschreibt die über NB-02 gespeicherten und über NB-03 exportierten Daten. |
| B1 | Dialoge sind die über NB-01 dargestellte Oberfläche. |
| B3 | Beschreibt den Inhalt der über NB-03 erzeugten Datei. |
| S3 | Beschreibt, was für die Erreichbarkeit dieser Schnittstellen beim Start nötig ist. |
| N2 | Regelt Authentifizierung, Zugriff und Fehlerverhalten an allen drei Schnittstellen. |

## Eingesetzte KI-Werkzeuge

Claude (Anthropic): Für die Verbindung und Expandierung unterschiedliche Ideen und Bausteine sowie die saubere Formulierung

Entwurf geprüft von Momosan009.
