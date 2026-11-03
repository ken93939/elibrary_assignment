package com.kenkwok.mirae.elibrary.content.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Book(
    UUID id,
    ContentType contentType,
    String title,
    List<String> authors,
    String description,
    String languageTag,
    String isbn13,
    String publisher,
    LocalDate publishedOn) {

  public Book {
    authors = List.copyOf(authors);
  }
}
