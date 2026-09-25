# S1 Nachbarsysteme

## S1.1 Zweck und Übersicht

Dieser Baustein beschreibt die externen Systeme, mit denen CampusSplit zur Laufzeit kommuniziert. Nachbarsysteme liegen außerhalb der fachlichen und technischen Kontrolle von CampusSplit. Deshalb werden ihre Aufgaben, ausgetauschten Daten, Fehlerfälle und Sicherheitsgrenzen explizit dokumentiert.

Im aktuellen Projektstand bestehen zwei externe Nachbarsysteme:

| ID | Nachbarsystem | Zweck |
|---|---|---|
| NB-01 | Frankfurter-Wechselkursdienst | Bereitstellung von Wechselkursen für die Umrechnung von Fremdwährungsausgaben |
| NB-02 | Google Identity / OpenID Connect | Anmeldung mit Google und kontrollierte Verknüpfung eines Google-Kontos mit einem lokalen CampusSplit-Konto |

CampusSplit bleibt für die eigene Fachlogik, Benutzerkonten, Gruppen, Ausgaben, Kostenanteile, Salden, Rückzahlungen und Exporte verantwortlich. Externe Systeme liefern nur die jeweils beschriebenen Informationen beziehungsweise Identitätsnachweise.

---

## S1.2 NB-01  Frankfurter-Wechselkursdienst

### S1.2.1 Zweck

CampusSplit verwendet den Frankfurter-Wechselkursdienst, wenn für eine Ausgabe ein Betrag aus einer Fremdwährung in die Gruppenwährung umgerechnet werden muss.

Die Wechselkursintegration ist hinter der internen Schnittstelle `CurrencyRatePort` gekapselt. Die Fachlogik ist dadurch nicht unmittelbar an den konkreten externen HTTP-Dienst gebunden.

### S1.2.2 Schnittstelle aus Sicht von CampusSplit

Die interne Schnittstelle benötigt:

- Ausgangswährung,
- Zielwährung,
- Datum.

Als Ergebnis wird ein `RateQuote` verwendet, das mindestens enthält:

- Ausgangswährung,
- Zielwährung,
- tatsächlich verwendetes Kursdatum,
- Wechselkurs.

Der konkrete HTTP-Client verwendet für den Abruf einen Endpunkt nach folgendem Schema:

`/v2/rate/{from}/{to}?date={date}`

Die Basis-URL ist über die Anwendungskonfiguration konfigurierbar.

### S1.2.3 Kommunikationsrichtung

Die Kommunikation erfolgt ausgehend von CampusSplit zum Wechselkursdienst.

CampusSplit übermittelt für die Kursabfrage:

- Währungscode der Ausgangswährung,
- Währungscode der Zielwährung,
- gewünschtes Datum.

Personenbezogene Daten, Gruppenbezeichnungen, E-Mail-Adressen, Beschreibungen von Ausgaben oder Benutzerkennungen sind für diese Anfrage nicht erforderlich und werden für die Kursabfrage nicht benötigt.

### S1.2.4 Validierung der Antwort

CampusSplit übernimmt die externe Antwort nicht ungeprüft.

Der Client prüft insbesondere:

- ob die zurückgelieferten Währungen den angefragten Währungen entsprechen,
- ob ein Kursdatum vorhanden und plausibel ist,
- ob ein Kurs vorhanden ist,
- ob der Kurs positiv ist.

Eine fachlich oder technisch unbrauchbare Antwort wird als Fehler behandelt.

### S1.2.5 Zeitverhalten und Nichtverfügbarkeit

Für die Verbindung zum externen Dienst sind begrenzte Wartezeiten vorgesehen. Im aktuellen Implementierungsstand gelten:

- Connect-Timeout: 3 Sekunden,
- Read-Timeout: 5 Sekunden.

Damit soll eine Störung des Nachbarsystems die CampusSplit-Anfrage nicht unbegrenzt blockieren.

Kann kein verwendbarer Wechselkurs ermittelt werden, darf CampusSplit keinen erfundenen oder stillschweigend angenommenen Kurs verwenden. Der Fehler wird in die Anwendung zurückgegeben und dort als verständlicher fachlicher Fehler behandelt.

### S1.2.6 Abgrenzung

