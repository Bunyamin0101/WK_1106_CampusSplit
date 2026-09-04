# N1/N2 — Ergänzung: Value Schema, Pflichtfelder und Warnmeldungen

Diese Ergänzung setzt die Review-Hinweise zu N1 und N2 um. Die bestehenden Dateien `N1_Nichtfunktionale Anforderungen_(ZO).md` und `N2_Querschnittskonzepte_(ZO).md` bleiben unverändert bestehen.

---

## 1. Value Schema

Ein Value Schema beschreibt, welche Werte für zentrale Eingaben erlaubt sind. Dadurch werden Validierung, Masken und Datenmodell konsistent verbunden.

| Feld / Wert | Datentyp | Erlaubte Werte | Regel |
|---|---|---|---|
| Name | Text | nicht leer | Muss für Registrierung vorhanden sein. |
| E-Mail | Email | gültiges E-Mail-Format | Muss eindeutig sein. |
| Passwort | Text | nicht leer; Mindestregel nach Implementierung | Wird nur als Hash gespeichert. |
| Gruppenname | Text | nicht leer | Gruppe darf nicht ohne Namen angelegt werden. |
| Gruppenwährung | CurrencyCodeDT | unterstützter Währungscode | Standard kann EUR sein. |
| Ausgabenbeschreibung | Text | nicht leer | Muss fachlich erkennbar sein. |
| Originalbetrag | MoneyAmountDT | > 0,00 | Betrag der tatsächlichen Ausgabe. |
| Originalwährung | CurrencyCodeDT | unterstützter Währungscode | Währung der tatsächlichen Ausgabe. |
| Abrechnungsbetrag | MoneyAmountDT | > 0,00 | Betrag in Gruppenwährung nach Umrechnung. |
| Wechselkurs | ExchangeRateDT | > 0 | Nur nötig, wenn Originalwährung und Gruppenwährung abweichen. |
| Datum | Date | gültiges Datum | Datum der Ausgabe. |
| Zahler | Identifier | vorhandenes Gruppenmitglied | Zahler muss Mitglied der Gruppe sein. |
| Beteiligte | Set<Identifier> | mindestens ein Gruppenmitglied | Alle Beteiligten müssen Gruppenmitglieder sein. |
| Aufteilungsart | SplitMethodDT | EQUAL, CUSTOM_AMOUNT | Bestimmt Berechnung der Kostenanteile. |
| Exportformat | ExportFormatDT | PDF, CSV | Bestimmt Exportausgabe. |

---

## 2. Pflichtfelder pro Use Case

| Use Case | Pflichtfelder | Besondere Prüfung |
|---|---|---|
| UC-01 Registrieren | Name, E-Mail, Passwort | E-Mail eindeutig; Passwort wird gehasht. |
| UC-02 Anmelden | E-Mail, Passwort | Keine genaue Auskunft, welches Feld falsch war. |
| UC-05 Gruppe erstellen | Gruppenname, Gruppenwährung | Ersteller wird ADMIN. |
| UC-07 Mitglied hinzufügen | E-Mail-Adresse | Benutzer existiert und ist noch kein Mitglied. |
| UC-08 Ausgabe erfassen | Beschreibung, Betrag, Währung, Datum, Zahler, Beteiligte, Aufteilungsart | Kostenanteile müssen zum Abrechnungsbetrag passen. |
| UC-09 Ausgabe bearbeiten | geänderte Ausgabendaten | Nach Änderung werden Anteile und Salden neu berechnet. |
| UC-10 Ausgabe löschen | Ausgabe-ID, Bestätigung | Ausgabe und Anteile werden gemeinsam entfernt. |
| UC-11 Salden anzeigen | Gruppen-ID | Benutzer muss Mitglied der Gruppe sein. |
| UC-12 Export erzeugen | Gruppe, Exportformat | Benutzer muss Mitglied der Gruppe sein. |

---

## 3. Pflichtfelder pro Maske

