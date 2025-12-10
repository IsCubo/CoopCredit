package com.riwi.coopcredit.infrastructure.adapter.output.persistence.mapper;

import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreditApplicationMapper.class})
public interface AffiliateMapper {

    // Mapeo de Entidad a Dominio
    @Mapping(target = "annualIncome", source = "annualIncome")
    Affiliate toDomain(AffiliateEntity entity);

    // Mapeo de Dominio a Entidad
    @Mapping(target = "annualIncome", source = "annualIncome")
    @Mapping(target = "user", ignore = true)
    AffiliateEntity toEntity(Affiliate domain);
}