package com.kenkwok.mirae.elibrary.lending.adapter.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BorrowContentRequest(@NotNull UUID contentId) {}
