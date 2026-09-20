package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Membership;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
  List<Membership> findByUserIdOrderById(Long userId);

  List<Membership> findByGroupIdOrderByUserId(Long groupId);

  Optional<Membership> findByGroupIdAndUserId(Long groupId, Long userId);
}
