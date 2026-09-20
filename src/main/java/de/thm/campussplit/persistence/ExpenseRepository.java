package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Expense;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findByGroupIdOrderByExpenseDateDescIdDesc(Long groupId);

  Optional<Expense> findByIdAndGroupId(Long id, Long groupId);
}
