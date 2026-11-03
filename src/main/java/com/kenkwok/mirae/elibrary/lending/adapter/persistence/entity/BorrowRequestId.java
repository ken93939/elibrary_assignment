package com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record BorrowRequestId(
    @Column(nullable = false, columnDefinition = "text") String borrowerSubject,
    @Column(nullable = false, length = 128) String idempotencyKey)
    implements Serializable {}
