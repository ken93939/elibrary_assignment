package com.kenkwok.mirae.elibrary.delivery.domain;

import java.util.UUID;

public record LoanAccess(UUID contentId, boolean active) {}
