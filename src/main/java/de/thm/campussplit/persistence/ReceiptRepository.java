package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Receipt;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
  interface Info {
    Long getId();

    Long getExpenseId();

    String getFilename();
  }

  @Query(
      "select r.id as id, r.expense.id as expenseId, r.filename as filename from Receipt r where"
          + " r.expense.group.id = :groupId order by r.id")
  List<Info> listForGroup(Long groupId);

  Optional<Receipt> findByIdAndExpenseId(Long id, Long expenseId);

  long countByExpenseId(Long expenseId);
}
