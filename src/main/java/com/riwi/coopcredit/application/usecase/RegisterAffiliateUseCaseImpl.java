package com.riwi.coopcredit.application.usecase;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.domain.model.Affiliate;
import com.riwi.coopcredit.domain.port.in.RegisterAffiliateUseCase;
import com.riwi.coopcredit.domain.port.out.AffiliateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterAffiliateUseCaseImpl implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort repo;

    public RegisterAffiliateUseCaseImpl(AffiliateRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Registra un nuevo afiliado después de realizar validaciones de unicidad.
     * @param affiliateToRegister El objeto Affiliate a registrar.
     * @return El objeto Affiliate registrado con su ID.
     */
    @Override
    @Transactional
    public Affiliate register(Affiliate affiliateToRegister) {

        if (repo.findByDocument(affiliateToRegister.getDocument()).isPresent()) {
            throw new DomainException("El documento de identidad " + affiliateToRegister.getDocument() + " ya se encuentra registrado.");
        }

        return repo.save(affiliateToRegister);
    }
}