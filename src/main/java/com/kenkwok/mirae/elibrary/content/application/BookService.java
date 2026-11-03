package com.kenkwok.mirae.elibrary.content.application;

import com.kenkwok.mirae.elibrary.content.application.model.BookPage;
import com.kenkwok.mirae.elibrary.content.application.port.BookQueryPort;
import com.kenkwok.mirae.elibrary.content.domain.Book;
import com.kenkwok.mirae.elibrary.inventory.application.port.InventoryQueryPort;
import com.kenkwok.mirae.elibrary.inventory.domain.Availability;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

  private final BookQueryPort bookQueryPort;
  private final InventoryQueryPort inventoryQueryPort;

  public BookService(BookQueryPort bookQueryPort, InventoryQueryPort inventoryQueryPort) {
    this.bookQueryPort = bookQueryPort;
    this.inventoryQueryPort = inventoryQueryPort;
  }

  public BookPage listBooks(int page, int size) {
    return bookQueryPort.findBooks(page, size);
  }

  public Book getBook(UUID bookId) {
    return bookQueryPort.findBook(bookId).orElseThrow(BookNotFoundException::new);
  }

  public Availability getBookAvailability(UUID bookId) {
    if (!bookQueryPort.existsBook(bookId)) {
      throw new BookNotFoundException();
    }
    return inventoryQueryPort.getAvailability(bookId);
  }
}
