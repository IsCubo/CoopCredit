package com.riwi.coopcredit.domain.port.in;

import com.riwi.coopcredit.domain.model.Affiliate;

public interface RegisterAffiliateUseCase {
    Affiliate register(Affiliate affiliateToRegister);
}
