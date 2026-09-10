# 2 Randbedingungen

Randbedingungen legen den verbindlichen Lösungsraum für die Architektur fest. Sie ergeben sich aus der Spezifikation, aus bereits getroffenen Technologieentscheidungen und aus organisatorischen Vorgaben des Hochschulprojekts.

Die nachfolgenden Tabellen dienen als Arbeitsindex für die Architektur. Detaillierte fachliche Begründungen stehen in der Spezifikation; konkrete technische Entscheidungen werden später in den Architecture Decision Records dokumentiert.

---

## 2.1 Technische Randbedingungen

| ID | Randbedingung | Beschreibung / Auswirkung |
|---|---|---|
| TECH-01 | Webanwendung | CampusSplit wird als browserbasierte Anwendung umgesetzt. Native Apps für Android oder iOS sind nicht Bestandteil der ersten Version. |
| TECH-02 | Getrenntes Frontend und Backend | Die Benutzeroberfläche und die Anwendungslogik werden getrennt. Das Frontend kommuniziert über eine REST-API mit dem Backend. |
| TECH-03 | Java 21 im Backend | Die serverseitige Implementierung erfolgt mit Java. Für die Architektur wird Java 21 als Zielplattform angenommen. |
| TECH-04 | Spring Boot Backend | Routing, Validierung, Services, REST-Controller und Datenzugriff werden im Backend mit Spring Boot umgesetzt. |
| TECH-05 | React mit TypeScript im Frontend | Die Browseroberfläche wird als React-Anwendung mit TypeScript umgesetzt. |
| TECH-06 | Relationale Datenbank | Benutzer, Gruppen, Mitgliedschaften, Ausgaben und Kostenanteile werden relational gespeichert. |
| TECH-07 | PostgreSQL | Als Datenbank wird PostgreSQL vorgesehen. Die Datenbank enthält die persistenten Anwendungsdaten. |
| TECH-08 | REST-API | Die Kommunikation zwischen Frontend und Backend erfolgt über HTTP/JSON. REST-Endpunkte bilden Use Cases wie Gruppenverwaltung, Ausgabenerfassung, Saldenanzeige und Export ab. |
| TECH-09 | Serverbasierte Fachlogik | Kostenanteile, Salden, Ausgleichsvorschläge, Autorisierung und Validierung werden zentral im Backend umgesetzt. Das Frontend dient nicht als alleinige Quelle fachlicher Wahrheit. |
| TECH-10 | Centgenaue Geldverarbeitung | Geldbeträge dürfen nicht mit ungenauen Gleitkommazahlen verarbeitet werden. Die Implementierung muss eine exakte Darstellung verwenden, z. B. centbasierte Ganzzahlen oder `BigDecimal`. |
| TECH-11 | Gruppenwährung und Fremdwährungen | Gruppen besitzen eine Gruppenwährung. Fremdwährungsausgaben werden in die Gruppenwährung umgerechnet, bevor Kostenanteile und Salden berechnet werden. |
| TECH-12 | Externer Wechselkursdienst | Für Fremdwährungsausgaben wird die Frankfurter API über HTTPS/REST/JSON angebunden. Der Dienst wird über einen isolierten Backend-Adapter gekapselt. |
| TECH-13 | Keine personenbezogenen Daten an Wechselkursdienst | An den Wechselkursdienst werden nur Ausgangswährung, Zielwährung und Datum übertragen. Gruppenname, Beschreibung, Mitglieder oder Benutzerinformationen werden nicht übertragen. |
| TECH-14 | PDF- und CSV-Export | Exporte werden aus Backend-Daten erzeugt und dem Benutzer als Datei bereitgestellt. Exportdateien enthalten keine Passwörter, Tokens oder technischen Geheimnisse. |
| TECH-15 | Keine Batch-Prozesse | CampusSplit verwendet in der ersten Version keine Cronjobs, Scheduler oder Hintergrundworker. Alle fachlichen Prozesse werden durch Benutzeraktionen ausgelöst. |
| TECH-16 | Keine Datenmigration | CampusSplit ist ein Greenfield-Projekt. Es gibt kein Vorgängersystem, aus dem Daten übernommen werden müssen. |
| TECH-17 | Desktop und Mobile | Zentrale Funktionen müssen im Desktop- und mobilen Browser nutzbar sein. |
| TECH-18 | Lokale Entwicklungsumgebung | Das System soll lokal entwickelbar und für Review/Präsentation startbar sein. Konkrete Startbefehle werden in der Implementierungsdokumentation festgelegt. |

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
| FUNC-05 | Kostenanteile müssen exakt dem Abrechnungsbetrag entsprechen. | Backend validiert und berechnet Kostenanteile centgenau. |
| FUNC-06 | Salden werden aus Ausgaben und Kostenanteilen berechnet. | Salden werden nicht als primäre Entität dauerhaft gespeichert. |
| FUNC-07 | Ausgleichsvorschläge führen keine Zahlungen aus. | Settlement-Vorschläge sind berechnete Anzeige-/Exportdaten, keine Transaktionen. |
| FUNC-08 | Exporte sind lesbare Ausgaben des Systems. | Exportlogik wird von der eigentlichen Fachlogik getrennt. |
| FUNC-09 | Nur Gruppenmitglieder dürfen Gruppendaten sehen. | Autorisierung ist bei allen gruppenbezogenen API-Endpunkten Pflicht. |
| FUNC-10 | Nur Administratoren dürfen Mitglieder hinzufügen. | Rollenprüfung erfolgt im Backend. |

