package com.kenkwok.mirae.elibrary.delivery.adapter.s3;

import com.kenkwok.mirae.elibrary.delivery.application.port.ContentStoragePort;
import com.kenkwok.mirae.elibrary.delivery.config.S3StorageProperties;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessError;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessException;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessLink;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
public class S3ContentStorageAdapter implements ContentStoragePort {

  private static final Logger logger = LoggerFactory.getLogger(S3ContentStorageAdapter.class);

  private final S3Presigner presigner;
  private final S3StorageProperties properties;

  public S3ContentStorageAdapter(S3Presigner presigner, S3StorageProperties properties) {
    this.presigner = presigner;
    this.properties = properties;
  }

  @Override
  public ContentAccessLink createDownloadLink(ContentFile file, Duration validity) {
    try {
      var request =
          GetObjectPresignRequest.builder()
              .signatureDuration(validity)
              .getObjectRequest(
                  GetObjectRequest.builder()
                      .bucket(properties.bucket())
                      .key(file.objectKey())
                      .build())
              .build();
      var signedRequest = presigner.presignGetObject(request);
      return new ContentAccessLink(signedRequest.url().toURI(), signedRequest.expiration());
    } catch (SdkException exception) {
      logger.warn(
          "Failed to generate a content access link. {}", exception.getClass().getSimpleName());
      throw new ContentAccessException(ContentAccessError.SERVICE_UNAVAILABLE, exception);
    } catch (java.net.URISyntaxException exception) {
      throw new IllegalStateException("Content access URL is invalid", exception);
    }
  }
}
