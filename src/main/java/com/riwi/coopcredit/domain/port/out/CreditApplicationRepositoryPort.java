package com.riwi.coopcredit.domain.port.out;

import com.riwi.coopcredit.domain.model.CreditApplication;
import java.util.Optional;
import java.util.List;

// Puerto para interactuar con la persistencia de Solicitudes de Crédito
public interface CreditApplicationRepositoryPort {

    CreditApplication save(CreditApplication application);
    Optional<CreditApplication> findById(Long id);
    List<CreditApplication> findAll();
    List<CreditApplication> findAllByAffiliateId(Long affiliateId);

}