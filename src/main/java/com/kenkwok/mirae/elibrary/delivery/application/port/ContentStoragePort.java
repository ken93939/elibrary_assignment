package com.kenkwok.mirae.elibrary.delivery.application.port;

import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessLink;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import java.time.Duration;

public interface ContentStoragePort {

  ContentAccessLink createDownloadLink(ContentFile file, Duration validity);
}
