package com.kenkwok.mirae.elibrary.delivery.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_files")
@Getter
@NoArgsConstructor
public class ContentFileEntity {

  @Id private UUID contentId;

  @Column(nullable = false, columnDefinition = "text")
  private String objectKey;
}
