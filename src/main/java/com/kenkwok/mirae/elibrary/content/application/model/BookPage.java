package com.kenkwok.mirae.elibrary.content.application.model;

import com.kenkwok.mirae.elibrary.content.domain.Book;
import java.util.List;

public record BookPage(List<Book> items, int page, int size, long totalItems, int totalPages) {

  public BookPage {
    items = List.copyOf(items);
  }
}
