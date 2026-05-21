package com.aicontract.contractanalyzer.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HuggingFaceServiceTest {

    @Test
    void generateSummarySummarizesLargeTextInChunks() {

        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HuggingFaceService service = new HuggingFaceService(restTemplate, new ObjectMapper());
        AtomicInteger requestCount = new AtomicInteger();
        String apiUrl = "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

        ReflectionTestUtils.setField(service, "apiUrl", apiUrl);
        ReflectionTestUtils.setField(service, "apiToken", "test-token");
        ReflectionTestUtils.setField(service, "maxAttempts", 1);
        ReflectionTestUtils.setField(service, "retryBackoffMs", 0L);

        server.expect(ExpectedCount.manyTimes(), requestTo(apiUrl))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(request -> {
                    requestCount.incrementAndGet();
                    return withSuccess(
                            "[{\"summary_text\":\"summary part\"}]",
                            MediaType.APPLICATION_JSON).createResponse(request);
                });

        String largeText = IntStream.range(0, 120)
                .mapToObj(i -> "Contract section " + i
                        + " states payment obligations, renewal dates, termination rights, confidentiality duties, "
                        + "liability limits, and governing law for the parties.")
                .collect(Collectors.joining("\n\n"));

        String summary = service.generateSummary(largeText);

        assertThat(summary).isEqualTo("summary part");
        assertThat(requestCount.get()).isGreaterThan(1);

        server.verify();
    }

    @Test
    void generateSummaryRetriesTransientFailures() {

        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HuggingFaceService service = new HuggingFaceService(restTemplate, new ObjectMapper());
        String apiUrl = "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

        ReflectionTestUtils.setField(service, "apiUrl", apiUrl);
        ReflectionTestUtils.setField(service, "apiToken", "test-token");
        ReflectionTestUtils.setField(service, "maxAttempts", 2);
        ReflectionTestUtils.setField(service, "retryBackoffMs", 0L);

        server.expect(requestTo(apiUrl))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        server.expect(requestTo(apiUrl))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(
                        "[{\"summary_text\":\"recovered summary\"}]",
                        MediaType.APPLICATION_JSON));

        String summary = service.generateSummary("This contract includes payment and renewal obligations.");

        assertThat(summary).isEqualTo("recovered summary");

        server.verify();
    }

    @Test
    void generateSummaryReturnsFallbackAfterRetriesAreExhausted() {

        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HuggingFaceService service = new HuggingFaceService(restTemplate, new ObjectMapper());
        String apiUrl = "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

        ReflectionTestUtils.setField(service, "apiUrl", apiUrl);
        ReflectionTestUtils.setField(service, "apiToken", "test-token");
        ReflectionTestUtils.setField(service, "maxAttempts", 2);
        ReflectionTestUtils.setField(service, "retryBackoffMs", 0L);

        server.expect(requestTo(apiUrl))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        server.expect(requestTo(apiUrl))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        String summary = service.generateSummary(
                "The customer must pay invoices within thirty days. "
                        + "The contract renews annually unless either party gives written notice.");

        assertThat(summary)
                .startsWith("AI summary is temporarily unavailable.")
                .contains("The customer must pay invoices within thirty days.");

        server.verify();
    }
}
