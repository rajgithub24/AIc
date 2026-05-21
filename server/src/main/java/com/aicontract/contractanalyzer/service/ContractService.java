package com.aicontract.contractanalyzer.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.aicontract.contractanalyzer.entity.Contract;
import com.aicontract.contractanalyzer.exception.ResourceNotFoundException;
import com.aicontract.contractanalyzer.repository.ContractRepository;

import lombok.RequiredArgsConstructor;
import com.aicontract.contractanalyzer.entity.User;
import com.aicontract.contractanalyzer.repository.UserRepository;
import com.aicontract.contractanalyzer.security.SecurityUtils;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private static final int MAX_PAGE_SIZE = 50;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "fileName",
            "status",
            "riskScore",
            "riskLevel");

    public Contract saveContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public Page<Contract> getAllContracts(
            int page,
            int size,
            String sortBy,
            String direction) {
        return contractRepository.findAll(
                createPageable(page, size, sortBy, direction, "id"));
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found: " + id));
    }

    public Page<Contract> searchContracts(
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction) {

        Pageable pageable = createPageable(page, size, sortBy, direction, "id");

        if (keyword == null || keyword.isBlank()) {
            User currentUser = getCurrentUser();

            return contractRepository
                    .findByUser(currentUser, pageable);
        }

        return contractRepository.searchByKeyword(keyword.trim(), pageable);
    }

    public Page<Contract> getContractsByStatus(
            String status,
            int page,
            int size,
            String sortBy,
            String direction) {
        return contractRepository.findByStatusIgnoreCase(
                status,
                createPageable(page, size, sortBy, direction, "id"));
    }

    public Page<Contract> getRiskyContracts(int page, int size) {
        return contractRepository.findRiskyContracts(
                createPageable(page, size, "riskScore", "desc", "riskScore"));
    }

    public User getCurrentAuthenticatedUser() {
        return getCurrentUser();
    }

    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String direction,
            String defaultSortBy) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : defaultSortBy;
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(safePage, safeSize, Sort.by(safeDirection, safeSortBy));
    }

    private User getCurrentUser() {

        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found"));
    }
}
