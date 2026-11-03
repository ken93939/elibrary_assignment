package com.kenkwok.mirae.elibrary.lending.domain;

public class LendingException extends RuntimeException {

  private final LendingError error;

  public LendingException(LendingError error) {
    super(error.getMessage());
    this.error = error;
  }

  public LendingError getError() {
    return error;
  }
}
