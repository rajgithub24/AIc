package com.aicontract.contractanalyzer.controller;

import com.aicontract.contractanalyzer.dto.AuthRequestDto;
import com.aicontract.contractanalyzer.dto.AuthResponseDto;
import com.aicontract.contractanalyzer.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDto register(
            @RequestBody AuthRequestDto request
    ) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(
            @RequestBody AuthRequestDto request
    ) {

        return authService.login(request);
    }
}