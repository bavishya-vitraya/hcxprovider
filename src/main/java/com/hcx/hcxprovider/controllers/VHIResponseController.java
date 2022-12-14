package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.dto.HCXResponseDTO;
import com.hcx.hcxprovider.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class VHIResponseController {

    @Autowired
    private PreAuthService preAuthService;

    @PostMapping("/preauth/on_submit")
    public String savePreAuthResponse(@RequestBody HCXResponseDTO hcxResponseDTO) throws Exception {
        log.info("Entered Save PreAuth Response Controller");
        return preAuthService.savePreAuthResponse(hcxResponseDTO.getPayload());
    }
}
