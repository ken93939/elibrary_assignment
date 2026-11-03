package com.kenkwok.mirae.elibrary.content.adapter.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kenkwok.mirae.elibrary.inventory.adapter.persistence.repository.InventoryRepository;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.LoanEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.LoanRepository;
import com.kenkwok.mirae.elibrary.support.EmbeddedPostgresConfiguration;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Import(EmbeddedPostgresConfiguration.class)
@Transactional
class BookControllerIntegrationTests {

  private static final String FIRST_BOOK_ID = "10000000-0000-4000-8000-000000000001";

  @Autowired private MockMvc mockMvc;

  @Autowired private LoanRepository loanRepository;
  @Autowired private InventoryRepository inventoryRepository;

  @Test
  void givenMultiAuthorBooks_whenListingBooks_thenReturnOrderedPagesWithAllAuthors()
      throws Exception {
    mockMvc
        .perform(get("/api/v1/books").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].id").value(FIRST_BOOK_ID))
        .andExpect(jsonPath("$.items[0].contentType").value("BOOK"))
        .andExpect(jsonPath("$.items[0].authors", hasSize(2)))
        .andExpect(jsonPath("$.items[0].authors[0]").value("Alice Example"))
        .andExpect(jsonPath("$.items[0].authors[1]").value("Bob Example"))
        .andExpect(jsonPath("$.items[0].availability").doesNotExist())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalItems").value(3))
        .andExpect(jsonPath("$.totalPages").value(3));

    mockMvc
        .perform(get("/api/v1/books").param("page", "1").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].id").value("10000000-0000-4000-8000-000000000002"))
        .andExpect(jsonPath("$.items[0].authors[0]").value("Chris Example"));
  }

  @Test
  void givenBooks_whenRequestingPageBeyondLastPage_thenReturnEmptyPageWithTotals()
      throws Exception {
    mockMvc
        .perform(get("/api/v1/books").param("page", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)))
        .andExpect(jsonPath("$.page").value(10))
        .andExpect(jsonPath("$.totalItems").value(3));
  }

  @Test
  void givenExistingBook_whenGettingBookDetails_thenReturnBookMetadata() throws Exception {
    mockMvc
        .perform(get("/api/v1/books/{bookId}", FIRST_BOOK_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(FIRST_BOOK_ID))
        .andExpect(jsonPath("$.title").value("Building APIs"))
        .andExpect(jsonPath("$.authors", hasSize(2)))
        .andExpect(jsonPath("$.isbn13").value("9780134685991"))
        .andExpect(jsonPath("$.publishedOn").value("2020-01-01"))
        .andExpect(jsonPath("$.availability").doesNotExist())
        .andExpect(jsonPath("$.assets").doesNotExist());
  }

  @ParameterizedTest
  @ValueSource(ints = {1073741823, 1073741824, Integer.MAX_VALUE})
  void givenBooks_whenRequestingLargePage_thenReturnEmptyPageWithTotals(int page) throws Exception {
    mockMvc
        .perform(get("/api/v1/books").param("page", Integer.toString(page)).param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)))
        .andExpect(jsonPath("$.page").value(page))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalItems").value(3))
        .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Test
  void givenActiveAndReturnedLoans_whenGettingAvailability_thenCountOnlyActiveLoans()
      throws Exception {
    // GIVEN
    UUID contentId = UUID.fromString(FIRST_BOOK_ID);
    Instant borrowedAt = Instant.parse("2026-10-01T00:00:00Z");
    Instant dueAt = borrowedAt.plus(Duration.ofDays(14));
    loanRepository.saveAllAndFlush(
        List.of(
            new LoanEntity(UUID.randomUUID(), contentId, "testUser1", borrowedAt, dueAt, null),
            new LoanEntity(
                UUID.randomUUID(),
                contentId,
                "testUser2",
                borrowedAt,
                dueAt,
                borrowedAt.plus(Duration.ofDays(1)))));

    // WHEN THEN
    mockMvc
        .perform(get("/api/v1/books/{bookId}/availability", FIRST_BOOK_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalLicenses").value(2))
        .andExpect(jsonPath("$.availableLicenses").value(1));
  }

  @Test
  void givenBookWithoutInventory_whenGettingAvailability_thenReturnZeroLicenses() throws Exception {
    // GIVEN
    inventoryRepository.deleteById(UUID.fromString(FIRST_BOOK_ID));
    inventoryRepository.flush();

    // WHEN THEN
    mockMvc
        .perform(get("/api/v1/books/{bookId}/availability", FIRST_BOOK_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalLicenses").value(0))
        .andExpect(jsonPath("$.availableLicenses").value(0));
  }

  @Test
  void givenBookWithUnknownMetadata_whenGettingBookDetails_thenReturnNullMetadata()
      throws Exception {
    mockMvc
        .perform(get("/api/v1/books/10000000-0000-4000-8000-000000000002"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value(nullValue()))
        .andExpect(jsonPath("$.languageTag").value(nullValue()))
        .andExpect(jsonPath("$.isbn13").value(nullValue()))
        .andExpect(jsonPath("$.publisher").value(nullValue()))
        .andExpect(jsonPath("$.publishedOn").value(nullValue()));
  }

  @Test
  void givenMissingBook_whenGettingDetailsOrAvailability_thenReturnNotFound() throws Exception {
    mockMvc
        .perform(get("/api/v1/books/10000000-0000-4000-8000-000000000099"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("The requested book was not found."))
        .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));

    mockMvc
        .perform(get("/api/v1/books/10000000-0000-4000-8000-000000000099/availability"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
  }
}
