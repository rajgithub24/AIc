package com.aicontract.contractanalyzer.controller;

import com.aicontract.contractanalyzer.ai.AiProcessingService;
import com.aicontract.contractanalyzer.document.PdfExtractionService;
import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.service.ContractService;
import com.aicontract.contractanalyzer.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private PdfExtractionService pdfExtractionService;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private AiProcessingService aiProcessingService;

    @Test
    void uploadFileSupportsSingularUploadPath() throws Exception {

        verifyUploadPath("/api/files/upload");
    }

    @Test
    void uploadFileSupportsPluralUploadsPath() throws Exception {

        verifyUploadPath("/api/files/uploads");
    }

    private void verifyUploadPath(String path) throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contract.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(fileStorageService.storeFile(any())).thenReturn("contract.pdf");
        when(pdfExtractionService.extractText("uploads/contract.pdf"))
                .thenReturn("Extracted contract text");
        when(contractService.saveContract(any()))
                .thenAnswer(invocation -> {
                    Contract contract = invocation.getArgument(0);
                    contract.setId(1L);
                    return contract;
                });

        mockMvc.perform(multipart(path).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(1))
                .andExpect(jsonPath("$.fileName").value("contract.pdf"));
    }
}
