package com.kenkwok.mirae.elibrary.inventory.adapter.persistence;

import com.kenkwok.mirae.elibrary.inventory.adapter.persistence.projection.AvailabilityProjection;
import com.kenkwok.mirae.elibrary.inventory.adapter.persistence.repository.InventoryRepository;
import com.kenkwok.mirae.elibrary.inventory.application.port.InventoryPort;
import com.kenkwok.mirae.elibrary.inventory.application.port.InventoryQueryPort;
import com.kenkwok.mirae.elibrary.inventory.domain.Availability;
import com.kenkwok.mirae.elibrary.inventory.domain.Inventory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InventoryPersistenceAdapter implements InventoryPort, InventoryQueryPort {

  private final InventoryRepository inventoryRepository;

  public InventoryPersistenceAdapter(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public Optional<Inventory> lockInventory(UUID contentId) {
    return inventoryRepository
        .lockInventory(contentId)
        .map(row -> new Inventory(row.getContentId(), row.getTotalLicenses()));
  }

  @Override
  public boolean existsContent(UUID contentId) {
    return inventoryRepository.existsContent(contentId);
  }

  @Override
  @Transactional(readOnly = true)
  public Availability getAvailability(UUID contentId) {
    return inventoryRepository
        .findAvailability(contentId)
        .map(this::toDomain)
        .orElseGet(() -> new Availability(0, 0));
  }

  private Availability toDomain(AvailabilityProjection projection) {
    return new Availability(projection.getTotalLicenses(), projection.getAvailableLicenses());
  }
}
