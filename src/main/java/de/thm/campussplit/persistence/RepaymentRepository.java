package de.thm.campussplit.persistence;

import de.thm.campussplit.domain.Repayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
  boolean existsByRequestId(String requestId);

  List<Repayment> findByGroupIdOrderByPaymentDateDescIdDesc(Long groupId);
}
