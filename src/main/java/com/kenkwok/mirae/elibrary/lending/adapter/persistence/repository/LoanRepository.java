package com.kenkwok.mirae.elibrary.lending.adapter.persistence.repository;

import com.kenkwok.mirae.elibrary.lending.adapter.persistence.entity.LoanEntity;
import com.kenkwok.mirae.elibrary.lending.adapter.persistence.projection.LoanProjection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

  @Query(
      """
      select loan.id as id, loan.contentId as contentId, loan.borrowerSubject as borrowerSubject,
             loan.borrowedAt as borrowedAt, loan.dueAt as dueAt, loan.returnedAt as returnedAt
      from LoanEntity loan
      where loan.id = :loanId and loan.borrowerSubject = :borrowerSubject
      """)
  Optional<LoanProjection> findLoan(UUID loanId, String borrowerSubject);

  @EntityGraph(attributePaths = "content")
  Page<LoanEntity> findByBorrowerSubjectAndReturnedAtIsNull(
      String borrowerSubject, Pageable pageable);

  boolean existsByContentIdAndBorrowerSubjectAndReturnedAtIsNull(
      UUID contentId, String borrowerSubject);

  long countByContentIdAndReturnedAtIsNull(UUID contentId);

  long countByBorrowerSubjectAndReturnedAtIsNull(String borrowerSubject);
}
