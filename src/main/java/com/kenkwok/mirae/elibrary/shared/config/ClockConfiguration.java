package com.kenkwok.mirae.elibrary.shared.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ClockConfiguration {

  @Bean
  public Clock createClock() {
    return Clock.systemUTC();
  }
}
