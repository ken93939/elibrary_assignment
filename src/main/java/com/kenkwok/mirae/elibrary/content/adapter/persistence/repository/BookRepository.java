package com.kenkwok.mirae.elibrary.content.adapter.persistence.repository;

import com.kenkwok.mirae.elibrary.content.adapter.persistence.entity.BookEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

  @Override
  @EntityGraph(attributePaths = {"content", "authors"})
  Page<BookEntity> findAll(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = {"content", "authors"})
  Optional<BookEntity> findById(UUID bookId);
}
