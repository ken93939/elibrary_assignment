package com.kenkwok.mirae.elibrary.delivery.adapter.web;

import com.kenkwok.mirae.elibrary.delivery.adapter.web.dto.ContentAccessLinkResponse;
import com.kenkwok.mirae.elibrary.delivery.application.ContentAccessService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/access-links")
public class ContentAccessController {

  private final ContentAccessService contentAccessService;

  public ContentAccessController(ContentAccessService contentAccessService) {
    this.contentAccessService = contentAccessService;
  }

  @PostMapping
  public ContentAccessLinkResponse getContentAccessLink(
      Principal principal, @PathVariable UUID loanId) {
    return ContentAccessResponseMapper.toDto(
        contentAccessService.getContentAccessLink(principal.getName(), loanId));
  }
}
