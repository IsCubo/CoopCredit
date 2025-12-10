package com.riwi.coopcredit.infrastructure.adapter.output.persistence.mapper;

import com.riwi.coopcredit.domain.model.ApplicationStatus;
import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity.CreditApplicationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Indica que es un componente de Spring (Singleton)
public interface CreditApplicationMapper {

    // Mapeo de Enums: Dominio -> Entidad
    CreditApplicationStatus toEntityStatus(ApplicationStatus domainStatus);

    // Mapeo de Enums: Entidad -> Dominio
    ApplicationStatus toDomainStatus(CreditApplicationStatus entityStatus);

    // Mapeo de Entidad a Dominio
    @Mapping(target = "status", source = "status")
    @Mapping(target = "affiliate", ignore = true) // Ignoramos el afiliado para evitar recursión
    CreditApplication toDomain(CreditApplicationEntity entity);

    // Mapeo de Dominio a Entidad
    @Mapping(target = "status", source = "status")
    @Mapping(target = "affiliate", ignore = true) // La relación se establece manualmente en el servicio
    CreditApplicationEntity toEntity(CreditApplication domain);
}