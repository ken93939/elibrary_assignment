package com.kenkwok.mirae.elibrary.delivery.domain;

public class ContentAccessException extends RuntimeException {

  private final ContentAccessError error;

  public ContentAccessException(ContentAccessError error) {
    this(error, null);
  }

  public ContentAccessException(ContentAccessError error, Throwable cause) {
    super(error.getMessage(), cause);
    this.error = error;
  }

  public ContentAccessError getError() {
    return error;
  }
}
