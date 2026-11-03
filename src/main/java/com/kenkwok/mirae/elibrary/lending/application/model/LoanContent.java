package com.kenkwok.mirae.elibrary.lending.application.model;

import com.kenkwok.mirae.elibrary.content.domain.ContentType;
import java.util.UUID;

public record LoanContent(UUID id, ContentType contentType, String title) {}
