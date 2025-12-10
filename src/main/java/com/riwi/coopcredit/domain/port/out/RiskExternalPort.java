package com.riwi.coopcredit.domain.port.out;

// Puerto para interactuar con el Microservicio de Evaluación de Riesgo (risk-central-mock-service)
public interface RiskExternalPort {
    Integer getRiskScore(String document, Double amount, Integer term);
}
