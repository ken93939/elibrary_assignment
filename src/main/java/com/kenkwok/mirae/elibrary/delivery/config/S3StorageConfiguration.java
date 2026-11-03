package com.kenkwok.mirae.elibrary.delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration(proxyBeanMethods = false)
public class S3StorageConfiguration {

  @Bean
  public DefaultCredentialsProvider createCredentialsProvider() {
    return DefaultCredentialsProvider.builder().build();
  }

  @Bean
  public S3Presigner createS3Presigner(
      S3StorageProperties properties, AwsCredentialsProvider credentials) {
    var builder =
        S3Presigner.builder()
            .region(Region.of(properties.region()))
            .credentialsProvider(credentials)
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(properties.pathStyleAccess())
                    .checksumValidationEnabled(false)
                    .build());
    if (properties.endpoint() != null) {
      builder.endpointOverride(properties.endpoint());
    }
    return builder.build();
  }
}
