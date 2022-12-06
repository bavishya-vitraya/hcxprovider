package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.model.PreAuthRequest;

public interface PreAuthService {

    String savePreAuthRequest(PreAuthRequest preAuthRequest) ;

    String savePreAuthResponse(String preAuth) throws Exception;


}
