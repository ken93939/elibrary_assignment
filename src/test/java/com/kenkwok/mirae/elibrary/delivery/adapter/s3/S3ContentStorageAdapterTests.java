package com.kenkwok.mirae.elibrary.delivery.adapter.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kenkwok.mirae.elibrary.delivery.config.S3StorageProperties;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessError;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentAccessException;
import com.kenkwok.mirae.elibrary.delivery.domain.ContentFile;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class S3ContentStorageAdapterTests {

  @Test
  void givenUnavailableCredentials_whenCreatingDownloadLink_thenRaiseServiceUnavailable() {
    // GIVEN
    try (var presigner =
        S3Presigner.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(
                () -> {
                  throw SdkClientException.create("Credential resolution failed");
                })
            .build()) {
      var properties = new S3StorageProperties("us-east-1", "elibrary-content", null, false);
      var adapter = new S3ContentStorageAdapter(presigner, properties);
      var file = new ContentFile(UUID.randomUUID(), "books/example.txt");

      // WHEN THEN
      assertThatThrownBy(() -> adapter.createDownloadLink(file, Duration.ofSeconds(60)))
          .isInstanceOfSatisfying(
              ContentAccessException.class,
              exception ->
                  assertThat(exception.getError())
                      .isEqualTo(ContentAccessError.SERVICE_UNAVAILABLE));
    }
  }
}
