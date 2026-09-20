package de.thm.campussplit.domain;

import de.thm.campussplit.service.BusinessException;
import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SplitService {
  public Map<Long, BigDecimal> calculate(
      BigDecimal total, List<Long> participants, SplitMethod method, Map<Long, BigDecimal> custom) {
    if (total == null || total.signum() <= 0 || total.scale() > 2 || total.precision() > 15)
      throw new BusinessException("Der Abrechnungsbetrag muss positiv und centgenau sein.");
    if (participants == null
        || participants.isEmpty()
        || participants.stream().anyMatch(Objects::isNull)
        || new HashSet<>(participants).size() != participants.size())
      throw new BusinessException(
          "Bitte mindestens ein Mitglied auswählen, ohne doppelte Einträge.");
    var ids = participants.stream().sorted().toList();
    var result = new LinkedHashMap<Long, BigDecimal>();
    if (method == SplitMethod.EQUAL) {
      long cents = total.movePointRight(2).longValueExact();
      for (int i = 0; i < ids.size(); i++)
        result.put(
            ids.get(i),
            BigDecimal.valueOf(cents / ids.size() + (i < cents % ids.size() ? 1 : 0), 2));
    } else if (method == SplitMethod.CUSTOM_AMOUNT) {
      for (Long id : ids) {
        BigDecimal value = custom == null ? null : custom.get(id);
        if (value == null || value.signum() < 0 || value.scale() > 2 || value.precision() > 15)
          throw new BusinessException(
              "Individuelle Anteile müssen vollständig, nicht negativ und centgenau sein.");
        result.put(id, value.setScale(2));
      }
      if (result.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(total) != 0)
        throw new BusinessException(
            "Die individuellen Anteile müssen zusammen den Abrechnungsbetrag in Gruppenwährung"
                + " ergeben.");
    } else throw new BusinessException("Bitte eine gültige Aufteilungsart wählen.");
    return result;
  }
}
