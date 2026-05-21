package com.aicontract.contractanalyzer.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {

    private String message;
    private int status;
    private LocalDateTime timestamp;
}