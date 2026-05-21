package com.aicontract.contractanalyzer.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aicontract.contractanalyzer.dto.ContractRequestDto;
import com.aicontract.contractanalyzer.dto.ContractResponseDto;
import com.aicontract.contractanalyzer.dto.PagedResponseDto;
import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Contract records, dashboard pagination, search, status filters, and risk views")
public class ContractController {

    private final ContractService contractService;
        private final com.aicontract.contractanalyzer.ai.AiProcessingService aiProcessingService;

    @PostMapping
    @Operation(summary = "Create a contract record manually")
    public ContractResponseDto createContract(
            @Valid @RequestBody ContractRequestDto request) {

        Contract contract = Contract.builder()
                .fileName(request.getFileName())
                .summary(request.getSummary())
                .status(request.getStatus())
                .build();

        return ContractResponseDto.from(contractService.saveContract(contract));
    }

    @GetMapping
    @Operation(summary = "List contracts with pagination and sorting")
    public PagedResponseDto<ContractResponseDto> getAllContracts(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size. Values above 50 are capped.") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, fileName, status, riskScore, riskLevel") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String direction) {
        return PagedResponseDto.from(
                contractService.getAllContracts(page, size, sortBy, direction),
                ContractResponseDto::from);
    }

    @GetMapping("/search")
    @Operation(summary = "Search contracts by keyword")
    public PagedResponseDto<ContractResponseDto> searchContracts(
            @Parameter(description = "Keyword matched against file name, summary, extracted text, clauses, and compliance keywords") @RequestParam("keyword") String keyword,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size. Values above 50 are capped.") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, fileName, status, riskScore, riskLevel") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String direction) {
        return PagedResponseDto.from(
                contractService.searchContracts(keyword, page, size, sortBy, direction),
                ContractResponseDto::from);
    }

    @PostMapping("/search")
    @Operation(summary = "Search contracts by keyword using a JSON body")
    public PagedResponseDto<ContractResponseDto> searchContractsByPost(
            @Parameter(description = "Optional query keyword. If omitted, the request body keyword is used.") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size. Values above 50 are capped.") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, fileName, status, riskScore, riskLevel") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String direction,
            @RequestBody(required = false) Map<String, String> request) {

        String searchKeyword = keyword;

        if ((searchKeyword == null || searchKeyword.isBlank()) && request != null) {
            searchKeyword = request.get("keyword");
        }

        return PagedResponseDto.from(
                contractService.searchContracts(searchKeyword, page, size, sortBy, direction),
                ContractResponseDto::from);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filter contracts by processing status")
    public PagedResponseDto<ContractResponseDto> getContractsByStatus(
            @Parameter(description = "Status such as PROCESSING, COMPLETED, or FAILED") @PathVariable String status,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size. Values above 50 are capped.") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, fileName, status, riskScore, riskLevel") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String direction) {
        return PagedResponseDto.from(
                contractService.getContractsByStatus(status, page, size, sortBy, direction),
                ContractResponseDto::from);
    }

    @GetMapping("/risk")
    @Operation(summary = "List medium and high risk contracts")
    public PagedResponseDto<ContractResponseDto> getRiskyContracts(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size. Values above 50 are capped.") @RequestParam(defaultValue = "10") int size) {
        return PagedResponseDto.from(
                contractService.getRiskyContracts(page, size),
                ContractResponseDto::from);
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get one contract by numeric ID")
    public ContractResponseDto getContractById(
            @Parameter(description = "Numeric contract ID") @PathVariable Long id) {
        return ContractResponseDto.from(contractService.getContractById(id));
    }

        @PostMapping("/{id:\\d+}/reprocess")
        @Operation(summary = "Re-run AI processing for a contract (useful for debugging)")
        public ContractResponseDto reprocessContract(
                        @Parameter(description = "Numeric contract ID") @PathVariable Long id) {

                var contract = contractService.getContractById(id);

                // Trigger async reprocessing; client can poll the contract for updates
                aiProcessingService.processContract(contract);

                return ContractResponseDto.from(contract);
        }

        @GetMapping("/{id:\\d+}/raw")
        @Operation(summary = "(Dev) Get raw extracted text for a contract")
        public java.util.Map<String, String> getRawExtractedText(
                        @Parameter(description = "Numeric contract ID") @PathVariable Long id) {

                var contract = contractService.getContractById(id);

                return java.util.Map.of(
                                "extractedText", contract.getExtractedText() == null ? "" : contract.getExtractedText(),
                                "status", contract.getStatus() == null ? "" : contract.getStatus());
        }
}
