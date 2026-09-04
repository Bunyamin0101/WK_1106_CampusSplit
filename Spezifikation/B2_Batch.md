# B2 — Batch

B2 beschreibt zeitgesteuerte oder automatisch gestartete Hintergrundverarbeitungen eines Systems. Für CampusSplit ist dieser Baustein fachlich geprüft, aber nicht anwendbar.

---

## B2.1 Einordnung

CampusSplit verwendet in der ersten Version keine Batch-Prozesse. Alle fachlichen Aktionen werden unmittelbar durch Benutzer:innen über die Dialoge der Anwendung ausgelöst.

| Thema | Bewertung für CampusSplit |
|------|-----------------------------|
| Zeitgesteuerte Verarbeitung | nicht vorgesehen |
| Hintergrundjobs | nicht vorgesehen |
| Automatische Nachtläufe | nicht vorgesehen |
| Automatischer Datenimport | nicht vorgesehen |
| Automatische Zahlungsabwicklung | nicht vorgesehen |
| Automatische Erinnerungen | nicht vorgesehen |

---

## B2.2 Begründung der Nichtanwendbarkeit

CampusSplit ist eine interaktive Webanwendung zur Verwaltung gemeinsamer Ausgaben. Die Anwendung reagiert auf Benutzeraktionen wie Gruppenerstellung, Ausgabenerfassung, Saldenanzeige und Exporterzeugung.

Es gibt keine fachliche Anforderung, die regelmäßig, zeitgesteuert oder ohne Benutzerinteraktion ausgeführt werden muss.

Beispiele für nicht vorhandene Batch-Prozesse:

| Möglicher Batch-Prozess | Status | Begründung |
|-------------------------|--------|------------|
| Tägliche automatische Abrechnung | nicht vorgesehen | Salden werden bei Bedarf angezeigt oder berechnet. |
| Nächtliche Datenbereinigung | nicht vorgesehen | Kein fachlicher Bedarf im Projektumfang. |
| Regelmäßiger Import von Bankdaten | nicht vorgesehen | Bankintegration ist ausgeschlossen. |
| Automatische Zahlungserinnerungen | nicht vorgesehen | Keine E-Mail- oder Benachrichtigungsfunktion im MVP. |
| Automatische Währungsaktualisierung | nicht vorgesehen | Mehrwährungen sind nicht Bestandteil der ersten Version. |

---

## B2.3 Abgrenzung zu interaktiven Funktionen

Einige CampusSplit-Funktionen wirken auf den ersten Blick wie Hintergrundlogik, sind aber keine Batch-Prozesse, weil sie direkt durch Benutzeraktionen ausgelöst werden.

| Funktion | Auslöser | Warum kein Batch? |
|----------|----------|-------------------|
| Ausgabe erfassen | Benutzer klickt auf „Speichern“ | Sofortige Verarbeitung einer Eingabe. |
| Salden anzeigen | Benutzer öffnet Saldenübersicht | Berechnung erfolgt bei Bedarf. |
| Export erzeugen | Benutzer wählt PDF oder CSV | Datei wird auf Anforderung erzeugt. |
| Gruppe erstellen | Benutzer sendet Formular ab | Direkte Speicherung einer Benutzeraktion. |

---

## B2.4 Ergebnis

B2 wird für CampusSplit ausdrücklich als **nicht anwendbar** markiert.

Die Nichtanwendbarkeit bedeutet nicht, dass der Baustein vergessen wurde. Der Baustein wurde geprüft und ausgeschlossen, weil CampusSplit keine zeitgesteuerten oder automatisch gestarteten Batch-Prozesse enthält.

---

## B2.5 Querverweise

| Baustein | Relevanz für B2 |
|----------|-----------------|
| P1 | Nichtziele schließen Bankintegration, Zahlungsabwicklung und automatische Zusatzdienste aus. |
| F2 | Alle fachlichen Funktionen werden als interaktive Use Cases beschrieben. |
| F3 | Berechnungen werden durch Use Cases ausgelöst und nicht zeitgesteuert ausgeführt. |
| B1 | Dialoge lösen die fachlichen Aktionen aus. |
| B3 | Exporte werden durch Benutzer:innen angefordert und nicht automatisch erzeugt. |
| S3 | Inbetriebnahme benötigt keine Scheduler, Cronjobs oder Hintergrunddienste. |
| N2 | Fehlerbehandlung und Validierung beziehen sich auf synchrone Benutzeraktionen. |
