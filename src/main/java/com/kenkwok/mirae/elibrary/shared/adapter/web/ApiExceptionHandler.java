package com.kenkwok.mirae.elibrary.shared.adapter.web;

import com.kenkwok.mirae.elibrary.shared.adapter.web.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ErrorResponse> handleDataAccessFailure(DataAccessException exception) {
    logger.error("Failed to access library data", exception);
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(toDto(ApiError.SERVICE_UNAVAILABLE));
  }

  @ExceptionHandler({
    PessimisticLockingFailureException.class,
    QueryTimeoutException.class,
    TransactionTimedOutException.class
  })
  public ResponseEntity<ErrorResponse> handleBusyOperation(RuntimeException exception) {
    logger.warn("Library operation timed out", exception);
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(toDto(ApiError.SERVICE_BUSY));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedFailure(Exception exception) {
    logger.error("Failed to process request", exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(toDto(ApiError.INTERNAL_ERROR));
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception exception,
      Object body,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ApiError error =
        switch (status.value()) {
          case 400 -> ApiError.INVALID_REQUEST;
          case 404 -> ApiError.NOT_FOUND;
          case 405 -> ApiError.METHOD_NOT_ALLOWED;
          case 406 -> ApiError.NOT_ACCEPTABLE;
          case 415 -> ApiError.UNSUPPORTED_MEDIA_TYPE;
          default -> ApiError.INTERNAL_ERROR;
        };
    return new ResponseEntity<>(toDto(error), headers, status);
  }

  private ErrorResponse toDto(ApiError error) {
    return new ErrorResponse(error.getMessage(), error.name());
  }
}
