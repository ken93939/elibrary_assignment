package com.kenkwok.mirae.elibrary.delivery.adapter.persistence.mapper;

import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.entity.ContentFileEntity;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;

public final class ContentFilePersistenceMapper {

  private ContentFilePersistenceMapper() {}

  public static ContentFile toDomain(ContentFileEntity entity) {
    return new ContentFile(entity.getContentId(), entity.getObjectKey());
  }
}
