package com.riwi.coopcredit.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreditApplication {

    private Long id;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private Integer riskScore;
    private String riskLevel;
    private Affiliate affiliate; // Relación con el Afiliado

    public CreditApplication(BigDecimal requestedAmount, Integer termMonths, Affiliate affiliate) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.affiliate = affiliate;
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.PENDIENTE; // Estado inicial
    }

    public CreditApplication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Affiliate getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Affiliate affiliate) {
        this.affiliate = affiliate;
    }

    //Verificar si la solicitud está pendiente
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDIENTE;
    }
}