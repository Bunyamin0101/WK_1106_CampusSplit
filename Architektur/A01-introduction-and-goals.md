# 1 Einführung und Ziele

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Nutzerinnen und Nutzer können Gruppen erstellen, Mitglieder hinzufügen, Ausgaben erfassen, Kostenanteile berechnen lassen, Salden anzeigen und Ausgabenübersichten als PDF oder CSV exportieren.

Dieses Kapitel fasst die Anforderungen und Qualitätsziele zusammen, die die Architektur von CampusSplit bestimmen. Die verbindliche fachliche Spezifikation liegt im Ordner [`../Spezifikation/`](../Spezifikation/) und wird hier referenziert, nicht vollständig wiederholt.

---

## 1.1 Anforderungsüberblick

CampusSplit ermöglicht angemeldeten Nutzern, Gruppen zu verwalten und gemeinsame Ausgaben zu erfassen. Aus den Angaben zu Betrag, Währung, Zahler und Beteiligten berechnet das System Kostenanteile, Salden und Ausgleichsvorschläge. Fremdwährungen werden bei Bedarf automatisch in die Gruppenwährung umgerechnet. Zusätzlich können Übersichten als PDF oder CSV exportiert werden.

CampusSplit führt keine echten Zahlungen aus. Weitere fachliche Abgrenzungen und Nichtziele sind in [`P1 — Ziele und Rahmenbedingungen`](../Spezifikation/P1_Ziele_und_Rahmenbedingungen.md) | und Rahmenbedingungen festgelegt.

Autoritative Quellen in der Spezifikation:

| Bereich | Quelle |
|---|---|
| Ziele, Umfang, Nichtziele, Stakeholder und Risiken | [`P1 — Ziele und Rahmenbedingungen`](../Spezifikation/P1_Ziele_und_Rahmenbedingungen.md) |
| Systemkontext, Datenflüsse und Systemgrenze | [`P2 — Architekturüberblick`](../Spezifikation/P2_Architekturueberblick.md) |
| Geschäftsprozess | [`F1 — Geschäftsprozesse`](../Spezifikation/F1-geschaeftsprozesse.md) |
| Use Cases | [`F2 — Anwendungsfälle`](../Spezifikation/F2-anwendungsfälle.md) |
| Fachliche Funktionen | [`F3 — Anwendungsfunktionen`](../Spezifikation/F3-anwendungsfunktionen.md) |
| Datenmodell | [`D1 — Datenmodell`](../Spezifikation/D1_Datenmodell.md) |
| Datentypen | [`D2 — Datentypenverzeichnis`](../Spezifikation/D2_Datentypenverzeichnis.md) |
| Dialoge | [`B1 — Dialogspezifikation`](../Spezifikation/B1_Dialogspezifikation.md) |
| Exportausgaben | [`B3 — Druck- und Exportausgaben`](../Spezifikation/B3_Druckausgaben.md) |
| Nachbarsysteme und externe Schnittstellen | [`S1 — Nachbarsysteme`](../Spezifikation/S1_Nachbarsysteme.md) |
| Inbetriebnahme | [`S3 — Inbetriebnahme`](../Spezifikation/S3_Inbetriebnahme.md) |
| Nichtfunktionale Anforderungen | [`N1 — Nichtfunktionale Anforderungen`](../Spezifikation/N1_Nichtfunktionale%20Anforderungen.md) |
| Querschnittskonzepte | [`N2 — Querschnittskonzepte`](../Spezifikation/N2_Querschnittskonzepte.md) |

---

## 1.2 Qualitätsziele

Die folgenden Qualitätsziele leiten die wichtigsten Architekturentscheidungen. Sie sind auf die Anforderungen des Hochschulprojekts und den fachlichen Kern von CampusSplit zugeschnitten.

