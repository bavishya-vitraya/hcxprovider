package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;

public interface CoverageEligibilityService {

    String saveCoverageEligibilityRequest(CoverageEligibilityRequest coverageEligibilityRequest) throws ProviderException;

}
