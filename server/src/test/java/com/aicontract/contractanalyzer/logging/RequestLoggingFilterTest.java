package com.aicontract.contractanalyzer.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void doFilterAddsRequestIdHeaderWhenMissing() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/contracts");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
        };

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader("X-Request-Id")).isNotBlank();
    }

    @Test
    void doFilterPreservesIncomingRequestId() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/contracts");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
        };

        request.addHeader("X-Request-Id", "request-123");

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader("X-Request-Id")).isEqualTo("request-123");
    }
}
