package com.kenkwok.mirae.elibrary.lending.application.port;

import com.kenkwok.mirae.elibrary.lending.application.model.LoanPage;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;
import java.util.Optional;
import java.util.UUID;

public interface LoanPort {

  Loan createLoan(Loan loan);

  Loan returnLoan(Loan loan);

  Optional<Loan> findLoan(UUID loanId, String borrowerSubject);

  LoanPage findActiveLoans(String borrowerSubject, int page, int size);

  boolean existsActiveLoan(UUID contentId, String borrowerSubject);

  long countActiveLoans(UUID contentId);
}
