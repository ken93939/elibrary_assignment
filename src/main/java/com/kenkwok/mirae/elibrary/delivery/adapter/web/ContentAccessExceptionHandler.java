package com.kenkwok.mirae.elibrary.delivery.adapter.web;

import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessException;
import com.kenkwok.mirae.elibrary.shared.adapter.web.dto.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContentAccessExceptionHandler {

  @ExceptionHandler(ContentAccessException.class)
  public ResponseEntity<ErrorResponse> handleContentAccessFailure(
      ContentAccessException exception) {
    HttpStatus status =
        switch (exception.getError()) {
          case LOAN_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case LOAN_NOT_ACTIVE -> HttpStatus.UNPROCESSABLE_CONTENT;
          case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    return ResponseEntity.status(status)
        .body(new ErrorResponse(exception.getMessage(), exception.getError().name()));
  }
}