| ID | Qualitätsziel | ISO-25010-Kategorie | Szenario / Messbarkeit | Architekturwirkung |
|---|---|---|---|---|
| QG-01 | **Korrekte Geldberechnung** | Functional Suitability / Reliability | Kostenanteile, Währungsumrechnungen und Salden sind centgenau; die Summe aller Kostenanteile entspricht dem Abrechnungsbetrag und die Summe aller Salden einer Gruppe ergibt 0,00. | Die Geld- und Berechnungslogik wird zentral umgesetzt und automatisiert testbar gemacht. |
| QG-02 | **Nachvollziehbare Salden** | Usability / Functional Suitability | Benutzer erkennen eindeutig, wer Geld schuldet und wer Geld zurückbekommt. | Debitoren, Kreditoren und Ausgleichsvorschläge werden als eigene fachliche Konzepte berücksichtigt. |
| QG-03 | **Sicherer Gruppenzugriff** | Security | Benutzer sehen nur Gruppen, in denen sie Mitglied sind; nur Administratoren können Mitglieder hinzufügen. | Authentifizierung und Autorisierung werden serverseitig geprüft. |
| QG-04 | **Wartbare und verständliche Struktur** | Maintainability | Teammitglieder können einzelne Bereiche ändern und nachvollziehen, ohne die gesamte Anwendung kennen zu müssen. | Fachlogik, Benutzeroberfläche, Persistenz und Export werden klar voneinander getrennt. |
| QG-05 | **Responsive Webnutzung** | Usability / Portability | Zentrale Funktionen sind auf Desktop und mobilen Browsern nutzbar. | Die Benutzeroberfläche wird für unterschiedliche Bildschirmgrößen ausgelegt. |
| QG-06 | **Robuster Umgang mit dem Wechselkursdienst** | Reliability / Maintainability | Ist der Wechselkursdienst nicht erreichbar, wird keine Fremdwährungsausgabe mit einem ungültigen oder erfundenen Kurs umgerechnet. | Der Wechselkursdienst wird klar von der fachlichen Berechnungslogik getrennt angebunden und Fehler werden kontrolliert behandelt. |
| QG-07 | **Einfacher Betrieb im Projektkontext** | Portability / Maintainability | Das System ist lokal startbar und für Entwicklung, Review und Präsentation nachvollziehbar betreibbar. | Konfiguration und Startschritte werden verständlich dokumentiert. |
| QG-08 | **Export ohne sensible Daten** | Security / Compatibility | PDF- und CSV-Exporte enthalten keine Passwörter, Sitzungsdaten oder technischen Interna. | Exportdaten werden fachlich aufbereitet und sensible Daten ausgeschlossen. |

QG-01 und QG-02 sind besonders wichtig, da korrekte und nachvollziehbare Berechnungen den Kern von CampusSplit bilden. Zusätzlich müssen Gruppendaten geschützt und Fehler bei der Währungsumrechnung kontrolliert behandelt werden.

QG-03, QG-06 und QG-08 schützen die Vertrauenswürdigkeit der Anwendung: Gruppendaten dürfen nicht fremden Personen sichtbar werden, externe API-Fehler dürfen keine falschen Abrechnungen erzeugen und Exporte dürfen keine sensiblen Daten enthalten.

---

## 1.3 Stakeholder

| Rolle | Architekturerwartung |
|---|---|
| Gast | Einfacher Einstieg über Registrierung und Anmeldung. |
| Nutzer / Gruppenmitglied | Korrekte Berechnungen, verständliche Bedienung und geschützter Zugriff auf Gruppendaten. |
| Gruppenadministrator | Verlässliche Verwaltung von Mitgliedern und Gruppenrechten. |
| Entwicklungsteam | Verständliche Struktur, klare Verantwortlichkeiten und testbare Fachlogik. |
| Dozent / Prüfer | Nachvollziehbarer Zusammenhang zwischen Spezifikation, Architektur und Implementierung. |

Technische Systeme und externe Dienste werden im Systemkontext in A03 beschrieben.

---

## 1.4 Architekturtreiber

Die folgenden Punkte beeinflussen die Architektur von CampusSplit besonders stark:

| Treiber | Konsequenz für die Architektur |
|---|---|
| Gemeinsame Ausgaben müssen centgenau berechnet werden | Die Geld- und Berechnungslogik wird zentral umgesetzt und automatisiert getestet. |
| Benutzer dürfen nur auf eigene Gruppen zugreifen | Berechtigungen müssen bei allen gruppenbezogenen Zugriffen geprüft werden. |
| Browserbasierte Nutzung | CampusSplit muss über einen aktuellen Webbrowser nutzbar sein. |
| Daten müssen dauerhaft gespeichert werden | Fachliche Daten werden in einer relationalen Datenbank gespeichert. |
| PDF- und CSV-Export ist Teil des Umfangs | Die Exportfunktion wird klar von der übrigen Fachlogik abgegrenzt. |
| Fremdwährungsausgaben müssen in die Gruppenwährung umgerechnet werden | Ein externer Wechselkursdienst wird über eine klar abgegrenzte Schnittstelle angebunden. |
| Hochschulprojekt mit begrenzter Zeit | Die Architektur konzentriert sich auf die für den MVP notwendigen Funktionen. |

---

## 1.5 Abgrenzung dieses Kapitels

Dieses Kapitel beschreibt Ziele, Stakeholder und Architekturtreiber. Es beschreibt noch keine konkrete Klassenstruktur, keine Datenbankmigrationen und keine vollständigen REST-Endpunkte. Diese Details folgen in späteren Architekturkapiteln oder in der Implementierung.

