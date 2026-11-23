# P1 — Ziele und Rahmenbedingungen

Grundlagenbaustein der CampusSplit-Spezifikation nach Siedersleben. Dieser Baustein beschreibt, warum das System entwickelt wird, für wen es gedacht ist und welche Rahmenbedingungen den Lösungsraum eingrenzen.

---

## P1.1 Ausgangssituation

Wohngemeinschaften, Reisegruppen und studentische Projektgruppen geben regelmäßig gemeinsam Geld aus: Ein Mitglied kauft für alle ein, bucht die Unterkunft oder bezahlt das Restaurant und streckt den Betrag vor. Heute halten diese Gruppen ihre Ausgaben meist in Chatverläufen, Notizen oder selbst gebauten Tabellen fest. Daraus entstehen wiederkehrende Probleme:

- **Aufwand:** Am Monats- oder Reiseende muss jemand alle Ausgaben einsammeln, aufteilen und gegeneinander verrechnen.
- **Rechenfehler und Rundungsstreit:** Beträge, die sich nicht glatt teilen lassen (z. B. 10,00 EUR auf drei Personen), führen zu Cent-Differenzen, die niemand nachvollziehen kann.
- **Unklarer Stand:** Ob eine Schuld schon zurückgezahlt wurde, ist im Chat schwer zu erkennen. Nachträgliche Änderungen fallen niemandem auf.
- **Fremdwährung:** Auf Reisen fallen Ausgaben in einer anderen Währung an. Jemand muss Kurse heraussuchen und umrechnen.
- **Verstreute Belege:** Kassenzettel und Rechnungen liegen bei unterschiedlichen Personen.

CampusSplit soll diese Gruppen dabei unterstützen, gemeinsame Ausgaben festzuhalten, fair aufzuteilen und zu klären, wer wem wie viel zurückzahlt. Das Geld selbst fließt weiterhin außerhalb der Anwendung.

---

## P1.2 Typische Einsatzsituationen

Die Ziele in P1.4 beziehen sich auf drei wiederkehrende Situationen. [F1 – Geschäftsprozesse](F1-geschaeftsprozesse.md) modelliert sie als Abläufe, in denen CampusSplit jeweils einzelne Schritte unterstützt.

| Situation | Beschreibung | Besonderheit |
| --- | --- | --- |
| WG-Monat | Mitbewohner erfassen laufend Einkäufe und Haushaltskosten und gleichen am Monatsende aus. | Viele kleine Ausgaben, gleiche Gruppe über lange Zeit |
| Gruppenreise | Freunde reisen gemeinsam, teilen Unterkunft, Fahrten und Freizeit und rechnen nach der Reise ab. | Ausgaben in Fremdwährung, ungleiche Anteile, Gruppe wird danach archiviert |
| Projektgruppe | Studierende teilen Kosten für Material, Druck oder Veranstaltungen. | Wenige Ausgaben, Nachweis durch Belege |

---

## P1.3 Stakeholder und Benutzergruppen

| Stakeholder | Interesse an CampusSplit |
| --- | --- |
| Benutzer (Mitglieder von WGs, Reise- und Projektgruppen) | Wenig Aufwand, verlässliche Beträge, klarer Stand der offenen Beträge |
| Gruppenadministratoren | Gruppe zusammenstellen, abgeschlossene Gruppen archivieren oder löschen |
| Modulverantwortliche (Auftraggeber und Prüfende) | Nachvollziehbare Spezifikation, lauffähige Anwendung, Einhaltung der Modulvorgaben |
| Projektteam (Projektleitung, Software Architect, Spec/Requirements Lead, Implementation Lead, QA/Test Lead; siehe [TEAMINFO](../TEAMINFO.md)) | Umfang, der im Semester umsetzbar und testbar ist |
| Betreiber der Projektinstanz (Projektteam) | Stabiler Betrieb ohne laufende Kosten, keine Haftung für Zahlungen |

---

## P1.4 Ziele und Erfolgskriterien

Die Ziele beschreiben die **Wirkung** für die Gruppen, nicht einzelne Funktionen. Jedes Ziel wird durch mindestens ein messbares Erfolgskriterium (EK) überprüft. Die Leistungsbereiche (LB) in P1.5 zeigen, mit welchen Funktionen das Ziel erreicht wird.

