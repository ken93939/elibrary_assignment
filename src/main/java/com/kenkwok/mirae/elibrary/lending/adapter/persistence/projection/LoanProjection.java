package com.kenkwok.mirae.elibrary.lending.adapter.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface LoanProjection {

  UUID getId();

  UUID getContentId();

  String getBorrowerSubject();

  Instant getBorrowedAt();

  Instant getDueAt();

  Instant getReturnedAt();
}
