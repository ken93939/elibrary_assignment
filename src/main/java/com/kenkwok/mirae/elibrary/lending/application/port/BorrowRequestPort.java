package com.kenkwok.mirae.elibrary.lending.application.port;

import com.kenkwok.mirae.elibrary.lending.application.model.BorrowRequest;
import java.util.Optional;
import java.util.UUID;

public interface BorrowRequestPort {

  boolean reserveBorrowRequest(String borrowerSubject, String key, UUID contentId);

  Optional<BorrowRequest> findBorrowRequest(String borrowerSubject, String key);

  void completeBorrowRequest(String borrowerSubject, String key, UUID loanId);
}
