package com.aicontract.contractanalyzer.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContractIntelligenceServiceTest {

    private final ContractIntelligenceService service = new ContractIntelligenceService();

    @Test
    void analyzeExtractsContractIntelligenceSignals() {

        String text = """
                Customer shall pay all invoices within net 30 days, including applicable taxes.
                Either party may terminate this agreement for material breach with written notice.
                Late payments accrue interest and a late fee until paid.
                Vendor may seek indemnification and limitation of liability applies to consequential damages.
                Supplier must follow GDPR, data protection, confidentiality, and audit compliance obligations.
                """;

        ContractIntelligenceResult result = service.analyze(text);

        assertThat(result.paymentObligations())
                .anyMatch(clause -> clause.contains("pay all invoices"));
        assertThat(result.terminationConditions())
                .anyMatch(clause -> clause.contains("terminate this agreement"));
        assertThat(result.penalties())
                .anyMatch(clause -> clause.contains("Late payments"));
        assertThat(result.riskyClauses())
                .anyMatch(clause -> clause.contains("indemnification"));
        assertThat(result.complianceKeywords())
                .contains("GDPR", "DATA PROTECTION", "CONFIDENTIALITY", "AUDIT", "COMPLIANCE");
        assertThat(result.riskScore()).isGreaterThan(0);
        assertThat(result.riskLevel()).isIn("LOW", "MEDIUM", "HIGH");
    }
}
