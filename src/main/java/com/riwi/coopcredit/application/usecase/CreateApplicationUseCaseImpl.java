package com.riwi.coopcredit.application.usecase;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.domain.port.in.CreateApplicationUseCase;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import com.riwi.coopcredit.domain.port.out.CreditApplicationRepositoryPort; // Necesitas crear este puerto
import com.riwi.coopcredit.domain.port.out.RiskExternalPort;
import com.riwi.coopcredit.domain.model.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreateApplicationUseCaseImpl implements CreateApplicationUseCase {

    private final AffiliateRepositoryPort repo;
    private final CreditApplicationRepositoryPort applicationRepositoryPort; // <- ¡Nuevo Puerto!
    private final RiskExternalPort riskExternalPort;

    public CreateApplicationUseCaseImpl(AffiliateRepositoryPort repo, CreditApplicationRepositoryPort applicationRepositoryPort, RiskExternalPort riskExternalPort) {
        this.repo = repo;
        this.applicationRepositoryPort = applicationRepositoryPort;
        this.riskExternalPort = riskExternalPort;
    }

    @Override
    @Transactional
    public CreditApplication create(Long affiliateId, BigDecimal amount, Integer term) {

        // 1. Buscar Afiliado
        Affiliate affiliate = repo.findById(affiliateId)
                .orElseThrow(() -> new DomainException("Afiliado con ID " + affiliateId + " no encontrado."));

        // 2. Crear objeto de Solicitud Inicial (en estado PENDIENTE)
        CreditApplication newApplication = new CreditApplication(amount, term, affiliate);

        // 3. Persistir la solicitud inicial (opcional, pero buena práctica para trazabilidad)
        // La solicitud se actualizará en la misma transacción.
        CreditApplication savedApplication = applicationRepositoryPort.save(newApplication);

        // 4. Llamar al Servicio de Riesgo (Puerto de Salida)
        // Convertimos BigDecimal a Double para la llamada externa (asumiendo que el mock lo pide así)
        Integer riskScore = riskExternalPort.getRiskScore(
                affiliate.getDocument(),
                amount.doubleValue(),
                term
        );

        // 5. Evaluar y determinar el estado (Lógica de Negocio de CoopCredit)
        ApplicationStatus finalStatus;
        String riskLevel;

        if (riskScore >= 700) {
            finalStatus = ApplicationStatus.APROBADA;
            riskLevel = "BAJO RIESGO";
        } else if (riskScore >= 500) {
            // Un riesgo medio puede ser aprobado si el monto es bajo, pero por simplicidad:
            finalStatus = ApplicationStatus.APROBADA;
            riskLevel = "MEDIO RIESGO";
        } else {
            finalStatus = ApplicationStatus.RECHAZADA;
            riskLevel = "ALTO RIESGO";
        }

        // 6. Actualizar el Modelo de Dominio con los resultados
        savedApplication.setStatus(finalStatus);
        savedApplication.setRiskScore(riskScore);
        savedApplication.setRiskLevel(riskLevel);

        // 7. Persistir la solicitud actualizada
        return applicationRepositoryPort.save(savedApplication);
    }
}