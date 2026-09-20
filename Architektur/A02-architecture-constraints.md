# 2 Randbedingungen

Randbedingungen legen den verbindlichen Lösungsraum für die Architektur fest. Sie ergeben sich aus der Spezifikation, aus bereits getroffenen Technologieentscheidungen und aus organisatorischen Vorgaben des Hochschulprojekts.

Noch offene Architekturentscheidungen werden nicht als verbindliche Randbedingungen behandelt, sondern separat dokumentiert.

Die nachfolgenden Tabellen dienen als Arbeitsindex für die Architektur. Detaillierte fachliche Begründungen stehen in der Spezifikation; konkrete technische Entscheidungen werden in den Architecture Decision Records dokumentiert.
---

## 2.1 Technische Randbedingungen

| ID | Randbedingung | Beschreibung / Auswirkung |
|---|---|---|
| TECH-01 | Webanwendung | CampusSplit wird als browserbasierte Anwendung umgesetzt. Native Apps für Android oder iOS sind nicht Bestandteil der ersten Version. |
| TECH-03 | Java 21 | Die serverseitige Implementierung erfolgt mit Java 21. |
| TECH-04 | Spring Boot | Die serverseitige Anwendung wird mit Spring Boot umgesetzt. |
| TECH-06 | Relationale Datenbank | Benutzer, Gruppen, Mitgliedschaften, Ausgaben und Kostenanteile werden relational gespeichert. |
| TECH-07 | PostgreSQL | Als Datenbank wird PostgreSQL verwendet. Sie enthält die persistenten Anwendungsdaten von CampusSplit. |
| TECH-09 | Serverseitige Fachlogik | Kostenanteile, Salden, Ausgleichsvorschläge, Autorisierung und Validierung werden zentral serverseitig umgesetzt. |
| TECH-10 | Centgenaue Geldverarbeitung | Geldbeträge dürfen nicht mit ungenauen Gleitkommazahlen verarbeitet werden. Die konkrete technische Darstellung wird in der zugehörigen Architekturentscheidung festgelegt. |
| TECH-11 | Gruppenwährung und Fremdwährungen | Gruppen besitzen eine Gruppenwährung. Fremdwährungsausgaben werden in die Gruppenwährung umgerechnet, bevor Kostenanteile und Salden berechnet werden. |
| TECH-12 | Externer Wechselkursdienst | Für Fremdwährungsausgaben wird die Frankfurter API angebunden. Der Zugriff auf den Dienst wird von der übrigen Fachlogik getrennt. |
| TECH-13 | Keine personenbezogenen Daten an Wechselkursdienst | An den Wechselkursdienst werden nur die für die Umrechnung notwendigen Währungsdaten übertragen. Gruppen-, Mitglieder- oder Benutzerdaten werden nicht übertragen. |
| TECH-14 | PDF- und CSV-Export | Ausgabenübersichten können als PDF- oder CSV-Datei bereitgestellt werden. Die Exporte enthalten keine Passwörter, Sitzungsdaten oder technischen Geheimnisse. |
| TECH-15 | Keine Batch-Prozesse | CampusSplit verwendet in der ersten Version keine Cronjobs, Scheduler oder Hintergrundworker. Fachliche Prozesse werden durch Benutzeraktionen ausgelöst. |
| TECH-16 | Keine Datenmigration | CampusSplit ist ein Greenfield-Projekt. Es gibt kein Vorgängersystem, aus dem Daten übernommen werden müssen. |
| TECH-17 | Desktop und Mobile | Die zentralen Funktionen müssen sowohl im Desktop- als auch im mobilen Browser nutzbar sein. |
| TECH-18 | Lokale Entwicklungsumgebung | Das System soll lokal entwickelbar und für Review und Präsentation startbar sein. Konkrete Startschritte werden in der Implementierungsdokumentation festgelegt. |

---

## 2.2 Organisatorische Randbedingungen

| ID | Randbedingung | Beschreibung / Auswirkung |
|---|---|---|
| ORG-01 | Hochschulprojekt | CampusSplit wird im Rahmen eines Hochschulprojekts entwickelt. Umfang und Architektur müssen für ein Semester realistisch bleiben. |
| ORG-02 | Team aus fünf Studierenden | Die Architektur muss so verständlich sein, dass verschiedene Teammitglieder einzelne Bereiche erklären und bearbeiten können. |
| ORG-03 | Review durch Dozent | Spezifikation, Architektur und Implementierung müssen nachvollziehbar zusammenpassen. |
| ORG-04 | GitHub als Repository | Dokumentation, Architektur und Code werden versioniert. Änderungen sollen nachvollziehbar über Commits erfolgen. |
| ORG-05 | Kleine, prüfbare Umsetzung | Zuerst wird ein MVP umgesetzt: Authentifizierung, Gruppen, Mitglieder, Ausgaben, Salden, Export und Wechselkursanbindung. |
| ORG-06 | Kein produktiver Zahlungsbetrieb | CampusSplit verarbeitet keine echten Zahlungen. Dadurch entfallen Zahlungsdienstleister, Bank-Compliance und Zahlungsstatusverwaltung. |
| ORG-07 | Keine professionelle Betriebsorganisation | Es gibt kein separates Betriebsteam. Betrieb und Start der Anwendung müssen einfach dokumentierbar bleiben. |

---

## 2.3 Fachliche Randbedingungen mit Architekturwirkung

