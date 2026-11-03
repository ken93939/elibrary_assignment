package com.kenkwok.mirae.elibrary.support;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class EmbeddedPostgresConfiguration {

  @Bean
  public EmbeddedPostgres startPostgres() throws IOException {
    return EmbeddedPostgres.builder().setPort(0).setLocaleConfig("locale", "C").start();
  }

  @Bean
  public DataSource createDataSource(EmbeddedPostgres postgres) {
    return postgres.getPostgresDatabase();
  }
}
