package com.kenkwok.mirae.elibrary.lending.adapter.persistence;

import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.BorrowRequestEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.BorrowRequestId;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.BorrowRequestRepository;
import com.kenkwok.mirae.elibrary.lending.application.model.BorrowRequest;
import com.kenkwok.mirae.elibrary.lending.application.port.BorrowRequestPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class BorrowRequestPersistenceAdapter implements BorrowRequestPort {

  private final BorrowRequestRepository borrowRequestRepository;

  public BorrowRequestPersistenceAdapter(BorrowRequestRepository borrowRequestRepository) {
    this.borrowRequestRepository = borrowRequestRepository;
  }

  @Override
  public boolean reserveBorrowRequest(String borrowerSubject, String key, UUID contentId) {
    return borrowRequestRepository.reserveBorrowRequest(borrowerSubject, key, contentId) == 1;
  }

  @Override
  public Optional<BorrowRequest> findBorrowRequest(String borrowerSubject, String key) {
    return borrowRequestRepository
        .findById(new BorrowRequestId(borrowerSubject, key))
        .map(row -> new BorrowRequest(row.getContentId(), row.getLoanId()));
  }

  @Override
  public void completeBorrowRequest(String borrowerSubject, String key, UUID loanId) {
    BorrowRequestEntity request =
        borrowRequestRepository
            .findById(new BorrowRequestId(borrowerSubject, key))
            .orElseThrow(() -> new IllegalStateException("Borrow request is missing"));
    request.completeBorrowRequest(loanId);
  }
}
