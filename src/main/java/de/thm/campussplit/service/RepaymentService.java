package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.persistence.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepaymentService {
  @PersistenceContext private EntityManager em;
  private final GroupService groups;
  private final ExpenseService expenses;
  private final RepaymentRepository payments;

  public RepaymentService(
      GroupService groups, ExpenseService expenses, RepaymentRepository payments) {
    this.groups = groups;
    this.expenses = expenses;
    this.payments = payments;
  }

  @Transactional
  public void record(
      Long groupId, Long senderId, Long recipientId, BigDecimal amount, String actor) {
    record(groupId, senderId, recipientId, amount, actor, java.util.UUID.randomUUID().toString());
  }

  @Transactional
  public void record(
      Long groupId,
      Long senderId,
      Long recipientId,
      BigDecimal amount,
      String actor,
      String requestId) {
    var member = groups.requireMember(groupId, actor);
    if (member.getRole() != Role.ADMIN
        && !member.getUser().getId().equals(senderId)
        && !member.getUser().getId().equals(recipientId))
      throw new AccessDeniedException(
          "Nur Beteiligte oder Gruppenadministratoren dürfen den Ausgleich bestätigen.");
    em.lock(member.getGroup(), LockModeType.PESSIMISTIC_WRITE);
    try {
      java.util.UUID.fromString(requestId);
    } catch (IllegalArgumentException | NullPointerException ex) {
      throw new BusinessException("Bitte die Seite neu laden.");
    }
    if (payments.existsByRequestId(requestId))
      throw new BusinessException(
          "Diese Zahlung wurde bereits erfasst. Bitte die aktuellen Salden prüfen.");
    if (amount == null || amount.signum() <= 0 || amount.scale() > 2 || amount.precision() > 15)
      throw new BusinessException("Bitte einen gültigen Betrag angeben.");
    var summary = expenses.summary(groupId, actor, null, null);
    boolean valid =
        summary.settlements().stream()
            .anyMatch(
                s ->
                    s.fromId().equals(senderId)
                        && s.toId().equals(recipientId)
                        && s.amount().compareTo(amount) >= 0);
    if (!valid)
      throw new BusinessException(
          "Dieser Vorschlag ist nicht mehr offen. Bitte die aktuellen Salden prüfen.");
    var byId = new java.util.HashMap<Long, User>();
    summary.members().forEach(m -> byId.put(m.getUser().getId(), m.getUser()));
    var p = new Repayment();
    p.setRequestId(requestId);
    p.setGroup(member.getGroup());
    p.setSender(byId.get(senderId));
    p.setRecipient(byId.get(recipientId));
    p.setRecordedBy(member.getUser());
    p.setAmount(amount);
    p.setPaymentDate(LocalDate.now());
    payments.saveAndFlush(p);
  }

  @Transactional
  public void cancel(Long groupId, Long paymentId, String reason, String actor) {
    var member = groups.requireMember(groupId, actor);
    em.lock(member.getGroup(), LockModeType.PESSIMISTIC_WRITE);
    var payment =
        payments
            .findById(paymentId)
            .filter(p -> p.getGroup().getId().equals(groupId))
            .orElseThrow(() -> new BusinessException("Rückzahlung nicht gefunden."));
    if (member.getRole() != Role.ADMIN
        && !member.getUser().getId().equals(payment.getSender().getId())
        && !member.getUser().getId().equals(payment.getRecipient().getId()))
      throw new AccessDeniedException(
          "Nur Beteiligte oder Gruppenadministratoren dürfen stornieren.");
    if (payment.isCancelled())
      throw new BusinessException("Diese Rückzahlung wurde bereits storniert.");
    if (reason == null || reason.isBlank() || reason.strip().length() > 250)
      throw new BusinessException("Bitte einen Stornogrund mit 1 bis 250 Zeichen angeben.");
    payment.setCancelledAt(java.time.Instant.now());
    payment.setCancelledBy(member.getUser());
    payment.setCancellationReason(reason.strip());
    payments.saveAndFlush(payment);
  }
}
