package com.kenkwok.mirae.elibrary.lending.adapter.web;

import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanContentResponse;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanPageResponse;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanResponse;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanSummaryResponse;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanPage;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanSummary;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;

public final class LoanResponseMapper {

  private LoanResponseMapper() {}

  public static LoanResponse toDto(Loan loan) {
    return new LoanResponse(
        loan.id(),
        loan.contentId(),
        loan.borrowedAt(),
        loan.dueAt(),
        loan.returnedAt(),
        loan.getStatus());
  }

  public static LoanPageResponse toDto(LoanPage page) {
    return new LoanPageResponse(
        page.items().stream().map(LoanResponseMapper::toDto).toList(),
        page.page(),
        page.size(),
        page.totalItems(),
        page.totalPages());
  }

  private static LoanSummaryResponse toDto(LoanSummary summary) {
    Loan loan = summary.loan();
    var content = summary.content();
    return new LoanSummaryResponse(
        loan.id(),
        loan.contentId(),
        loan.borrowedAt(),
        loan.dueAt(),
        loan.returnedAt(),
        loan.getStatus(),
        new LoanContentResponse(content.id(), content.contentType(), content.title()));
  }
}
