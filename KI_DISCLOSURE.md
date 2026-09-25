# KI-Offenlegung – CampusSplit

Stand: 25. September 2026

Diese Offenlegung beschreibt den im Projekt nachvollziehbaren Einsatz von KI. Sie ist keine Erklärung, dass alle Inhalte eigenständig ohne KI erstellt oder von allen Teammitgliedern vollständig geprüft wurden. Zusätzliche Vorgaben des Moduls zur Kennzeichnung und Abgabe bleiben zu beachten.

## Eingesetzte Werkzeuge

- **OpenAI ChatGPT und Codex:** Unterstützung bei Entwicklung, Fehlerbehebung, Refactoring, Tests, Dokumentation sowie Bedienung von GitHub-, Railway- und Google-Konfigurationen. Codex führte dabei auch Dateiänderungen, Tests und Git-Befehle aus und unterstützte über Browserbedienung die Bereitstellung.
- **GitHub Copilot:** In [TEAMINFO.md](TEAMINFO.md) als Werkzeug zur Code-Vervollständigung angegeben. Einzelne Vorschläge, Nutzungszeiträume und die Zuordnung zu Teammitgliedern sind in der hier verfügbaren Dokumentation nicht vollständig erfasst.
- **Mermaid und PlantUML:** Werkzeuge zur Darstellung technischer Diagramme, selbst keine generativen KI-Systeme. Soweit Diagrammtexte mit KI erstellt oder überarbeitet wurden, fällt dies unter die Dokumentationsunterstützung.

Die genaue Modellversion und eine vollständige Aufstellung sämtlicher Sitzungen wurden nicht durchgängig festgehalten. Deshalb werden hier keine ungesicherten Modellbezeichnungen oder Prozentangaben zum KI-Anteil genannt.

## Umfang der Unterstützung

Die KI-Nutzung ging über Rechtschreibkorrekturen und einzelne Code-Vervollständigungen hinaus. Auf Grundlage von Anforderungen, Rückmeldungen, Screenshots und vorhandenen Projektdateien wurden auch konkrete Implementierungen und Dokumenttexte erzeugt und überarbeitet.

| Bereich | Art der KI-Unterstützung |
| --- | --- |
| Fachlogik und Persistenz | Unterstützung bei Entitäten, Repositorys, Kostenaufteilung, Rückzahlungen, Belegen und Änderungshistorie sowie bei Fehlerkorrekturen |
| Benutzeroberfläche | Überarbeitung von Thymeleaf-Templates und CSS, Formularen, Gruppenseiten, Aktivitäten und Navigation; Umsetzung wiederholter Gestaltungswünsche |
| Verständliche Darstellung | Lesbare Namen statt interner Kennungen, deutsche Datums- und Betragsdarstellung sowie verständliche Aktivitätsdetails |
| Abrechnungen | Überarbeitung von PDF- und CSV-Ausgaben, Tabellen, Salden, Rückzahlungen und Exportdarstellung |
| Anmeldung | Unterstützung bei vorhandener Google-OAuth2/OpenID-Connect-Integration, Kontoverknüpfung und Darstellung der Google-Option auf Login und Registrierung |
| Tests | Erstellung bzw. Anpassung und Ausführung automatisierter Tests sowie Unterstützung bei manuellen Browserprüfungen |
| Bereitstellung | Git-Commits und Pushes, Railway-Konfiguration, Google-Cloud-Konfiguration, Unterstützung bei Datenbanksicherung und Wiederherstellungstest |
| Dokumentation | Unterstützung bei Erläuterungen, Architekturideen, README und dieser KI-Offenlegung |

Die Tabelle beschreibt Unterstützungsbereiche, keine vollständige zeilengenaue Urheberschaft. Nicht jede Änderung im Repository stammt aus derselben KI-Sitzung. Auch spätere Änderungen anderer Teammitglieder können den beschriebenen Stand verändern.

## Vorgehensweise

Die Arbeit erfolgte iterativ: Anforderungen wurden beschrieben, Änderungen umgesetzt und anschließend anhand von Code, Tests oder der laufenden Oberfläche überprüft. Die auftraggebende Person gab Rückmeldungen und ließ Vorschläge teilweise erneut ändern oder zurücknehmen. Beispielsweise wurden die Position der Archivierungsfunktion, die Darstellung der Ausgaben und die Reihenfolge der Google-Anmeldeoption mehrfach angepasst.

KI-generierter Code wurde in das Projekt übernommen und in Git versioniert. Die Commit-Historie dokumentiert Änderungen, ist aber kein vollständiges Promptprotokoll und kein Nachweis einer ausschließlich menschlichen Urheberschaft. Eine Bitte, Code oder Oberfläche „menschlicher“ zu gestalten, ändert nichts an der hier offengelegten KI-Unterstützung.

## Prüfung und Grenzen

Automatisierte Tests wurden während der Bearbeitung ausgeführt. Dabei wurden unter anderem Kostenberechnungen, Webabläufe, Aktivitätsdarstellung und Exporte geprüft. Zusätzlich wurden online beispielhaft Registrierung und Anmeldung, Google-Verknüpfung, Ausgaben, individuelle Aufteilung, Rückzahlungen, Belege sowie Archivierungs- und Löschabläufe getestet.

Diese Prüfungen sind keine vollständige Sicherheits-, Datenschutz- oder Abnahmeprüfung. Erfolgreiche Tests belegen nur die jeweils geprüften Fälle und den damaligen Stand. Sie garantieren weder Fehlerfreiheit noch die Erfüllung aller Bewertungskriterien. Aussagen über eine vollständige persönliche Prüfung durch jedes Teammitglied können aus der dokumentierten KI-Arbeit nicht abgeleitet werden.

Vor der Abgabe muss die Projektgruppe insbesondere sicherstellen, dass sie die eingereichte Implementierung erklären kann, die Dokumentation mit dem Code übereinstimmt und die tatsächliche Nutzung weiterer KI-Werkzeuge oder Sitzungen in dieser Offenlegung ergänzt ist. Individuelle Eigenständigkeitserklärungen müssen die jeweiligen Personen selbst und wahrheitsgemäß abgeben.

## Umgang mit Daten und Zugangsdaten

Die KI-Unterstützung umfasste Zugriff auf Projektcode, Screenshots, Testdaten und autorisierte Browseransichten. Bei der Bereitstellung wurden auch Zugangskonfigurationen verarbeitet. Deshalb wird nicht behauptet, dass während der gesamten Zusammenarbeit ausschließlich öffentliche oder vollständig anonymisierte Informationen verwendet wurden.

Geheime Zugangsdaten werden nicht in dieser Offenlegung aufgeführt. Google-Clientschlüssel und produktive Datenbankzugänge gehören in die Laufzeitkonfiguration und nicht in das öffentliche Repository. Für Funktionstests wurden eigens angelegte Beispieldaten verwendet; das schließt die Sichtbarkeit von Kontoangaben während autorisierter Browserarbeit nicht aus.

## Nachvollziehbarkeit

Die Änderungen lassen sich über die Git-Historie, die Testdateien und die Projektdokumentation nachvollziehen. Diese Offenlegung wurde selbst mit KI-Unterstützung erstellt. Sie beschreibt den verfügbaren Kenntnisstand und ist von der Projektgruppe vor der Abgabe auf Vollständigkeit zu prüfen.
