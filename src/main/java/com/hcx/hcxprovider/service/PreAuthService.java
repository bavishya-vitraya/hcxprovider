package com.hcx.hcxprovider.service;

import com.hcx.hcxprovider.model.PreAuthRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface PreAuthService {

    String savePreAuthRequest(PreAuthRequest preAuthRequest);
}
