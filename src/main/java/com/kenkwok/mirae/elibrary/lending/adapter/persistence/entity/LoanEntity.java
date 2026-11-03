package com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity;

import com.kenkwok.mirae.elibrary.content.adapter.persistence.entity.DigitalContentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor
public class LoanEntity {

  @Id private UUID id;

  @Column(name = "content_id", nullable = false, updatable = false)
  private UUID contentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id", insertable = false, updatable = false)
  private DigitalContentEntity content;

  @Column(nullable = false, updatable = false, columnDefinition = "text")
  private String borrowerSubject;

  @Column(nullable = false, updatable = false)
  private Instant borrowedAt;

  @Column(nullable = false, updatable = false)
  private Instant dueAt;

  private Instant returnedAt;

  public LoanEntity(
      UUID id,
      UUID contentId,
      String borrowerSubject,
      Instant borrowedAt,
      Instant dueAt,
      Instant returnedAt) {
    this.id = id;
    this.contentId = contentId;
    this.borrowerSubject = borrowerSubject;
    this.borrowedAt = borrowedAt;
    this.dueAt = dueAt;
    this.returnedAt = returnedAt;
  }

  public void recordReturn(Instant returnTime) {
    returnedAt = returnTime;
  }
}
