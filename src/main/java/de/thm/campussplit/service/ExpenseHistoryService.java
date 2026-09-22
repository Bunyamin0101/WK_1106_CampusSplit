package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.persistence.ExpenseChangeRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class ExpenseHistoryService {
  private final ExpenseChangeRepository changes;
  private final GroupService groups;

  public ExpenseHistoryService(ExpenseChangeRepository changes, GroupService groups) {
    this.changes = changes;
    this.groups = groups;
  }

  @Transactional(readOnly = true)
  public List<ExpenseChange> list(Long groupId, String actor) {
    groups.requireMember(groupId, actor);
    return changes.findByGroupIdOrderByIdDesc(groupId);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void record(Expense e, User actor, String details) {
    changes.save(new ExpenseChange(e.getGroup().getId(), e.getId(), actor, details));
  }

  public Map<String, String> snapshot(Expense e) {
    var values = new LinkedHashMap<String, String>();
    values.put("Beschreibung", e.getDescription());
    values.put(
        "Betrag",
        e.getOriginalAmount().setScale(2).toPlainString() + " " + e.getOriginalCurrency());
    values.put("Bezahlt von", e.getPaidBy().getName() + " (#" + e.getPaidBy().getId() + ")");
    values.put("Datum", e.getExpenseDate().toString());
    values.put("Kategorie", e.getCategory() == null ? "Keine" : e.getCategory().getName());
    values.put("Aufteilung", e.getSplitMethod().name());
    values.put(
        "Beteiligte und Kostenanteile",
        e.getShares().stream()
            .sorted(Comparator.comparing(s -> s.getUser().getId()))
            .map(
                s ->
                    s.getUser().getName()
                        + " (#"
                        + s.getUser().getId()
                        + "): "
                        + s.getShareAmount().setScale(2).toPlainString()
                        + " "
                        + e.getGroup().getCurrency())
            .collect(Collectors.joining(", ")));
    return values;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void changed(Expense e, User actor, Map<String, String> before) {
    var after = snapshot(e);
    var lines = new ArrayList<String>();
    after.forEach(
        (key, value) -> {
          if (!Objects.equals(before.get(key), value))
            lines.add(key + ": " + before.getOrDefault(key, "—") + " → " + value);
        });
    if (!lines.isEmpty())
      record(
          e,
          actor,
          (before.isEmpty() ? "Ausgabe erstellt" : "Ausgabe geändert")
              + "\n"
              + String.join("\n", lines));
  }
}
