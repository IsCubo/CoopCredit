package com.riwi.coopcredit.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AffiliateRequest {

    @NotBlank(message = "El documento es obligatorio")
    private String document;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario debe ser mayor a 0")
    private BigDecimal annualIncome;

    @NotNull(message = "La fecha de afiliación es obligatoria")
    private LocalDate affiliationDate;

    @NotBlank(message = "El estado es obligatorio")
    private String status;
}
