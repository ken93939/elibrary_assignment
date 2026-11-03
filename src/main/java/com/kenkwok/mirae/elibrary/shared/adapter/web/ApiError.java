package com.kenkwok.mirae.elibrary.shared.adapter.web;

public enum ApiError {
  SERVICE_UNAVAILABLE("A required dependency is temporarily unavailable."),
  SERVICE_BUSY("The operation is busy; retry shortly."),
  INTERNAL_ERROR("The request could not be completed."),
  INVALID_REQUEST("One or more request fields are invalid."),
  NOT_FOUND("The request could not be completed."),
  METHOD_NOT_ALLOWED("The request could not be completed."),
  NOT_ACCEPTABLE("The request could not be completed."),
  UNSUPPORTED_MEDIA_TYPE("The request could not be completed."),
  UNAUTHENTICATED("Supply valid HTTP Basic credentials."),
  ACCESS_DENIED("Access denied.");

  private final String message;

  ApiError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