Der Frankfurter-Wechselkursdienst ist ausschließlich Quelle für Wechselkursinformationen.

Er ist nicht verantwortlich für:

- Speicherung von CampusSplit-Ausgaben,
- Gruppenverwaltung,
- Kostenaufteilung,
- Saldenberechnung,
- Rückzahlungen,
- Authentifizierung,
- Exporterstellung.

Die Verwendung eines externen Wechselkurses ändert nichts daran, dass CampusSplit die eigentliche Abrechnung selbst durchführt.

---

## S1.3 NB-02 – Google Identity / OpenID Connect

### S1.3.1 Zweck

Google Identity wird als externer Identitätsanbieter für zwei Anwendungsfälle verwendet:

1. Anmeldung bei CampusSplit mit einem Google-Konto,
2. Verknüpfung eines bereits bestehenden lokalen CampusSplit-Kontos mit einer Google-Identität.

Die Integration basiert auf OAuth 2.0 / OpenID Connect.

### S1.3.2 Aktivierung und Konfiguration

Die Google-Anmeldung ist konfigurierbar und im aktuellen Projektstand standardmäßig deaktiviert.

Die Aktivierung erfolgt über die Anwendungskonfiguration. Client-ID und Client-Secret werden über Umgebungsvariablen beziehungsweise externe Konfiguration bereitgestellt und gehören nicht in die fachliche Projektdokumentation oder in öffentlich versionierte Zugangsdaten.

Die Konfiguration verwendet insbesondere:

- Aktivierungsstatus der Google-Anmeldung,
- Google Client-ID,
- Google Client-Secret.

### S1.3.3 Angeforderte OIDC-Informationen

CampusSplit fordert für die Google-Anmeldung die Scopes

- `openid`,
- `profile`,
- `email`

an.

Für die CampusSplit-Identität sind insbesondere relevant:

- stabile Google-Subject-ID (`sub`),
- E-Mail-Adresse,
- Status der E-Mail-Verifikation,
- Name beziehungsweise Profilname, soweit von Google geliefert.

CampusSplit benötigt für diesen Zweck keinen Zugriff auf Google Drive, Gmail, Kontakte oder andere Google-Dienste.

### S1.3.4 Redirect und Anmeldung

Die Anmeldung folgt dem üblichen OAuth2-/OIDC-Ablauf. CampusSplit leitet den Benutzer zum Identitätsanbieter weiter. Nach erfolgreicher Authentifizierung erfolgt die Rückleitung an den von Spring Security vorgesehenen Callback:

`/login/oauth2/code/{registrationId}`

CampusSplit verarbeitet anschließend die vom OIDC-Anbieter bereitgestellten Identitätsinformationen.

### S1.3.5 Anforderungen an die Google-Identität

Für die Verwendung einer Google-Identität verlangt CampusSplit eine verifizierte E-Mail-Adresse.

Eine Google-Identität darf nicht allein deshalb automatisch mit einem bestehenden lokalen Konto verbunden werden, weil dieselbe E-Mail-Adresse vorkommt. Für die dauerhafte Zuordnung wird die stabile Google-Subject-ID verwendet.

Dadurch wird vermieden, dass eine neue oder nicht bereits verknüpfte externe Identität ohne kontrollierten Verknüpfungsvorgang ein bestehendes lokales Konto übernimmt.

### S1.3.6 Anmeldung mit bereits verknüpftem Google-Konto

Ist die Google-Subject-ID bereits einem CampusSplit-Benutzer zugeordnet, kann diese Zuordnung zur Anmeldung verwendet werden.

Die externe Authentifizierung durch Google ersetzt dabei nicht die CampusSplit-Benutzerentität. Gruppenmitgliedschaften, Rollen, Ausgaben und andere Fachdaten bleiben weiterhin dem lokalen CampusSplit-Konto zugeordnet.

### S1.3.7 Verknüpfung mit bestehendem CampusSplit-Konto

Ein angemeldeter CampusSplit-Benutzer kann eine Google-Identität kontrolliert mit seinem lokalen Konto verknüpfen.

Vor der Verknüpfung werden Konflikte geprüft. Insbesondere darf eine Google-Subject-ID nicht gleichzeitig mehreren lokalen CampusSplit-Konten zugeordnet werden.

