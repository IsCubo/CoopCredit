package com.riwi.coopcredit.infrastructure.adapter.input.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AffiliateResponse {
    private Long id;
    private String document;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal annualIncome;
    private LocalDate registrationDate;
}