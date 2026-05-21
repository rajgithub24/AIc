package com.aicontract.contractanalyzer.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aicontract.contractanalyzer.ai.AiProcessingService;
import com.aicontract.contractanalyzer.document.PdfExtractionService;
import com.aicontract.contractanalyzer.dto.FileUploadResponseDto;
import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.service.ContractService;
import com.aicontract.contractanalyzer.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File Uploads", description = "PDF upload endpoint that starts async AI contract processing")
public class FileUploadController {

        private final FileStorageService fileStorageService;
        private final PdfExtractionService pdfExtractionService;
        private final ContractService contractService;
        private final AiProcessingService aiProcessingService;

        @PostMapping(value = { "/upload", "/uploads" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Upload a PDF contract and start AI processing")
        public ResponseEntity<FileUploadResponseDto> uploadFile(
                        @Parameter(description = "PDF contract file", required = true) @RequestParam("file") MultipartFile file) {

                String fileName = fileStorageService.storeFile(file);

                String extractedText = pdfExtractionService.extractText(
                                "uploads/" + fileName);

                Contract contract = Contract.builder()
                                .fileName(fileName)
                                .status("PROCESSING")
                                .extractedText(extractedText)
                                .user(
                                                contractService.getCurrentAuthenticatedUser())
                                .build();

                contract = contractService.saveContract(contract);

                aiProcessingService.processContract(contract);

                FileUploadResponseDto response = FileUploadResponseDto.builder()
                                .contractId(contract.getId())
                                .fileName(fileName)
                                .message("PDF uploaded. AI summary and contract intelligence processing started.")
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/download/{contractId}")
        @Operation(summary = "Download the PDF file associated with a contract")
        public ResponseEntity<Resource> downloadFile(
                @PathVariable Long contractId) {

            Contract contract = contractService.getContractById(contractId);
            Resource resource = fileStorageService.loadFileAsResource(
                    contract.getFileName());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + contract.getFileName() + "\"")
                    .body(resource);
        }

}
