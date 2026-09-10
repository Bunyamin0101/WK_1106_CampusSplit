# 1 Einführung und Ziele

CampusSplit ist eine Webanwendung zur Verwaltung gemeinsamer Ausgaben in Gruppen. Nutzerinnen und Nutzer können Gruppen erstellen, Mitglieder hinzufügen, Ausgaben erfassen, Kostenanteile berechnen lassen, Salden anzeigen und Ausgabenübersichten als PDF oder CSV exportieren.

Dieses Kapitel fasst die Anforderungen und Qualitätsziele zusammen, die die Architektur von CampusSplit bestimmen. Die verbindliche fachliche Spezifikation liegt im Ordner [`../Spezifikation/`](../Spezifikation/) und wird hier referenziert, nicht vollständig wiederholt.

---

## 1.1 Anforderungsüberblick

Der fachliche Kern von CampusSplit besteht aus folgendem Ablauf: Eine angemeldete Person erstellt oder öffnet eine Gruppe, fügt bei Bedarf Mitglieder hinzu und erfasst gemeinsame Ausgaben. Für jede Ausgabe werden Zahler, Betrag, Währung, Datum, Beteiligte und Aufteilungsart festgelegt. CampusSplit berechnet daraus Kostenanteile, Gruppensalden sowie Ausgleichsvorschläge zwischen Debitoren und Kreditoren. Optional kann eine Ausgabenübersicht als PDF oder CSV exportiert werden.

Die Verarbeitung erfolgt synchron durch Benutzeraktionen. CampusSplit führt keine echten Zahlungen aus, besitzt keine Bankintegration, keine Chatfunktion, keine OCR-Belegerkennung und keine KI-Funktion zur Laufzeit.

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

Die folgenden Qualitätsziele leiten die wichtigsten Architekturentscheidungen. Sie sind bewusst auf die Anforderungen des Hochschulprojekts und den fachlichen Kern von CampusSplit zugeschnitten.

| ID | Qualitätsziel | ISO-25010-Kategorie | Szenario / Messbarkeit | Architekturwirkung |
|---|---|---|---|---|
| QG-01 | **Korrekte Geldberechnung** | Functional Suitability / Reliability | Kostenanteile und Salden sind centgenau; die Summe aller Kostenanteile entspricht dem Abrechnungsbetrag; die Summe aller Salden einer Gruppe ergibt 0,00. | Geldlogik wird zentral im Backend gekapselt und automatisiert testbar gemacht. |
| QG-02 | **Nachvollziehbare Salden** | Usability / Functional Suitability | Benutzer erkennen eindeutig, wer Geld schuldet und wer Geld zurückbekommt. | Debitoren, Kreditoren und Ausgleichsvorschläge werden als eigene fachliche Konzepte modelliert. |
| QG-03 | **Sicherer Gruppenzugriff** | Security | Benutzer sehen nur Gruppen, in denen sie Mitglied sind; nur Administratoren können Mitglieder hinzufügen. | Authentifizierung und Autorisierung werden backendseitig geprüft, nicht nur im Frontend ausgeblendet. |
| QG-04 | **Wartbare und verständliche Struktur** | Maintainability | Teammitglieder können ihren Bereich erklären und ändern, ohne die gesamte Anwendung zu verstehen. | Trennung in Frontend, REST-API, Services, Domäne, Persistenz und Exportmodul. |
| QG-05 | **Responsive Webnutzung** | Usability / Portability | Zentrale Funktionen sind auf Desktop und mobilen Browsern nutzbar. | Frontend wird als browserbasierte React-Anwendung umgesetzt. |
| QG-06 | **Robuster Umgang mit externer API** | Reliability / Maintainability | Fällt der Wechselkursdienst aus, wird keine Fremdwährungsausgabe mit erfundenem Kurs gespeichert. | Wechselkursdienst wird über einen eigenen Adapter gekapselt; Fehler werden kontrolliert behandelt. |
| QG-07 | **Einfacher Betrieb im Projektkontext** | Portability / Maintainability | Das System ist lokal startbar und für Review, Entwicklung und Präsentation nachvollziehbar betreibbar. | Klare Konfiguration, dokumentierte Startschritte und getrennte Komponenten. |
| QG-08 | **Export ohne sensible Daten** | Security / Compatibility | PDF- und CSV-Export enthalten keine Passwörter, Tokens oder technischen Interna. | Exportdaten werden fachlich aufbereitet und sicherheitsrelevante Daten ausgeschlossen. |

