package com.kenkwok.mirae.elibrary.inventory.application.port;

import com.kenkwok.mirae.elibrary.inventory.domain.Inventory;
import java.util.Optional;
import java.util.UUID;

public interface InventoryPort {

  Optional<Inventory> lockInventory(UUID contentId);

  boolean existsContent(UUID contentId);
}
