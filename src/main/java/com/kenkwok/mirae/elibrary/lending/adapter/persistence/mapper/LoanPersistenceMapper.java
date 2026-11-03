package com.kenkwok.mirae.elibrary.lending.adapter.persistence.mapper;

import com.kenkwok.mirae.elibrary.content.adapter.persistence.entity.DigitalContentEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.LoanEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.projection.LoanProjection;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanContent;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanSummary;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;

public final class LoanPersistenceMapper {

  private LoanPersistenceMapper() {}

  public static Loan toDomain(LoanEntity loan) {
    return new Loan(
        loan.getId(),
        loan.getContentId(),
        loan.getBorrowerSubject(),
        loan.getBorrowedAt(),
        loan.getDueAt(),
        loan.getReturnedAt());
  }

  public static Loan toDomain(LoanProjection loan) {
    return new Loan(
        loan.getId(),
        loan.getContentId(),
        loan.getBorrowerSubject(),
        loan.getBorrowedAt(),
        loan.getDueAt(),
        loan.getReturnedAt());
  }

  public static LoanEntity toEntity(Loan loan) {
    return new LoanEntity(
        loan.id(),
        loan.contentId(),
        loan.borrowerSubject(),
        loan.borrowedAt(),
        loan.dueAt(),
        loan.returnedAt());
  }

  public static LoanSummary toModel(LoanEntity loan) {
    DigitalContentEntity content = loan.getContent();
    return new LoanSummary(
        toDomain(loan),
        new LoanContent(content.getId(), content.getContentType(), content.getTitle()));
  }
}
