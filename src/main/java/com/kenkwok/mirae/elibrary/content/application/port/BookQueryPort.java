package com.kenkwok.mirae.elibrary.content.application.port;

import com.kenkwok.mirae.elibrary.content.application.model.BookPage;
import com.kenkwok.mirae.elibrary.content.domain.Book;
import java.util.Optional;
import java.util.UUID;

public interface BookQueryPort {

  BookPage findBooks(int page, int size);

  Optional<Book> findBook(UUID bookId);

  boolean existsBook(UUID bookId);
}
