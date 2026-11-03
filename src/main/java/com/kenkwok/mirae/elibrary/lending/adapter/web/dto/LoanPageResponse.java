package com.kenkwok.mirae.elibrary.lending.adapter.web.dto;

import java.util.List;

public record LoanPageResponse(
    List<LoanSummaryResponse> items, int page, int size, long totalItems, int totalPages) {}
