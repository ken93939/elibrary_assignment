package com.kenkwok.mirae.elibrary.delivery.adapter.web.dto;

import java.net.URI;
import java.time.Instant;

public record ContentAccessLinkResponse(String method, URI url, Instant expiresAt) {}
