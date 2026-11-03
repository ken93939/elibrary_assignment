package com.kenkwok.mirae.elibrary;

import com.kenkwok.mirae.elibrary.support.EmbeddedPostgresConfiguration;
import com.kenkwok.mirae.elibrary.support.S3MockConfiguration;
import org.springframework.boot.SpringApplication;

public class TestElibraryApplication {

  public static void main(String[] args) {
    SpringApplication.from(ElibraryApplication::main)
        .with(EmbeddedPostgresConfiguration.class, S3MockConfiguration.class)
        .run(args);
  }
}
