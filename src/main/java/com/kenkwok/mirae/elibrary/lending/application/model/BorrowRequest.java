package com.kenkwok.mirae.elibrary.lending.application.model;

import java.util.UUID;

public record BorrowRequest(UUID contentId, UUID loanId) {}
