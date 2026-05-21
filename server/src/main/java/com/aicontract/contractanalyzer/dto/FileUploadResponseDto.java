package com.aicontract.contractanalyzer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDto {

    private Long contractId;
    private String fileName;
    private String message;
}
