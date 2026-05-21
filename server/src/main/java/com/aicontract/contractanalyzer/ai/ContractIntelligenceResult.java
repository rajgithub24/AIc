package com.aicontract.contractanalyzer.ai;

import java.util.List;

public record ContractIntelligenceResult(
        List<String> riskyClauses,
        List<String> paymentObligations,
        List<String> terminationConditions,
        List<String> penalties,
        List<String> complianceKeywords,
        int riskScore,
        String riskLevel) {
}
