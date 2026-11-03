package com.kenkwok.mirae.elibrary.delivery.adapter.lending;

import com.kenkwok.mirae.elibrary.delivery.application.port.LoanAccessPort;
import com.kenkwok.mirae.elibrary.delivery.domain.LoanAccess;
import com.kenkwok.mirae.elibrary.lending.application.LoanService;
import com.kenkwok.mirae.elibrary.lending.domain.LendingError;
import com.kenkwok.mirae.elibrary.lending.domain.LendingException;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;
import com.kenkwok.mirae.elibrary.lending.domain.LoanStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LoanAccessAdapter implements LoanAccessPort {

  private final LoanService loanService;

  public LoanAccessAdapter(LoanService loanService) {
    this.loanService = loanService;
  }

  @Override
  public Optional<LoanAccess> findLoanAccess(String borrowerSubject, UUID loanId) {
    try {
      Loan loan = loanService.getLoan(borrowerSubject, loanId);
      return Optional.of(new LoanAccess(loan.contentId(), loan.getStatus() == LoanStatus.ACTIVE));
    } catch (LendingException exception) {
      if (exception.getError() == LendingError.LOAN_NOT_FOUND) {
        return Optional.empty();
      }
      throw exception;
    }
  }
}
