package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Category;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findAllByOrderByName();
}
