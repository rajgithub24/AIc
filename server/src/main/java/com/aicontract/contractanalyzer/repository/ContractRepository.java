package com.aicontract.contractanalyzer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.aicontract.contractanalyzer.entity.User;
import com.aicontract.contractanalyzer.entity.Contract;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

        @Query("""
                        SELECT c FROM Contract c
                        WHERE LOWER(c.fileName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.extractedText) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.riskyClauses) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.paymentObligations) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.terminationConditions) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.penalties) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(c.complianceKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        ORDER BY c.id DESC
                        """)
        Page<Contract> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

        Page<Contract> findByStatusIgnoreCase(String status, Pageable pageable);

        List<Contract> findByUser(User user);

        Optional<Contract> findByIdAndUser(
                        Long id,
                        User user);

        Page<Contract> findByUser(
                        User user,
                        Pageable pageable);

        @Query("""
                        SELECT c FROM Contract c
                        WHERE UPPER(c.riskLevel) IN ('MEDIUM', 'HIGH')
                           OR c.riskScore >= 35
                        ORDER BY c.riskScore DESC, c.id DESC
                        """)
        Page<Contract> findRiskyContracts(Pageable pageable);
}
