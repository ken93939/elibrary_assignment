package com.kenkwok.mirae.elibrary.lending.adapter.web;

import com.kenkwok.mirae.elibrary.lending.domain.LendingException;
import com.kenkwok.mirae.elibrary.shared.adapter.web.dto.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoanExceptionHandler {

  @ExceptionHandler(LendingException.class)
  public ResponseEntity<ErrorResponse> handleLendingFailure(LendingException exception) {
    HttpStatus status =
        switch (exception.getError()) {
          case CONTENT_NOT_FOUND, LOAN_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case INVALID_IDEMPOTENCY_KEY -> HttpStatus.BAD_REQUEST;
          case IDEMPOTENCY_KEY_REUSED -> HttpStatus.CONFLICT;
          case CONTENT_UNAVAILABLE, CONTENT_ALREADY_BORROWED -> HttpStatus.UNPROCESSABLE_CONTENT;
        };
    return ResponseEntity.status(status)
        .body(new ErrorResponse(exception.getMessage(), exception.getError().name()));
  }
}
