package com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository;

import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, Long> {
    Optional<AffiliateEntity> findByDocument(String document);
    Optional<AffiliateEntity> findByEmail(String email);
}