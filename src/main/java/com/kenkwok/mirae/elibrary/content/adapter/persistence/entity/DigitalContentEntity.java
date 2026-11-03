package com.kenkwok.mirae.elibrary.content.adapter.persistence.entity;

import com.kenkwok.mirae.elibrary.content.domain.ContentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "digital_contents")
@Getter
@NoArgsConstructor
public class DigitalContentEntity {

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ContentType contentType;

  @Column(nullable = false, length = 300)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(length = 35)
  private String languageTag;

  @Column(nullable = false)
  private Instant createdAt;
}
