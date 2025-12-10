package com.riwi.coopcredit.infrastructure.adapter.output.external.dto;

import lombok.Data;

@Data
public class RiskResponseDto {
    private String documento;
    private Integer score;
    private String nivelRiesgo; // ALTO, MEDIO, BAJO
    private String detalle;
}