package com.kenkwok.mirae.elibrary.content.adapter.persistence;

import com.kenkwok.mirae.elibrary.content.adapter.persistence.entity.BookEntity;
import com.kenkwok.mirae.elibrary.content.adapter.persistence.entity.DigitalContentEntity;
import com.kenkwok.mirae.elibrary.content.adapter.persistence.repository.BookRepository;
import com.kenkwok.mirae.elibrary.content.application.model.BookPage;
import com.kenkwok.mirae.elibrary.content.application.port.BookQueryPort;
import com.kenkwok.mirae.elibrary.content.domain.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class BookPersistenceAdapter implements BookQueryPort {

  private final BookRepository bookRepository;

  public BookPersistenceAdapter(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Override
  public BookPage findBooks(int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by("content.title", "id"));
    Page<BookEntity> books = findBookPage(pageable);
    List<Book> items = books.stream().map(this::toDomain).toList();
    return new BookPage(items, page, size, books.getTotalElements(), books.getTotalPages());
  }

  @Override
  public Optional<Book> findBook(UUID bookId) {
    return bookRepository.findById(bookId).map(this::toDomain);
  }

  @Override
  public boolean existsBook(UUID bookId) {
    return bookRepository.existsById(bookId);
  }

  private Page<BookEntity> findBookPage(Pageable pageable) {
    if (pageable.getOffset() > Integer.MAX_VALUE) {
      long totalItems = bookRepository.count();
      if (pageable.getOffset() >= totalItems) {
        return new PageImpl<>(List.of(), pageable, totalItems);
      }
    }
    return bookRepository.findAll(pageable);
  }

  private Book toDomain(BookEntity book) {
    DigitalContentEntity content = book.getContent();
    return new Book(
        book.getId(),
        content.getContentType(),
        content.getTitle(),
        book.getAuthors(),
        content.getDescription(),
        content.getLanguageTag(),
        book.getIsbn13(),
        book.getPublisher(),
        book.getPublishedOn());
  }
}
