package com.kenkwok.mirae.elibrary.delivery.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kenkwok.mirae.elibrary.delivery.adapter.persistence.repository.ContentFileRepository;
import com.kenkwok.mirae.elibrary.delivery.adapter.web.dto.ContentAccessLinkResponse;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.LoanEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.BorrowRequestRepository;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.LoanRepository;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanResponse;
import com.kenkwok.mirae.elibrary.support.EmbeddedPostgresConfiguration;
import com.kenkwok.mirae.elibrary.support.S3MockConfiguration;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Import({EmbeddedPostgresConfiguration.class, S3MockConfiguration.class})
class ContentAccessControllerIntegrationTests {

  private static final UUID CONTENT_ID = UUID.fromString("10000000-0000-4000-8000-000000000001");
  private static final String ACCESS_PATH = "/api/v1/loans/{id}/access-links";

  @Autowired private MockMvc mockMvc;
  @Autowired private BorrowRequestRepository borrowRequestRepository;
  @Autowired private LoanRepository loanRepository;
  @Autowired private ContentFileRepository contentFileRepository;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void clearLoans() {
    borrowRequestRepository.deleteAllInBatch();
    loanRepository.deleteAllInBatch();
  }

  @Test
  void givenActiveLoan_whenRequestingAccessLink_thenDownloadPrimaryContent() throws Exception {
    // GIVEN
    LoanResponse loan = borrowContent();
    Instant beforeRequest = Instant.now();

    // WHEN THEN
    var result = requestAccessLink(loan.id()).andExpect(status().isOk()).andReturn();
    var link =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), ContentAccessLinkResponse.class);

    // THEN
    assertThat(link.method()).isEqualTo("GET");
    assertThat(link.expiresAt())
        .isBetween(beforeRequest.plusSeconds(59), Instant.now().plusSeconds(61));
    assertThat(link.url().getRawQuery()).contains("X-Amz-Expires=60", "X-Amz-Signature=");
    try (var client = HttpClient.newHttpClient()) {

      // WHEN
      var response =
          client.send(
              HttpRequest.newBuilder(link.url()).timeout(Duration.ofSeconds(5)).GET().build(),
              HttpResponse.BodyHandlers.ofByteArray());

      // THEN
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body())
          .isEqualTo(
              new ClassPathResource("content/books/building-apis.txt").getContentAsByteArray());
    }

    // WHEN THEN
    requestAccessLink(loan.id()).andExpect(status().isOk());

    // THEN
    assertThat(loanRepository.count()).isEqualTo(1);
  }

  @Test
  void givenReturnedLoan_whenRequestingAccessLink_thenReturnUnprocessableContent()
      throws Exception {
    // GIVEN
    LoanResponse loan = borrowContent();
    mockMvc
        .perform(
            post("/api/v1/loans/{id}/return", loan.id())
                .with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk());

    // WHEN THEN
    requestAccessLink(loan.id())
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("LOAN_NOT_ACTIVE"));
  }

  @Test
  void givenUnauthorizedOrInvalidRequest_whenRequestingAccessLink_thenRejectRequest()
      throws Exception {
    // GIVEN
    LoanResponse loan = borrowContent();

    // WHEN THEN
    mockMvc
        .perform(post(ACCESS_PATH, loan.id()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));

    // WHEN THEN
    mockMvc
        .perform(post(ACCESS_PATH, loan.id()).with(httpBasic("testUser2", "testUser2-password")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("LOAN_NOT_FOUND"));

    // WHEN THEN
    requestAccessLink(UUID.randomUUID())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("LOAN_NOT_FOUND"));

    // WHEN THEN
    mockMvc
        .perform(post(ACCESS_PATH, "invalid-id").with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
  }

  @Test
  void givenOverdueUnreturnedLoan_whenRequestingAccessLink_thenReturnAccessLink() throws Exception {
    // GIVEN
    Instant borrowedAt = Instant.now().minus(Duration.ofDays(15));
    LoanEntity loan =
        loanRepository.saveAndFlush(
            new LoanEntity(
                UUID.randomUUID(),
                CONTENT_ID,
                "testUser1",
                borrowedAt,
                borrowedAt.plus(Duration.ofDays(14)),
                null));

    // WHEN THEN
    requestAccessLink(loan.getId()).andExpect(status().isOk());
  }

  @Test
  void givenActiveLoanWithoutPrimaryFile_whenRequestingAccessLink_thenReturnInternalError()
      throws Exception {
    // GIVEN
    LoanResponse loan = borrowContent();
    var file = contentFileRepository.findById(CONTENT_ID).orElseThrow();
    contentFileRepository.deleteById(CONTENT_ID);
    try {

      // WHEN THEN
      requestAccessLink(loan.id())
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    } finally {
      contentFileRepository.saveAndFlush(file);
    }
  }

  private LoanResponse borrowContent() throws Exception {
    var result =
        mockMvc
            .perform(
                post("/api/v1/loans")
                    .with(httpBasic("testUser1", "testUser1-password"))
                    .header("Idempotency-Key", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"contentId\":\"" + CONTENT_ID + "\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper.readValue(result.getResponse().getContentAsString(), LoanResponse.class);
  }

  private ResultActions requestAccessLink(UUID loanId) throws Exception {
    return mockMvc.perform(
        post(ACCESS_PATH, loanId).with(httpBasic("testUser1", "testUser1-password")));
  }
}
