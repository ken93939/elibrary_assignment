package com.kenkwok.mirae.elibrary.delivery.adapter.persistence;

import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.mapper.ContentFilePersistenceMapper;
import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.repository.ContentFileRepository;
import com.kenkwok.mirae.elibrary.delivery.application.port.ContentFilePort;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class ContentFilePersistenceAdapter implements ContentFilePort {

  private final ContentFileRepository contentFileRepository;

  public ContentFilePersistenceAdapter(ContentFileRepository contentFileRepository) {
    this.contentFileRepository = contentFileRepository;
  }

  @Override
  public Optional<ContentFile> findPrimaryFile(UUID contentId) {
    return contentFileRepository.findById(contentId).map(ContentFilePersistenceMapper::toDomain);
  }
}
