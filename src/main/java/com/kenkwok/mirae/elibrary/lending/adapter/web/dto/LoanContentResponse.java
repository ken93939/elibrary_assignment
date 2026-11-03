package com.kenkwok.mirae.elibrary.lending.adapter.web.dto;

import com.kenkwok.mirae.elibrary.content.domain.ContentType;
import java.util.UUID;

public record LoanContentResponse(UUID id, ContentType contentType, String title) {}
