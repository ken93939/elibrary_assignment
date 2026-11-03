package com.kenkwok.mirae.elibrary.content.application;

public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException() {
    super("The requested book was not found.");
  }
}
