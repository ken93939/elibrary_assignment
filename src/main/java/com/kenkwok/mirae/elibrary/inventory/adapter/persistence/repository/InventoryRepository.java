package com.kenkwok.mirae.elibrary.inventory.adapter.persistence.repository;

import com.kenkwok.mirae.elibrary.inventory.adapter.persistence.entity.InventoryEntity;
import com.kenkwok.mirae.elibrary.inventory.adapter.persistence.projection.AvailabilityProjection;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select inventory from InventoryEntity inventory where inventory.contentId = :contentId")
  Optional<InventoryEntity> lockInventory(UUID contentId);

  @Query(
      "select count(content) > 0 from DigitalContentEntity content where content.id = :contentId")
  boolean existsContent(UUID contentId);

  @Query(
      value =
          """
          SELECT inventory.total_licenses AS "totalLicenses",
                 greatest(inventory.total_licenses - count(loan.id), 0) AS "availableLicenses"
          FROM content_inventory inventory
          LEFT JOIN loans loan
            ON loan.content_id = inventory.content_id AND loan.returned_at IS NULL
          WHERE inventory.content_id = :contentId
          GROUP BY inventory.content_id, inventory.total_licenses
          """,
      nativeQuery = true)
  Optional<AvailabilityProjection> findAvailability(UUID contentId);
}
