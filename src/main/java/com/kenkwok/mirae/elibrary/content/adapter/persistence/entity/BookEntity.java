package com.kenkwok.mirae.elibrary.content.adapter.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
public class BookEntity {

  @Id
  @Column(name = "content_id")
  private UUID id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "content_id")
  private DigitalContentEntity content;

  @ElementCollection
  @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "content_id"))
  @OrderColumn(name = "author_order")
  @Column(name = "name", nullable = false, length = 200)
  private List<String> authors = new ArrayList<>();

  @Column(length = 13, unique = true)
  private String isbn13;

  @Column(length = 200)
  private String publisher;

  private LocalDate publishedOn;
}
