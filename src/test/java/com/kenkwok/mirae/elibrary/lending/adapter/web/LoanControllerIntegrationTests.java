package com.kenkwok.mirae.elibrary.lending.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.BorrowRequestRepository;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.LoanRepository;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanResponse;
import com.kenkwok.mirae.elibrary.lending.domain.LoanStatus;
import com.kenkwok.mirae.elibrary.support.EmbeddedPostgresConfiguration;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Import(EmbeddedPostgresConfiguration.class)
class LoanControllerIntegrationTests {

  private static final String FIRST_BOOK_ID = "10000000-0000-4000-8000-000000000001";
  private static final String SECOND_BOOK_ID = "10000000-0000-4000-8000-000000000002";
  private static final String UNAVAILABLE_BOOK_ID = "10000000-0000-4000-8000-000000000003";

  @Autowired private MockMvc mockMvc;
  @Autowired private BorrowRequestRepository borrowRequestRepository;
  @Autowired private LoanRepository loanRepository;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void clearLoans() {
    borrowRequestRepository.deleteAllInBatch();
    loanRepository.deleteAllInBatch();
  }

  @Test
  void givenAvailableContent_whenBorrowingAndReturning_thenUpdateLoanAndAvailability()
      throws Exception {
    LoanResponse loan =
        readLoan(
            borrowContent("testUser1", FIRST_BOOK_ID, "borrow-book")
                .andExpect(status().isCreated())
                .andReturn());
    assertThat(loan.status()).isEqualTo(LoanStatus.ACTIVE);
    assertThat(loan.dueAt()).isEqualTo(loan.borrowedAt().plus(Duration.ofDays(14)));

    mockMvc
        .perform(
            get("/api/v1/loans")
                .with(httpBasic("testUser1", "testUser1-password"))
                .param("status", "ACTIVE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].id").value(loan.id().toString()))
        .andExpect(jsonPath("$.items[0].content.title").value("Building APIs"))
        .andExpect(jsonPath("$.totalItems").value(1));

    mockMvc
        .perform(
            get("/api/v1/loans/{id}", loan.id()).with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    mockMvc
        .perform(get("/api/v1/books/{id}/availability", FIRST_BOOK_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableLicenses").value(1));

    MvcResult returned =
        mockMvc
            .perform(
                post("/api/v1/loans/{id}/return", loan.id())
                    .with(httpBasic("testUser1", "testUser1-password")))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(readLoan(returned).returnedAt()).isNotNull();

    MvcResult repeated =
        mockMvc
            .perform(
                post("/api/v1/loans/{id}/return", loan.id())
                    .with(httpBasic("testUser1", "testUser1-password")))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(readLoan(repeated)).isEqualTo(readLoan(returned));

    mockMvc
        .perform(get("/api/v1/loans").with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)));

    mockMvc
        .perform(get("/api/v1/books/{id}/availability", FIRST_BOOK_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableLicenses").value(2));
  }

  @Test
  void givenReturnedLoan_whenReplayingBorrowRequest_thenReturnOriginalResponse() throws Exception {
    // GIVEN
    MvcResult first =
        borrowContent("testUser1", FIRST_BOOK_ID, "original-request")
            .andExpect(status().isCreated())
            .andReturn();
    LoanResponse loan = readLoan(first);
    mockMvc
        .perform(
            post("/api/v1/loans/{id}/return", loan.id())
                .with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk());

    // WHEN THEN
    MvcResult replay =
        borrowContent("testUser1", FIRST_BOOK_ID, "original-request")
            .andExpect(status().isCreated())
            .andReturn();

    // THEN
    assertThat(replay.getResponse().getContentAsString())
        .isEqualTo(first.getResponse().getContentAsString());
    assertThat(countLoans()).isEqualTo(1);

    // WHEN THEN
    mockMvc
        .perform(
            get("/api/v1/loans/{id}", loan.id()).with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("RETURNED"));

    // WHEN THEN
    LoanResponse newLoan =
        readLoan(
            borrowContent("testUser1", FIRST_BOOK_ID, "new-request")
                .andExpect(status().isCreated())
                .andReturn());

    // THEN
    assertThat(newLoan.id()).isNotEqualTo(loan.id());
    assertThat(countLoans()).isEqualTo(2);
  }

  @Test
  void givenOwnedAndOtherLoans_whenRequestingLargePage_thenReturnOwnedActiveTotals()
      throws Exception {
    // GIVEN
    LoanResponse returnedLoan =
        readLoan(
            borrowContent("testUser1", FIRST_BOOK_ID, "returned-request")
                .andExpect(status().isCreated())
                .andReturn());
    mockMvc
        .perform(
            post("/api/v1/loans/{id}/return", returnedLoan.id())
                .with(httpBasic("testUser1", "testUser1-password")))
        .andExpect(status().isOk());
    borrowContent("testUser1", FIRST_BOOK_ID, "active-request").andExpect(status().isCreated());
    borrowContent("testUser2", FIRST_BOOK_ID, "other-user-request").andExpect(status().isCreated());

    // WHEN THEN
    mockMvc
        .perform(
            get("/api/v1/loans")
                .with(httpBasic("testUser1", "testUser1-password"))
                .param("page", Integer.toString(Integer.MAX_VALUE))
                .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)))
        .andExpect(jsonPath("$.page").value(Integer.MAX_VALUE))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalItems").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));

    // WHEN THEN
    mockMvc
        .perform(
            get("/api/v1/loans")
                .with(httpBasic("testUser2", "testUser2-password"))
                .param("page", "1073741824")
                .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)))
        .andExpect(jsonPath("$.totalItems").value(1));
  }

  @Test
  void givenAnotherBorrowersLoan_whenListingReadingOrReturning_thenHideLoan() throws Exception {
    // GIVEN
    LoanResponse loan =
        readLoan(
            borrowContent("testUser1", FIRST_BOOK_ID, "testUser1-request")
                .andExpect(status().isCreated())
                .andReturn());

    // WHEN THEN
    mockMvc
        .perform(get("/api/v1/loans").with(httpBasic("testUser2", "testUser2-password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)));

    // WHEN THEN
    mockMvc
        .perform(
            get("/api/v1/loans/{id}", loan.id()).with(httpBasic("testUser2", "testUser2-password")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("LOAN_NOT_FOUND"));

    // WHEN THEN
    mockMvc
        .perform(
            post("/api/v1/loans/{id}/return", loan.id())
                .with(httpBasic("testUser2", "testUser2-password")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("LOAN_NOT_FOUND"));
  }

  @Test
  void givenUnavailableBorrowedOrMissingContent_whenBorrowing_thenRejectRequest() throws Exception {
    // GIVEN
    borrowContent("testUser1", FIRST_BOOK_ID, "first-request").andExpect(status().isCreated());

    // WHEN THEN
    borrowContent("testUser1", FIRST_BOOK_ID, "second-request")
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("CONTENT_ALREADY_BORROWED"));

    // WHEN THEN
    borrowContent("testUser1", UNAVAILABLE_BOOK_ID, "unavailable-request")
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("CONTENT_UNAVAILABLE"));

    // WHEN THEN
    borrowContent("testUser1", "10000000-0000-4000-8000-000000000099", "missing-request")
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CONTENT_NOT_FOUND"));
  }

  @Test
  void givenSuccessfulBorrowRequest_whenReusingKeyForDifferentContent_thenReturnConflict()
      throws Exception {
    // GIVEN
    borrowContent("testUser1", FIRST_BOOK_ID, "shared-key").andExpect(status().isCreated());

    // WHEN THEN
    borrowContent("testUser1", SECOND_BOOK_ID, "shared-key")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_REUSED"));

    // WHEN THEN
    borrowContent("testUser2", FIRST_BOOK_ID, "shared-key").andExpect(status().isCreated());

    // THEN
    assertThat(countLoans()).isEqualTo(2);
  }

  @Test
  void givenFailedBorrowRequest_whenRetryingKeyWithAvailableContent_thenCreateLoan()
      throws Exception {
    // GIVEN
    borrowContent("testUser1", UNAVAILABLE_BOOK_ID, "retry-key")
        .andExpect(status().isUnprocessableContent());

    // THEN
    assertThat(borrowRequestRepository.count()).isZero();

    // WHEN THEN
    borrowContent("testUser1", FIRST_BOOK_ID, "retry-key").andExpect(status().isCreated());

    // THEN
    assertThat(countLoans()).isEqualTo(1);
  }

  @Test
  void givenInvalidOrUnauthenticatedRequests_whenCallingLoanEndpoints_thenRejectRequests()
      throws Exception {
    // GIVEN

    // WHEN THEN
    mockMvc
        .perform(
            post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contentId\":\"" + FIRST_BOOK_ID + "\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));

    // WHEN THEN
    mockMvc
        .perform(
            post("/api/v1/loans")
                .with(httpBasic("testUser1", "testUser1-password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contentId\":\"" + FIRST_BOOK_ID + "\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_IDEMPOTENCY_KEY"));

    // WHEN THEN
    mockMvc
        .perform(
            post("/api/v1/loans")
                .with(httpBasic("testUser1", "testUser1-password"))
                .header("Idempotency-Key", "invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contentId\":null}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

    // WHEN THEN
    mockMvc
        .perform(
            post("/api/v1/loans")
                .with(httpBasic("testUser1", "testUser1-password"))
                .header("Idempotency-Key", "unknown-field")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contentId\":\"" + FIRST_BOOK_ID + "\",\"borrowerId\":\"testUser2\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    for (String loanStatus : java.util.List.of("RETURNED", "UNKNOWN", "active")) {

      // WHEN THEN
      mockMvc
          .perform(
              get("/api/v1/loans")
                  .with(httpBasic("testUser1", "testUser1-password"))
                  .param("status", loanStatus))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
  }

  @Test
  void givenAvailableContent_whenBorrowingConcurrentlyWithSameKey_thenCreateOneLoan()
      throws Exception {
    // GIVEN
    CountDownLatch start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var first =
          executor.submit(
              () -> {
                start.await();
                return borrowContent("testUser1", FIRST_BOOK_ID, "concurrent-request")
                    .andExpect(status().isCreated())
                    .andReturn();
              });
      var second =
          executor.submit(
              () -> {
                start.await();
                return borrowContent("testUser1", FIRST_BOOK_ID, "concurrent-request")
                    .andExpect(status().isCreated())
                    .andReturn();
              });

      // WHEN
      start.countDown();

      // THEN
      assertThat(readLoan(first.get(10, TimeUnit.SECONDS)))
          .isEqualTo(readLoan(second.get(10, TimeUnit.SECONDS)));
    }

    // THEN
    assertThat(countLoans()).isEqualTo(1);
  }

  @Test
  void givenOneAvailableLicense_whenTwoBorrowersBorrowConcurrently_thenCreateOnlyOneLoan()
      throws Exception {
    // GIVEN
    CountDownLatch start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var first =
          executor.submit(
              () -> {
                start.await();
                return borrowContent("testUser1", SECOND_BOOK_ID, "testUser1-race")
                    .andReturn()
                    .getResponse()
                    .getStatus();
              });
      var second =
          executor.submit(
              () -> {
                start.await();
                return borrowContent("testUser2", SECOND_BOOK_ID, "testUser2-race")
                    .andReturn()
                    .getResponse()
                    .getStatus();
              });

      // WHEN
      start.countDown();

      // THEN
      assertThat(
              java.util.List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(201, 422);
    }

    // THEN
    assertThat(countLoans()).isEqualTo(1);
  }

  private ResultActions borrowContent(String username, String contentId, String key)
      throws Exception {
    return mockMvc.perform(
        post("/api/v1/loans")
            .with(httpBasic(username, username + "-password"))
            .header("Idempotency-Key", key)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"contentId\":\"" + contentId + "\"}"));
  }

  private LoanResponse readLoan(MvcResult result) throws Exception {
    return objectMapper.readValue(result.getResponse().getContentAsString(), LoanResponse.class);
  }

  private long countLoans() {
    return loanRepository.count();
  }
}
