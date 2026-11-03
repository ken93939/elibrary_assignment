package com.kenkwok.mirae.elibrary.delivery.application.port;

import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import java.util.Optional;
import java.util.UUID;

public interface ContentFilePort {

  Optional<ContentFile> findPrimaryFile(UUID contentId);
}
