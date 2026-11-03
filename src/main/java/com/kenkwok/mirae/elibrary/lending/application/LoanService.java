package com.kenkwok.mirae.elibrary.lending.application;

import com.kenkwok.mirae.elibrary.inventory.application.port.InventoryPort;
import com.kenkwok.mirae.elibrary.inventory.domain.Inventory;
import com.kenkwok.mirae.elibrary.lending.application.model.BorrowRequest;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanPage;
import com.kenkwok.mirae.elibrary.lending.application.port.BorrowRequestPort;
import com.kenkwok.mirae.elibrary.lending.application.port.LoanPort;
import com.kenkwok.mirae.elibrary.lending.domain.LendingError;
import com.kenkwok.mirae.elibrary.lending.domain.LendingException;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoanService {

  private final LoanPort loanPort;
  private final InventoryPort inventoryPort;
  private final BorrowRequestPort borrowRequestPort;
  private final Clock clock;

  public LoanService(
      LoanPort loanPort,
      InventoryPort inventoryPort,
      BorrowRequestPort borrowRequestPort,
      Clock clock) {
    this.loanPort = loanPort;
    this.inventoryPort = inventoryPort;
    this.borrowRequestPort = borrowRequestPort;
    this.clock = clock;
  }

  @Transactional(timeout = 5)
  public Loan borrowContent(String borrowerSubject, UUID contentId, String key) {
    validateIdempotencyKey(key);
    if (!borrowRequestPort.reserveBorrowRequest(borrowerSubject, key, contentId)) {
      return replayBorrowRequest(borrowerSubject, contentId, key);
    }
    Inventory inventory =
        inventoryPort
            .lockInventory(contentId)
            .orElseThrow(
                () ->
                    new LendingException(
                        inventoryPort.existsContent(contentId)
                            ? LendingError.CONTENT_UNAVAILABLE
                            : LendingError.CONTENT_NOT_FOUND));
    if (loanPort.existsActiveLoan(contentId, borrowerSubject)) {
      throw new LendingException(LendingError.CONTENT_ALREADY_BORROWED);
    }
    if (loanPort.countActiveLoans(contentId) >= inventory.totalLicenses()) {
      throw new LendingException(LendingError.CONTENT_UNAVAILABLE);
    }
    Loan loan = loanPort.createLoan(Loan.createLoan(contentId, borrowerSubject, getCurrentTime()));
    borrowRequestPort.completeBorrowRequest(borrowerSubject, key, loan.id());
    return loan;
  }

  public Loan getLoan(String borrowerSubject, UUID loanId) {
    return loanPort
        .findLoan(loanId, borrowerSubject)
        .orElseThrow(() -> new LendingException(LendingError.LOAN_NOT_FOUND));
  }

  public LoanPage getActiveLoans(String borrowerSubject, int page, int size) {
    return loanPort.findActiveLoans(borrowerSubject, page, size);
  }

  @Transactional(timeout = 5)
  public Loan returnLoan(String borrowerSubject, UUID loanId) {
    Loan loan = getLoan(borrowerSubject, loanId);
    if (loan.returnedAt() != null) {
      return loan;
    }
    inventoryPort
        .lockInventory(loan.contentId())
        .orElseThrow(() -> new IllegalStateException("Loan inventory is missing"));
    Loan currentLoan = getLoan(borrowerSubject, loanId);
    return currentLoan.returnedAt() != null
        ? currentLoan
        : loanPort.returnLoan(currentLoan.returnLoan(getCurrentTime()));
  }

  private Loan replayBorrowRequest(String borrowerSubject, UUID contentId, String key) {
    BorrowRequest request =
        borrowRequestPort
            .findBorrowRequest(borrowerSubject, key)
            .orElseThrow(() -> new IllegalStateException("Borrow request is missing"));
    if (!request.contentId().equals(contentId)) {
      throw new LendingException(LendingError.IDEMPOTENCY_KEY_REUSED);
    }
    if (request.loanId() == null) {
      throw new IllegalStateException("Borrow request is incomplete");
    }
    return getLoan(borrowerSubject, request.loanId()).getCreationSnapshot();
  }

  private void validateIdempotencyKey(String key) {
    if (key == null || !key.matches("[!-~]{1,128}")) {
      throw new LendingException(LendingError.INVALID_IDEMPOTENCY_KEY);
    }
  }

  private Instant getCurrentTime() {
    return clock.instant().truncatedTo(ChronoUnit.MICROS);
  }
}
