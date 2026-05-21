package com.aicontract.contractanalyzer.ai;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class ContractIntelligenceService {

    private static final int MAX_CLAUSES_PER_CATEGORY = 8;

    private static final List<String> RISK_TERMS = List.of(
            "indemnify", "indemnification", "hold harmless", "unlimited liability",
            "sole discretion", "without notice", "auto-renew", "automatic renewal",
            "non-compete", "exclusive remedy", "warranty disclaimer", "as is",
            "limitation of liability", "consequential damages", "arbitration",
            "jurisdiction", "governing law", "force majeure", "assignment",
            "notice period", "renewal", "termination fee", "default");

    private static final List<String> PAYMENT_TERMS = List.of(
            "payment", "invoice", "fee", "fees", "due", "payable", "taxes",
            "billing", "reimburse", "purchase order", "net 30", "net thirty",
            "installment", "advance payment", "prepayment", "payment plan",
            "recurring payment", "late payment", "chargeback");

    private static final List<String> TERMINATION_TERMS = List.of(
            "terminate", "termination", "cancel", "cancellation", "breach",
            "notice period", "written notice", "expiration", "renewal", "survive",
            "suspension", "default", "wind down", "termination fee", "material breach");

    private static final List<String> PENALTY_TERMS = List.of(
            "penalty", "penalties", "late fee", "interest", "liquidated damages",
            "damages", "forfeit", "suspension", "default", "chargeback",
            "withhold", "fine", "penalty clause", "chargeback");

    private static final List<String> COMPLIANCE_TERMS = List.of(
            "gdpr", "hipaa", "pci", "aml", "kyc", "soc 2", "sox",
            "data protection", "personal data", "privacy", "confidentiality",
            "audit", "regulatory", "compliance", "sanctions", "anti-bribery",
            "anti-corruption", "cybersecurity", "data breach", "export control",
            "trade sanctions", "confidential information");

    public ContractIntelligenceResult analyze(String text) {

        List<String> sentences = splitIntoSentences(text);

        List<String> riskyClauses = extractClauses(sentences, RISK_TERMS);
        List<String> paymentObligations = extractClauses(sentences, PAYMENT_TERMS);
        List<String> terminationConditions = extractClauses(sentences, TERMINATION_TERMS);
        List<String> penalties = extractClauses(sentences, PENALTY_TERMS);
        List<String> complianceKeywords = extractKeywords(text, COMPLIANCE_TERMS);

        int riskScore = calculateRiskScore(
                riskyClauses,
                paymentObligations,
                terminationConditions,
                penalties,
                complianceKeywords);

        return new ContractIntelligenceResult(
                riskyClauses,
                paymentObligations,
                terminationConditions,
                penalties,
                complianceKeywords,
                riskScore,
                calculateRiskLevel(riskScore));
    }

    private List<String> splitIntoSentences(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = text.replaceAll("\\s+", " ").trim();
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(normalizedText);

        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String sentence = normalizedText.substring(start, end).trim();

            if (!sentence.isBlank()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    private List<String> extractClauses(List<String> sentences, List<String> terms) {

        Set<String> matches = new LinkedHashSet<>();

        for (String sentence : sentences) {
            String normalizedSentence = sentence.toLowerCase(Locale.US);

            if (containsAny(normalizedSentence, terms)) {
                matches.add(sentence);
            }

            if (matches.size() >= MAX_CLAUSES_PER_CATEGORY) {
                break;
            }
        }

        return List.copyOf(matches);
    }

    private List<String> extractKeywords(String text, List<String> terms) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = text.toLowerCase(Locale.US);
        List<String> keywords = new ArrayList<>();

        for (String term : terms) {
            if (normalizedText.contains(term)) {
                keywords.add(term.toUpperCase(Locale.US));
            }
        }

        return keywords;
    }

    private boolean containsAny(String text, List<String> terms) {

        for (String term : terms) {
            if (text.contains(term)) {
                return true;
            }
        }

        return false;
    }

    private int calculateRiskScore(
            List<String> riskyClauses,
            List<String> paymentObligations,
            List<String> terminationConditions,
            List<String> penalties,
            List<String> complianceKeywords) {

        int score = 0;
        score += riskyClauses.size() * 15;
        score += penalties.size() * 12;
        score += terminationConditions.size() * 8;
        score += paymentObligations.size() * 5;
        score += complianceKeywords.size() * 4;

        return Math.min(score, 100);
    }

    private String calculateRiskLevel(int riskScore) {

        if (riskScore >= 70) {
            return "HIGH";
        }

        if (riskScore >= 35) {
            return "MEDIUM";
        }

        return "LOW";
    }
}
