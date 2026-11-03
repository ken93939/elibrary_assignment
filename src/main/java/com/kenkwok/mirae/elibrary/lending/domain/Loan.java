package com.kenkwok.mirae.elibrary.lending.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record Loan(
    UUID id,
    UUID contentId,
    String borrowerSubject,
    Instant borrowedAt,
    Instant dueAt,
    Instant returnedAt) {

  public static Loan createLoan(UUID contentId, String borrowerSubject, Instant borrowedAt) {
    return new Loan(
        UUID.randomUUID(),
        contentId,
        borrowerSubject,
        borrowedAt,
        borrowedAt.plus(Duration.ofDays(14)),
        null);
  }

  public LoanStatus getStatus() {
    return returnedAt == null ? LoanStatus.ACTIVE : LoanStatus.RETURNED;
  }

  public Loan returnLoan(Instant returnTime) {
    return returnedAt == null
        ? new Loan(id, contentId, borrowerSubject, borrowedAt, dueAt, returnTime)
        : this;
  }

  public Loan getCreationSnapshot() {
    return new Loan(id, contentId, borrowerSubject, borrowedAt, dueAt, null);
  }
}
