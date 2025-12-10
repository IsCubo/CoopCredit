package com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository;

import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {
}