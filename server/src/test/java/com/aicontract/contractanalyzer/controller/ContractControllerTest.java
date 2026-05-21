package com.aicontract.contractanalyzer.controller;

import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.service.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

@WebMvcTest(ContractController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @Test
    void getAllContractsSupportsPaginationParameters() throws Exception {

        when(contractService.getAllContracts(0, 5, "id", "desc"))
                .thenReturn(new PageImpl<>(
                        List.of(Contract.builder()
                                .id(1L)
                                .fileName("contract.pdf")
                                .extractedText("Very large extracted PDF text that should not be returned")
                                .build()),
                        PageRequest.of(0, 5),
                        1));

        mockMvc.perform(get("/api/contracts")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fileName").value("contract.pdf"))
                .andExpect(jsonPath("$.content[0]", not(hasKey("extractedText"))))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(contractService).getAllContracts(0, 5, "id", "desc");
    }

    @Test
    void searchContractsSupportsGetRequests() throws Exception {

        when(contractService.searchContracts("loan", 0, 10, "id", "desc"))
                .thenReturn(new PageImpl<>(List.of(Contract.builder()
                        .id(1L)
                        .fileName("loan.pdf")
                        .extractedText("Very large extracted PDF text that should not be returned")
                        .build())));

        mockMvc.perform(get("/api/contracts/search")
                        .param("keyword", "loan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fileName").value("loan.pdf"))
                .andExpect(jsonPath("$.content[0]", not(hasKey("extractedText"))));

        verify(contractService).searchContracts("loan", 0, 10, "id", "desc");
    }

    @Test
    void searchContractsSupportsPostRequests() throws Exception {

        when(contractService.searchContracts("loan", 0, 10, "id", "desc"))
                .thenReturn(new PageImpl<>(List.of(Contract.builder()
                        .id(1L)
                        .fileName("loan.pdf")
                        .extractedText("Very large extracted PDF text that should not be returned")
                        .build())));

        mockMvc.perform(post("/api/contracts/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"loan\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fileName").value("loan.pdf"))
                .andExpect(jsonPath("$.content[0]", not(hasKey("extractedText"))));

        verify(contractService).searchContracts("loan", 0, 10, "id", "desc");
    }
}
