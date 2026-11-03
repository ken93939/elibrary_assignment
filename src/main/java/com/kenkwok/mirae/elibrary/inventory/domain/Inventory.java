package com.kenkwok.mirae.elibrary.inventory.domain;

import java.util.UUID;

public record Inventory(UUID contentId, int totalLicenses) {}