---

## 2.4 Sicherheits- und Datenschutzrandbedingungen

| ID | Randbedingung | Architekturwirkung |
|---|---|---|
| SEC-01 | Registrierung und Anmeldung sind öffentlich erreichbar. | Auth-Endpunkte sind von geschützten Endpunkten zu trennen. |
| SEC-02 | Alle übrigen Funktionen benötigen Anmeldung. | Backend prüft Authentifizierung bei geschützten API-Endpunkten. |
| SEC-03 | Zugriff nur auf eigene Gruppen. | Jede Gruppen-, Ausgaben-, Salden- und Exportanfrage benötigt Membership-Prüfung. |
| SEC-04 | Passwörter nicht im Klartext speichern. | Benutzerentität enthält nur Passwort-Hash, nie Klartextpasswort. |
| SEC-05 | Keine sensiblen Daten im Export. | Exportdaten werden explizit zusammengestellt, nicht direkt aus Entitäten serialisiert. |
| SEC-06 | Keine sensiblen Daten in Logs. | Logging darf keine Passwörter, Tokens oder vollständigen personenbezogenen Detaildaten ausgeben. |
| SEC-07 | Externe API ohne personenbezogene Nutzdaten. | Der Wechselkursadapter sendet ausschließlich technische Währungsparameter. |

---

## 2.5 Konventionen

| ID | Konvention | Beschreibung |
|---|---|---|
| CONV-01 | Sprache der Dokumentation | Spezifikation und Architektur werden auf Deutsch verfasst. Technische Bezeichner im Code können englisch sein. |
| CONV-02 | arc42-Struktur | Die Architektur folgt der arc42-Struktur mit nummerierten Dateien `A01`, `A02`, `A03` usw. |
| CONV-03 | Spezifikation und Architektur trennen | Die Spezifikation beschreibt fachlich, was das System leisten soll. Die Architektur beschreibt, wie das System technisch strukturiert wird. |
| CONV-04 | Eine Datei pro Architekturkapitel | Jedes arc42-Kapitel erhält eine eigene Markdown-Datei im Ordner `Architektur/`. |
| CONV-05 | Mermaid-Diagramme | Diagramme werden bevorzugt als versionierbarer Mermaid-Code in Markdown eingebettet, nicht als Screenshot. |
| CONV-06 | REST-Namenskonvention | API-Endpunkte beginnen mit `/api/` und verwenden ressourcenorientierte Pfade wie `/api/groups/{groupId}/expenses`. |
| CONV-07 | Kleine Commits | Änderungen sollen über nachvollziehbare, thematisch kleine Commits erfolgen. |
| CONV-08 | Conventional Commits | Commit-Nachrichten sollen dem Muster `type(scope): description` folgen, z. B. `docs(arch): add context view`. |
| CONV-09 | Keine Code-Details in der Spezifikation | Klassen, Packages, SQL und konkrete Bibliotheken gehören in Architektur oder Implementierung, nicht in die fachliche Spezifikation. |
| CONV-10 | Konsistenz zwischen Spec, Architektur und Code | Entitäten, Begriffe und Use Cases sollen in Spezifikation, Architektur und Implementierung gleich benannt oder eindeutig zugeordnet werden. |

---

## 2.6 Auswirkungen auf spätere Architekturentscheidungen

Die Randbedingungen führen zu mehreren verbindlichen Architekturentscheidungen, die später als ADRs dokumentiert werden sollten:

| ADR-Vorschlag | Entscheidung |
|---|---|
| ADR-001 | Spring Boot als Backend-Plattform |
| ADR-002 | React + TypeScript als Frontend-Technologie |
| ADR-003 | PostgreSQL als relationale Datenbank |
| ADR-004 | REST-API zwischen Frontend und Backend |
| ADR-005 | Backendseitige Salden- und Geldbetragsberechnung |
| ADR-006 | Externer Wechselkursdienst über isolierten Adapter |
| ADR-007 | PDF/CSV-Export als separates Backend-Modul |

---

## 2.7 Abgrenzung dieses Kapitels

Dieses Kapitel legt Randbedingungen fest. Es beschreibt noch nicht die interne Bausteinstruktur, Laufzeitszenarien, Deployment-Topologie oder konkrete Architecture Decision Records. Diese folgen in späteren Kapiteln.

