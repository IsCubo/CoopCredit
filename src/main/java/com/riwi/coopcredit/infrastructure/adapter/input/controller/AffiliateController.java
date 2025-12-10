package com.riwi.coopcredit.infrastructure.adapter.input.controller;

import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.AffiliateResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.mapper.AffiliateRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/affiliates")
@RequiredArgsConstructor
@Tag(name = "Afiliados", description = "Endpoints para gestión de afiliados (Cooperativistas)")
@SecurityRequirement(name = "BearerAuth")
public class AffiliateController {

    private final AffiliateRepositoryPort affiliateRepositoryPort;
    private final AffiliateRestMapper mapper;

    @GetMapping
    @Operation(
            summary = "Obtener todos los afiliados",
            description = "Retorna la lista completa de afiliados registrados en el sistema. " +
                    "Requiere rol ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de afiliados obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado: token JWT inválido o expirado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere rol ROLE_ADMIN"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<AffiliateResponse>> getAllAffiliates() {
        List<AffiliateResponse> responses = affiliateRepositoryPort.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}