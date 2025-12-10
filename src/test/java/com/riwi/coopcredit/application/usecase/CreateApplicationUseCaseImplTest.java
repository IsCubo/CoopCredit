package com.riwi.coopcredit.application.usecase;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.model.ApplicationStatus;
import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import com.riwi.coopcredit.domain.port.out.CreditApplicationRepositoryPort;
import com.riwi.coopcredit.domain.port.out.RiskExternalPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para CreateApplicationUseCaseImpl")
class CreateApplicationUseCaseImplTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @Mock
    private CreditApplicationRepositoryPort applicationRepositoryPort;

    @Mock
    private RiskExternalPort riskExternalPort;

    @InjectMocks
    private CreateApplicationUseCaseImpl createApplicationUseCase;

    private Affiliate affiliate;
    private CreditApplication creditApplication;

    @BeforeEach
    void setUp() {
        // Arrange: Preparar datos de prueba
        affiliate = new Affiliate(
                1L,
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0,
                LocalDate.now().minusMonths(7), // Más de 6 meses
                null
        );

        creditApplication = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
    }

    @Test
    @DisplayName("Debe crear solicitud de crédito con riesgo bajo y estado APROBADA")
    void testCreateApplicationWithLowRiskApproved() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(750); // Score alto = bajo riesgo

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.APROBADA, result.getStatus());
        assertEquals(750, result.getRiskScore());
        assertEquals("BAJO RIESGO", result.getRiskLevel());

        // Verify
        verify(affiliateRepositoryPort, times(1)).findById(1L);
        verify(applicationRepositoryPort, times(2)).save(any(CreditApplication.class));
        verify(riskExternalPort, times(1)).getRiskScore("1017654311", 5000000.0, 36);
    }

    @Test
    @DisplayName("Debe crear solicitud de crédito con riesgo medio y estado APROBADA")
    void testCreateApplicationWithMediumRiskApproved() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(600); // Score medio = riesgo medio

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.APROBADA, result.getStatus());
        assertEquals(600, result.getRiskScore());
        assertEquals("MEDIO RIESGO", result.getRiskLevel());

        // Verify
        verify(riskExternalPort, times(1)).getRiskScore("1017654311", 5000000.0, 36);
    }

    @Test
    @DisplayName("Debe crear solicitud de crédito con riesgo alto y estado RECHAZADA")
    void testCreateApplicationWithHighRiskRejected() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(400); // Score bajo = alto riesgo

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.RECHAZADA, result.getStatus());
        assertEquals(400, result.getRiskScore());
        assertEquals("ALTO RIESGO", result.getRiskLevel());

        // Verify
        verify(riskExternalPort, times(1)).getRiskScore("1017654311", 5000000.0, 36);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el afiliado no existe")
    void testCreateApplicationAffiliateNotFound() {
        // Arrange
        when(affiliateRepositoryPort.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            createApplicationUseCase.create(999L, new BigDecimal("5000000"), 36);
        });

        assertTrue(exception.getMessage().contains("no encontrado"));

        // Verify
        verify(affiliateRepositoryPort, times(1)).findById(999L);
        verify(applicationRepositoryPort, never()).save(any());
        verify(riskExternalPort, never()).getRiskScore(anyString(), anyDouble(), anyInt());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el servicio de riesgo falla")
    void testCreateApplicationRiskServiceFails() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenThrow(new DomainException("Error de servicio externo de riesgo"));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            createApplicationUseCase.create(1L, new BigDecimal("5000000"), 36);
        });

        assertTrue(exception.getMessage().contains("Error de servicio externo"));

        // Verify
        verify(riskExternalPort, times(1)).getRiskScore(anyString(), anyDouble(), anyInt());
    }

    @Test
    @DisplayName("Debe guardar la solicitud dos veces (inicial y actualizada)")
    void testCreateApplicationSavesTwice() {
        // Arrange
        CreditApplication savedApp1 = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
        savedApp1.setId(1L);

        CreditApplication savedApp2 = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
        savedApp2.setId(1L);
        savedApp2.setStatus(ApplicationStatus.APROBADA);
        savedApp2.setRiskScore(750);
        savedApp2.setRiskLevel("BAJO RIESGO");

        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(savedApp1)
                .thenReturn(savedApp2);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(750);

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.APROBADA, result.getStatus());

        // Verify
        verify(applicationRepositoryPort, times(2)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Debe convertir BigDecimal a Double correctamente para el servicio de riesgo")
    void testCreateApplicationConvertsBigDecimalToDouble() {
        // Arrange
        BigDecimal amount = new BigDecimal("7500000.50");
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(700);

        // Act
        createApplicationUseCase.create(1L, amount, 48);

        // Assert & Verify
        verify(riskExternalPort, times(1)).getRiskScore(
                "1017654311",
                7500000.50,
                48
        );
    }

    @Test
    @DisplayName("Debe manejar score de riesgo en límite (500)")
    void testCreateApplicationWithBoundaryRiskScore500() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(500); // Límite entre medio y alto

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertEquals(ApplicationStatus.APROBADA, result.getStatus());
        assertEquals("MEDIO RIESGO", result.getRiskLevel());
    }

    @Test
    @DisplayName("Debe manejar score de riesgo en límite (700)")
    void testCreateApplicationWithBoundaryRiskScore700() {
        // Arrange
        when(affiliateRepositoryPort.findById(1L))
                .thenReturn(Optional.of(affiliate));
        when(applicationRepositoryPort.save(any(CreditApplication.class)))
                .thenReturn(creditApplication);
        when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
                .thenReturn(700); // Límite entre medio y bajo

        // Act
        CreditApplication result = createApplicationUseCase.create(
                1L,
                new BigDecimal("5000000"),
                36
        );

        // Assert
        assertEquals(ApplicationStatus.APROBADA, result.getStatus());
        assertEquals("BAJO RIESGO", result.getRiskLevel());
    }
}
