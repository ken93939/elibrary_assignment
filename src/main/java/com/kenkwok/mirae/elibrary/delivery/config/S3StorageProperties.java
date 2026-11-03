package com.kenkwok.mirae.elibrary.delivery.config;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("elibrary.storage.s3")
@Validated
public record S3StorageProperties(
    @NotBlank String region, @NotBlank String bucket, URI endpoint, boolean pathStyleAccess) {}
