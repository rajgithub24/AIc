package com.aicontract.contractanalyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(length = 5000)
    private String summary;

    private String status;

    @Column(length = 100000)
    private String extractedText;

    @Column(length = 20000)
    private String riskyClauses;

    @Column(length = 20000)
    private String paymentObligations;

    @Column(length = 20000)
    private String terminationConditions;

    @Column(length = 20000)
    private String penalties;

    @Column(length = 5000)
    private String complianceKeywords;

    @Column(length = 10000)
    private String riskAnalysis;

    private Integer riskScore;

    private String riskLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
