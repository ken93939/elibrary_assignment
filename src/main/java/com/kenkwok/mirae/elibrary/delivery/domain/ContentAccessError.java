package com.kenkwok.mirae.elibrary.delivery.domain;

public enum ContentAccessError {
  LOAN_NOT_FOUND("The requested loan was not found."),
  LOAN_NOT_ACTIVE("This loan has been returned and cannot grant new access links."),
  SERVICE_UNAVAILABLE("Content storage is temporarily unavailable.");

  private final String message;

  ContentAccessError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
