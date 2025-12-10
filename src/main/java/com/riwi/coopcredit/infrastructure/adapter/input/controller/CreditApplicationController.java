package com.riwi.coopcredit.infrastructure.adapter.input.controller;

import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.domain.port.in.CreateApplicationUseCase;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.ApplicationRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.ApplicationResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.mapper.ApplicationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Crédito", description = "Endpoints para gestión de solicitudes de crédito y evaluación de riesgo")
@SecurityRequirement(name = "BearerAuth")
public class CreditApplicationController {

    private final CreateApplicationUseCase createApplicationUseCase;
    private final ApplicationRestMapper mapper;

    @PostMapping
    @Operation(
            summary = "Crear nueva solicitud de crédito",
            description = "Crea una nueva solicitud de crédito para un afiliado. " +
                    "La solicitud inicia en estado PENDIENTE y será evaluada automáticamente. " +
                    "Requiere rol ROLE_AFILIADO o ROLE_ADMIN. " +
                    "El afiliado debe estar ACTIVO y cumplir con los requisitos de antigüedad (mínimo 6 meses)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Solicitud de crédito creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validación fallida: monto inválido, plazo fuera de rango, afiliado inactivo, etc."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado: token JWT inválido o expirado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere rol ROLE_AFILIADO o ROLE_ADMIN"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Afiliado no encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor o falla en la evaluación de riesgo"
            )
    })
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest request) {

        // 1. Llamar al Caso de Uso (Lógica de Negocio)
        CreditApplication createdApplication = createApplicationUseCase.create(
                request.getAffiliateId(),
                request.getRequestedAmount(),
                request.getTermMonths()
        );

        // 2. Mapear el Dominio a la Respuesta DTO
        ApplicationResponse response = mapper.toResponse(createdApplication);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}