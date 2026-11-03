package com.kenkwok.mirae.elibrary.inventory.application.port;

import com.kenkwok.mirae.elibrary.inventory.domain.Availability;
import java.util.UUID;

public interface InventoryQueryPort {

  Availability getAvailability(UUID contentId);
}
