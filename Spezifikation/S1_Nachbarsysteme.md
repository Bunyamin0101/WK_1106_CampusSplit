# S1 — Nachbarsysteme

CampusSplit tauscht mit zwei fremd betriebenen Systemen Daten aus ([P2](P2-architekturueberblick.md)). Für jedes gibt es einen Steckbrief.

## S1.1 NS-01 — Frankfurter-Wechselkursdienst

Öffentlicher Dienst, der Referenzkurse für Währungen liefert.

| Frage | Antwort |
|---|---|
| Daten und Richtung | CampusSplit sendet Ausgangswährung, Zielwährung und Datum. Der Dienst liefert den Kurs und das Datum, für das er gilt. Personenbezogene Daten oder Inhalte von Gruppen werden nicht gesendet. |
| Zuständigkeit | Der Dienst wird von Dritten betrieben. CampusSplit hat keinen Einfluss auf Verfügbarkeit und Inhalt. |
| Anpassungen dort | keine |
| Kopplung | lose: Ohne den Dienst funktioniert alles außer der Umrechnung von Fremdwährungsausgaben und der Euro-Gesamtsumme. |
| Prüfung der Antwort | Die Antwort wird nur verwendet, wenn sie zum angefragten Währungspaar gehört, ihr Datum nicht nach dem angefragten Datum liegt und der Kurs größer 0 ist ([D2](D2-datentypenverzeichnis.md#wechselkurs)). |
| Frequenz und Art | bei Bedarf, ohne Zwischenspeicher: beim Speichern einer Ausgabe in Fremdwährung (AF-04, Kurs zum Ausgabedatum) und bei jedem Aufruf von DLG-04, wenn der Benutzer Gruppen in US-Dollar hat (AF-06, Kurs des heutigen Tages) |
| Störung | Es wird kein Kurs angenommen. Die Ausgabe wird nicht gespeichert bzw. die Euro-Gesamtsumme entfällt, jeweils mit Meldung ([NFR-07](N1-nichtfunktionale-anforderungen.md#n14-zuverlässigkeit)). |

## S1.2 NS-02 — Google-Anmeldung

Anmeldedienst von Google nach OpenID Connect. CampusSplit nutzt ihn nur, wenn er im Betrieb eingeschaltet ist ([S3](S3-inbetriebnahme.md)); sonst sind die Google-Schaltflächen ausgeblendet.

| Frage | Antwort |
|---|---|
| Daten und Richtung | CampusSplit leitet den Benutzer zur Anmeldung bei Google weiter. Google liefert die Google-Kennung, die E-Mail-Adresse, ob diese bestätigt ist, und den Namen. CampusSplit fragt nur diese Angaben an und sendet keine Gruppen-, Ausgaben- oder Abrechnungsdaten an Google. |
| Zuständigkeit | Google prüft die Identität. CampusSplit entscheidet, welchem CampusSplit-Konto sie gehört ([QK-07](N2-querschnittskonzepte.md#n23-identität)). Der Zugang bei Google wird vom Projektteam verwaltet. |
| Anpassungen dort | Bei Google muss für jede Betriebsumgebung die Rückleitungsadresse von CampusSplit eingetragen sein. |
| Kopplung | lose: Ohne Google funktionieren Registrierung und Anmeldung mit E-Mail-Adresse und Passwort weiter. Benutzer, die nur ein Google-Konto haben, können sich dann nicht anmelden. |
| Frequenz und Art | bei jeder Anmeldung oder Registrierung mit Google und bei jeder Verknüpfung (UC-01, UC-02, UC-06) |
| Störung | Die Anmeldung bricht ab, DLG-02 zeigt eine Meldung. Es wird kein Konto angelegt oder verknüpft. |