### Ziele

| ID | Ziel | Bezug zur Ausgangssituation | Erfolgskriterien | Erreicht durch |
| --- | --- | --- | --- | --- |
| Z-01 | Gruppen rechnen ihre gemeinsamen Ausgaben mit deutlich weniger Aufwand ab als mit Chat und Tabelle. | Aufwand | EK-01, EK-02 | LB-03, LB-06 |
| Z-02 | Alle Mitglieder einer Gruppe sehen dieselben, centgenau richtigen Beträge; Rundungen sind geregelt und führen nicht zu Differenzen. | Rechenfehler und Rundungsstreit | EK-03, EK-04 | LB-03, LB-06 |
| Z-03 | Jedes Mitglied kann nachvollziehen, wie sein Saldo zustande kommt und wer wann welche Ausgabe geändert hat. | Unklarer Stand | EK-05, EK-06 | LB-05, LB-08, LB-10 |
| Z-04 | Gruppen können Ausgaben in Euro und US-Dollar gemeinsam abrechnen, ohne selbst Kurse zu recherchieren. | Fremdwährung | EK-07, EK-08 | LB-04 |
| Z-05 | Die offenen Beträge entsprechen jederzeit dem tatsächlichen Stand: Geleistete Rückzahlungen sind erfasst, Fehlerfassungen lassen sich korrigieren, ohne Spuren zu verwischen. | Unklarer Stand | EK-09, EK-10 | LB-07 |
| Z-06 | Gruppen können ihre Abrechnung unabhängig von CampusSplit weitergeben und aufbewahren. | Aufwand, verstreute Belege | EK-11 | LB-10 |
| Z-07 | Ausgaben, Belege und Salden einer Gruppe sind nur den Mitgliedern dieser Gruppe zugänglich. | Belege und Beträge sind persönliche Angaben | EK-12 | LB-01, LB-02 |
| Z-08 | Neue Benutzer kommen ohne Anleitung zur ersten erfassten Ausgabe. | Akzeptanz in der Gruppe | EK-13 | LB-01, LB-02, LB-03 |

### Erfolgskriterien

Der **Abnahmekatalog** ist die Testfallsammlung der QA (siehe [N1](N1_Nichtfunktionale%20Anforderungen.md) und [S3](S3_Inbetriebnahme.md)). Er umfasst mindestens drei Testgruppen mit zusammen mindestens 30 Ausgaben, darunter nicht glatt teilbare Beträge (z. B. 10,00 EUR auf drei Personen), individuelle Aufteilungen, Ausgaben in Fremdwährung in beiden Richtungen, Rückzahlungen und Stornierungen. Die **Referenzrechnung** ist eine von CampusSplit unabhängige Nachrechnung in einer Tabellenkalkulation nach den Regeln aus [F3](F3-anwendungsfunktionen.md).

