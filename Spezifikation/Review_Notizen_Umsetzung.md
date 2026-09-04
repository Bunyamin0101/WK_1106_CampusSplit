# Review-Notizen — Umsetzung und offene Punkte

Dieses Dokument hält die Hinweise aus dem Review fest und ordnet sie den vorhandenen Spezifikationsbausteinen zu.

Wichtig: Bereits vor zwei bis drei Wochen gepushte Überarbeitungen werden nicht überschrieben. Stattdessen werden fehlende Punkte ergänzt oder als neue, nachvollziehbare Ergänzungsdateien dokumentiert.

---

## 1. Grundregel für die weitere Bearbeitung

| Regel | Bedeutung |
|------|-----------|
| Alte Pushes bleiben bestehen | Bereits vorhandene Dateien werden nicht ohne ausdrückliche Abstimmung überschrieben. |
| Fehlende Bausteine ergänzen | Nicht vorhandene Bausteine werden als neue Dateien ergänzt. |
| Korrekturen nachvollziehbar machen | Review-Hinweise werden hier dokumentiert und anschließend gezielt umgesetzt. |
| Weniger Fließtext | Längere Abschnitte werden nach Möglichkeit durch Tabellen, Mermaid-Diagramme und klare Verweise ersetzt. |
| Markdown statt Bilddateien | Diagramme sollen bevorzugt als Mermaid direkt in Markdown eingebettet werden. |

---

## 2. Checkliste der Review-Hinweise

| Nr. | Review-Hinweis | Betroffener Baustein | Bewertung | Geplante Umsetzung |
|----|----------------|----------------------|-----------|--------------------|
| 1 | E1.10 ausdünnen | E1 | offen, aber alte Datei nicht überschreiben | Kürzere Ergänzung oder spätere gezielte Überarbeitung. |
| 2 | Lesebeispiele raus | E1 | offen, aber alte Datei nicht überschreiben | Lesebeispiele bei nächster E1-Überarbeitung entfernen. |
| 3 | Querverweise/Querbeispiele raus | E1 | offen, aber alte Datei nicht überschreiben | Querverweise kürzen und nur notwendige Verweise behalten. |
| 4 | P1.1 raus: wer wo gearbeitet hat | P1 | relevant | In neuer P1-Datei keine Arbeitsaufteilung aufnehmen. |
| 5 | P1.4 Tabelle | P1 | relevant | Ziele, Umfang und Nichtziele tabellarisch darstellen. |
| 6 | P1 und P2 separat | P1/P2 | relevant | Neue getrennte Dateien für P1 und P2 anlegen, ohne alte Kombi-Datei zu löschen. |
| 7 | Keine Grafik als Screenshot/Datei | P2/F1/D1/B1 | relevant | Diagramme bevorzugt als Mermaid in Markdown einbetten. |
| 8 | P2.1 unklar, DB-Trennung | P2 | relevant | Systemkontext mit klarer Trennung Browser, CampusSplit, Datenbank und externen Diensten ergänzen. |
| 9 | Nachbarsysteme zu leicht gemacht | P2/S1 | teilweise bereits bearbeitet | S1 wurde bereits überarbeitet; nicht überschreiben, nur ergänzend dokumentieren. |
| 10 | API einbinden | S1/P2 | teilweise bereits bearbeitet | Externe API und interne REST-API klarer als Schnittstellen benennen. |
| 11 | Es fehlt ein Akteur | F1/F2/P2 | relevant | Zusätzlichen Akteur einführen, z. B. externer AI-Service oder externer Wechselkurs-Service. |
| 12 | AI Service = Schnittstellen | S1/P2 | optional, falls KI/API im Scope bleibt | AI-Service als Nachbarsystem nur aufnehmen, wenn fachlich wirklich verwendet. |
| 13 | PDF = Datenfluss | P2/S1/B3 | relevant | Exportdatei/PDF als ausgehenden Datenfluss darstellen. |
| 14 | Struktur ist OK | Gesamt | erledigt | Struktur wird beibehalten. |
| 15 | Diagramm sehr mager | P2/F1/D1 | relevant | Mermaid-Diagramme aussagekräftiger gestalten. |
| 16 | Kreditor/Debitor einführen | D1/F3/E2 | relevant | Begriffe Schuldner/Gläubiger bzw. Debitor/Kreditor sauber aufnehmen. |
| 17 | Nicht zu ausführlich, weniger Text | Gesamt | relevant | Längere Textblöcke kürzen, Tabellen bevorzugen. |
| 18 | F1.3 besser verlinken | F1 | offen, alte Datei nicht überschreiben | Verweise auf F2/F3/D1/B1 ergänzen. |
| 19 | Use-Case-Überblick ausbessern und hoch platzieren | F2 | teilweise vorhanden | Use-Case-Index weiter oben platzieren, falls Datei später überarbeitet wird. |
| 20 | Geld aufteilen richtig machen | F3/D1/D2 | wichtig | Rundung, Kostenanteile, Salden, Kreditor/Debitor konsistent halten. |
| 21 | D1.3 und F3 sauber verlinken | D1/F3 | offen | Verweis von abgeleiteten Informationen auf AF-02/AF-03 ergänzen. |
| 22 | D2 Überschrift davor | D2 | offen | Klare Überschrift vor Datentypenkatalog ergänzen. |
| 23 | Implementierung besser recherchieren, Many, ansonsten Rundungsfaktor | D1/D2/F3 | relevant | m:n-Beziehungen über Membership und ExpenseShare klar darstellen; Rundung als fachliche Regel dokumentieren. |
| 24 | Datenstruktur intern besser strukturieren | D1 | relevant | Entitäten, abgeleitete Daten und Invarianten klar trennen. |
| 25 | MoneyAmount könnte Currency enthalten | D2 | relevant | Entscheidung dokumentieren: entweder MoneyAmount enthält Währung oder Currency bleibt getrennt. |
| 26 | B1 Überblick muss hoch | B1 | offen, alte Datei nicht überschreiben | Dialogindex und Navigationsübersicht an den Anfang stellen. |
| 27 | B1 nicht umfangreich genug / mehr ausbessern | B1 | teilweise bereits bearbeitet | Alte B1 nicht überschreiben; bei Bedarf Ergänzung mit Maskenpflichtfeldern erstellen. |
| 28 | Button löst Use Case aus | B1/F2 | relevant | Aktionen in B1 explizit mit Use Cases verbinden. |
| 29 | B3 Exporte mehr schreiben | B3 | teilweise bereits bearbeitet | Exportdaten, Datenfluss und Formatregeln ergänzend dokumentieren. |
| 30 | S3 hinzufügen | S3 | erledigt | S3 wurde als eigene Datei ergänzt. |
| 31 | Einträge teilweise lang, besser Tabelle | Gesamt | relevant | In neuen Ergänzungen Tabellen bevorzugen. |
| 32 | N1 Value Schema nicht erwähnt | N1/D2 | offen | Value-Schema als Validierungs-/Werteschema ergänzen. |
| 33 | N2.4 pro Maske, pro Use Case Pflichtfelder | N2/B1/F2 | offen | Pflichtfelder je Maske/Use Case tabellarisch ergänzen. |
| 34 | Warnmeldungen Dialog/Querschnitt | B1/N2 | offen | Warn- und Fehlermeldungen systematisch ergänzen. |
| 35 | N2.5 auf F3 verweisen, um zu kürzen | N2/F3 | offen | N2 Geldbetragsverarbeitung kürzer halten und auf F3 verweisen. |

