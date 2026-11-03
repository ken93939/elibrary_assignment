package com.kenkwok.mirae.elibrary.lending.application.model;

import com.kenkwok.mirae.elibrary.lending.domain.Loan;

public record LoanSummary(Loan loan, LoanContent content) {}
