package com.riwi.coopcredit.domain.port.in;

import com.riwi.coopcredit.domain.model.CreditApplication;

import java.math.BigDecimal;

public interface CreateApplicationUseCase {

    CreditApplication create(Long affiliateId, BigDecimal amount, Integer term);
}