Eine bereits anderweitig verwendete Google-Identität wird daher abgewiesen.

Die Verknüpfung ergänzt die Anmeldemöglichkeit des lokalen Kontos. Sie überträgt keine Gruppen- oder Abrechnungsdaten an Google.

### S1.3.8 Datenschutz und Sicherheitsgrenze

Google ist für die externe Authentifizierung verantwortlich. CampusSplit ist für die interne Zuordnung der authentifizierten Identität zu einem CampusSplit-Konto verantwortlich.

Für die Integration sollen nur die zur Identifikation erforderlichen OIDC-Daten verarbeitet werden.

Nicht an Google übertragen werden müssen insbesondere:

- Gruppeninhalte,
- Ausgaben,
- Kostenanteile,
- Salden,
- Rückzahlungen,
- Belege,
- Exportinhalte.

Client-Secrets und andere Zugangsdaten für die OIDC-Konfiguration dürfen nicht im Repository veröffentlicht werden.

### S1.3.9 Nichtverfügbarkeit und Fehlerfälle

Ist die Google-Anmeldung deaktiviert oder nicht korrekt konfiguriert, darf die Anwendung nicht so tun, als sei eine funktionierende Google-Anmeldung verfügbar.

Fehler bei der externen Authentifizierung oder bei der Zuordnung einer Google-Identität dürfen nicht zu einer unkontrollierten Kontoübernahme führen.

Insbesondere werden folgende Situationen als Fehler behandelt:

- E-Mail-Adresse fehlt,
- E-Mail-Adresse ist nicht verifiziert,
- externe Identität kann keinem zulässigen Konto zugeordnet werden,
- Google-Subject-ID ist bereits mit einem anderen Konto verknüpft,
- erforderliche Google-Konfiguration fehlt oder ist ungültig,
- der externe Authentifizierungsvorgang schlägt fehl.

Die lokale CampusSplit-Fachlogik und die fachlichen Daten bleiben von einer Störung des Google-Nachbarsystems getrennt.

---

## S1.4 Übergreifende Regeln für Nachbarsysteme

| ID | Regel |
|---|---|
| NB-R01 | Externe Systeme dürfen nur für den jeweils dokumentierten Zweck verwendet werden. |
| NB-R02 | CampusSplit darf externe Antworten nicht ungeprüft als fachlich gültig übernehmen. |
| NB-R03 | Fehler eines Nachbarsystems dürfen nicht zur Erzeugung erfundener fachlicher Daten führen. |
| NB-R04 | Zugangsdaten und Secrets externer Systeme dürfen nicht im Repository veröffentlicht werden. |
| NB-R05 | Es werden nur die für den jeweiligen Integrationszweck erforderlichen Daten an das Nachbarsystem übertragen. |
| NB-R06 | Fachliche CampusSplit-Daten bleiben unter Kontrolle der CampusSplit-Anwendung. |
| NB-R07 | Eine Störung eines Nachbarsystems muss als kontrollierter Fehler behandelt werden und darf nicht zu einer unkontrollierten Konto- oder Datenänderung führen. |
| NB-R08 | Die konkrete technische Anbindung externer Systeme wird über dafür vorgesehene Integrations- beziehungsweise Konfigurationskomponenten gekapselt. |

---

## S1.5 Datenflüsse

### S1.5.1 Wechselkursabfrage

Der vereinfachte Datenfluss lautet:

1. CampusSplit erkennt, dass für eine Ausgabe eine Währungsumrechnung erforderlich ist.
2. Die Fachlogik fordert über `CurrencyRatePort` einen Kurs für Ausgangswährung, Zielwährung und Datum an.
3. `FrankfurterCurrencyRateClient` sendet die HTTP-Anfrage an den externen Wechselkursdienst.
4. Der externe Dienst liefert Kursinformationen zurück.
5. CampusSplit validiert die Antwort.
6. Ein gültiger Kurs wird als `RateQuote` an die Fachlogik zurückgegeben.
7. CampusSplit verwendet die Kursinformation für die eigene Abrechnung.

### S1.5.2 Google-Anmeldung

Der vereinfachte Datenfluss lautet:

