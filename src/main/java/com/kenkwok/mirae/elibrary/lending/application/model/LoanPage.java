package com.kenkwok.mirae.elibrary.lending.application.model;

import java.util.List;

public record LoanPage(
    List<LoanSummary> items, int page, int size, long totalItems, int totalPages) {

  public LoanPage {
    items = List.copyOf(items);
  }
}
