package com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_application")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CreditApplicationStatus status;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    // Relación Muchos-1 con AffiliateEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;

    // Enum auxiliar para la persistencia
    public enum CreditApplicationStatus {
        PENDIENTE, APROBADA, RECHAZADA
    }
}