package com.aicontract.contractanalyzer.repository;

import com.aicontract.contractanalyzer.entity.Contract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void searchByKeywordSearchesDashboardFields() {

        Contract contract = Contract.builder()
                .fileName("loan-agreement.pdf")
                .summary("Standard commercial contract")
                .status("COMPLETED")
                .extractedText("Borrower must repay the loan principal.")
                .riskyClauses("[\"Unlimited liability applies\"]")
                .paymentObligations("[\"Monthly payment is due\"]")
                .terminationConditions("[]")
                .penalties("[]")
                .complianceKeywords("[\"KYC\"]")
                .riskScore(45)
                .riskLevel("MEDIUM")
                .build();

        contractRepository.save(contract);

        assertThat(contractRepository.searchByKeyword("loan", PageRequest.of(0, 5)).getContent())
                .extracting(Contract::getFileName)
                .containsExactly("loan-agreement.pdf");

        assertThat(contractRepository.searchByKeyword("kyc", PageRequest.of(0, 5)).getContent())
                .extracting(Contract::getFileName)
                .containsExactly("loan-agreement.pdf");
    }

    @Test
    void findByStatusIgnoreCaseOrderByIdDescFiltersByStatus() {

        Contract processing = contractRepository.save(Contract.builder()
                .fileName("processing.pdf")
                .status("PROCESSING")
                .build());
        Contract completed = contractRepository.save(Contract.builder()
                .fileName("completed.pdf")
                .status("COMPLETED")
                .build());

        assertThat(contractRepository.findByStatusIgnoreCase("completed", PageRequest.of(0, 5)).getContent())
                .extracting(Contract::getId)
                .containsExactly(completed.getId())
                .doesNotContain(processing.getId());
    }

    @Test
    void findRiskyContractsReturnsMediumAndHighRiskContractsFirst() {

        Contract lowRisk = contractRepository.save(Contract.builder()
                .fileName("low.pdf")
                .status("COMPLETED")
                .riskScore(10)
                .riskLevel("LOW")
                .build());
        Contract mediumRisk = contractRepository.save(Contract.builder()
                .fileName("medium.pdf")
                .status("COMPLETED")
                .riskScore(45)
                .riskLevel("MEDIUM")
                .build());
        Contract highRisk = contractRepository.save(Contract.builder()
                .fileName("high.pdf")
                .status("COMPLETED")
                .riskScore(90)
                .riskLevel("HIGH")
                .build());

        assertThat(contractRepository.findRiskyContracts(PageRequest.of(0, 5)).getContent())
                .extracting(Contract::getId)
                .containsExactly(highRisk.getId(), mediumRisk.getId())
                .doesNotContain(lowRisk.getId());
    }

    @Test
    void findAllSupportsPagination() {

        contractRepository.save(Contract.builder()
                .fileName("first.pdf")
                .status("COMPLETED")
                .build());
        contractRepository.save(Contract.builder()
                .fileName("second.pdf")
                .status("COMPLETED")
                .build());
        contractRepository.save(Contract.builder()
                .fileName("third.pdf")
                .status("COMPLETED")
                .build());

        assertThat(contractRepository.findAll(PageRequest.of(0, 2)).getContent())
                .hasSize(2);
        assertThat(contractRepository.findAll(PageRequest.of(1, 2)).getContent())
                .hasSize(1);
    }
}
