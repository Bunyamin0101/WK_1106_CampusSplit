# B1 Dialogspezifikation

B1 beschreibt die Dialoge, über die Benutzer mit CampusSplit arbeiten.
Ein Dialog ist eine Bildschirmansicht mit einem klaren Zweck, zum
Beispiel „Ausgabe erfassen" oder „Gruppendetail anzeigen".

Die Dialoge orientieren sich an den Use Cases aus [F2 ---
Anwendungsfälle](F2-anwendungsfälle.md). Der aktuelle
Implementierungsstand enthält darüber hinaus Funktionen, für die F2
derzeit keinen eigenen Use Case ausweist, insbesondere Google-Anmeldung
und -Verknüpfung, Profilpflege, Rückzahlungen, Belege, Aktivitäten sowie
Archivierung und Löschen von Gruppen. Diese Funktionen werden in B1 als
implementierte Erweiterungen dokumentiert, ohne neue UC-Nummern zu
erfinden.

Wie die Dialoge konkret gestaltet werden (Farben, Pixelmaße oder
CSS-Details), wird hier nicht festgelegt. UC-03 (Abmelden) besitzt
keinen eigenen Dialog; siehe
[B1.9](#b19-rollen-navigation-und-abmeldung).

## B1.1 Übersicht

  -------------------------------------------------------------------------------------------------------------------------------------------
  ID                Dialog               Use Case / Bezug                                                              Zugriff
  ----------------- -------------------- ----------------------------------------------------------------------------- ----------------------
  DLG-01            Registrierung        [UC-01](F2-anwendungsfälle.md#uc-01--registrieren)                     Gast

  DLG-02            Anmeldung            [UC-02](F2-anwendungsfälle.md#uc-02--anmelden)                         Gast

  DLG-03            Dashboard            [UC-04](F2-anwendungsfälle.md#uc-04--dashboard-anzeigen)               Benutzer

  DLG-04            Gruppe erstellen     [UC-05](F2-anwendungsfälle.md#uc-05--gruppe-erstellen)                 Benutzer

  DLG-05            Gruppendetail        [UC-06](F2-anwendungsfälle.md#uc-06--gruppe-anzeigen) sowie            Gruppenmitglied
                                         implementierte Erweiterungen                                                  

  DLG-06            Mitglied hinzufügen  [UC-07](F2-anwendungsfälle.md#uc-07--mitglied-zur-gruppe-hinzufügen)   Gruppenadministrator

  DLG-07            Ausgabe erfassen     [UC-08](F2-anwendungsfälle.md#uc-08--ausgabe-erfassen)                 Gruppenmitglied

  DLG-08            Ausgabe bearbeiten   [UC-09](F2-anwendungsfälle.md#uc-09--ausgabe-bearbeiten)               Gruppenmitglied

  DLG-09            Ausgabe löschen      [UC-10](F2-anwendungsfälle.md#uc-10--ausgabe-löschen)                  Gruppenmitglied

  DLG-10            Salden und Ausgleich [UC-11](F2-anwendungsfälle.md#uc-11--salden-anzeigen)                  Gruppenmitglied

  DLG-11            Export               [UC-12](F2-anwendungsfälle.md#uc-12--ausgabenübersicht-exportieren)    Gruppenmitglied

  DLG-12            Profil und           implementierte Erweiterung; derzeit kein eigener F2-Use-Case                  Benutzer
                    Kontoeinstellungen                                                                                 
  -------------------------------------------------------------------------------------------------------------------------------------------

Die in F2 dokumentierten Akteure und Use Cases sind im
[Use-Case-Diagramm in
F2.8](F2-anwendungsfälle.md#f28-use-case-diagramm) dargestellt.

### Zuordnung der Kern-Dialoge zu F2

``` mermaid
flowchart LR
    DLG04["DLG-04 Gruppe erstellen"] --> UC05["UC-05 Gruppe erstellen"]
    DLG06["DLG-06 Mitglied hinzufügen"] --> UC07["UC-07 Mitglied hinzufügen"]
    DLG07["DLG-07 Ausgabe erfassen"] --> UC08["UC-08 Ausgabe erfassen"]
    DLG08["DLG-08 Ausgabe bearbeiten"] --> UC09["UC-09 Ausgabe bearbeiten"]
    DLG09["DLG-09 Ausgabe löschen"] --> UC10["UC-10 Ausgabe löschen"]
    DLG10["DLG-10 Salden und Ausgleich"] --> UC11["UC-11 Salden anzeigen"]
    DLG11["DLG-11 Export"] --> UC12["UC-12 Export"]
```

## B1.2 Zugriff

### DLG-01 Registrierung

Ein Gast kann ein CampusSplit-Konto mit E-Mail und Passwort anlegen.

  -----------------------------------------------------------------------
  Feld                    Pflicht                 Prüfung
  ----------------------- ----------------------- -----------------------
  Name                    Ja                      darf nicht leer sein;
                                                  maximal 100 Zeichen

  E-Mail                  Ja                      gültiges Format;
                                                  maximal 254 Zeichen;
                                                  noch nicht vergeben

  Passwort                Ja                      mindestens 10 Zeichen;
                                                  maximal 72 UTF-8-Bytes
  -----------------------------------------------------------------------

Die allgemeinen Validierungsregeln stehen in [N2.4 ---
Validierung](N2_Querschnittskonzepte.md#n24-validierung).

Wenn die Google-Anmeldung konfiguriert ist, bietet die
Registrierungsseite zusätzlich „Mit Google registrieren" an. Die
technische Google-/OIDC-Anbindung wird als Nachbarsystem in [S1 ---
Nachbarsysteme](S1_Nachbarsysteme.md) beschrieben.

Aktionen:

-   „Konto erstellen" →
    [UC-01](F2-anwendungsfälle.md#uc-01--registrieren)
-   „Mit Google registrieren" → Google-Anmeldung, sofern aktiviert
-   „Anmelden" → DLG-02 /
    [UC-02](F2-anwendungsfälle.md#uc-02--anmelden)

Nach erfolgreicher Registrierung mit E-Mail und Passwort wird zur
Anmeldung weitergeleitet.

### DLG-02 Anmeldung

  Feld       Pflicht   Prüfung
  ---------- --------- ------------------------
  E-Mail     Ja        gültiges E-Mail-Format
  Passwort   Ja        darf nicht leer sein

Aktionen:

-   „Anmelden" → [UC-02](F2-anwendungsfälle.md#uc-02--anmelden)
-   „Mit Google anmelden" → Google-Anmeldung, sofern aktiviert
-   „Jetzt registrieren" → DLG-01 /
    [UC-01](F2-anwendungsfälle.md#uc-01--registrieren)

Bei falschen Zugangsdaten zeigt das System eine allgemeine
Fehlermeldung. Für fehlgeschlagene oder abgebrochene Google-Anmeldungen
werden ebenfalls verständliche Fehlermeldungen angezeigt. Bestehende
CampusSplit-Konten werden nicht automatisch mit einem Google-Konto
zusammengeführt.

Siehe [N2.2 --- Authentifizierung und
Sitzung](N2_Querschnittskonzepte.md#n22-authentifizierung-und-sitzung).

Erfolgreich → Startseite `/`; von dort führt „Meine Gruppen“ zu DLG-03 Dashboard.

## B1.3 Übersicht

### DLG-03 Dashboard

Das Dashboard ist die über „Meine Gruppen“ erreichbare Übersicht für angemeldete Benutzer.

Es zeigt:

-   aktive Gruppen,
-   archivierte Gruppen in einem getrennten Bereich,
-   Beträge nach Währung,
-   je Währung „Du bekommst zurück", „Du schuldest" und „Ausgaben diesen
    Monat",
-   soweit Wechselkurse verfügbar sind, eine zusammengefasste
    Gesamtübersicht in EUR.

Wenn noch keine aktive Gruppe vorhanden ist, wird ein Hinweis zum
Erstellen einer Gruppe angezeigt. Kann die EUR-Gesamtübersicht wegen
fehlender Wechselkurse nicht berechnet werden, wird dies angezeigt.

Aktionen:

-   „Neue Gruppe" → DLG-04 /
    [UC-05](F2-anwendungsfälle.md#uc-05--gruppe-erstellen)
-   Gruppe auswählen → DLG-05 /
    [UC-06](F2-anwendungsfälle.md#uc-06--gruppe-anzeigen)
-   „Profil" → DLG-12
-   „Abmelden" → DLG-02 /
    [UC-03](F2-anwendungsfälle.md#uc-03--abmelden)

Die EUR-Umrechnung verwendet den in [S1 ---
Nachbarsysteme](S1_Nachbarsysteme.md) beschriebenen Wechselkursdienst.

## B1.4 Gruppenverwaltung

### DLG-04 Gruppe erstellen

  -----------------------------------------------------------------------
  Feld                    Pflicht                 Prüfung
  ----------------------- ----------------------- -----------------------
  Gruppenname             Ja                      darf nicht leer sein

  Beschreibung            Nein                    optional

  Gruppenwährung          Ja                      Währung, in der Salden
                                                  und
                                                  Ausgleichsvorschläge
                                                  geführt werden
  -----------------------------------------------------------------------

Aktionen:

-   „Gruppe erstellen" →
    [UC-05](F2-anwendungsfälle.md#uc-05--gruppe-erstellen)
-   „Abbrechen" → zurück ohne Speichern

Nach dem Speichern öffnet sich die neue Gruppe in DLG-05. Der Ersteller
wird Administrator. Die Berechtigungen stehen in [N2.3 --- Autorisierung
und
Gruppenrechte](N2_Querschnittskonzepte.md#n23-autorisierung-und-gruppenrechte).

### DLG-05 Gruppendetail

DLG-05 ist die zentrale Arbeitsansicht einer Gruppe. Sie bündelt mehrere
Funktionen auf einer Seite.

Angezeigt werden insbesondere:

-   Gruppenname und Gruppenstatus,
-   Mitglieder und Rollen,
-   Ausgaben,
-   Kostenanteile,
-   Belege zu Ausgaben,
-   Salden und Ausgleichsvorschläge,
-   erfasste Rückzahlungen,
-   Aktivitäten bzw. Änderungshistorie,
-   Export,
-   Verwaltungsaktionen für Administratoren.

Aktionen:

-   „Ausgabe hinzufügen" → DLG-07 /
    [UC-08](F2-anwendungsfälle.md#uc-08--ausgabe-erfassen)
-   Ausgabe bearbeiten → DLG-08 /
    [UC-09](F2-anwendungsfälle.md#uc-09--ausgabe-bearbeiten)
-   „Mitglied hinzufügen" → DLG-06 /
    [UC-07](F2-anwendungsfälle.md#uc-07--mitglied-zur-gruppe-hinzufügen),
    nur für Administratoren
-   Salden und Ausgleichsvorschläge ansehen → DLG-10 /
    [UC-11](F2-anwendungsfälle.md#uc-11--salden-anzeigen)
-   Export herunterladen → DLG-11 /
    [UC-12](F2-anwendungsfälle.md#uc-12--ausgabenübersicht-exportieren)
-   Rückzahlung erfassen → implementierte Erweiterung
-   Rückzahlung stornieren → implementierte Erweiterung
-   Beleg hochladen, herunterladen oder entfernen → implementierte
    Erweiterung
-   Aktivitäten nach Ausgaben, Belegen und Rückzahlungen filtern →
    implementierte Erweiterung
-   Gruppe archivieren oder wiederherstellen → implementierte
    Erweiterung; nur Administrator
-   Gruppe endgültig löschen → implementierte Erweiterung; nur
    Administrator

Wer kein Mitglied der Gruppe ist, erhält keinen Zugriff. Siehe [N2.3 ---
Autorisierung und
Gruppenrechte](N2_Querschnittskonzepte.md#n23-autorisierung-und-gruppenrechte).

#### Rückzahlungen

Eine Rückzahlung wird mit Sender, Empfänger und Betrag erfasst. Nach
erfolgreicher Erfassung werden die offenen Salden aktualisiert. Eine
erfasste Rückzahlung kann storniert werden; dabei wird ein
Stornierungsgrund angegeben und die Salden werden erneut berechnet.

#### Einzelne Ausgaben und Teilzahlungen

Unter „Zurückgezahlter Betrag“ lässt sich „Einzelne Ausgabe begleichen (optional)“ aufklappen. Jede zuordenbare Ausgabe zeigt Beschreibung, Datum, offenen Betrag und ein eigenes Betragsfeld. „Zahlung erfassen“ speichert den eingegebenen positiven Betrag; Teilzahlungen sind möglich. Es wird kein Geld überwiesen.

Zur Auswahl stehen Ausgaben, die der Empfänger des aktuellen Ausgleichsvorschlags bezahlt hat und an denen der Sender beteiligt ist. Bereits zugeordnete, nicht stornierte Zahlungen werden vom jeweiligen Anteil abgezogen. Frühere Rückzahlungen zwischen diesen Personen ohne Ausgabenzuordnung werden für diese Auswahl auf die ältesten Ausgaben angerechnet (Datum, danach Kennung). Jeder angebotene Betrag ist zusätzlich durch den aktuellen Ausgleichsvorschlag begrenzt. Bei verrechneten Gruppenschulden muss deshalb nicht jede Ausgabe einzeln auswählbar sein.

Das Backend berechnet die Grenze beim Speichern erneut und verhindert Überzahlungen, ungültige Zuordnungen und doppelte Anfragen. Gruppenadministratoren sowie Sender oder Empfänger dürfen erfassen. Eine Stornierung mit Begründung hebt die Wirkung auf Saldo und offene Anteile auf. Die Zuordnung erscheint in Aktivitäten, PDF und CSV.

#### Belege

Zu einer Ausgabe können Belege als Foto oder PDF hinzugefügt werden. Die
Oberfläche weist auf eine maximale Dateigröße von 10 MB und höchstens
fünf Belege pro Ausgabe hin. Vorhandene Belege können heruntergeladen
oder entfernt werden.

#### Aktivitäten

Die Gruppenansicht enthält eine chronologische Aktivitätsansicht. Sie
kann nach „Alle", „Ausgaben", „Belege" und „Rückzahlungen" gefiltert
werden.

#### Archivierung und Löschen

Administratoren können eine Gruppe archivieren und später
wiederherstellen. Archivierte Gruppen bleiben auf dem Dashboard in einem
eigenen Bereich einsehbar.

Für das endgültige Löschen muss der Gruppenname als Bestätigung
eingegeben werden. Die Oberfläche weist darauf hin, dass dabei die
Gruppe einschließlich Ausgaben, Kostenanteilen, Belegen, Rückzahlungen
und Aktivitäten gelöscht wird; Benutzerkonten bleiben erhalten.

### DLG-06 Mitglied hinzufügen

Die Funktion ist in das Gruppendetail integriert. Ein Administrator gibt
die E-Mail-Adresse eines bestehenden CampusSplit-Kontos ein.

  -----------------------------------------------------------------------
  Feld                    Pflicht                 Prüfung
  ----------------------- ----------------------- -----------------------
  E-Mail                  Ja                      gültige E-Mail-Adresse;
                                                  Konto muss existieren;
                                                  Person darf noch kein
                                                  Mitglied sein

  -----------------------------------------------------------------------

Aktionen:

-   „Hinzufügen" →
    [UC-07](F2-anwendungsfälle.md#uc-07--mitglied-zur-gruppe-hinzufügen)
-   Rückkehr zum Gruppendetail nach Verarbeitung

Nur Administratoren dürfen Mitglieder hinzufügen. Siehe [N2.3 ---
Autorisierung und
Gruppenrechte](N2_Querschnittskonzepte.md#n23-autorisierung-und-gruppenrechte).

## B1.5 Ausgabenverwaltung

### DLG-07 Ausgabe erfassen

  ---------------------------------------------------------------------------------------------------------------
  Feld                    Pflicht                 Prüfung / Verhalten
  ----------------------- ----------------------- ---------------------------------------------------------------
  Beschreibung            Ja                      darf nicht leer sein

  Betrag                  Ja                      gültiger Geldbetrag nach [D2.3 ---
                                                  MoneyAmountDT](D2_Datentypenverzeichnis.md#d23-moneyamountdt)

  Währung                 Ja                      standardmäßig die Gruppenwährung

  Datum                   Ja                      gültiges Datum

  Kategorie               Nein                    optional

  Zahler                  Ja                      muss zulässiges Gruppenmitglied sein

  Beteiligte              Ja                      mindestens eine Person

  Aufteilungsart          Ja                      `EQUAL` oder `CUSTOM_AMOUNT`, siehe [D2.6 ---
                                                  SplitMethodDT](D2_Datentypenverzeichnis.md#d26-splitmethoddt)

  Kostenanteil pro Person bei `CUSTOM_AMOUNT`     Summe muss zum Gesamtbetrag passen
  ---------------------------------------------------------------------------------------------------------------

Bei `CUSTOM_AMOUNT` wird für die ausgewählten Beteiligten jeweils ein
eigener Betrag angegeben. Die Summe der Kostenanteile muss dem
Gesamtbetrag entsprechen.

Wird eine andere Währung als die Gruppenwährung verwendet, rechnet
CampusSplit den Betrag für die Abrechnung in die Gruppenwährung um. Die
Regeln zur Geldbetragsverarbeitung stehen in [N2.5 ---
Geldbetragsverarbeitung](N2_Querschnittskonzepte.md#n25-geldbetragsverarbeitung);
der externe Wechselkursdienst ist in [S1 ---
Nachbarsysteme](S1_Nachbarsysteme.md) beschrieben.

#### Mockup

``` text
+--------------------------------------------------+
| Ausgabe erfassen                                 |
+--------------------------------------------------+
| Beschreibung *  [____________________________]   |
| Betrag *        [__________] [EUR v]             |
| Datum *         [__/__/____]                     |
| Kategorie       [ auswählen                 v ]  |
|                                                  |
| Bezahlt von *   [ auswählen                 v ]  |
| Beteiligte *    [x] Anna  [x] Max  [ ] Lisa     |
|                                                  |
| Aufteilung *    (x) Gleichmäßig                  |
|                 ( ) Eigene Beträge               |
|                                                  |
|                 [Abbrechen]  [Ausgabe speichern] |
+--------------------------------------------------+
```

Aktionen:

-   „Ausgabe speichern" →
    [UC-08](F2-anwendungsfälle.md#uc-08--ausgabe-erfassen)
-   „Abbrechen" → DLG-05

Mögliche Fehler sind unter anderem ein ungültiger Betrag, ein
unzulässiger Zahler, fehlende Beteiligte, eine nicht passende Summe
individueller Kostenanteile oder ein nicht verfügbarer Wechselkurs.
Allgemeine Prüfungen stehen in [N2.4 ---
Validierung](N2_Querschnittskonzepte.md#n24-validierung).

Nach dem Speichern wird zum Gruppendetail zurückgekehrt.

### DLG-08 Ausgabe bearbeiten

DLG-08 verwendet dieselben fachlichen Felder wie
[DLG-07](#dlg-07-ausgabe-erfassen), ist jedoch mit den bestehenden
Werten vorausgefüllt.

Aktionen:

-   „Änderungen speichern" →
    [UC-09](F2-anwendungsfälle.md#uc-09--ausgabe-bearbeiten)
-   „Ausgabe löschen" → DLG-09 /
    [UC-10](F2-anwendungsfälle.md#uc-10--ausgabe-löschen)
-   „Abbrechen" → DLG-05

Änderungen an Ausgaben werden in der implementierten Änderungshistorie
der Gruppe berücksichtigt.

### DLG-09 Ausgabe löschen

Das Löschen einer Ausgabe wird aus der Gruppenansicht ausgelöst. Die
zugehörigen Kostenanteile werden zusammen mit der Ausgabe entfernt. Die
Aktion gehört zu
[UC-10](F2-anwendungsfälle.md#uc-10--ausgabe-löschen).

Nach erfolgreichem Löschen wird zum Gruppendetail zurückgekehrt.

## B1.6 Saldenverwaltung

### DLG-10 Salden und Ausgleich

Die Salden und Ausgleichsvorschläge sind in das Gruppendetail
integriert.

Pro Mitglied wird der Saldo in der Gruppenwährung dargestellt.
Zusätzlich zeigt CampusSplit Vorschläge, wer wem welchen Betrag zahlen
sollte.

Die Berechnung ist in [F3 ---
Anwendungsfunktionen](F3-anwendungsfunktionen.md), insbesondere
[AF-02](F3-anwendungsfunktionen.md#af-02--gruppensalden-berechnen) und
[AF-03](F3-anwendungsfunktionen.md#af-03--ausgleichsvorschläge-berechnen),
beschrieben.

Erfasste Rückzahlungen beeinflussen die offenen Salden. Die eigentliche
Geldübertragung findet weiterhin außerhalb von CampusSplit statt.

#### Mockup

``` text
+--------------------------------------------------+
| Salden                                           |
+--------------------------------------------------+
| Anna        bekommt 24,50 EUR zurück             |
| Max         schuldet 14,50 EUR                   |
| Lisa        schuldet 10,00 EUR                   |
|                                                  |
| Wer zahlt wem?                                   |
| Max  -> Anna     14,50 EUR                       |
| Lisa -> Anna     10,00 EUR                       |
+--------------------------------------------------+
```

## B1.7 Export

### DLG-11 Export

Die Exportfunktion ist in das Gruppendetail integriert. Die Oberfläche enthält keine Datumsfelder. Der Export-Endpunkt unterstützt weiterhin optionale `from`-/`to`-Parameter; der normale Dialog exportiert ohne Zeitraumbegrenzung.

  -----------------------------------------------------------------------
  Feld                    Pflicht                 Verhalten
  ----------------------- ----------------------- -----------------------
  Exportinhalt            Ja                      `Gesamte Abrechnung`,
                                                  `Offene Beträge` oder
                                                  `Nur Ausgaben`

  Format                  Ja                      PDF oder CSV-Dateien
                                                  als ZIP

  -----------------------------------------------------------------------

Die implementierten Exportinhalte entsprechen den technischen Werten:

-   `all` → Gesamte Abrechnung,
-   `open` → Offene Beträge,
-   `expenses` → Nur Ausgaben.

Bei PDF wird eine PDF-Datei heruntergeladen. Beim CSV-Export werden die
CSV-Dateien als ZIP-Archiv bereitgestellt.

Aktion:

-   „Herunterladen" →
    [UC-12](F2-anwendungsfälle.md#uc-12--ausgabenübersicht-exportieren)

Kann der Export nicht erstellt werden, wird eine verständliche
Fehlermeldung ausgegeben. Die fachlichen Inhalte der Exportdateien
werden in [B3 --- Druck- und Exportausgaben](B3_Druckausgaben.md)
beschrieben. Ergänzende Sicherheitsregeln stehen in [N2.8 ---
Exportsicherheit](N2_Querschnittskonzepte.md#n28-exportsicherheit).

## B1.8 Profil und Kontoeinstellungen

### DLG-12 Profil und Kontoeinstellungen

DLG-12 ist für angemeldete Benutzer über „Profil" in der Navigation
erreichbar. F2 enthält für diese implementierte Funktion derzeit keinen
eigenen Use Case.

Angezeigt werden:

-   E-Mail-Adresse,
-   Anzeigename,
-   Status der Google-Verknüpfung,
-   Hinweis, ob die Anmeldung mit E-Mail und Passwort weiterhin
    verfügbar ist.

Aktionen:

-   Anzeigenamen ändern und speichern,
-   Google-Konto mit einem bestehenden CampusSplit-Konto verknüpfen,
    sofern Google-Anmeldung aktiviert ist.

Für die Verknüpfung muss bei einem bestehenden Passwortkonto das
aktuelle CampusSplit-Passwort bestätigt werden. Anschließend wird die
Google-Anmeldung durchgeführt. Gruppen, Ausgaben und die
CampusSplit-Anmelde-E-Mail bleiben erhalten.

Die Google-/OIDC-Anbindung wird in [S1 ---
Nachbarsysteme](S1_Nachbarsysteme.md) beschrieben.

## B1.9 Rollen, Navigation und Abmeldung

Ab DLG-03 gilt: Inhalte einer Gruppe sind nur für Gruppenmitglieder
zugänglich. Verwaltungsfunktionen wie Mitglied hinzufügen, Archivieren,
Wiederherstellen und endgültiges Löschen sind Administratoren
vorbehalten. Die Regeln stehen in [N2.3 --- Autorisierung und
Gruppenrechte](N2_Querschnittskonzepte.md#n23-autorisierung-und-gruppenrechte).

Für angemeldete Benutzer enthält die Navigation insbesondere:

-   „Meine Gruppen" → DLG-03,
-   „Profil" → DLG-12,
-   „Abmelden" → [UC-03](F2-anwendungsfälle.md#uc-03--abmelden).

Die Abmeldung besitzt keinen eigenen Dialog. Nach der Abmeldung wird
wieder die Anmeldung angeboten.

## B1.10 Dialogfluss

``` mermaid
flowchart TD
    DLG01["DLG-01 Registrierung"] --> DLG02["DLG-02 Anmeldung"]
    DLG02 --> DLG03["DLG-03 Dashboard"]
    DLG01 -->|Google, sofern aktiviert| DLG03

    DLG03 --> DLG04["DLG-04 Gruppe erstellen"]
    DLG04 --> DLG05["DLG-05 Gruppendetail"]
    DLG03 --> DLG05
    DLG03 --> DLG12["DLG-12 Profil"]

    DLG05 --> DLG06["DLG-06 Mitglied hinzufügen"]
    DLG05 --> DLG07["DLG-07 Ausgabe erfassen"]
    DLG05 --> DLG08["DLG-08 Ausgabe bearbeiten"]
    DLG08 --> DLG09["DLG-09 Ausgabe löschen"]
    DLG05 --> DLG10["DLG-10 Salden und Ausgleich"]
    DLG05 --> DLG11["DLG-11 Export"]

    DLG06 --> DLG05
    DLG07 --> DLG05
    DLG08 --> DLG05
    DLG09 --> DLG05

    DLG03 -->|Abmelden| DLG02
    DLG12 -->|Abmelden| DLG02
```

## B1.11 Nicht Bestandteil von B1

B1 legt keine Farben, Pixelmaße, CSS-Klassen oder konkrete technische
REST-Endpunkte fest.

Die fachlichen Exportinhalte stehen in [B3 --- Druck- und
Exportausgaben](B3_Druckausgaben.md). Datenobjekte und Datentypen werden
in D1 und D2 beschrieben. Technische Details der externen Systeme
gehören zu S1.

## B1.12 Querverweise

  --------------------------------------------------------------------------------------------
  Baustein                                                 Relevanz
  -------------------------------------------------------- -----------------------------------
  [F2 --- Anwendungsfälle](F2-anwendungsfälle.md)   Use Cases und Use-Case-Diagramm;
                                                           bildet noch nicht alle
                                                           implementierten Erweiterungen ab

  [F3 ---                                                  Berechnungen und
  Anwendungsfunktionen](F3-anwendungsfunktionen.md)        Anwendungsfunktionen

  [D1 --- Datenmodell](D1_Datenmodell.md)                  Datenobjekte, die in den Dialogen
                                                           verwendet werden

  [D2 ---                                                  Datentypen wie MoneyAmountDT und
  Datentypenverzeichnis](D2_Datentypenverzeichnis.md)      SplitMethodDT

  [B3 --- Druck- und Exportausgaben](B3_Druckausgaben.md)  Inhalt des Exports aus DLG-11

  [S1 --- Nachbarsysteme](S1_Nachbarsysteme.md)            Wechselkursdienst sowie externe
                                                           Google-/OIDC-Anbindung

  [N1 --- Nichtfunktionale                                 Anforderungen an Bedienbarkeit und
  Anforderungen](N1_Nichtfunktionale%20Anforderungen.md)   Darstellung

  [N2 ---                                                  Authentifizierung, Autorisierung,
  Querschnittskonzepte](N2_Querschnittskonzepte.md)        Validierung, Geldbeträge,
                                                           Fehlerbehandlung und
                                                           Exportsicherheit
  --------------------------------------------------------------------------------------------

## Eingesetzte KI-Werkzeuge

Claude (Anthropic) und ChatGPT (OpenAI) wurden unterstützend für
Formulierungen, Strukturierung und die Prüfung von Querverweisen
verwendet.

Die fachlichen Inhalte wurden anschließend mit dem aktuellen
Projektstand, den vorhandenen Use Cases und den übrigen
Spezifikationsbausteinen abgeglichen.

