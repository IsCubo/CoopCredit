package com.riwi.coopcredit.application.usecase;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para RegisterAffiliateUseCaseImpl")
class RegisterAffiliateUseCaseImplTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private RegisterAffiliateUseCaseImpl registerAffiliateUseCase;

    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        // Arrange: Preparar datos de prueba
        affiliate = new Affiliate(
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0
        );
    }

    @Test
    @DisplayName("Debe registrar un nuevo afiliado exitosamente")
    void testRegisterAffiliateSuccess() {
        // Arrange
        Affiliate savedAffiliate = new Affiliate(
                1L,
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0,
                LocalDate.now(),
                null
        );

        when(affiliateRepositoryPort.findByDocument("1017654311"))
                .thenReturn(Optional.empty());
        when(affiliateRepositoryPort.save(any(Affiliate.class)))
                .thenReturn(savedAffiliate);

        // Act
        Affiliate result = registerAffiliateUseCase.register(affiliate);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1017654311", result.getDocument());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Pérez", result.getLastName());

        // Verify
        verify(affiliateRepositoryPort, times(1)).findByDocument("1017654311");
        verify(affiliateRepositoryPort, times(1)).save(affiliate);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el documento ya existe")
    void testRegisterAffiliateWithDuplicateDocument() {
        // Arrange
        Affiliate existingAffiliate = new Affiliate(
                1L,
                "1017654311",
                "Existing",
                "User",
                "existing@example.com",
                2500000.0,
                LocalDate.now(),
                null
        );

        when(affiliateRepositoryPort.findByDocument("1017654311"))
                .thenReturn(Optional.of(existingAffiliate));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            registerAffiliateUseCase.register(affiliate);
        });

        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));

        // Verify
        verify(affiliateRepositoryPort, times(1)).findByDocument("1017654311");
        verify(affiliateRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el salario es inválido (en el constructor)")
    void testRegisterAffiliateWithInvalidSalary() {
        // Act & Assert - La excepción se lanza en el constructor, no en register()
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Affiliate(
                    "1017654312",
                    "María",
                    "García",
                    "maria@example.com",
                    -1000.0 // Salario negativo
            );
        });

        assertTrue(exception.getMessage().contains("salario anual debe ser un valor positivo"));

        // Verify - No se debe llamar al repositorio
        verify(affiliateRepositoryPort, never()).findByDocument(any());
        verify(affiliateRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el salario es cero (en el constructor)")
    void testRegisterAffiliateWithZeroSalary() {
        // Act & Assert - La excepción se lanza en el constructor, no en register()
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Affiliate(
                    "1017654313",
                    "Carlos",
                    "Rodríguez",
                    "carlos@example.com",
                    0.0 // Salario cero
            );
        });

        assertTrue(exception.getMessage().contains("salario anual debe ser un valor positivo"));

        // Verify - No se debe llamar al repositorio
        verify(affiliateRepositoryPort, never()).findByDocument(any());
        verify(affiliateRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe llamar al repositorio exactamente una vez para guardar")
    void testRegisterAffiliateCallsRepositoryOnce() {
        // Arrange
        Affiliate savedAffiliate = new Affiliate(
                1L,
                "1017654311",
                "Juan",
                "Pérez",
                "juan@example.com",
                3500000.0,
                LocalDate.now(),
                null
        );

        when(affiliateRepositoryPort.findByDocument("1017654311"))
                .thenReturn(Optional.empty());
        when(affiliateRepositoryPort.save(any(Affiliate.class)))
                .thenReturn(savedAffiliate);

        // Act
        registerAffiliateUseCase.register(affiliate);

        // Assert & Verify
        verify(affiliateRepositoryPort, times(1)).save(any(Affiliate.class));
    }
}
