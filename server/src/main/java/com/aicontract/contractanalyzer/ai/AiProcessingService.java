package com.aicontract.contractanalyzer.ai;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.repository.ContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiProcessingService {

    private final HuggingFaceService huggingFaceService;
    private final ContractIntelligenceService contractIntelligenceService;
    private final ContractRepository contractRepository;
    private final ObjectMapper objectMapper;

    @Async
    public void processContract(Contract contract) {

        try {

            log.info("Starting AI processing for contract: {}",
                    contract.getId());

            contract.setStatus("PROCESSING");

            contractRepository.save(contract);

            String summary =
                    huggingFaceService.generateSummary(
                            contract.getExtractedText()
                    );

            ContractIntelligenceResult intelligence =
                    contractIntelligenceService.analyze(
                            contract.getExtractedText()
                    );

            contract.setSummary(summary);
            contract.setRiskyClauses(
                    objectMapper.writeValueAsString(intelligence.riskyClauses()));
            contract.setPaymentObligations(
                    objectMapper.writeValueAsString(intelligence.paymentObligations()));
            contract.setTerminationConditions(
                    objectMapper.writeValueAsString(intelligence.terminationConditions()));
            contract.setPenalties(
                    objectMapper.writeValueAsString(intelligence.penalties()));
            contract.setComplianceKeywords(
                    objectMapper.writeValueAsString(intelligence.complianceKeywords()));

            // Log intelligence for troubleshooting (sizes and score)
            log.debug("Contract {} intelligence: riskyClauses={}, paymentObligations={}, terminationConditions={}, penalties={}, complianceKeywords={}, riskScore={}",
                    contract.getId(),
                    intelligence.riskyClauses().size(),
                    intelligence.paymentObligations().size(),
                    intelligence.terminationConditions().size(),
                    intelligence.penalties().size(),
                    intelligence.complianceKeywords().size(),
                    intelligence.riskScore());

            String riskAnalysis = huggingFaceService.generateRiskAnalysis(
                    contract.getExtractedText(),
                    intelligence);

            contract.setRiskAnalysis(riskAnalysis);
            contract.setRiskScore(intelligence.riskScore());
            contract.setRiskLevel(intelligence.riskLevel());

            contract.setStatus("COMPLETED");

            contractRepository.save(contract);

            log.info("AI processing completed for contract: {}",
                    contract.getId());

        } catch (Exception e) {

            log.error("AI processing failed for contract: {}",
                    contract.getId(),
                    e);

            contract.setStatus("FAILED");

            contractRepository.save(contract);
        }
    }
}
