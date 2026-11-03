package com.kenkwok.mirae.elibrary.support;

import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.repository.ContentFileRepository;
import com.kenkwok.mirae.elibrary.delivery.config.S3StorageProperties;
import java.io.IOException;
import java.time.Duration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration(proxyBeanMethods = false)
public class S3MockConfiguration {

  @Bean
  public S3MockServer startS3Mock() throws IOException, InterruptedException {
    return new S3MockServer();
  }

  @Bean
  @Primary
  public S3StorageProperties createS3MockProperties(S3MockServer server) {
    return new S3StorageProperties("us-east-1", "elibrary-content", server.getEndpoint(), true);
  }

  @Bean
  @Primary
  public AwsCredentialsProvider createS3MockCredentials() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create("demo-key", "demo-secret"));
  }

  @Bean
  public S3Client createS3MockClient(
      S3StorageProperties properties, AwsCredentialsProvider credentials) {
    return S3Client.builder()
        .endpointOverride(properties.endpoint())
        .region(Region.of(properties.region()))
        .credentialsProvider(credentials)
        .forcePathStyle(true)
        .httpClientBuilder(
            UrlConnectionHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(2))
                .socketTimeout(Duration.ofSeconds(2)))
        .overrideConfiguration(
            builder ->
                builder
                    .apiCallTimeout(Duration.ofSeconds(10))
                    .apiCallAttemptTimeout(Duration.ofSeconds(5)))
        .build();
  }

  @Bean
  public ApplicationRunner seedContentFiles(
      S3Client client,
      S3StorageProperties properties,
      ContentFileRepository contentFileRepository) {
    return args -> {
      client.createBucket(builder -> builder.bucket(properties.bucket()));
      for (var file : contentFileRepository.findAll()) {
        String key = file.getObjectKey();
        byte[] content = new ClassPathResource("content/" + key).getContentAsByteArray();
        client.putObject(
            builder -> builder.bucket(properties.bucket()).key(key).contentType("text/plain"),
            RequestBody.fromBytes(content));
      }
    };
  }
}
