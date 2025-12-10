package com.riwi.coopcredit.infrastructure.adapter.output.persistence;

import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.mapper.AffiliateMapper;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.AffiliateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AffiliatePersistenceAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository affiliateJpaRepository;
    private final AffiliateMapper affiliateMapper;

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entityToSave = affiliateMapper.toEntity(affiliate);
        // 2. Guardar la Entidad JPA
        AffiliateEntity savedEntity = affiliateJpaRepository.save(entityToSave);
        // 3. Convertir la Entidad JPA guardada de nuevo al Modelo de Dominio y retornarla
        return affiliateMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return affiliateJpaRepository.findById(id)
                .map(affiliateMapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        return affiliateJpaRepository.findByDocument(document)
                .map(affiliateMapper::toDomain);
    }

    @Override
    public List<Affiliate> findAll() {
        return affiliateJpaRepository.findAll().stream()
                .map(affiliateMapper::toDomain)
                .collect(Collectors.toList());
    }
}