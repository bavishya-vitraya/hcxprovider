package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.model.ClaimRequest;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.service.ClaimRequestService;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import com.hcx.hcxprovider.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/vhiRequest")
public class VHIRequestController {

    @Autowired
    PreAuthService preAuthService;
    @Autowired
    CoverageEligibilityService coverageEligibilityService;
    @Autowired
    ClaimRequestService claimRequestService;

    @PostMapping(value = "/savePreAuthReq")
    public String savePreAuthRequest(@RequestBody PreAuthRequest preAuthRequest)  {
        log.info("Entered Save PreAuth Controller");
        return preAuthService.savePreAuthRequest(preAuthRequest);

    }

    @PostMapping(value = "/saveCoverageEligibility")
    public String saveCoverageEligibilityRequest(@RequestBody CoverageEligibilityRequest coverageEligibilityRequest) {
        log.info("Entered Save CoverageEligibility Controller");
        return coverageEligibilityService.saveCoverageEligibilityRequest(coverageEligibilityRequest);
    }

    @PostMapping(value = "/saveClaimRequest")
    public String saveClaimRequest(@RequestBody ClaimRequest claimRequest)  {
        log.info("Entered Save ClaimRequest Controller");
        return claimRequestService.saveClaimRequest(claimRequest);
    }

}