| ID | Ziel | Messgröße | Zielwert | Messverfahren |
| --- | --- | --- | --- | --- |
| EK-01 | Z-01 | Zeit vom Erfassen der ersten Ausgabe bis zur Anzeige der Ausgleichsvorschläge im Szenario „WG-Monat“ (4 Mitglieder, 20 vorgegebene Belege, davon 3 mit individueller Aufteilung; Gruppe und Mitglieder sind angelegt) | Median ≤ 20 Minuten | Moderierter Test mit 5 Personen, die CampusSplit nicht kennen; Zeitmessung mit Stoppuhr |
| EK-02 | Z-01 | Anzahl der Ausgleichsvorschläge einer Gruppe im Verhältnis zur Anzahl der Mitglieder mit Saldo ungleich 0,00 | höchstens (Anzahl dieser Mitglieder − 1) in 100 % der Fälle | Prüfung in allen Testgruppen des Abnahmekatalogs nach jedem Schritt |
| EK-03 | Z-02 | Abweichung von Abrechnungsbeträgen, Kostenanteilen, Salden und Ausgleichsvorschlägen gegenüber der Referenzrechnung | 0,00 in 100 % der Fälle | Abnahmekatalog, Vergleich mit Referenzrechnung |
| EK-04 | Z-02 | Summe aller Salden einer Gruppe | genau 0,00 in 100 % der Fälle | Prüfung in allen Testgruppen nach jedem Schritt des Abnahmekatalogs |
| EK-05 | Z-03 | Abweichung zwischen dem ausgewiesenen Saldo eines Mitglieds und der Nachrechnung allein aus der heruntergeladenen gesamten Abrechnung (bezahlte Ausgaben, Kostenanteile, Rückzahlungen) | 0,00 für jedes Mitglied | Je Testgruppe des Abnahmekatalogs, durch eine Person, die die Gruppe nicht angelegt hat |
| EK-06 | Z-03 | Anteil der Anlagen, Änderungen und Löschungen von Ausgaben sowie der hinzugefügten und entfernten Belege, die in den Aktivitäten mit Person, Zeitpunkt und geänderten Werten erscheinen | 100 % | Testlauf mit 20 protokollierten Änderungen, Abgleich mit dem Testprotokoll |
| EK-07 | Z-04 | Fremdwährungsausgaben, bei denen Kurs und Kursdatum gespeichert und in Gruppenansicht und Abrechnung sichtbar sind, das Kursdatum nicht nach dem Ausgabedatum liegt und der Abrechnungsbetrag der Referenzrechnung entspricht | 100 % der Fremdwährungsausgaben | Abnahmekatalog |
| EK-08 | Z-04 | Änderung des Abrechnungsbetrags, wenn eine Fremdwährungsausgabe erneut gespeichert wird, ohne Betrag, Währung oder Ausgabedatum zu ändern | 0 Änderungen in 10 Versuchen, verteilt auf mindestens zwei Kalendertage | Gezielter Test |
| EK-09 | Z-05 | Testfälle, in denen nach Erfassung einer Rückzahlung über den vollen Vorschlagsbetrag der Vorschlag entfällt und beide Salden um den Betrag verändert sind, und in denen nach Stornierung der vorherige Stand wiederhergestellt und die Rückzahlung mit Grund weiterhin sichtbar ist | 100 % | Abnahmekatalog |
| EK-10 | Z-05 | Doppelt erfasste Rückzahlungen beim wiederholten Absenden derselben Erfassung (Doppelklick, Zurück und erneut Absenden) | 0 in 10 Versuchen | Gezielter Test |
| EK-11 | Z-06 | Abweichungen der heruntergeladenen gesamten Abrechnung (PDF und Tabellendateien) von der Gruppenansicht bei Salden, Ausgleichsvorschlägen, Ausgaben und Kostenanteilen | 0 Abweichungen | Je Testgruppe des Abnahmekatalogs, beide Formate |
| EK-12 | Z-07 | Erfolgreiche Zugriffe eines Gasts und eines angemeldeten Nichtmitglieds auf Gruppenansicht, Ausgaben, Belege, Aktivitäten und Abrechnung einer fremden Gruppe | 0 in 100 % der Versuche | Zugriffstest über alle genannten Funktionen, auch über direkt aufgerufene Adressen |
| EK-13 | Z-08 | Testpersonen, die sich ohne Hilfe registrieren, eine Gruppe anlegen, ein vorbereitetes zweites Konto als Mitglied hinzufügen und eine Ausgabe erfassen | mindestens 4 von 5 Personen in je ≤ 10 Minuten | Moderierter Test ohne Hilfestellung, Personen ohne Vorkenntnis der Anwendung |

---

## P1.5 Leistungsumfang

Die Tabelle legt den Umfang der Version 1.0 fest. Jeder Anwendungsfall in [F2](F2-anwendungsf%C3%A4lle.md) nennt den Leistungsbereich (LB), zu dem er gehört.

