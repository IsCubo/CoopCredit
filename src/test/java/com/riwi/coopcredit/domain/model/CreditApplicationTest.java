package com.riwi.coopcredit.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para el modelo CreditApplication")
class CreditApplicationTest {

    private Affiliate affiliate;
    private CreditApplication creditApplication;

    @BeforeEach
    void setUp() {
        affiliate = new Affiliate(
                1L,
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0,
                LocalDate.now().minusMonths(7),
                null
        );

        creditApplication = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
    }

    @Test
    @DisplayName("Debe crear una CreditApplication válida")
    void testCreateValidCreditApplication() {
        // Assert
        assertNotNull(creditApplication);
        assertEquals(new BigDecimal("5000000"), creditApplication.getRequestedAmount());
        assertEquals(36, creditApplication.getTermMonths());
        assertEquals(affiliate, creditApplication.getAffiliate());
        assertEquals(ApplicationStatus.PENDIENTE, creditApplication.getStatus());
        assertNotNull(creditApplication.getApplicationDate());
    }

    @Test
    @DisplayName("Debe establecer el estado de la solicitud correctamente")
    void testSetApplicationStatus() {
        // Act
        creditApplication.setStatus(ApplicationStatus.APROBADA);

        // Assert
        assertEquals(ApplicationStatus.APROBADA, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Debe establecer el score de riesgo correctamente")
    void testSetRiskScore() {
        // Act
        creditApplication.setRiskScore(750);

        // Assert
        assertEquals(750, creditApplication.getRiskScore());
    }

    @Test
    @DisplayName("Debe establecer el nivel de riesgo correctamente")
    void testSetRiskLevel() {
        // Act
        creditApplication.setRiskLevel("BAJO RIESGO");

        // Assert
        assertEquals("BAJO RIESGO", creditApplication.getRiskLevel());
    }

    @Test
    @DisplayName("Debe verificar que una solicitud está en estado PENDIENTE")
    void testIsPendingTrue() {
        // Assert
        assertTrue(creditApplication.isPending());
    }

    @Test
    @DisplayName("Debe verificar que una solicitud NO está en estado PENDIENTE")
    void testIsPendingFalse() {
        // Arrange
        creditApplication.setStatus(ApplicationStatus.APROBADA);

        // Assert
        assertFalse(creditApplication.isPending());
    }

    @Test
    @DisplayName("Debe obtener y establecer el ID correctamente")
    void testSetAndGetId() {
        // Act
        creditApplication.setId(1L);

        // Assert
        assertEquals(1L, creditApplication.getId());
    }

    @Test
    @DisplayName("Debe obtener y establecer el monto solicitado correctamente")
    void testSetAndGetRequestedAmount() {
        // Arrange
        BigDecimal newAmount = new BigDecimal("7500000");

        // Act
        creditApplication.setRequestedAmount(newAmount);

        // Assert
        assertEquals(newAmount, creditApplication.getRequestedAmount());
    }

    @Test
    @DisplayName("Debe obtener y establecer el plazo en meses correctamente")
    void testSetAndGetTermMonths() {
        // Arrange
        Integer newTerm = 48;

        // Act
        creditApplication.setTermMonths(newTerm);

        // Assert
        assertEquals(newTerm, creditApplication.getTermMonths());
    }

    @Test
    @DisplayName("Debe obtener y establecer la fecha de solicitud correctamente")
    void testSetAndGetApplicationDate() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);

        // Act
        creditApplication.setApplicationDate(newDate);

        // Assert
        assertEquals(newDate, creditApplication.getApplicationDate());
    }

    @Test
    @DisplayName("Debe obtener y establecer el afiliado correctamente")
    void testSetAndGetAffiliate() {
        // Arrange
        Affiliate newAffiliate = new Affiliate(
                2L,
                "1017654312",
                "María",
                "García",
                "maria@example.com",
                2800000.0,
                LocalDate.now().minusMonths(8),
                null
        );

        // Act
        creditApplication.setAffiliate(newAffiliate);

        // Assert
        assertEquals(newAffiliate, creditApplication.getAffiliate());
        assertEquals("1017654312", creditApplication.getAffiliate().getDocument());
    }

    @Test
    @DisplayName("Debe crear CreditApplication con constructor sin argumentos")
    void testCreateCreditApplicationWithNoArgsConstructor() {
        // Act
        CreditApplication app = new CreditApplication();

        // Assert
        assertNotNull(app);
        assertNull(app.getId());
        assertNull(app.getRequestedAmount());
        assertNull(app.getTermMonths());
    }

    @Test
    @DisplayName("Debe cambiar estado de PENDIENTE a RECHAZADA")
    void testChangeStatusFromPendingToRejected() {
        // Arrange
        assertTrue(creditApplication.isPending());

        // Act
        creditApplication.setStatus(ApplicationStatus.RECHAZADA);

        // Assert
        assertFalse(creditApplication.isPending());
        assertEquals(ApplicationStatus.RECHAZADA, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Debe manejar múltiples cambios de estado")
    void testMultipleStatusChanges() {
        // Arrange
        assertEquals(ApplicationStatus.PENDIENTE, creditApplication.getStatus());

        // Act & Assert
        creditApplication.setStatus(ApplicationStatus.APROBADA);
        assertEquals(ApplicationStatus.APROBADA, creditApplication.getStatus());

        creditApplication.setStatus(ApplicationStatus.RECHAZADA);
        assertEquals(ApplicationStatus.RECHAZADA, creditApplication.getStatus());

        creditApplication.setStatus(ApplicationStatus.APROBADA);
        assertEquals(ApplicationStatus.APROBADA, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Debe establecer score de riesgo en límite bajo (300)")
    void testSetRiskScoreLow() {
        // Act
        creditApplication.setRiskScore(300);

        // Assert
        assertEquals(300, creditApplication.getRiskScore());
    }

    @Test
    @DisplayName("Debe establecer score de riesgo en límite alto (950)")
    void testSetRiskScoreHigh() {
        // Act
        creditApplication.setRiskScore(950);

        // Assert
        assertEquals(950, creditApplication.getRiskScore());
    }

    @Test
    @DisplayName("Debe mantener la fecha de aplicación después de cambios de estado")
    void testApplicationDatePersistsAfterStatusChange() {
        // Arrange
        LocalDateTime originalDate = creditApplication.getApplicationDate();

        // Act
        creditApplication.setStatus(ApplicationStatus.APROBADA);
        creditApplication.setRiskScore(750);

        // Assert
        assertEquals(originalDate, creditApplication.getApplicationDate());
    }

    @Test
    @DisplayName("Debe comparar dos CreditApplications con el mismo ID")
    void testCreditApplicationEquality() {
        // Arrange
        CreditApplication app1 = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
        app1.setId(1L);

        CreditApplication app2 = new CreditApplication(
                new BigDecimal("5000000"),
                36,
                affiliate
        );
        app2.setId(1L);

        // Assert
        assertEquals(app1.getId(), app2.getId());
    }
}
