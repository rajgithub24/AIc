package com.aicontract.contractanalyzer.dto;

import com.aicontract.contractanalyzer.entity.Contract;

public record ContractResponseDto(
        Long id,
        String fileName,
        String summary,
        String status,
        String riskyClauses,
        String paymentObligations,
        String terminationConditions,
        String penalties,
        String complianceKeywords,
        String riskAnalysis,
        Integer riskScore,
        String riskLevel) {

    public static ContractResponseDto from(Contract contract) {
        return new ContractResponseDto(
                contract.getId(),
                contract.getFileName(),
                contract.getSummary(),
                contract.getStatus(),
                contract.getRiskyClauses(),
                contract.getPaymentObligations(),
                contract.getTerminationConditions(),
                contract.getPenalties(),
                contract.getComplianceKeywords(),
                contract.getRiskAnalysis(),
                contract.getRiskScore(),
                contract.getRiskLevel());
    }
}
