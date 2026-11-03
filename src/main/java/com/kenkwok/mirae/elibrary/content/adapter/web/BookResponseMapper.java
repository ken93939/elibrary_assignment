package com.kenkwok.mirae.elibrary.content.adapter.web;

import com.kenkwok.mirae.elibrary.content.adapter.web.dto.AvailabilityResponse;
import com.kenkwok.mirae.elibrary.content.adapter.web.dto.BookDetailResponse;
import com.kenkwok.mirae.elibrary.content.adapter.web.dto.BookPageResponse;
import com.kenkwok.mirae.elibrary.content.adapter.web.dto.BookSummaryResponse;
import com.kenkwok.mirae.elibrary.content.application.model.BookPage;
import com.kenkwok.mirae.elibrary.content.domain.Book;
import com.kenkwok.mirae.elibrary.inventory.domain.Availability;

public final class BookResponseMapper {

  private BookResponseMapper() {}

  public static BookPageResponse toDto(BookPage page) {
    return new BookPageResponse(
        page.items().stream().map(BookResponseMapper::toSummaryDto).toList(),
        page.page(),
        page.size(),
        page.totalItems(),
        page.totalPages());
  }

  public static BookDetailResponse toDto(Book book) {
    return new BookDetailResponse(
        book.id(),
        book.contentType(),
        book.title(),
        book.authors(),
        book.description(),
        book.languageTag(),
        book.isbn13(),
        book.publisher(),
        book.publishedOn());
  }

  public static AvailabilityResponse toDto(Availability availability) {
    return new AvailabilityResponse(availability.totalLicenses(), availability.availableLicenses());
  }

  private static BookSummaryResponse toSummaryDto(Book book) {
    return new BookSummaryResponse(book.id(), book.contentType(), book.title(), book.authors());
  }
}
