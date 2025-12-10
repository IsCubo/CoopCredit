package com.riwi.coopcredit.infrastructure.adapter.input.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long affiliateId; // Mostrar solo el ID del afiliado
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private LocalDateTime applicationDate;
    private String status; // Ej: APROBADA, RECHAZADA
    private Integer riskScore;
    private String riskLevel;
}