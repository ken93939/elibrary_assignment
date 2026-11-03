package com.kenkwok.mirae.elibrary.lending.domain;

public enum LendingError {
  CONTENT_NOT_FOUND("The requested digital content was not found."),
  LOAN_NOT_FOUND("The requested loan was not found."),
  CONTENT_UNAVAILABLE("All lending licences for this content are in use."),
  CONTENT_ALREADY_BORROWED("You already have an active loan for this content."),
  IDEMPOTENCY_KEY_REUSED("This key was already used successfully with different input."),
  INVALID_IDEMPOTENCY_KEY(
      "Supply a nonblank Idempotency-Key of 1 to 128 visible ASCII characters.");

  private final String message;

  LendingError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
