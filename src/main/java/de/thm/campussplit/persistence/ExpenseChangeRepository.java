package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.ExpenseChange;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseChangeRepository extends JpaRepository<ExpenseChange, Long> {
  List<ExpenseChange> findByGroupIdOrderByIdDesc(Long groupId);
}
