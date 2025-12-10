package com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "affiliate")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String document;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "annual_income", precision = 15, scale = 2, nullable = false)
    private BigDecimal annualIncome;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    // Relación 1-1 con UserEntity (Usuario de Seguridad)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    // Relación 1-Muchos con Solicitudes de Crédito
    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditApplicationEntity> applications;
}