1. Der Benutzer startet die Google-Anmeldung in CampusSplit.
2. CampusSplit leitet zum Google-Identitätsanbieter weiter.
3. Google authentifiziert den Benutzer.
4. Google leitet den Benutzer über den konfigurierten OAuth2-/OIDC-Callback zurück.
5. CampusSplit verarbeitet die OIDC-Identität.
6. Verifizierte E-Mail-Adresse und stabile Subject-ID werden geprüft.
7. CampusSplit ordnet die Identität einem zulässigen lokalen Konto zu.
8. Die weitere Autorisierung und Fachverarbeitung erfolgt innerhalb von CampusSplit.

### S1.5.3 Google-Kontoverknüpfung

Der vereinfachte Datenfluss lautet:

1. Ein bereits angemeldeter Benutzer startet die Verknüpfung seines lokalen Kontos mit Google.
2. CampusSplit startet einen dafür vorgesehenen Google-OIDC-Ablauf.
3. Nach erfolgreicher Google-Authentifizierung prüft CampusSplit die externe Identität.
4. CampusSplit prüft, ob die Google-Subject-ID bereits anderweitig verwendet wird.
5. Bei konfliktfreier Zuordnung wird die Google-Identität dem lokalen Konto zugeordnet.
6. Gruppen- und Abrechnungsdaten bleiben unverändert bei CampusSplit.

---

## S1.6 Nicht als Nachbarsystem behandelt

Folgende Bestandteile sind keine externen Nachbarsysteme im Sinne dieses Bausteins:

- die CampusSplit-Datenbank,
- Spring Security,
- Thymeleaf,
- PDF- und ZIP-Bibliotheken,
- der Webbrowser des Benutzers,
- interne Services und Repositories der CampusSplit-Anwendung.

Sie sind entweder Teil der eigenen technischen Lösung oder Ausführungsumgebung und werden daher in anderen Spezifikations- beziehungsweise Architekturbausteinen behandelt.

---

## S1.7 Querverweise

Für die Beschreibung der Nachbarsysteme sind insbesondere folgende Dokumente relevant:

- [B1 – Dialogspezifikation](B1_Dialogspezifikation.md) – Benutzeroberflächen für Anmeldung, Profil, Gruppen und Ausgaben.
- [B3 – Druck- und Exportausgaben](B3_Druckausgaben.md) – Ausgabe der von CampusSplit selbst berechneten Abrechnungsdaten.
- [D1 – Datenmodell](D1_Datenmodell.md) – persistierte CampusSplit-Daten und Beziehungen.
- [D2 – Datentypenverzeichnis](D2_Datentypenverzeichnis.md) – fachliche Datentypen, insbesondere Geldbeträge und Währungen.
- [F2 – Anwendungsfälle](F2-anwendungsfälle.md) – Abläufe aus Benutzersicht.
- [F3 – Anwendungsfunktionen](F3-anwendungsfunktionen.md) – fachliche Funktionen der Anwendung.
- [N1 – Nichtfunktionale Anforderungen](N1_Nichtfunktionale Anforderungen.md) – Qualitäts- und Sicherheitsanforderungen.
- [N2 – Querschnittskonzepte](N2_Querschnittskonzepte.md) – übergreifende Konzepte wie Authentifizierung, Fehlerbehandlung und Währungen.
- [A03 – Kontext und Abgrenzung](../Architektur/A03-context-and-scope.md) – Systemkontext und externe Kommunikationspartner.
- [A08 – Querschnittliche Konzepte](../Architektur/A08-cross-cutting-concepts.md) – technische Sicherheits- und Integrationskonzepte.

Die konkreten Abschnittsanker müssen den tatsächlich vorhandenen Überschriften der referenzierten Dokumente entsprechen. Dieser Baustein führt keine neuen Use-Case-IDs ein, die in F2 nicht vorhanden sind.

---

## Eingesetzte KI-Werkzeuge

Claude (Anthropic) und ChatGPT (OpenAI) wurden unterstützend für Formulierungen, Strukturierung und die Prüfung von Querverweisen verwendet.

Die fachlichen Inhalte wurden anschließend mit dem aktuellen Implementierungsstand und den vorhandenen Spezifikationsbausteinen abgeglichen.
