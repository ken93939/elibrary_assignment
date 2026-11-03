package com.kenkwok.mirae.elibrary.content.adapter.web;

import com.kenkwok.mirae.elibrary.content.adapter.web.dto.AvailabilityResponse;
import com.kenkwok.mirae.elibrary.content.adapter.web.dto.BookDetailResponse;
import com.kenkwok.mirae.elibrary.content.adapter.web.dto.BookPageResponse;
import com.kenkwok.mirae.elibrary.content.application.BookService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public BookPageResponse listBooks(
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return BookResponseMapper.toDto(bookService.listBooks(page, size));
  }

  @GetMapping("/{bookId}")
  public BookDetailResponse getBook(@PathVariable UUID bookId) {
    return BookResponseMapper.toDto(bookService.getBook(bookId));
  }

  @GetMapping("/{bookId}/availability")
  public AvailabilityResponse getBookAvailability(@PathVariable UUID bookId) {
    return BookResponseMapper.toDto(bookService.getBookAvailability(bookId));
  }
}
