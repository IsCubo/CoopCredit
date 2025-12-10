package com.riwi.coopcredit.infrastructure.adapter.output.external.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskRequestDto {
    private String documento;
    private Double monto;
    private Integer plazo;
}