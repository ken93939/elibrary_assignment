package com.kenkwok.mirae.elibrary.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration(proxyBeanMethods = false)
@Profile("demo")
public class DemoUserConfiguration {

  @Bean
  public UserDetailsService createDemoUsers(
      PasswordEncoder passwordEncoder,
      @Value("${elibrary.demo.test-user1-password}") String testUser1Password,
      @Value("${elibrary.demo.test-user2-password}") String testUser2Password) {
    return new InMemoryUserDetailsManager(
        User.withUsername("testUser1")
            .password(passwordEncoder.encode(testUser1Password))
            .roles("MEMBER")
            .build(),
        User.withUsername("testUser2")
            .password(passwordEncoder.encode(testUser2Password))
            .roles("MEMBER")
            .build());
  }
}
