package com.aicontract.contractanalyzer.ai;

import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aicontract.contractanalyzer.dto.HuggingFaceRequestDto;
import com.aicontract.contractanalyzer.dto.HuggingFaceResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.max-attempts}")
    private int maxAttempts;

    @Value("${huggingface.api.retry-backoff-ms}")
    private long retryBackoffMs;

    private static final int CHUNK_SIZE = 1800;
    private static final int MAX_COMBINE_PASSES = 3;
    private static final int FALLBACK_SENTENCE_LIMIT = 5;

    public String generateSummary(String text) {

        List<String> chunks = splitIntoChunks(text);

        if (chunks.isEmpty()) {
            return "No text available to summarize.";
        }

        try {
            if (chunks.size() == 1) {
                return summarizeChunkWithRetry(chunks.get(0));
            }

            List<String> chunkSummaries = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                String chunkPrompt = "Summarize this contract section clearly and concisely:\n\n"
                        + chunks.get(i);

                chunkSummaries.add("Section " + (i + 1) + ": " + summarizeChunkWithRetry(chunkPrompt));
            }

            String combinedSummaries = String.join("\n\n", chunkSummaries);

            return summarizeCombinedSummaries(combinedSummaries, 1);

        } catch (Exception e) {
            log.warn("AI summary generation failed after retries. Returning fallback summary. Cause: {}", e.toString());
            return buildFallbackSummary(text, chunks.size());
        }
    }

    private String summarizeCombinedSummaries(String combinedSummaries, int combinePass) throws Exception {

        List<String> summaryChunks = splitIntoChunks(combinedSummaries);

        if (summaryChunks.size() == 1 || combinePass >= MAX_COMBINE_PASSES) {
            return summarizeChunkWithRetry("Create one final executive summary from these section summaries:\n\n"
                    + combinedSummaries);
        }

        List<String> condensedSummaries = new ArrayList<>();

        for (String summaryChunk : summaryChunks) {
            condensedSummaries.add(summarizeChunkWithRetry(
                    "Condense these section summaries while preserving key obligations, dates, risks, and parties:\n\n"
                            + summaryChunk));
        }

        return summarizeCombinedSummaries(String.join("\n\n", condensedSummaries), combinePass + 1);
    }

    private String summarizeChunkWithRetry(String text) throws Exception {

        int attempts = Math.max(1, maxAttempts);
        Exception lastFailure = null;

        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                return summarizeChunk(text);
            } catch (Exception e) {
                lastFailure = e;

                if (attempt == attempts || !isRetryable(e)) {
                    throw e;
                }

                log.warn(
                        "Hugging Face request failed on attempt {} of {}. Retrying. Cause: {}",
                        attempt,
                        attempts,
                        e.toString());
                sleepBeforeRetry(attempt);
            }
        }

        if (lastFailure == null) {
            throw new IllegalStateException("Hugging Face summary failed without a captured exception");
        }

        throw new IllegalStateException("Failed to summarize text after retries", lastFailure);
    }

    private String summarizeChunk(String text) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String requestBody = objectMapper.writeValueAsString(
                new HuggingFaceRequestDto(text));

        String responseBody = restTemplate.execute(
                apiUrl,
                HttpMethod.POST,
                request -> {
                    request.getHeaders().putAll(headers);
                    StreamUtils.copy(
                            requestBody,
                            StandardCharsets.UTF_8,
                            request.getBody());
                },
                response -> StreamUtils.copyToString(
                        response.getBody(),
                        StandardCharsets.UTF_8));

        List<HuggingFaceResponseDto> summaries = objectMapper.readValue(
                responseBody,
                new TypeReference<>() {
                });

        if (summaries.isEmpty() || summaries.getFirst().getSummary_text() == null) {
            throw new RuntimeException("Hugging Face returned no summary");
        }

        return summaries.getFirst().getSummary_text();
    }

    private boolean isRetryable(Exception exception) {

        if (exception instanceof ResourceAccessException) {
            return true;
        }

        if (exception instanceof HttpStatusCodeException httpException) {
            HttpStatusCode statusCode = httpException.getStatusCode();

            return statusCode.is5xxServerError()
                    || statusCode.value() == 408
                    || statusCode.value() == 429;
        }

        return exception instanceof RestClientException;
    }

    private void sleepBeforeRetry(int attempt) {

        long backoff = Math.max(0, retryBackoffMs) * attempt;

        if (backoff == 0) {
            return;
        }

        try {
            Thread.sleep(backoff);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI retry interrupted", e);
        }
    }

    private String buildFallbackSummary(String text, int chunkCount) {

        List<String> fallbackSentences = splitIntoSentences(normalizeText(text));

        if (fallbackSentences.isEmpty()) {
            return "Unable to generate an AI summary, and no readable text was extracted from the PDF.";
        }

        return "Unable to generate an AI summary. Fallback extract from "
            + chunkCount
            + " document chunk(s): "
            + String.join(" ", fallbackSentences.stream()
                .limit(FALLBACK_SENTENCE_LIMIT)
                .toList());
    }

    public String generateRiskAnalysis(String text, ContractIntelligenceResult intelligence) {
        List<String> chunks = splitIntoChunks(text);

        if (chunks.isEmpty()) {
            return buildFallbackRiskAnalysis(intelligence);
        }

        try {
            if (chunks.size() == 1) {
                String prompt = buildRiskAnalysisPrompt(chunks.get(0), intelligence);
                return summarizeChunkWithRetry(prompt);
            }

            List<String> sectionAnalyses = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                String sectionPrompt = buildRiskAnalysisPrompt(chunks.get(i), intelligence);
                sectionAnalyses.add("Section " + (i + 1) + ": " + summarizeChunkWithRetry(sectionPrompt));
            }

            return summarizeCombinedRiskResults(String.join("\n\n", sectionAnalyses), 1);
        } catch (Exception e) {
            log.warn("AI risk analysis generation failed. Falling back to deterministic risk signals. Cause: {}", e.toString());
            return buildFallbackRiskAnalysis(intelligence);
        }
    }

    private String buildRiskAnalysisPrompt(String text, ContractIntelligenceResult intelligence) {
        StringBuilder metadata = new StringBuilder();

        metadata.append("Use the contract text and extracted risk signals to produce a clear risk analysis summary. ");
        metadata.append("Identify top risk categories, call out compliance gaps, and recommend next review steps.\n\n");

        metadata.append("Extracted risk score: ").append(intelligence.riskScore()).append("\n");
        metadata.append("Extracted risk level: ").append(intelligence.riskLevel()).append("\n");

        if (!intelligence.riskyClauses().isEmpty()) {
            metadata.append("Risky clauses: ").append(String.join("; ", intelligence.riskyClauses())).append("\n");
        }
        if (!intelligence.paymentObligations().isEmpty()) {
            metadata.append("Payment obligations: ").append(String.join("; ", intelligence.paymentObligations())).append("\n");
        }
        if (!intelligence.terminationConditions().isEmpty()) {
            metadata.append("Termination conditions: ").append(String.join("; ", intelligence.terminationConditions())).append("\n");
        }
        if (!intelligence.penalties().isEmpty()) {
            metadata.append("Penalties: ").append(String.join("; ", intelligence.penalties())).append("\n");
        }
        if (!intelligence.complianceKeywords().isEmpty()) {
            metadata.append("Compliance keywords: ").append(String.join(", ", intelligence.complianceKeywords())).append("\n");
        }

        metadata.append("\nContract text:\n").append(text);

        return metadata.toString();
    }

    private String summarizeCombinedRiskResults(String combinedAnalyses, int combinePass) throws Exception {
        List<String> riskChunks = splitIntoChunks(combinedAnalyses);

        if (riskChunks.size() == 1 || combinePass >= MAX_COMBINE_PASSES) {
            return summarizeChunkWithRetry("Create one final risk overview from the following section analyses. Highlight the most serious risks, compliance priorities, and an actionable review recommendation:\n\n" + combinedAnalyses);
        }

        List<String> condensedAnalyses = new ArrayList<>();

        for (String riskChunk : riskChunks) {
            condensedAnalyses.add(summarizeChunkWithRetry(
                    "Condense these risk summaries into sharper review guidance while preserving the strongest risk findings:\n\n"
                            + riskChunk));
        }

        return summarizeCombinedRiskResults(String.join("\n\n", condensedAnalyses), combinePass + 1);
    }

    private String buildFallbackRiskAnalysis(ContractIntelligenceResult intelligence) {
        if (intelligence == null) {
            return "Risk analysis could not be generated because contract intelligence was unavailable.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("AI risk analysis is temporarily unavailable. Here are the extracted contract risk signals and priorities:\n");
        builder.append("Risk level: ").append(intelligence.riskLevel()).append("\n");
        builder.append("Risk score: ").append(intelligence.riskScore()).append("/100\n\n");

        appendFallbackSection(builder, "Top risky clauses", intelligence.riskyClauses());
        appendFallbackSection(builder, "Payment and fee obligations", intelligence.paymentObligations());
        appendFallbackSection(builder, "Termination and renewal risks", intelligence.terminationConditions());
        appendFallbackSection(builder, "Penalty and liability signals", intelligence.penalties());
        appendFallbackSection(builder, "Compliance keywords", intelligence.complianceKeywords());

        builder.append("\nRecommended review focus: verify penalty limits, termination rights, indemnity exposure, and compliance requirements.");

        return builder.toString();
    }

    private void appendFallbackSection(StringBuilder builder, String title, List<String> values) {
        if (values.isEmpty()) {
            return;
        }

        builder.append(title).append(": \n");
        values.stream()
                .limit(FALLBACK_SENTENCE_LIMIT)
                .forEach(value -> builder.append("- ").append(value).append("\n"));
        builder.append("\n");
    }

    private List<String> splitIntoChunks(String text) {

        String normalizedText = normalizeText(text);

        if (normalizedText.isBlank()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        String[] paragraphs = normalizedText.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {
            appendTextUnit(paragraph.trim(), currentChunk, chunks);
        }

        flushChunk(currentChunk, chunks);

        return chunks;
    }

    private String normalizeText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t\\x0B\\f]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private void appendTextUnit(String textUnit, StringBuilder currentChunk, List<String> chunks) {

        if (textUnit.isBlank()) {
            return;
        }

        if (textUnit.length() <= CHUNK_SIZE) {
            appendToChunk(textUnit, currentChunk, chunks);
            return;
        }

        for (String sentence : splitIntoSentences(textUnit)) {
            if (sentence.length() <= CHUNK_SIZE) {
                appendToChunk(sentence, currentChunk, chunks);
            } else {
                splitLongSentence(sentence, currentChunk, chunks);
            }
        }
    }

    private List<String> splitIntoSentences(String text) {

        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(text);

        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String sentence = text.substring(start, end).trim();

            if (!sentence.isBlank()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    private void splitLongSentence(String sentence, StringBuilder currentChunk, List<String> chunks) {

        StringBuilder currentSegment = new StringBuilder();

        for (String word : sentence.split("\\s+")) {
            if (word.length() > CHUNK_SIZE) {
                if (!currentSegment.isEmpty()) {
                    appendToChunk(currentSegment.toString(), currentChunk, chunks);
                    currentSegment.setLength(0);
                }

                flushChunk(currentChunk, chunks);
                splitLongWord(word, chunks);
                continue;
            }

            if (!currentSegment.isEmpty()
                    && currentSegment.length() + 1 + word.length() > CHUNK_SIZE) {
                appendToChunk(currentSegment.toString(), currentChunk, chunks);
                currentSegment.setLength(0);
            }

            if (!currentSegment.isEmpty()) {
                currentSegment.append(' ');
            }

            currentSegment.append(word);
        }

        if (!currentSegment.isEmpty()) {
            appendToChunk(currentSegment.toString(), currentChunk, chunks);
        }
    }

    private void splitLongWord(String word, List<String> chunks) {

        for (int start = 0; start < word.length(); start += CHUNK_SIZE) {
            chunks.add(word.substring(start, Math.min(start + CHUNK_SIZE, word.length())));
        }
    }

    private void appendToChunk(String textUnit, StringBuilder currentChunk, List<String> chunks) {

        int separatorLength = currentChunk.isEmpty() ? 0 : 2;

        if (!currentChunk.isEmpty()
                && currentChunk.length() + separatorLength + textUnit.length() > CHUNK_SIZE) {
            flushChunk(currentChunk, chunks);
        }

        if (!currentChunk.isEmpty()) {
            currentChunk.append("\n\n");
        }

        currentChunk.append(textUnit);
    }

    private void flushChunk(StringBuilder currentChunk, List<String> chunks) {

        if (currentChunk.isEmpty()) {
            return;
        }

        chunks.add(currentChunk.toString());
        currentChunk.setLength(0);
    }
}
