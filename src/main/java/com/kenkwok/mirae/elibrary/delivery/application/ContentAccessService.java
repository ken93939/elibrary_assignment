package com.kenkwok.mirae.elibrary.delivery.application;

import com.kenkwok.mirae.elibrary.delivery.application.port.ContentFilePort;
import com.kenkwok.mirae.elibrary.delivery.application.port.ContentStoragePort;
import com.kenkwok.mirae.elibrary.delivery.application.port.LoanAccessPort;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessError;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessException;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessLink;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import com.kenkwok.mirae.elibrary.delivery.domain.LoanAccess;
import java.time.Duration;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ContentAccessService {

  private static final Duration LINK_VALIDITY = Duration.ofSeconds(60);

  private final LoanAccessPort loanAccessPort;
  private final ContentFilePort contentFilePort;
  private final ContentStoragePort contentStoragePort;

  public ContentAccessService(
      LoanAccessPort loanAccessPort,
      ContentFilePort contentFilePort,
      ContentStoragePort contentStoragePort) {
    this.loanAccessPort = loanAccessPort;
    this.contentFilePort = contentFilePort;
    this.contentStoragePort = contentStoragePort;
  }

  public ContentAccessLink getContentAccessLink(String borrowerSubject, UUID loanId) {
    LoanAccess access =
        loanAccessPort
            .findLoanAccess(borrowerSubject, loanId)
            .orElseThrow(() -> new ContentAccessException(ContentAccessError.LOAN_NOT_FOUND));
    if (!access.active()) {
      throw new ContentAccessException(ContentAccessError.LOAN_NOT_ACTIVE);
    }
    ContentFile file =
        contentFilePort
            .findPrimaryFile(access.contentId())
            .orElseThrow(() -> new IllegalStateException("Primary content file is missing"));
    return contentStoragePort.createDownloadLink(file, LINK_VALIDITY);
  }
}