| ID | Randbedingung | Architekturwirkung |
|---|---|---|
| FUNC-01 | Jede Ausgabe gehört zu genau einer Gruppe. | `Expense` wird gruppenbezogen modelliert und jeder Zugriff prüft die Gruppenmitgliedschaft. |
| FUNC-02 | Jeder Benutzer kann Mitglied mehrerer Gruppen sein. | `Membership` löst die n:m-Beziehung zwischen Benutzer und Gruppe auf. |
| FUNC-03 | Jede Ausgabe besitzt einen Zahler. | `Expense.paidByUserId` verweist auf ein Gruppenmitglied. |
| FUNC-04 | Jede Ausgabe besitzt mindestens einen Kostenanteil. | `ExpenseShare` wird als eigene Entität modelliert. |
| FUNC-05 | Kostenanteile müssen exakt dem Abrechnungsbetrag entsprechen. | Die Anwendung validiert und berechnet Kostenanteile centgenau. |
| FUNC-06 | Salden werden aus Ausgaben und Kostenanteilen berechnet. | Salden werden nicht als primäre Entität dauerhaft gespeichert. |
| FUNC-07 | Ausgleichsvorschläge führen keine Zahlungen aus. | Ausgleichsvorschläge sind berechnete Anzeige- und Exportdaten, keine Transaktionen. |
| FUNC-08 | Exporte sind lesbare Ausgaben des Systems. | Die Exportlogik wird von der eigentlichen Fachlogik getrennt. |
| FUNC-09 | Nur Gruppenmitglieder dürfen Gruppendaten sehen. | Die Berechtigung wird bei allen gruppenbezogenen Zugriffen geprüft. |
| FUNC-10 | Nur Administratoren dürfen Mitglieder hinzufügen. | Die Rollenprüfung erfolgt serverseitig. |

---

## 2.4 Sicherheits- und Datenschutzrandbedingungen

| ID | Randbedingung | Architekturwirkung |
|---|---|---|
| SEC-01 | Registrierung und Anmeldung sind öffentlich erreichbar. | Öffentliche Funktionen werden klar von geschützten Bereichen getrennt. |
| SEC-02 | Alle übrigen Funktionen benötigen Anmeldung. | Die Anwendung prüft die Authentifizierung bei allen geschützten Zugriffen. |
| SEC-03 | Zugriff nur auf eigene Gruppen. | Jede Gruppen-, Ausgaben-, Salden- und Exportanfrage benötigt eine Prüfung der Gruppenmitgliedschaft. |
| SEC-04 | Passwörter nicht im Klartext speichern. | Benutzerkonten enthalten nur Passwort-Hashes, niemals Klartextpasswörter. |
| SEC-05 | Keine sensiblen Daten im Export. | Exportdaten werden gezielt zusammengestellt und enthalten keine vertraulichen technischen Daten. |
| SEC-06 | Keine sensiblen Daten in Logs. | Logging darf keine Passwörter, Tokens oder vollständigen personenbezogenen Detaildaten ausgeben. |
| SEC-07 | Externe API ohne personenbezogene Nutzdaten. | An den Wechselkursdienst werden ausschließlich die für die Umrechnung notwendigen Währungsdaten übertragen. |
---

## 2.5 Konventionen

| ID | Konvention | Beschreibung |
|---|---|---|
| CONV-01 | Sprache der Dokumentation | Spezifikation und Architektur werden auf Deutsch verfasst. Technische Bezeichner im Code können englisch sein. |
| CONV-02 | arc42-Struktur | Die Architektur folgt der arc42-Struktur mit nummerierten Dateien `A01`, `A02`, `A03` usw. |
| CONV-03 | Spezifikation und Architektur trennen | Die Spezifikation beschreibt fachlich, was das System leisten soll. Die Architektur beschreibt, wie das System technisch strukturiert wird. |
| CONV-04 | Eine Datei pro Architekturkapitel | Jedes arc42-Kapitel erhält eine eigene Markdown-Datei im Ordner `Architektur/`. |
| CONV-05 | Mermaid-Diagramme | Diagramme werden bevorzugt als Mermaid-Code in Markdown eingebettet. |
| CONV-07 | Kleine Commits | Änderungen sollen über nachvollziehbare, thematisch kleine Commits erfolgen. |
| CONV-08 | Conventional Commits | Commit-Nachrichten sollen dem Muster `type(scope): description` folgen, z. B. `docs(arch): update constraints`. |
| CONV-09 | Keine Code-Details in der Spezifikation | Klassen, Packages, SQL und konkrete Bibliotheken gehören in Architektur oder Implementierung, nicht in die fachliche Spezifikation. |
| CONV-10 | Konsistenz zwischen Spec, Architektur und Code | Entitäten, Begriffe und Use Cases sollen in Spezifikation, Architektur und Implementierung gleich benannt oder eindeutig zugeordnet werden. |

---

## 2.6 Offene Architekturentscheidungen

Einige technische Entscheidungen sind noch nicht endgültig getroffen. Sie werden in den Architecture Decision Records dokumentiert und anschließend in den betroffenen Architekturkapiteln einheitlich übernommen.

| Thema | Aktueller Stand |
|---|---|
| Oberflächentechnologie | Noch offen ist die Entscheidung zwischen Spring Boot mit Thymeleaf und einem getrennten React-/TypeScript-Frontend. |
| Kommunikation mit der Benutzeroberfläche | Hängt von der gewählten Oberflächentechnologie ab. Eine REST-API wird daher noch nicht als verbindlich festgelegt. |
| Darstellung von Geldbeträgen | Eine centgenaue Verarbeitung ist verpflichtend. Die konkrete technische Darstellung wird in einem ADR festgelegt. |

Bereits getroffene Architekturentscheidungen werden in den zugehörigen ADRs dokumentiert.

---

## 2.7 Abgrenzung dieses Kapitels

Dieses Kapitel legt Randbedingungen fest. Es beschreibt noch nicht die interne Bausteinstruktur, Laufzeitszenarien, Deployment-Topologie oder konkrete Architecture Decision Records. Diese folgen in späteren Kapiteln.

