package com.aicontract.contractanalyzer.controller;

import com.aicontract.contractanalyzer.entity.User;
import com.aicontract.contractanalyzer.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ContractService contractService;

    @GetMapping("/me")
    public User getCurrentUser() {

        return contractService
                .getCurrentAuthenticatedUser();
    }
}