| Maske | Pflichtfelder | Warn-/Fehlermeldung bei Fehler |
|---|---|---|
| DLG-01 Registrierung | Name, E-Mail, Passwort | Bitte füllen Sie alle Pflichtfelder aus. |
| DLG-02 Anmeldung | E-Mail, Passwort | Anmeldung fehlgeschlagen. Bitte prüfen Sie Ihre Eingaben. |
| DLG-04 Gruppe erstellen | Gruppenname, Gruppenwährung | Bitte geben Sie einen Gruppennamen und eine Gruppenwährung an. |
| DLG-06 Mitgliederverwaltung | E-Mail-Adresse | Dieses Mitglied kann nicht hinzugefügt werden. |
| DLG-07 Ausgabe erfassen | Beschreibung, Betrag, Währung, Datum, Zahler, Beteiligte, Aufteilung | Die Ausgabe kann erst gespeichert werden, wenn alle Pflichtfelder gültig sind. |
| DLG-08 Ausgabe bearbeiten | Beschreibung, Betrag, Währung, Datum, Zahler, Beteiligte, Aufteilung | Die geänderte Ausgabe ist fachlich nicht gültig. |
| DLG-11 Export | Exportformat | Bitte wählen Sie PDF oder CSV. |

---

## 4. Warnmeldungen

Warnmeldungen weisen auf wichtige Folgen hin, ohne zwingend ein technischer Fehler zu sein.

| Situation | Warnmeldung | Aktion |
|---|---|---|
| Ausgabe löschen | Diese Ausgabe und ihre Kostenanteile werden gelöscht. | Benutzer muss bestätigen. |
| Fremdwährungsausgabe | Für diese Ausgabe wird ein Wechselkurs verwendet. | Kurs wird vor Speicherung ermittelt. |
| Wechselkurs nicht verfügbar | Es konnte kein Wechselkurs ermittelt werden. | Speichern der Fremdwährungsausgabe wird abgebrochen. |
| Export ohne Ausgaben | Im gewählten Zeitraum gibt es keine Ausgaben. | Export kann mit Hinweis erzeugt werden. |
| Salden ausgeglichen | Alle Salden sind ausgeglichen. | Keine Ausgleichszahlung erforderlich. |

---

## 5. Bezug zu N2.5 Geldbetragsverarbeitung

N2.5 muss nicht alle Berechnungsdetails wiederholen. Die ausführliche fachliche Berechnung gehört in F3.

| Thema | Hauptort | N2-Verweis |
|---|---|---|
| Kostenanteile berechnen | F3 AF-01 | N2 nennt nur die systemweite Regel. |
| Salden berechnen | F3 AF-02 | N2 verweist auf F3 statt Berechnung zu wiederholen. |
| Ausgleichsvorschläge | F3 AF-03 | N2 beschreibt nur Debitor/Kreditor-Prinzip. |
| Rundung | F3 und D2 | N2 nennt centgenaue, deterministische Verarbeitung. |
| Fremdwährung | S1, D1/D2, F3 | N2 verweist auf Wechselkurs- und Geldbetragsregeln. |

---

## 6. Fit Criteria für N1

| ID | Fit Criterion |
|---|---|
| FIT-VAL-01 | Eine Ausgabe mit Betrag <= 0,00 wird abgelehnt. |
| FIT-VAL-02 | Eine Ausgabe ohne Beteiligte wird abgelehnt. |
| FIT-VAL-03 | Eine Ausgabe mit falscher Summe der Kostenanteile wird abgelehnt. |
| FIT-VAL-04 | Eine Fremdwährungsausgabe ohne gültigen Wechselkurs wird nicht gespeichert. |
| FIT-VAL-05 | Ein Nichtmitglied kann keine Gruppendaten abrufen. |
| FIT-VAL-06 | Ein MEMBER kann keine neuen Mitglieder hinzufügen. |
| FIT-VAL-07 | Ein PDF-/CSV-Export enthält keine Passwörter, Passwort-Hashes oder Sessiondaten. |

---

## 7. Querverweise

| Baustein | Relevanz |
|---|---|
| B1 | Pflichtfelder und Warnmeldungen erscheinen in den Masken. |
| F2 | Use Cases definieren, wann welche Eingaben erfolgen. |
| F3 | Berechnungslogik für Geldaufteilung und Salden. |
| D1/D2 | Datenmodell und Datentypen bestimmen erlaubte Werte. |
| S1 | Wechselkursdienst ist bei Fremdwährungen relevant. |
| B3 | Export muss Wert- und Sicherheitsregeln einhalten. |
| N1/N2 | Diese Ergänzung konkretisiert die nichtfunktionalen und querschnittlichen Anforderungen. |