---

## 3. Bereits ergänzt

| Datei | Zweck | Status |
|------|-------|--------|
| `Spezifikation/B2_Batch.md` | Batch als nicht anwendbar begründen | erledigt |
| `Spezifikation/S2_Datenmigration.md` | Datenmigration als nicht anwendbar begründen | erledigt |
| `Spezifikation/S3_Inbetriebnahme.md` | Inbetriebnahme ergänzen | erledigt |

---

## 4. Nächste sinnvolle Ergänzungen ohne alte Dateien zu überschreiben

| Priorität | Ergänzung | Neuer Dateivorschlag | Begründung |
|----------|-----------|----------------------|------------|
| 1 | P1 und P2 trennen | `P1_Ziele_und_Rahmenbedingungen.md`, `P2_Architekturueberblick.md` | Review fordert getrennte Bausteine. |
| 2 | API- und Nachbarsystem-Ergänzung | `S1_API_und_Nachbarsysteme_Ergaenzung.md` | Nachbarsysteme wurden als zu leicht bewertet. |
| 3 | Value-Schema und Pflichtfelder | `N1_N2_ValueSchema_Pflichtfelder_Ergaenzung.md` | Review nennt fehlendes Value Schema und Pflichtfelder pro Maske/Use Case. |
| 4 | B1-Maskenübersicht | `B1_Maskenuebersicht_Ergaenzung.md` | Überblick soll höher und Button-Use-Case-Zuordnung klarer werden. |
| 5 | D1/D2/F3 Konsistenz | `D1_D2_F3_Konsistenz_Ergaenzung.md` | Geldaufteilung, Rundung, Kreditor/Debitor und Currency-Entscheidung bündeln. |

---

## 5. Empfohlene Reihenfolge

1. P1 und P2 getrennt ergänzen.
2. P2/S1 um API, Nachbarsysteme, Akteur und Datenflüsse ergänzen.
3. D1/D2/F3 fachlich schärfen: Debitor/Kreditor, Rundung, MoneyAmount/Currency.
4. B1 mit Maskenübersicht, Pflichtfeldern und Button-Use-Case-Zuordnung ergänzen.
5. N1/N2 um Value-Schema, Pflichtfelder und Warnmeldungen ergänzen.
6. E1 erst zuletzt kürzen, weil E1 nur die fertige Struktur erklärt.

---

## 6. Hinweis zur Abgabe

Für die Abgabe ist wichtig, dass die alten Inhalte nicht widersprüchlich zu den Ergänzungen werden. Wenn alte Dateien stehen bleiben, sollten neue Ergänzungsdateien eindeutig als Review-Ergänzungen markiert sein. Dadurch bleibt nachvollziehbar, was ursprünglich vorhanden war und was nach dem Review ergänzt wurde.
