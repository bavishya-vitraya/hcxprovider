package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.model.PreAuthResponse;
import org.springframework.amqp.core.AmqpTemplate;

public interface PreAuthService {

    String savePreAuthRequest(PreAuthRequest preAuthRequest);

    String savePreAuthResponse(String preAuth);


}
