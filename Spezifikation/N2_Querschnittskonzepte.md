# N2 - Querschnittskonzepte

Manche Themen lassen sich nicht sauber einem einzelnen Baustein oder Use Case zuordnen, weil sie an mehreren Stellen im System gleichzeitig auftauchen. Solche Themen fassen wir hier als Querschnittskonzepte zusammen, damit wir sie nicht bei jedem Use Case neu erfinden müssen.

Bei CampusSplit betrifft das vor allem sieben Bereiche: Authentifizierung, Autorisierung, Validierung, Geldbetragsverarbeitung, Fehlerbehandlung, Logging und Exportsicherheit.

Der Sinn von N2 ist, diese Konzepte einmal festzulegen, statt sie in jedem Kapitel neu und möglicherweise widersprüchlich zu beschreiben.

## N2.1 Konzeptkatalog

| ID                                           | Konzept                         | Kurzbeschreibung                                                       |
| -------------------------------------------- | -------------------------------- | ---------------------------------------------------------------------- |
| [N2.2](#n22-authentifizierung-und-sitzung)   | Authentifizierung und Sitzung   | Einheitlicher Zugriffsschutz für angemeldete Benutzer:innen            |
| [N2.3](#n23-autorisierung-und-gruppenrechte) | Autorisierung und Gruppenrechte | Zugriff auf Gruppen und Aktionen abhängig von Mitgliedschaft und Rolle |
| [N2.4](#n24-validierung)                     | Validierung                     | Einheitliche Prüfung von Eingaben vor Speicherung                      |
| [N2.5](#n25-geldbetragsverarbeitung)         | Geldbetragsverarbeitung         | Centgenaue und konsistente Verarbeitung von Geldbeträgen, inklusive Fremdwährungsumrechnung |
| [N2.6](#n26-fehlerbehandlung)                | Fehlerbehandlung                | Einheitliches Verhalten bei fachlichen und technischen Fehlern         |
| [N2.7](#n27-logging)                         | Logging                         | Nachvollziehbare Protokollierung ohne sensible Daten                   |
| [N2.8](#n28-exportsicherheit)                | Exportsicherheit                | Sichere und konsistente Erzeugung von PDF- und CSV-Exporten            |

## N2.2 Authentifizierung und Sitzung

### Anliegen

In CampusSplit stecken sensible Informationen: wer in welcher Gruppe ist, wer wieviel ausgegeben hat und wie die Salden aussehen. Zugreifen sollen darauf nur angemeldete Benutzer:innen können.

Daraus folgt direkt: Bis auf Registrierung und Anmeldung braucht jeder fachliche Use Case eine gültige Sitzung.

### Strategie

- Registrierung und Anmeldung sind die einzigen Bereiche, die ohne Login erreichbar sind.
- Für alle übrigen Funktionen ist eine authentifizierte Sitzung Voraussetzung.
- Mit einer erfolgreichen Anmeldung wird automatisch eine Sitzung angelegt.
- Über diese Sitzung weiß das System, welcher [User](D1_Datenmodell.md#user) gerade aktiv ist.
- Meldet sich jemand ab, wird die Sitzung sofort beendet.
- Wer nicht angemeldet ist, landet automatisch auf der Anmeldeseite.
- Passwörter werden zu keinem Zeitpunkt im Klartext gespeichert.
- Gespeichert wird ausschließlich ein Passwort-Hash (siehe `passwordHash` bei [User](D1_Datenmodell.md#user)).

### Betroffene Use Cases

- UC-01 Registrieren
- UC-02 Anmelden
- UC-03 Abmelden
- UC-04 Dashboard anzeigen
- UC-05 Gruppe erstellen
- UC-06 Gruppe anzeigen
- UC-07 Mitglied zur Gruppe hinzufügen
- UC-08 Ausgabe erfassen
- UC-09 Ausgabe bearbeiten
- UC-10 Ausgabe löschen
- UC-11 Salden anzeigen
- UC-12 Ausgabenübersicht exportieren

### Regeln

| ID      | Regel                                                                                                                  |
| ------- | ---------------------------------------------------------------------------------------------------------------------- |
| AUTH-01 | Ohne Anmeldung dürfen nur Registrierung und Anmeldung ausgeführt werden.                                               |
| AUTH-02 | Nach erfolgreicher Anmeldung wird der Benutzer zum Dashboard weitergeleitet.                                           |
| AUTH-03 | Nach Abmeldung ist kein Zugriff auf geschützte Dialoge mehr möglich.                                                   |
| AUTH-04 | Passwörter werden nur als Hash gespeichert.                                                                            |
| AUTH-05 | Fehlgeschlagene Anmeldungen geben keine detaillierten Informationen darüber preis, ob E-Mail oder Passwort falsch war. |

### Querverweise

| Baustein | Relevanz                                                               |
| -------- | ---------------------------------------------------------------------- |
| [F2](F2-anwendungsfälle.md)       | Beschreibt Registrierung, Anmeldung und Abmeldung als Use Cases.       |
| [B1](B1_Dialogspezifikation.md)       | Beschreibt Login-, Registrierungs- und Abmeldedialoge.                 |
| [D1](D1_Datenmodell.md#user)       | [User](D1_Datenmodell.md#user) enthält E-Mail-Adresse und Passwort-Hash. |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Sicherheitsanforderungen fordern Authentifizierung und Passwortschutz. |

## N2.3 Autorisierung und Gruppenrechte

### Anliegen

Da CampusSplit von mehreren Personen gleichzeitig genutzt wird, muss klar geregelt sein, wer worauf zugreifen darf: Man sieht grundsätzlich nur die [Group](D1_Datenmodell.md#group)n, in denen man selbst Mitglied ist.

Innerhalb einer Gruppe gibt es außerdem noch einmal Unterschiede, denn nicht jede Aktion darf jedes Mitglied ausführen. Neue Mitglieder hinzuzufügen ist zum Beispiel den Gruppenadministrator:innen vorbehalten.

Autorisierung ist dabei bewusst als **Querschnittskonzept** angelegt: Sie gilt nicht nur für einen einzelnen Use Case, sondern greift bei praktisch jeder Aktion, die sich auf eine Gruppe bezieht.

### Strategie

- Jede [Group](D1_Datenmodell.md#group) besitzt eine oder mehrere [Membership](D1_Datenmodell.md#membership)-Einträge.
- Eine Membership verknüpft einen [User](D1_Datenmodell.md#user) mit einer Group.
- Ob jemand auf Gruppendaten zugreifen darf, wird immer über diese Membership geprüft.
- Rollen ([MembershipRoleDT](D2_Datentypenverzeichnis.md#d25-membershiproledt)) gelten immer nur innerhalb der jeweiligen Gruppe, nicht gruppenübergreifend.
- Der Ersteller einer Gruppe erhält automatisch die Rolle ADMIN.
- Alle anderen Mitglieder erhalten die Rolle MEMBER.
- Nur ADMIN darf neue Mitglieder hinzufügen.
- MEMBER darf Ausgaben erfassen, bearbeiten, löschen, Salden anzeigen und Exporte erzeugen.

### Rollen

| Rolle  | Bedeutung                                               |
| ------ | -------------------------------------------------------- |
| ADMIN  | Gruppenadministrator mit erweiterten Verwaltungsrechten |
| MEMBER | Normales Gruppenmitglied                                |

### Berechtigungen

| Aktion              | ADMIN | MEMBER |
| -------------------- | ----- | ------ |
| Gruppe anzeigen     | ja    | ja     |
| Ausgaben anzeigen   | ja    | ja     |
| Ausgabe erfassen    | ja    | ja     |
| Ausgabe bearbeiten  | ja    | ja     |
| Ausgabe löschen     | ja    | ja     |
| Salden anzeigen     | ja    | ja     |
| Export erzeugen     | ja    | ja     |
| Mitglied hinzufügen | ja    | nein   |

### Betroffene Use Cases

Autorisierung greift in jedem Use Case, der sich auf eine bestehende Gruppe bezieht:

| Use Case | Anknüpfende Regel |
| -------- | ------------------ |
| UC-05 Gruppe erstellen | AUT-06 (Ersteller wird ADMIN, Gruppe braucht mindestens einen ADMIN) |
| UC-06 Gruppe anzeigen | AUT-01 |
| UC-07 Mitglied zur Gruppe hinzufügen | AUT-04 |
| UC-08 Ausgabe erfassen | AUT-02, AUT-03 |
| UC-09 Ausgabe bearbeiten | AUT-02, AUT-03 |
| UC-10 Ausgabe löschen | AUT-02, AUT-03 |
| UC-11 Salden anzeigen | AUT-01, AUT-02 |
| UC-12 Ausgabenübersicht exportieren | AUT-01, AUT-02 |

### Regeln

| ID     | Regel                                                                         |
| ------ | ------------------------------------------------------------------------------ |
| AUT-01 | Ein Benutzer darf eine Gruppe nur sehen, wenn er Mitglied dieser Gruppe ist.  |
| AUT-02 | Ein Benutzer darf Ausgaben nur für Gruppen sehen, in denen er Mitglied ist.   |
| AUT-03 | Ein Benutzer darf nur Ausgaben in Gruppen erfassen, in denen er Mitglied ist. |
| AUT-04 | Nur Gruppenadministrator:innen dürfen Mitglieder hinzufügen.                  |
| AUT-05 | Rollen gelten ausschließlich innerhalb der jeweiligen Gruppe.                 |
| AUT-06 | Jede Gruppe muss mindestens einen Administrator besitzen.                     |

### Querverweise

| Baustein | Relevanz                                                                          |
| -------- | ------------------------------------------------------------------------------- |
| [F2](F2-anwendungsfälle.md)       | UC-05 bis UC-12 setzen Mitgliedschaft oder Administratorrechte voraus.            |
| [D1](D1_Datenmodell.md#membership)       | [Membership](D1_Datenmodell.md#membership) verbindet [User](D1_Datenmodell.md#user) und [Group](D1_Datenmodell.md#group). |
| [D2](D2_Datentypenverzeichnis.md#d25-membershiproledt)       | [MembershipRoleDT](D2_Datentypenverzeichnis.md#d25-membershiproledt) definiert ADMIN und MEMBER. |
| [B1](B1_Dialogspezifikation.md)       | Dialoge blenden Aktionen abhängig von Berechtigungen ein oder aus.                |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Zugriffsschutz und Autorisierung werden als Sicherheitsanforderungen beschrieben. |

## N2.4 Validierung

### Anliegen

Überall dort, wo Benutzer:innen Daten eingeben, kann auch etwas Falsches oder Unvollständiges dabei sein. Würden solche Daten ungeprüft gespeichert, hätte das direkte Folgen für Saldenberechnung und Exporte - am Ende kämen einfach falsche Zahlen heraus.

Wichtig ist Validierung deshalb überall dort, wo Nutzer:innen etwas eintragen: bei Registrierung, Gruppenerstellung, Mitgliederverwaltung, Ausgabenerfassung, Ausgabenbearbeitung und Export.

### Strategie

- Eingaben werden immer vor dem Speichern geprüft, nie danach.
- Welche Felder tatsächlich Pflichtfelder sind, ist je Bereich unterschiedlich und wird konkret in der Tabelle unten festgelegt, statt es pauschal für alle Formulare gleich zu behandeln.
- Beträge müssen ein gültiges [MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt)-Format haben.
- E-Mail-Adressen müssen ein gültiges Format haben.
- Wer etwas in einer Gruppe tut, muss auch Mitglied dieser Gruppe sein.
- Auch Zahler und Beteiligte einer [Expense](D1_Datenmodell.md#expense) müssen Mitglieder der Gruppe sein.
- Die einzelnen [ExpenseShare](D1_Datenmodell.md#expenseshare)s müssen in Summe exakt dem Abrechnungsbetrag (settlementAmount) der Ausgabe entsprechen.
- Bei einer Fremdwährungsausgabe muss vor dem Speichern ein gültiger Wechselkurs ermittelt worden sein.
- Fehlermeldungen werden verständlich direkt im betroffenen Dialog angezeigt.
- Ist eine Eingabe ungültig, wird nichts gespeichert.

### Validierungsbereiche

Statt pauschal "Pflichtfelder dürfen nicht leer sein" auf alle Formulare anzuwenden, wird hier je Bereich konkret festgelegt, welche Felder tatsächlich verpflichtend sind:

| Bereich              | Pflichtfelder                                              | Optionale Felder                  |
| --------------------- | ------------------------------------------------------------ | ----------------------------------- |
| Registrierung        | Name, E-Mail, Passwort                                       | –                                    |
| Anmeldung            | E-Mail, Passwort                                              | –                                    |
| Gruppenerstellung    | Gruppenname, Gruppenwährung                                   | Beschreibung                        |
| Mitgliederverwaltung | E-Mail-Adresse des neuen Mitglieds                            | –                                    |
| Ausgabenerfassung    | Betrag, Währung, Zahler, mindestens eine beteiligte Person, Aufteilung | Beschreibung, Datum, Kategorie |
| Export               | Exportformat                                                  | Zeitraum                            |

### Regeln

| ID     | Regel                                                                  |
| ------ | ---------------------------------------------------------------------- |
| VAL-01 | Pflichtfelder dürfen nicht leer sein.                                  |
| VAL-02 | E-Mail-Adressen müssen formal gültig sein.                             |
| VAL-03 | Eine E-Mail-Adresse darf nur einem Benutzerkonto zugeordnet sein.      |
| VAL-04 | Ein Gruppenname darf nicht leer sein.                                  |
| VAL-05 | Der Betrag einer Ausgabe muss größer als 0.00 sein.                    |
| VAL-06 | Der Zahler einer Ausgabe muss Mitglied der Gruppe sein.                |
| VAL-07 | Jede beteiligte Person einer Ausgabe muss Mitglied der Gruppe sein.    |
| VAL-08 | Mindestens eine beteiligte Person muss ausgewählt sein.                |
| VAL-09 | Die Summe aller Kostenanteile muss exakt dem Abrechnungsbetrag entsprechen. |
| VAL-10 | Ein Exportformat muss PDF oder CSV sein.                               |
| VAL-11 | Bei einer Ausgabe in Fremdwährung muss ein gültiger Wechselkurs vorliegen, bevor gespeichert wird. |

### Fehlerdarstellung

Validierungsfehler werden direkt im jeweiligen Dialog angezeigt.

Beispiele:

| Situation                          | Beispielmeldung                                            |
| ------------------------------------ | -------------------------------------------------------------|
| Gruppenname fehlt                  | „Bitte geben Sie einen Gruppennamen ein."                  |
| Betrag ist ungültig                | „Der Betrag muss größer als 0,00 sein."                    |
| Keine beteiligte Person ausgewählt | „Bitte wählen Sie mindestens ein Gruppenmitglied aus."     |
| Aufteilungssumme ist falsch        | „Die Summe der Anteile muss dem Abrechnungsbetrag entsprechen." |
| Benutzer ist kein Gruppenmitglied  | „Sie haben keinen Zugriff auf diese Gruppe."               |
| Wechselkurs konnte nicht ermittelt werden | „Für diese Währung konnte aktuell kein Kurs ermittelt werden." |

### Querverweise

| Baustein | Relevanz                                                                      |
| -------- | ------------------------------------------------------------------------------- |
| [F2](F2-anwendungsfälle.md)       | Use Cases beschreiben, wann Eingaben erfolgen.                                |
| [F3](F3-anwendungsfunktionen.md)       | Kostenaufteilung und Saldenberechnung setzen gültige Eingaben voraus.         |
| [D1](D1_Datenmodell.md#d15-datenmodell-invarianten)       | Datenmodell-Invarianten definieren fachlich erlaubte Zustände.                |
| [D2](D2_Datentypenverzeichnis.md)       | Datentypen bestimmen gültige Wertebereiche.                                   |
| [B1](B1_Dialogspezifikation.md)       | Dialoge zeigen Validierungsfehler an.                                         |
| [S1](S1_Nachbarsysteme.md)       | Liefert den für die Validierung nötigen Wechselkurs bei Fremdwährungsausgaben. |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Datenkonsistenz und Benutzerfreundlichkeit fordern verständliche Validierung. |

## N2.5 Geldbetragsverarbeitung

### Anliegen

Da es bei CampusSplit im Kern um Geld geht, muss hier besonders sauber gerechnet werden. Schon kleine Rundungsfehler summieren sich über mehrere Ausgaben hinweg und führen am Ende zu Salden, die nicht mehr stimmen. Zusätzlich unterstützt CampusSplit Ausgaben in einer anderen Währung als der Gruppenwährung, was eine zuverlässige Umrechnung erfordert. Deshalb legen wir für den gesamten Umgang mit Geldbeträgen eine einheitliche Regel fest, statt das jeder Funktion einzeln zu überlassen.

> **Hinweis:** Die konkrete Berechnungslogik für Kostenaufteilung, Saldenberechnung und Ausgleichsvorschläge ist sehr umfangreich und wird ausführlich in [F3-anwendungsfunktionen.md](F3-anwendungsfunktionen.md) beschrieben. Die technischen Details der Wechselkursermittlung stehen in [S1 — Nachbarsysteme](S1_Nachbarsysteme.md). Hier in N2.5 stehen nur die querschnittlichen Grundregeln, die für die Geldverarbeitung überall im System gelten.

### Strategie

- Geldbeträge werden centgenau verarbeitet ([MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt)).
- Jede Gruppe hat eine feste Gruppenwährung ([CurrencyCodeDT](D2_Datentypenverzeichnis.md#d24-currencycodedt)), in der Salden und Ausgleichsvorschläge immer angegeben werden.
- Eine Ausgabe kann in einer anderen Währung erfasst werden als die Gruppenwährung; in diesem Fall wird über den externen Wechselkursdienst ein [ExchangeRateDT](D2_Datentypenverzeichnis.md#d24a-exchangeratedt) ermittelt und der Betrag in die Gruppenwährung umgerechnet.
- Beträge werden immer mit zwei Nachkommastellen dargestellt.
- Ausgaben und Kostenanteile können nicht negativ sein.
- Ein Saldo dagegen kann positiv, negativ oder genau null sein.
- Berechnungen laufen deterministisch ab, also bei gleicher Eingabe immer mit demselben Ergebnis.
- Die Kostenanteile einer Ausgabe müssen in Summe dem Abrechnungsbetrag in Gruppenwährung entsprechen.
- Die Summe aller Salden innerhalb einer Gruppe muss 0.00 ergeben.

### Fachliche Bedeutung von Salden

| Saldo   | Bedeutung                    |
| ------- | ------------------------------ |
| Positiv | Mitglied bekommt Geld zurück |
| Negativ | Mitglied schuldet Geld       |
| 0.00    | Mitglied ist ausgeglichen    |

### Rundungsstrategie

Lässt sich ein Betrag bei gleichmäßiger Aufteilung nicht glatt durch die Anzahl der Personen teilen, wird trotzdem centgenau aufgeteilt - die Differenz von einem Cent bekommt einfach eine der beteiligten Personen ab.

Beispiel: 10.00 € werden auf drei Personen verteilt.

| Person   | Anteil |
| -------- | ------ |
| Person A | 3.34 € |
| Person B | 3.33 € |
| Person C | 3.33 € |

Wichtig ist dabei, dass die Rundung nachvollziehbar und deterministisch bleibt: Bei identischer Eingabe kommt jedes Mal dieselbe Verteilung heraus. Die genaue Rechenlogik dazu steht in F3.

### Fremdwährungsumrechnung

Bei einer Ausgabe in Fremdwährung gilt:

- Die Umrechnung erfolgt nur, wenn Originalwährung und Gruppenwährung voneinander abweichen.
- Der Wechselkurs wird beim externen Wechselkursdienst ermittelt, nicht selbst angenommen oder erfunden.
- Der Abrechnungsbetrag in Gruppenwährung ergibt sich aus Originalbetrag multipliziert mit dem ermittelten Kurs, anschließend centgenau gerundet.
- Originalbetrag, Originalwährung und der verwendete Kurs bleiben dauerhaft nachvollziehbar mit der Ausgabe verknüpft.
- Kann kein Kurs ermittelt werden, wird die Ausgabe nicht gespeichert.

### Regeln

| ID       | Regel                                                                |
| -------- | ----------------------------------------------------------------------|
| MONEY-01 | Geldbeträge werden nicht mit ungenauen Gleitkommazahlen verarbeitet. |
| MONEY-02 | Ausgaben müssen größer als 0.00 sein.                                |
| MONEY-03 | Kostenanteile dürfen nicht negativ sein.                             |
| MONEY-04 | Salden dürfen negativ, positiv oder null sein.                       |
| MONEY-05 | Die Summe der Kostenanteile entspricht exakt dem Abrechnungsbetrag.  |
| MONEY-06 | Die Summe aller Gruppensalden ergibt exakt 0.00 in Gruppenwährung.   |
| MONEY-07 | Jede Gruppe hat eine feste Gruppenwährung, in der Salden dargestellt werden. |
| MONEY-08 | Weicht die Originalwährung einer Ausgabe von der Gruppenwährung ab, wird ein Wechselkurs über den externen Dienst ermittelt und die Umrechnung nachvollziehbar gespeichert. |

### Querverweise

| Baustein | Relevanz                                                                         |
| -------- | ----------------------------------------------------------------------------------|
| [F3](F3-anwendungsfunktionen.md)       | [F3-anwendungsfunktionen.md](F3-anwendungsfunktionen.md) beschreibt AF-01, AF-02 und AF-03 mit der vollständigen Berechnungslogik. |
| [D1](D1_Datenmodell.md#expense)       | [Expense](D1_Datenmodell.md#expense), [ExpenseShare](D1_Datenmodell.md#expenseshare), Balance und SettlementProposal verwenden Geldbeträge. |
| [D2](D2_Datentypenverzeichnis.md#d23-moneyamountdt)       | [MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt), [CurrencyCodeDT](D2_Datentypenverzeichnis.md#d24-currencycodedt) und [ExchangeRateDT](D2_Datentypenverzeichnis.md#d24a-exchangeratedt) definieren Wertebereiche und Regeln. |
| [S1](S1_Nachbarsysteme.md)       | Externer Wechselkursdienst zur Ermittlung des Umrechnungskurses. |
| [B1](B1_Dialogspezifikation.md)       | Dialoge erfassen und zeigen Geldbeträge, inklusive Fremdwährung.                 |
| [B3](B3_Druckausgaben.md)       | Exportdateien enthalten Geldbeträge, Wechselkurse und Salden.                    |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Genauigkeits- und Datenkonsistenzanforderungen beziehen sich auf Geldberechnung. |

## N2.6 Fehlerbehandlung

### Anliegen

Fehler lassen sich nie ganz vermeiden. Die Anwendung muss trotzdem einheitlich reagieren, verständlich informieren und darf dabei keine Daten verlieren.

### Grundregeln

- Fehlermeldungen sind verständlich und werden nicht technisch angezeigt.
- Ungültige Eingaben verhindern die Speicherung.
- Fehlende Berechtigung führt zu einer klaren Zugriff-verweigert-Meldung.
- Fehlgeschlagene Speicherung hinterlässt keine unvollständigen Daten.
- Nach einem Fehler bleibt die Anwendung in einem stabilen, wiederholbaren Zustand.

### Fehlerarten

| Fehlerart            | Beispiel                          | Verhalten                             |
| ---------------------- | ------------------------------------ | ---------------------------------------|
| Validierungsfehler    | Betrag ungültig                    | Feldbezogene Meldung                  |
| Autorisierungsfehler  | Kein Gruppenmitglied               | Zugriff verweigert                    |
| Nicht gefunden        | Gruppe existiert nicht             | Meldung mit Rückkehrmöglichkeit       |
| Persistenz-/Exportfehler | Speichern oder Export schlägt fehl | Keine Teilspeicherung, erneut möglich |
| Wechselkursfehler     | Wechselkursdienst nicht erreichbar | Keine Speicherung mit erfundenem Kurs, erneut möglich (siehe FX-06 in S1) |
| Sitzungsfehler        | Sitzung abgelaufen                 | Weiterleitung zur Anmeldung           |

### Regeln

| ID     | Regel                                                                            |
| ------ | ------------------------------------------------------------------------------- |
| ERR-01 | Benutzer:innen erhalten verständliche Fehlermeldungen.                           |
| ERR-02 | Technische Fehlermeldungen werden nicht direkt angezeigt.                        |
| ERR-03 | Bei fehlender Berechtigung wird die Aktion abgelehnt.                            |
| ERR-04 | Bei fehlerhafter Ausgabeerfassung entsteht keine Teilspeicherung.                |
| ERR-05 | Nach einem Fehler bleibt ein stabiler Systemzustand erhalten.                    |
| ERR-06 | Wiederholbare Aktionen wie Export, Speichern oder Wechselkursermittlung können erneut ausgelöst werden. |

### Querverweise

| Baustein | Relevanz                                                                  |
| -------- | --------------------------------------------------------------------------|
| [F2](F2-anwendungsfälle.md)       | Exception-Szenarien beschreiben Fehler in Use Cases.                      |
| [B1](B1_Dialogspezifikation.md)       | Dialoge zeigen Fehlerzustände und Validierungsfehler.                     |
| [S1](S1_Nachbarsysteme.md)       | Fehlerfälle des Wechselkursdienstes und ihr Verhalten.                    |
| [S3](S3_Inbetriebnahme.md)       | Funktionstests prüfen zentrale Fehlerfälle nach Inbetriebnahme.           |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Zuverlässigkeit und verständliche Fehlerbehandlung werden dort gefordert. |

## N2.7 Logging

### Anliegen

Damit sich technische Fehler und wichtige Ereignisse im Nachhinein nachvollziehen lassen, braucht CampusSplit ein grundlegendes Logging.

Dabei geht es uns nur um Fehlersuche und Betrieb - Logging ist ausdrücklich kein vollständiges Audit-System und ersetzt auch keine fachliche Historie der Zahlungen.

### Strategie

- Technische Fehler werden protokolliert, damit sie sich später nachvollziehen lassen.
- Auch sicherheitsrelevante Ereignisse können protokolliert werden.
- Passwörter oder Passwort-Hashes tauchen in Logeinträgen nicht auf.
- Sessiontokens werden ebenfalls nicht protokolliert.
- Vollständige sensible Zugangsdaten landen grundsätzlich nicht im Log.
- Fachliche Inhalte werden nur dann protokolliert, wenn sie für die Fehlersuche nötig und gleichzeitig unkritisch sind.
- Benutzer:innen bekommen keine technischen Logdetails zu sehen.

### Mögliche Logereignisse

| Ereignis                            | Zweck                                  |
| -------------------------------------- | ------------------------------------------|
| Fehlgeschlagene Anmeldung           | Erkennen von Zugriffsproblemen         |
| Zugriff verweigert                  | Nachvollziehen unberechtigter Zugriffe |
| Fehler beim Speichern einer Ausgabe | Fehlersuche                            |
| Fehler bei Exporterzeugung          | Fehlersuche                            |
| Fehler beim Datenbankzugriff        | Betrieb und Diagnose                   |
| Fehler beim Wechselkursdienst       | Fehlersuche                            |
| Anwendung gestartet                 | Betriebsinformation                    |

### Regeln

| ID     | Regel                                                                              |
| ------ | ------------------------------------------------------------------------------------|
| LOG-01 | Passwörter werden niemals protokolliert.                                           |
| LOG-02 | Passwort-Hashes werden nicht protokolliert.                                        |
| LOG-03 | Sessiontokens werden nicht protokolliert.                                          |
| LOG-04 | Technische Fehler können mit Zeitstempel protokolliert werden.                     |
| LOG-05 | Benutzer:innen sehen verständliche Fehlermeldungen, keine technischen Stacktraces. |
| LOG-06 | Logs dienen nicht als fachliche Zahlungshistorie.                                  |

### Querverweise

| Baustein | Relevanz                                                    |
| -------- | -------------------------------------------------------------|
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Sicherheitsanforderungen verbieten sensible Daten in Logs.  |
| [S3](S3_Inbetriebnahme.md)       | Logdaten werden als betrieblicher Datenbereich beschrieben. |
| [F2](F2-anwendungsfälle.md)       | Fehlerfälle in Use Cases können Logeinträge auslösen.       |
| [N2.6](#n26-fehlerbehandlung)     | Fehlerbehandlung und Logging wirken zusammen.               |

## N2.8 Exportsicherheit

### Anliegen

CampusSplit kann [Group](D1_Datenmodell.md#group)ndaten, [Expense](D1_Datenmodell.md#expense)n, [ExpenseShare](D1_Datenmodell.md#expenseshare)s und Salden als PDF oder CSV exportieren. Dabei muss sichergestellt sein, dass die Exporte fachlich korrekt sind und keine sensiblen oder unnötigen technischen Informationen enthalten.

### Strategie

- Exporte dürfen nur von Mitgliedern der jeweiligen Gruppe erzeugt werden.
- Ein Export bezieht sich immer auf genau eine Gruppe.
- Die Exportdaten werden jedes Mal aus dem aktuellen Datenbestand berechnet.
- Die exportierten Salden müssen mit der Berechnung aus F3 übereinstimmen und werden in Gruppenwährung dargestellt.
- Bei Fremdwährungsausgaben werden zusätzlich Originalbetrag, Originalwährung und verwendeter Wechselkurs exportiert.
- Passwörter, Passwort-Hashes und Sessioninformationen tauchen in keiner Exportdatei auf.
- Technische IDs werden nur exportiert, wenn sie fachlich tatsächlich gebraucht werden.
- Exporte verändern keine gespeicherten Daten.
- In der ersten Version werden Exportdateien nicht dauerhaft als eigene fachliche Entität gespeichert.

### Exportformate

| Format | Zweck                                            |
| -------- | --------------------------------------------------|
| PDF    | Lesbare Übersicht für Dokumentation und Ausdruck |
| CSV    | Tabellarische Weiterverarbeitung                 |

### Regeln

| ID         | Regel                                                                     |
| ---------- | ---------------------------------------------------------------------------|
| EXP-SEC-01 | Nur Gruppenmitglieder dürfen Exporte ihrer Gruppe erzeugen.               |
| EXP-SEC-02 | Exporte enthalten keine Passwörter oder Passwort-Hashes.                  |
| EXP-SEC-03 | Exporte enthalten keine Session- oder Tokeninformationen.                 |
| EXP-SEC-04 | Salden und Kostenanteile werden in Gruppenwährung mit zwei Nachkommastellen dargestellt. |
| EXP-SEC-05 | Exportierte Salden stimmen mit der aktuellen Berechnung überein.          |
| EXP-SEC-06 | Exporte führen keine Zahlungen aus.                                       |
| EXP-SEC-07 | Exporte verändern keine gespeicherten Daten.                              |
| EXP-SEC-08 | Bei Fremdwährungsausgaben werden Originalbetrag, Originalwährung und Wechselkurs im Export ausgegeben. |

### Querverweise

| Baustein | Relevanz                                                                   |
| -------- | -----------------------------------------------------------------------------|
| [F2](F2-anwendungsfälle.md)       | UC-12 löst den Export aus.                                                 |
| [F3](F3-anwendungsfunktionen.md)       | AF-04 bereitet Exportdaten fachlich auf.                                   |
| [D1](D1_Datenmodell.md#group)       | Exportdaten stammen aus [Group](D1_Datenmodell.md#group)n, [Membership](D1_Datenmodell.md#membership)s, [Expense](D1_Datenmodell.md#expense)n und [ExpenseShare](D1_Datenmodell.md#expenseshare)s. |
| [D2](D2_Datentypenverzeichnis.md#d27-exportformatdt)       | [ExportFormatDT](D2_Datentypenverzeichnis.md#d27-exportformatdt), [MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt) und [ExchangeRateDT](D2_Datentypenverzeichnis.md#d24a-exchangeratedt) bestimmen Exportwerte. |
| [B1](B1_Dialogspezifikation.md)       | DLG-11 beschreibt den Exportdialog.                                        |
| [B3](B3_Druckausgaben.md)       | Beschreibt Inhalt und Struktur der PDF- und CSV-Ausgaben.                  |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Sicherheits- und Konsistenzanforderungen gelten auch für Exporte.          |

## N2.9 Nicht Bestandteil von N2

Ein paar Querschnittskonzepte, die man sich grundsätzlich vorstellen könnte, haben wir für die erste Version von CampusSplit bewusst ausgeklammert.

| Thema                       | Status           | Begründung                                                                                                                   |
| ------------------------------ | ------------------ | ---------------------------------------------------------------------------------------------------------------------------- |
| Mehrmandantenfähigkeit      | nicht vorgesehen | CampusSplit unterscheidet nur zwischen Benutzern und Gruppen - Organisationen oder Mandanten gibt es in diesem Modell nicht. |
| Zahlungsabwicklung          | nicht vorgesehen | CampusSplit berechnet zwar Salden, führt aber selbst keine Zahlungen aus.                                                    |
| Bankintegration             | nicht vorgesehen | Es werden keinerlei Bankdaten verarbeitet.                                                                                   |
| Vollständige Audit-Historie | nicht vorgesehen | Änderungen an Daten werden nicht als eigene fachliche Historie mitgeschrieben.                                               |
| Echtzeit-Kommunikation      | nicht vorgesehen | Weder Live-Chat noch Echtzeit-Synchronisation gehören zum Projektumfang.                                                     |
| E-Mail-Benachrichtigungen   | nicht vorgesehen | Ein externer E-Mail-Dienst ist nicht vorgesehen.                                                                             |

Wir nennen diese Punkte hier bewusst, damit klar wird: Das Fehlen dieser Funktionen ist keine Lücke, sondern eine bewusste Entscheidung, um den Projektumfang realistisch zu halten.

## N2.10 Querverweise

| Baustein | Relevanz für N2                                                                                                   |
| -------- | ---------------------------------------------------------------------------------------------------------------------|
| [P1](P1_Ziele_und_Rahmenbedingungen.md)       | Projektziele, Nichtziele und Rahmenbedingungen begrenzen die Querschnittskonzepte.                                |
| [P2](P2_Architekturueberblick.md)       | Systemkontext zeigt, welche Nachbarsysteme von Querschnittskonzepten betroffen sind.                              |
| [F1](F1-geschaeftsprozesse.md)       | Geschäftsprozess zeigt, wo Authentifizierung, Validierung, Geldberechnung und Export relevant werden.             |
| [F2](F2-anwendungsfälle.md)       | Use Cases bilden die sichtbare Oberfläche der Querschnittskonzepte.                                               |
| [F3](F3-anwendungsfunktionen.md)       | Anwendungsfunktionen setzen Geldbetragsverarbeitung, Validierung und Exportregeln fachlich um.                    |
| [D1](D1_Datenmodell.md#d15-datenmodell-invarianten)       | Datenmodell-Invarianten bilden die Grundlage für Validierung und Autorisierung. |
| [D2](D2_Datentypenverzeichnis.md)       | Datentypen wie [MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt), ExchangeRateDT, [MembershipRoleDT](D2_Datentypenverzeichnis.md#d25-membershiproledt), SplitMethodDT und ExportFormatDT stützen die Querschnittsregeln. |
| [B1](B1_Dialogspezifikation.md)       | Dialoge zeigen Validierungsfehler, Fehlerzustände und berechtigungsabhängige Aktionen.                            |
| [B3](B3_Druckausgaben.md)       | Exporte folgen den Regeln der Exportsicherheit.                                                                   |
| [S1](S1_Nachbarsysteme.md)       | Externer Wechselkursdienst; Validierung, Geldverarbeitung und Fehlerbehandlung berücksichtigen dessen Verhalten.  |
| [S3](S3_Inbetriebnahme.md)       | Inbetriebnahme und Releases müssen Konfiguration, Datenbeständigkeit und Logging berücksichtigen.                 |
| [N1](N1_Nichtfunktionale%20Anforderungen.md)       | Nichtfunktionale Anforderungen definieren messbare Qualitätskriterien für die hier beschriebenen Konzepte.        |
| [E2](E2_Glossar.md)       | Glossar definiert zentrale Begriffe wie Authentifizierung, Autorisierung, Saldo, Export und Gruppenadministrator. |
