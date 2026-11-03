package com.kenkwok.mirae.elibrary.content.adapter.web.dto;

import com.kenkwok.mirae.elibrary.content.domain.ContentType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookDetailResponse(
    UUID id,
    ContentType contentType,
    String title,
    List<String> authors,
    String description,
    String languageTag,
    String isbn13,
    String publisher,
    LocalDate publishedOn) {}
