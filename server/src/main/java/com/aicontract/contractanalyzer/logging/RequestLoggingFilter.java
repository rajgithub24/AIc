package com.aicontract.contractanalyzer.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        long startTime = System.currentTimeMillis();

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.info(
                "Incoming request requestId={} method={} path={} query={} remoteAddress={}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr());

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;

            log.info(
                    "Completed request requestId={} method={} path={} status={} durationMs={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs);

            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {

        String requestId = request.getHeader(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return requestId;
    }
}
