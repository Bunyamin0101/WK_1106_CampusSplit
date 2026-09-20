package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Group;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {}
