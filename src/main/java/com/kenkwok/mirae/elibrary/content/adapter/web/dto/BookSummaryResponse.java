package com.kenkwok.mirae.elibrary.content.adapter.web.dto;

import com.kenkwok.mirae.elibrary.content.domain.ContentType;
import java.util.List;
import java.util.UUID;

public record BookSummaryResponse(
    UUID id, ContentType contentType, String title, List<String> authors) {}
