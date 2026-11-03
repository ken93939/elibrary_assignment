package com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "borrow_requests")
@Getter
@NoArgsConstructor
public class BorrowRequestEntity {

  @EmbeddedId private BorrowRequestId id;

  @Column(nullable = false, updatable = false)
  private UUID contentId;

  @Column(unique = true)
  private UUID loanId;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public void completeBorrowRequest(UUID loanId) {
    this.loanId = loanId;
  }
}
