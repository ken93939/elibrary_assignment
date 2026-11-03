package com.kenkwok.mirae.elibrary.delivery.adapter.persistence.repository;

import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.entity.ContentFileEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentFileRepository extends JpaRepository<ContentFileEntity, UUID> {}
