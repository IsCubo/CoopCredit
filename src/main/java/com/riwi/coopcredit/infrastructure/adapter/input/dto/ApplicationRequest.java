package com.riwi.coopcredit.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplicationRequest {

    @NotNull(message = "El ID del afiliado es obligatorio")
    private Long affiliateId;

    @NotNull(message = "El monto solicitado es obligatorio")
    @DecimalMin(value = "100.00", message = "El monto debe ser al menos 100")
    private BigDecimal requestedAmount;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es de 6 meses")
    private Integer termMonths;
}