package com.kenkwok.mirae.elibrary.delivery.domain;

import java.net.URI;
import java.time.Instant;

public record ContentAccessLink(URI url, Instant expiresAt) {}
