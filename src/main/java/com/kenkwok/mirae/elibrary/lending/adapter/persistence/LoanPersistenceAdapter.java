package com.kenkwok.mirae.elibrary.lending.adapter.persistence;

import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.LoanEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.mapper.LoanPersistenceMapper;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository.LoanRepository;
import com.kenkwok.mirae.elibrary.lending.application.model.LoanPage;
import com.kenkwok.mirae.elibrary.lending.application.port.LoanPort;
import com.kenkwok.mirae.elibrary.lending.domain.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class LoanPersistenceAdapter implements LoanPort {

  private final LoanRepository loanRepository;

  public LoanPersistenceAdapter(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  @Override
  public Loan createLoan(Loan loan) {
    LoanEntity entity = loanRepository.saveAndFlush(LoanPersistenceMapper.toEntity(loan));
    return LoanPersistenceMapper.toDomain(entity);
  }

  @Override
  public Loan returnLoan(Loan loan) {
    LoanEntity entity =
        loanRepository
            .findById(loan.id())
            .orElseThrow(() -> new IllegalStateException("Loan is missing"));
    entity.recordReturn(loan.returnedAt());
    loanRepository.flush();
    return LoanPersistenceMapper.toDomain(entity);
  }

  @Override
  public Optional<Loan> findLoan(UUID loanId, String borrowerSubject) {
    return loanRepository.findLoan(loanId, borrowerSubject).map(LoanPersistenceMapper::toDomain);
  }

  @Override
  public LoanPage findActiveLoans(String borrowerSubject, int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowedAt", "id"));
    Page<LoanEntity> loans = findActiveLoanPage(borrowerSubject, pageable);
    return new LoanPage(
        loans.stream().map(LoanPersistenceMapper::toModel).toList(),
        page,
        size,
        loans.getTotalElements(),
        loans.getTotalPages());
  }

  @Override
  public boolean existsActiveLoan(UUID contentId, String borrowerSubject) {
    return loanRepository.existsByContentIdAndBorrowerSubjectAndReturnedAtIsNull(
        contentId, borrowerSubject);
  }

  @Override
  public long countActiveLoans(UUID contentId) {
    return loanRepository.countByContentIdAndReturnedAtIsNull(contentId);
  }

  private Page<LoanEntity> findActiveLoanPage(String borrowerSubject, Pageable pageable) {
    if (pageable.getOffset() > Integer.MAX_VALUE) {
      long totalItems = loanRepository.countByBorrowerSubjectAndReturnedAtIsNull(borrowerSubject);
      if (pageable.getOffset() >= totalItems) {
        return new PageImpl<>(List.of(), pageable, totalItems);
      }
    }
    return loanRepository.findByBorrowerSubjectAndReturnedAtIsNull(borrowerSubject, pageable);
  }
}