| ID | Leistungsbereich | Umfang | Dient |
| --- | --- | --- | --- |
| LB-01 | Konto und Anmeldung | Registrierung und Anmeldung mit E-Mail-Adresse und Passwort; Abmeldung. Optional: Registrierung und Anmeldung mit einem Google-Konto sowie nachträgliche Verknüpfung eines bestehenden Benutzerkontos mit einem Google-Konto nach Bestätigung des Passworts. Konten mit derselben E-Mail-Adresse werden nie automatisch zusammengeführt. Im Profil den Anzeigenamen ändern. | Z-07, Z-08 |
| LB-02 | Gruppenverwaltung | Gruppe mit Name, optionaler Beschreibung und Gruppenwährung (EUR oder USD, später nicht änderbar) anlegen; die anlegende Person wird Gruppenadministrator. Registrierte Benutzer per E-Mail-Adresse als Mitglied hinzufügen. Gruppe archivieren und wiederherstellen. Gruppe nach Eingabe des Gruppennamens endgültig löschen. | Z-07, Z-08 |
| LB-03 | Ausgaben | Ausgabe mit Beschreibung, Originalbetrag, Originalwährung, Ausgabedatum, Zahler, optionaler Kategorie und Beteiligten erfassen, ändern und löschen. Aufteilung gleichmäßig oder mit individuellen Beträgen. | Z-01, Z-02, Z-08 |
| LB-04 | Fremdwährung | Ausgabe in der jeweils anderen Währung (EUR bzw. USD) erfassen; Umrechnung in die Gruppenwährung mit dem Kurs zum Ausgabedatum; der Kurs bleibt mit der Ausgabe gespeichert. | Z-04 |
| LB-05 | Belege | Bis zu fünf Belege (Foto als JPEG oder PNG, oder PDF; je höchstens 10 MB) je Ausgabe hinzufügen, ansehen und entfernen. | Z-03 |
| LB-06 | Salden und Ausgleich | Saldo jedes Mitglieds in Gruppenwährung; Ausgleichsvorschläge „Wer zahlt wem?“. | Z-01, Z-02 |
| LB-07 | Rückzahlungen | Außerhalb von CampusSplit geleistete Zahlung zu einem Ausgleichsvorschlag ganz oder teilweise erfassen, optional einer Ausgabe zugeordnet; erfasste Rückzahlung mit Grund stornieren. | Z-05 |
| LB-08 | Aktivitäten | Protokoll der Ereignisse zu Ausgaben, Belegen und Zahlungen einer Gruppe, neueste zuerst, nach Art filterbar. | Z-03 |
| LB-09 | Persönliche Übersicht | Für den angemeldeten Benutzer je Währung: Summe der Forderungen, Summe der Schulden und eigene Monatsausgaben über alle seine Gruppen; ergänzend eine Euroübersicht zum aktuellen Kurs, nur zur Information. | Z-01 |
| LB-10 | Abrechnung herunterladen | Gruppenabrechnung als PDF oder als Tabellendateien (ZIP-Archiv); Abrechnungsumfang wählbar: gesamte Abrechnung, offene Beträge, nur Ausgaben. | Z-03, Z-06 |

---

## P1.6 Nichtziele

Die folgenden Punkte gehören ausdrücklich **nicht** zum Umfang der Version 1.0.

