package com.kenkwok.mirae.elibrary.content.adapter.web.dto;

import java.util.List;

public record BookPageResponse(
    List<BookSummaryResponse> items, int page, int size, long totalItems, int totalPages) {}
