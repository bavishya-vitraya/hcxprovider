package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.PreAuthRequest;

public interface PreAuthService {

    String savePreAuthRequest(PreAuthRequest preAuthRequest) throws ProviderException;

    String savePreAuthResponse(String preAuth) throws Exception;


}
