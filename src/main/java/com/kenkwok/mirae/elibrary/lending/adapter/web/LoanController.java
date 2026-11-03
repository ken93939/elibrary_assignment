package com.kenkwok.mirae.elibrary.lending.adapter.web;

import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.BorrowContentRequest;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanPageResponse;
import com.kenkwok.mirae.elibrary.lending.adapter.web.dto.LoanResponse;
import com.kenkwok.mirae.elibrary.lending.application.LoanService;
import com.kenkwok.mirae.elibrary.lending.domain.LoanStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

  private final LoanService loanService;

  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  @PostMapping
  public ResponseEntity<LoanResponse> borrowContent(
      Principal principal,
      @RequestHeader(value = "Idempotency-Key", required = false) String key,
      @Valid @RequestBody BorrowContentRequest request) {
    LoanResponse response =
        LoanResponseMapper.toDto(
            loanService.borrowContent(principal.getName(), request.contentId(), key));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public LoanPageResponse getLoans(
      Principal principal,
      @RequestParam(defaultValue = "ACTIVE") LoanStatus status,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    if (status != LoanStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    return LoanResponseMapper.toDto(loanService.getActiveLoans(principal.getName(), page, size));
  }

  @GetMapping("/{loanId}")
  public LoanResponse getLoan(Principal principal, @PathVariable UUID loanId) {
    return LoanResponseMapper.toDto(loanService.getLoan(principal.getName(), loanId));
  }

  @PostMapping("/{loanId}/return")
  public LoanResponse returnLoan(Principal principal, @PathVariable UUID loanId) {
    return LoanResponseMapper.toDto(loanService.returnLoan(principal.getName(), loanId));
  }
}
