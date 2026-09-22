package de.thm.campussplit.service;

import de.thm.campussplit.domain.*;
import de.thm.campussplit.persistence.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class ReceiptService {
  @PersistenceContext private EntityManager em;
  private final ExpenseHistoryService history;
  private final GroupService groups;
  private final ExpenseRepository expenses;
  private final ReceiptRepository receipts;

  public ReceiptService(
      GroupService groups,
      ExpenseRepository expenses,
      ReceiptRepository receipts,
      ExpenseHistoryService history) {
    this.history = history;
    this.groups = groups;
    this.expenses = expenses;
    this.receipts = receipts;
  }

  public List<ReceiptRepository.Info> list(Long groupId, String actor) {
    groups.requireMember(groupId, actor);
    return receipts.listForGroup(groupId);
  }

  private Expense expense(Long groupId, Long expenseId, String actor) {
    groups.requireMember(groupId, actor);
    return expenses
        .findByIdAndGroupId(expenseId, groupId)
        .orElseThrow(() -> new BusinessException("Ausgabe nicht gefunden."));
  }

  public Receipt get(Long groupId, Long expenseId, Long id, String actor) {
    expense(groupId, expenseId, actor);
    return receipts
        .findByIdAndExpenseId(id, expenseId)
        .orElseThrow(() -> new BusinessException("Beleg nicht gefunden."));
  }

  @Transactional
  public void upload(Long groupId, Long expenseId, MultipartFile file, String actor) {
    var e = expense(groupId, expenseId, actor);
    em.lock(e, LockModeType.PESSIMISTIC_WRITE);
    if (file.isEmpty() || file.getSize() > 10 * 1024 * 1024)
      throw new BusinessException("Bitte eine Datei bis 10 MB auswählen.");
    if (receipts.countByExpenseId(expenseId) >= 5)
      throw new BusinessException("Pro Ausgabe sind höchstens fünf Belege möglich.");
    try {
      byte[] data = file.getBytes();
      String type = detect(data);
      String name =
          Optional.ofNullable(file.getOriginalFilename()).orElse("Beleg").replace('\\', '/');
      name = name.substring(name.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "");
      if (name.isBlank()) name = "Beleg";
      if (name.length() > 180) name = name.substring(0, 180);
      var r = new Receipt();
      r.setExpense(e);
      r.setFilename(name);
      r.setMediaType(type);
      r.setData(data);
      r.setUploadedBy(groups.currentUser(actor));
      receipts.saveAndFlush(r);
      history.record(e, r.getUploadedBy(), "Beleg hinzugefügt: " + name + " (#" + r.getId() + ")");
    } catch (IOException ex) {
      throw new BusinessException(
          "Die Datei konnte nicht gelesen werden. Bitte ein gültiges JPG, PNG oder PDF wählen.");
    }
  }

  private String detect(byte[] data) throws IOException {
    if (data.length >= 5
        && new String(data, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-")) {
      try (var pdf = Loader.loadPDF(data)) {
        if (pdf.isEncrypted() || pdf.getNumberOfPages() == 0) throw new IOException();
      }
      return "application/pdf";
    }
    try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
      var readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new IOException();
      var reader = readers.next();
      try {
        reader.setInput(input);
        String format = reader.getFormatName();
        if (!format.equalsIgnoreCase("JPEG") && !format.equalsIgnoreCase("PNG"))
          throw new IOException();
        if ((long) reader.getWidth(0) * reader.getHeight(0) > 40000000L) throw new IOException();
        if (reader.read(0) == null) throw new IOException();
        return format.equalsIgnoreCase("PNG") ? "image/png" : "image/jpeg";
      } finally {
        reader.dispose();
      }
    }
  }

  @Transactional
  public void delete(Long groupId, Long expenseId, Long id, String actor) {
    var e = expense(groupId, expenseId, actor);
    em.lock(e, LockModeType.PESSIMISTIC_WRITE);
    var receipt = get(groupId, expenseId, id, actor);
    history.record(
        e,
        groups.currentUser(actor),
        "Beleg entfernt: " + receipt.getFilename() + " (#" + id + ")");
    receipts.delete(receipt);
  }
}
