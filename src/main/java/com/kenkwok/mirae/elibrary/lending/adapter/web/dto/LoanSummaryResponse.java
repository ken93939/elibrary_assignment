package com.kenkwok.mirae.elibrary.lending.adapter.web.dto;

import com.kenkwok.mirae.elibrary.lending.domain.LoanStatus;
import java.time.Instant;
import java.util.UUID;

public record LoanSummaryResponse(
    UUID id,
    UUID contentId,
    Instant borrowedAt,
    Instant dueAt,
    Instant returnedAt,
    LoanStatus status,
    LoanContentResponse content) {}
