package com.riwi.coopcredit.infrastructure.adapter.input.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String username;

    @NotBlank(message = "El documento es obligatorio")
    private String document;

    @Email(message = "El formato del email es inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El salario anual es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario anual debe ser un valor positivo")
    private BigDecimal annualIncome;
}