QG-01 und QG-02 sind die wichtigsten fachlichen Qualitätsziele: Wenn Kostenanteile, Salden und Ausgleichsvorschläge nicht korrekt oder nicht nachvollziehbar sind, erfüllt CampusSplit seinen Kernzweck nicht.

QG-03, QG-06 und QG-08 schützen die Vertrauenswürdigkeit der Anwendung: Gruppendaten dürfen nicht fremden Personen sichtbar werden, externe API-Fehler dürfen keine falschen Abrechnungen erzeugen und Exporte dürfen keine sensiblen Daten enthalten.

---

## 1.3 Stakeholder

| Rolle | Bezug zur Architektur | Architekturerwartung |
|---|---|---|
| Gast | Nutzt Registrierung und Anmeldung. | Einfacher Einstieg, verständliche Fehlermeldungen, keine unnötige Komplexität. |
| Nutzer / Gruppenmitglied | Nutzt Gruppen, Ausgaben, Salden und Export. | Korrekte Berechnung, übersichtliche Bedienung, Zugriff nur auf eigene Gruppen. |
| Gruppenadministrator | Verwaltet Gruppenmitglieder. | Klare Rechteprüfung und verständliche Verwaltungsfunktionen. |
| Entwicklungsteam | Plant, implementiert, testet und präsentiert die Anwendung. | Verständliche Struktur, klare Verantwortlichkeiten, testbare Fachlogik. |
| Dozent / Prüfer | Bewertet Spezifikation, Architektur und Implementierung. | Nachvollziehbarer Zusammenhang zwischen Spezifikation, Architekturentscheidungen und Code. |
| Datenbank | Speichert fachliche Daten dauerhaft. | Konsistente Datenstruktur, kontrollierte Zugriffe, keine unvollständigen Speicherzustände. |
| Frankfurter Wechselkursdienst | Liefert Wechselkurse bei Fremdwährungsausgaben. | Klar abgegrenzter REST-Aufruf ohne personenbezogene Daten. |

---

## 1.4 Architekturtreiber

Die folgenden Punkte beeinflussen die Architektur besonders stark:

| Treiber | Konsequenz für die Architektur |
|---|---|
| Gemeinsame Ausgaben müssen centgenau berechnet werden | Zentrale Geldlogik im Backend; keine Berechnung nur im Frontend. |
| Benutzer dürfen nur eigene Gruppen sehen | Autorisierung muss bei jeder gruppenbezogenen Backend-Anfrage geprüft werden. |
| Frontend und Backend sind getrennt | Kommunikation erfolgt über eine REST-API. |
| Daten müssen dauerhaft gespeichert werden | Relationale Datenbank mit klaren Entitäten und Beziehungen. |
| PDF/CSV-Export ist Teil des Umfangs | Eigenes Exportmodul oder klar abgegrenzte Exportlogik. |
| Fremdwährungsausgaben benötigen Wechselkurse | Externe REST-Schnittstelle wird isoliert angebunden. |
| Hochschulprojekt mit begrenzter Zeit | MVP-orientierte Architektur ohne unnötige Zusatzdienste. |

---

## 1.5 Abgrenzung dieses Kapitels

Dieses Kapitel beschreibt Ziele, Stakeholder und Architekturtreiber. Es beschreibt noch keine konkrete Klassenstruktur, keine Datenbankmigrationen und keine vollständigen REST-Endpunkte. Diese Details folgen in späteren Architekturkapiteln oder in der Implementierung.

