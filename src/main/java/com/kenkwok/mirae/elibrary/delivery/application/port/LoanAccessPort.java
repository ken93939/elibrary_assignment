package com.kenkwok.mirae.elibrary.delivery.application.port;

import com.kenkwok.mirae.elibrary.delivery.domain.LoanAccess;
import java.util.Optional;
import java.util.UUID;

public interface LoanAccessPort {

  Optional<LoanAccess> findLoanAccess(String borrowerSubject, UUID loanId);
}
