package com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository;

import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {
    List<CreditApplicationEntity> findAllByAffiliateId(Long affiliateId);
}