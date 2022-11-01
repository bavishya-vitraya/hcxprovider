package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import com.hcx.hcxprovider.service.PreAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VHIRequestController {

    @Autowired
    PreAuthService preAuthService;
    @Autowired
    CoverageEligibilityService coverageEligibilityService;

    @PostMapping(value = "/savePreAuthReq")
    public String savePreAuthRequest(@RequestBody PreAuthRequest preAuthRequest){
        return preAuthService.savePreAuthRequest(preAuthRequest);

    }

    @PostMapping(value = "/saveCoverageEligibility")
    public String saveCoverageEligibilityRequest(@RequestBody CoverageEligibilityRequest coverageEligibilityRequest){
        return coverageEligibilityService.saveCoverageEligibilityRequest(coverageEligibilityRequest);
    }


}
