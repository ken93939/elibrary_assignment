package com.kenkwok.mirae.elibrary.security;

import com.kenkwok.mirae.elibrary.shared.adapter.web.ApiError;
import com.kenkwok.mirae.elibrary.shared.adapter.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain configureSecurity(HttpSecurity http, ObjectMapper objectMapper)
      throws Exception {
    AuthenticationEntryPoint entryPoint =
        (request, response, exception) ->
            writeErrorResponse(
                response, objectMapper, HttpStatus.UNAUTHORIZED, ApiError.UNAUTHENTICATED);
    return http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/books",
                        "/api/v1/books/*",
                        "/api/v1/books/*/availability")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(basic -> basic.authenticationEntryPoint(entryPoint))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(entryPoint)
                    .accessDeniedHandler(
                        (request, response, exception) ->
                            writeErrorResponse(
                                response,
                                objectMapper,
                                HttpStatus.FORBIDDEN,
                                ApiError.ACCESS_DENIED)))
        .build();
  }

  @Bean
  public PasswordEncoder createPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private void writeErrorResponse(
      HttpServletResponse response, ObjectMapper objectMapper, HttpStatus status, ApiError error)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getOutputStream(), new ErrorResponse(error.getMessage(), error.name()));
  }
}
