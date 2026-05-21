package com.aicontract.contractanalyzer.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PagedResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty) {

    public static <T> PagedResponseDto<T> from(Page<T> page) {
        return new PagedResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty());
    }

    public static <T, R> PagedResponseDto<R> from(Page<T> page, Function<T, R> mapper) {
        return new PagedResponseDto<>(
                page.getContent().stream()
                        .map(mapper)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty());
    }
}
