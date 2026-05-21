package com.aicontract.contractanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractRequestDto {

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Summary is required")
    private String summary;

    @NotBlank(message = "Status is required")
    private String status;
}