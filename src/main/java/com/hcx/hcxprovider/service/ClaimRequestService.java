package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.ClaimRequest;

public interface ClaimRequestService {
    String saveClaimRequest(ClaimRequest claimRequest) throws ProviderException;
}
