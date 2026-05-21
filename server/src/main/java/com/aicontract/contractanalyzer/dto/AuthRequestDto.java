package com.aicontract.contractanalyzer.dto;

import lombok.Data;

@Data
public class AuthRequestDto {

    private String email;

    private String password;
}