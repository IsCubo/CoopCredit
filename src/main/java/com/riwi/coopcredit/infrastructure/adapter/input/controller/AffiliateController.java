package com.riwi.coopcredit.infrastructure.adapter.input.controller;

import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.AffiliateRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.AffiliateResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.mapper.AffiliateRestMapper;
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

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener afiliado por ID",
            description = "Retorna los detalles de un afiliado específico. " +
                    "Requiere rol ROLE_ADMIN o ser el mismo afiliado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Afiliado obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado: token JWT inválido o expirado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Afiliado no encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<AffiliateResponse> getAffiliateById(@PathVariable Long id) {
        return affiliateRepositoryPort.findById(id)
                .map(affiliate -> ResponseEntity.ok(mapper.toResponse(affiliate)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Crear nuevo afiliado",
            description = "Crea un nuevo afiliado en el sistema. " +
                    "Requiere rol ROLE_ADMIN. " +
                    "El documento debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Afiliado creado exitosamente",
                    content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validación fallida: documento duplicado, salario inválido, etc."
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
    public ResponseEntity<AffiliateResponse> createAffiliate(@Valid @RequestBody AffiliateRequest request) {
        Affiliate affiliate = mapper.toDomain(request);
        Affiliate saved = affiliateRepositoryPort.save(affiliate);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar afiliado",
            description = "Actualiza la información de un afiliado existente. " +
                    "Requiere rol ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Afiliado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validación fallida"
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
                    responseCode = "404",
                    description = "Afiliado no encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<AffiliateResponse> updateAffiliate(
            @PathVariable Long id,
            @Valid @RequestBody AffiliateRequest request) {
        return affiliateRepositoryPort.findById(id)
                .map(existing -> {
                    Affiliate updated = mapper.toDomain(request);
                    updated.setId(id);
                    Affiliate saved = affiliateRepositoryPort.save(updated);
                    return ResponseEntity.ok(mapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}