| ID | Nichtziel | Begründung |
| --- | --- | --- |
| NZ-01 | CampusSplit überweist, empfängt oder verwahrt kein Geld und ist an keine Bank oder keinen Zahlungsdienst angebunden. | Wer Gelder für andere entgegennimmt oder weiterleitet, erbringt einen erlaubnispflichtigen Zahlungsdienst (RB-04). Die Gruppen zahlen bereits auf eigenen Wegen. |
| NZ-02 | Kein Ausgleich über Gruppen- oder Währungsgrenzen hinweg: Forderungen aus einer Gruppe werden nicht mit Schulden aus einer anderen Gruppe oder in einer anderen Währung verrechnet. Die Euroübersicht (LB-09) ist reine Information. | Gruppen haben unterschiedliche Mitglieder; eine Verrechnung bräuchte die Zustimmung aller Betroffenen. Kurse ändern sich täglich, eine Verrechnung wäre nicht stabil. |
| NZ-03 | Keine weiteren Währungen als Euro (EUR) und US-Dollar (USD). | Begrenzung des Umfangs und des Testaufwands für Kursfälle. |
| NZ-04 | Keine Übernahme von Ausgaben aus Chatverläufen, Tabellen oder anderen Anwendungen. | Die Altdaten der Gruppen liegen in uneinheitlicher Form vor. Daher entfällt der Baustein S2 (siehe [E1.2](E1_Leseanleitung.md#e12-aufbau-der-spezifikation)). |
| NZ-05 | Keine Benachrichtigungen und keine Einladungen per E-Mail; Mitglieder müssen sich selbst registrieren, bevor sie einer Gruppe hinzugefügt werden. | Kein E-Mail-Versand und damit kein weiteres Nachbarsystem; personenbezogene Daten verlassen CampusSplit nicht. |
| NZ-06 | Mitglieder werden nicht aus einer Gruppe entfernt, und die Rolle Gruppenadministrator wird nicht übertragen. | Salden hängen an Mitgliedern; eine Regel für das Ausscheiden mit offenem Saldo ist nicht festgelegt. |
| NZ-07 | Keine Selbstverwaltung des Benutzerkontos über den Anzeigenamen hinaus: kein Zurücksetzen des Passworts, keine Änderung der E-Mail-Adresse, kein Löschen des Kontos durch den Benutzer. | Diese Funktionen setzen einen E-Mail-Versand voraus (NZ-05). Zu den Folgen siehe R-02 und RB-03. |
| NZ-08 | Keine eigenen Kategorien; es gilt die feste Liste aus [E2](E2_Glossar.md) (Begriff „Kategorie“). | Einheitliche Darstellung, kleiner Umfang. |
| NZ-09 | Keine Budget- oder Haushaltsbuchfunktionen, keine Auswertungen nach Kategorien, keine wiederkehrenden Ausgaben. | CampusSplit klärt offene Beträge; Haushaltsplanung ist ein anderer Zweck. |

---

## P1.7 Rahmenbedingungen 

Rahmenbedingungen sind Vorgaben, die das Projekt nicht selbst entscheidet. Technische Vorgaben stehen in der Architekturdokumentation, [Kapitel 2](../Architektur/A02-architecture-constraints.md).

| ID | Art | Rahmenbedingung | Auswirkung auf die Spezifikation |
| --- | --- | --- | --- |
| RB-01 | organisatorisch | Hochschulprojekt im Modul Projekt I (Softwaretechnik), WK_1106, mit fünf Personen in festen Rollen und festem Abgabetermin im Semester. | Umfang auf Version 1.0 begrenzt (P1.5, P1.6). |
| RB-02 | organisatorisch | Für Betrieb und externe Dienste steht kein Projektbudget zur Verfügung. | Nachbarsysteme müssen ohne Vertrag und Nutzungsentgelt nutzbar sein ([P2.4](P2_Architekturueberblick.md#p24-nachbarsysteme)). |
| RB-03 | rechtlich | Die Datenschutz-Grundverordnung gilt: Namen, E-Mail-Adressen, Ausgaben und Belege sind personenbezogene Daten. Für einen dauerhaften öffentlichen Betrieb sind Betreiberangaben und Datenschutzhinweise erforderlich; die Projektinstanz dient bis dahin nur der Vorführung im Modul. | Datensparsamkeit: An Nachbarsysteme gehen keine Gruppen- oder Personendaten (P2.4). Zugriff nur für Mitglieder (Z-07). |
| RB-04 | rechtlich | Wer Geldbeträge für andere entgegennimmt oder weiterleitet, braucht eine Erlaubnis als Zahlungsdienst. | CampusSplit dokumentiert Zahlungen nur (NZ-01). |
| RB-05 | fachlich | Wechselkurse stammen aus einer öffentlich zugänglichen Quelle mit Tagesreferenzkursen. Nicht für jeden Kalendertag wird ein Kurs veröffentlicht; dann gilt der letzte veröffentlichte Kurs vor dem gewünschten Tag. Referenzkurse können von dem Kurs abweichen, den eine Bank oder ein Kartenanbieter tatsächlich berechnet hat. | Das Kursdatum kann vor dem Ausgabedatum liegen (EK-07). Siehe Annahme AN-04. |
| RB-06 | organisatorisch | Die Google-Anmeldung ist bei Google im Testmodus registriert; in diesem Modus können sich nur von den Betreibern freigeschaltete Testkonten mit Google anmelden. | Die Anmeldung mit E-Mail und Passwort ist der Regelweg; die Google-Anmeldung ist optional (LB-01). |

---

## P1.8 Annahmen

| ID | Annahme | Folge, falls sie nicht zutrifft |
| --- | --- | --- |
| AN-01 | Die Mitglieder einer Gruppe kennen sich und vertrauen sich grundsätzlich. Deshalb darf jedes Mitglied jede Ausgabe der Gruppe erfassen, ändern und löschen; die Aktivitäten machen jede Änderung sichtbar (Z-03). | Gruppen bräuchten Freigaben oder Rechte je Ausgabe; das liegt außerhalb von Version 1.0. |
| AN-02 | Alle Personen, mit denen geteilt wird, besitzen ein CampusSplit-Konto. | Personen ohne Konto können keine Kostenanteile tragen. |
| AN-03 | Zahlungen zwischen Mitgliedern erfolgen außerhalb von CampusSplit (bar, Überweisung, Bezahl-App) und werden von einer beteiligten Person oder dem Gruppenadministrator erfasst. | Nicht erfasste Zahlungen lassen Salden zu hoch erscheinen. |
| AN-04 | Gruppen akzeptieren den Referenzkurs als Grundlage der Umrechnung. Wer den tatsächlich belasteten Betrag verwenden will, erfasst die Ausgabe direkt in der Gruppenwährung. | Abweichungen zum Kontoauszug würden als Fehler wahrgenommen. |
| AN-05 | Gruppen bleiben in der Größe überschaubar (typisch 2 bis 10 Mitglieder); verbindliche Obergrenzen legt das Mengengerüst in [N1](N1_Nichtfunktionale%20Anforderungen.md) fest. | Darstellung und Antwortzeiten wären neu zu bewerten. |

---

## P1.9 Risiken

Wahrscheinlichkeit (W) und Auswirkung (A) werden mit niedrig, mittel oder hoch bewertet.

| ID | Risiko (Ursache → Wirkung) | W | A | Umgang |
| --- | --- | --- | --- | --- |
| R-01 | Der Wechselkursdienst ist nicht erreichbar oder wird eingestellt → Fremdwährungsausgaben können nicht gespeichert werden, die Euroübersicht fehlt. | mittel | mittel | Vermindern: Ausgaben in Gruppenwährung, Salden und bereits gespeicherte Kurse bleiben unberührt ([P2.4](P2_Architekturueberblick.md#p24-nachbarsysteme)). |
| R-02 | Die Google-Anmeldung entfällt (Testmodus endet, Zugangsdaten ungültig) → Benutzer, die sich nur mit Google registriert haben, besitzen kein Passwort und können sich nicht mehr anmelden; ein Zurücksetzen gibt es nicht (NZ-07). | mittel | hoch | Akzeptiert für Version 1.0; die Registrierung mit E-Mail und Passwort bleibt der Regelweg (RB-06). |
| R-03 | Die Projektinstanz endet mit dem Hostingtarif, oder Daten gehen verloren; automatische Sicherungen sind nicht eingerichtet → Gruppen verlieren ihre Abrechnungsdaten. | mittel | hoch | Vermindern: Jede Gruppe kann ihre gesamte Abrechnung jederzeit herunterladen (Z-06). |
| R-04 | Ein Mitglied ändert oder löscht versehentlich oder absichtlich fremde Ausgaben (AN-01) → Streit über Beträge. | mittel | mittel | Vermindern: Aktivitäten mit alten und neuen Werten (Z-03). |
| R-05 | Belege enthalten weitere persönliche Angaben (Adressen, Teile von Kartennummern) → Datenschutzverletzung bei unberechtigtem Zugriff. | niedrig | hoch | Vermeiden: Zugriff nur für Mitglieder (Z-07); Belege werden mit der Ausgabe bzw. Gruppe gelöscht. |
| R-06 | Benutzer verstehen die Euroübersicht als verbindliche Verrechnung → sie zahlen Beträge über Gruppen hinweg falsch aus. | niedrig | mittel | Vermeiden: Die Euroübersicht ist als Information gekennzeichnet; Ausgleichsvorschläge gibt es nur je Gruppe (NZ-02). |


---


## Eingesetzte KI-Werkzeuge

ChatGPT (OpenAI) wurde unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet. Die fachlichen Inhalte wurden anschließend mit dem Projektkontext und den übrigen Spezifikationsbausteinen abgeglichen.

