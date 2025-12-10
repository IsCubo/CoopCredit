package com.riwi.coopcredit.infrastructure.adapter.output.persistence;

import com.riwi.coopcredit.domain.model.CreditApplication;
import com.riwi.coopcredit.domain.port.out.CreditApplicationRepositoryPort;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.mapper.CreditApplicationMapper;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.CreditApplicationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditApplicationPersistenceAdapter implements CreditApplicationRepositoryPort {

    private final CreditApplicationJpaRepository applicationJpaRepository;
    private final CreditApplicationMapper applicationMapper;

    @Override
    public CreditApplication save(CreditApplication application) {
        CreditApplicationEntity entityToSave = applicationMapper.toEntity(application);
        CreditApplicationEntity savedEntity = applicationJpaRepository.save(entityToSave);
        return applicationMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return applicationJpaRepository.findById(id)
                .map(applicationMapper::toDomain);
    }

    @Override
    public List<CreditApplication> findAll() {
        return applicationJpaRepository.findAll().stream()
                .map(applicationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditApplication> findAllByAffiliateId(Long affiliateId) {
        return applicationJpaRepository.findAllByAffiliateId(affiliateId).stream()
                .map(applicationMapper::toDomain)
                .collect(Collectors.toList());
    }
}