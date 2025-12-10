package com.riwi.coopcredit.domain.model;

import com.riwi.coopcredit.domain.exception.DomainException;

import java.time.LocalDate;
import java.util.List;

public class Affiliate {

    // Constructor sin argumentos para MapStruct
    public Affiliate() {}

    private Long id;
    private String document; // Cédula/Documento, debe ser único
    private String firstName;
    private String lastName;
    private String email;
    private Double annualIncome; // Salario anual
    private LocalDate registrationDate;
    private List<CreditApplication> applications; // Relación con solicitudes

    // Constructor completo
    public Affiliate(Long id, String document, String firstName, String lastName, String email, Double annualIncome, LocalDate registrationDate, List<CreditApplication> applications) {
        this.id = id;
        this.document = document;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.annualIncome = annualIncome;
        this.registrationDate = registrationDate;
        this.applications = applications;
        this.validate();
    }

    // Constructor sin ID (para objetos nuevos)
    public Affiliate(String document, String firstName, String lastName, String email, Double annualIncome) {
        this.document = document;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.annualIncome = annualIncome;
        this.registrationDate = LocalDate.now();
        this.validate();
    }

    // Método de validación del dominio
    private void validate() {
        if (this.annualIncome <= 0) {
            throw new DomainException("El salario anual debe ser un valor positivo.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<CreditApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<CreditApplication> applications) {
        this.applications = applications;
    }
}