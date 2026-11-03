package com.kenkwok.mirae.elibrary.inventory.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_inventory")
@Getter
@NoArgsConstructor
public class InventoryEntity {

  @Id private UUID contentId;

  @Column(nullable = false)
  private int totalLicenses;
}
