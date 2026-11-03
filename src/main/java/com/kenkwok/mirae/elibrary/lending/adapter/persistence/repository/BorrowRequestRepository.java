package com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository;

import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.BorrowRequestEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.BorrowRequestId;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BorrowRequestRepository
    extends JpaRepository<BorrowRequestEntity, BorrowRequestId> {

  @Modifying
  @Query(
      value =
          """
      INSERT INTO borrow_requests (borrower_subject, idempotency_key, content_id)
      VALUES (:borrowerSubject, :key, :contentId)
      ON CONFLICT (borrower_subject, idempotency_key) DO NOTHING
      """,
      nativeQuery = true)
  int reserveBorrowRequest(String borrowerSubject, String key, UUID contentId);
}
