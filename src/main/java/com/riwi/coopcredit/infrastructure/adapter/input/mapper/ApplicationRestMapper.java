package com.riwi.coopcredit.infrastructure.adapter.input.mapper;

import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.ApplicationRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.ApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationRestMapper {

    // Request a Domain (Solo para los campos que vienen en la solicitud)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "riskScore", ignore = true)
    @Mapping(target = "riskLevel", ignore = true)
    @Mapping(target = "affiliate", ignore = true)
    // El caso de uso maneja la creación final y el mapeo del ID del afiliado.
    CreditApplication toDomain(ApplicationRequest request);

    // Domain a Response
    @Mapping(target = "affiliateId", source = "affiliate.id") // Extraer el ID del afiliado
    @Mapping(target = "status", source = "status") // MapStruct maneja la conversión de Enum a String
    ApplicationResponse toResponse(CreditApplication domain);
}