package com.riwi.coopcredit.domain.model;

import com.riwi.coopcredit.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para el modelo Affiliate")
class AffiliateTest {

    @Test
    @DisplayName("Debe crear un Affiliate válido con constructor sin ID")
    void testCreateValidAffiliateWithoutId() {
        // Act
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );

        // Assert
        assertNotNull(affiliate);
        assertEquals("1017654311", affiliate.getDocument());
        assertEquals("Juan", affiliate.getFirstName());
        assertEquals("Pérez", affiliate.getLastName());
        assertEquals("juan@example.com", affiliate.getEmail());
        assertEquals(3500000.0, affiliate.getAnnualIncome());
        assertNotNull(affiliate.getRegistrationDate());
        assertEquals(LocalDate.now(), affiliate.getRegistrationDate());
    }

    @Test
    @DisplayName("Debe crear un Affiliate válido con constructor completo")
    void testCreateValidAffiliateWithId() {
        // Act
        Affiliate affiliate = new Affiliate(
                1L,
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0,
                LocalDate.now().minusMonths(6),
                null
        );

        // Assert
        assertNotNull(affiliate);
        assertEquals(1L, affiliate.getId());
        assertEquals("1017654311", affiliate.getDocument());
        assertEquals("Juan", affiliate.getFirstName());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el salario es negativo")
    void testCreateAffiliateWithNegativeSalary() {
        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Affiliate(
                    "1017654311",
                    "Juan",
                    "Pérez",
                    "juan@example.com",
                    -1000.0
            );
        });

        assertTrue(exception.getMessage().contains("salario anual debe ser un valor positivo"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el salario es cero")
    void testCreateAffiliateWithZeroSalary() {
        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Affiliate(
                    "1017654311",
                    "Juan",
                    "Pérez",
                    "juan@example.com",
                    0.0
            );
        });

        assertTrue(exception.getMessage().contains("salario anual debe ser un valor positivo"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, -1.0, -0.01, 0.0})
    @DisplayName("Debe lanzar excepción para salarios inválidos (negativos o cero)")
    void testCreateAffiliateWithInvalidSalaries(double salary) {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            new Affiliate(
                    "1017654311",
                    "Juan",
                    "Pérez",
                    "juan@example.com",
                    salary
            );
        });
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1.0, 1000.0, 3500000.0, 999999999.0})
    @DisplayName("Debe crear Affiliate con salarios válidos (positivos)")
    void testCreateAffiliateWithValidSalaries(double salary) {
        // Act
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                salary
        );

        // Assert
        assertNotNull(affiliate);
        assertEquals(salary, affiliate.getAnnualIncome());
    }

    @Test
    @DisplayName("Debe actualizar el salario correctamente")
    void testUpdateAnnualIncome() {
        // Arrange
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );

        // Act
        affiliate.setAnnualIncome(4000000.0);

        // Assert
        assertEquals(4000000.0, affiliate.getAnnualIncome());
    }

    @Test
    @DisplayName("Debe actualizar el documento correctamente")
    void testUpdateDocument() {
        // Arrange
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );

        // Act
        affiliate.setDocument("1017654312");

        // Assert
        assertEquals("1017654312", affiliate.getDocument());
    }

    @Test
    @DisplayName("Debe actualizar el email correctamente")
    void testUpdateEmail() {
        // Arrange
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );

        // Act
        affiliate.setEmail("newemail@example.com");

        // Assert
        assertEquals("newemail@example.com", affiliate.getEmail());
    }

    @Test
    @DisplayName("Debe crear Affiliate con constructor sin argumentos")
    void testCreateAffiliateWithNoArgsConstructor() {
        // Act
        Affiliate affiliate = new Affiliate();

        // Assert
        assertNotNull(affiliate);
        assertNull(affiliate.getId());
        assertNull(affiliate.getDocument());
        assertNull(affiliate.getFirstName());
    }

    @Test
    @DisplayName("Debe obtener y establecer ID correctamente")
    void testSetAndGetId() {
        // Arrange
        Affiliate affiliate = new Affiliate();

        // Act
        affiliate.setId(1L);

        // Assert
        assertEquals(1L, affiliate.getId());
    }

    @Test
    @DisplayName("Debe obtener y establecer fecha de registro correctamente")
    void testSetAndGetRegistrationDate() {
        // Arrange
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );
        LocalDate newDate = LocalDate.now().minusMonths(12);

        // Act
        affiliate.setRegistrationDate(newDate);

        // Assert
        assertEquals(newDate, affiliate.getRegistrationDate());
    }

    @Test
    @DisplayName("Debe obtener y establecer lista de aplicaciones correctamente")
    void testSetAndGetApplications() {
        // Arrange
        Affiliate affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );

        // Act
        affiliate.setApplications(null);

        // Assert
        assertNull(affiliate.getApplications());
    }
}
