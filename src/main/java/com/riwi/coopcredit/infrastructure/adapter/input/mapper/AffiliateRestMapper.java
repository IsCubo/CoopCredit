package com.riwi.coopcredit.infrastructure.adapter.input.mapper;

import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.AffiliateRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.dto.AffiliateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AffiliateRestMapper {

    AffiliateResponse toResponse(Affiliate domain);

    Affiliate toDomain(AffiliateRequest request);
}