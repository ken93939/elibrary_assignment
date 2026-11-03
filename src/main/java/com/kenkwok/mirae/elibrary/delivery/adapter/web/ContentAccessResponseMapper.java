package com.kenkwok.mirae.elibrary.delivery.adapter.web;

import com.kenkwok.mirae.elibrary.delivery.adapter.web.dto.ContentAccessLinkResponse;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessLink;

public final class ContentAccessResponseMapper {

  private ContentAccessResponseMapper() {}

  public static ContentAccessLinkResponse toDto(ContentAccessLink link) {
    return new ContentAccessLinkResponse("GET", link.url(), link.expiresAt());
  }
}
