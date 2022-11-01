package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.service.ListenerService;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ListenerServiceImpl implements ListenerService {

    @Value("classpath:resources/keys/vitraya-mock-provider-private-key.pem")
    String privateKeyPath;

    @Value("${protocolBasePath}")
    String protocolBasePath;

    @Value("${authBasePath}")
    String authBasePath;

    @Value("${participantCode}")
    String participantCode;

    @Value("${username}")
    String username;

    @Value("${password}")
    String password;

    @Value("${igUrl}")
    String igUrl;

    @Value("classpath:resources/input/message.txt")
    String payloadPath;

    public Map<String, Object> setConfig() throws IOException {
        Map<String, Object> config = new HashMap<String, Object>();
        File file = new ClassPathResource("keys/vitraya-mock-provider-private-key.pem").getFile();
        String privateKey= FileUtils.readFileToString(file);
        config.put("protocolBasePath", protocolBasePath);
        config.put("authBasePath", authBasePath);
        config.put("participantCode","1-bdaf4872-d41f-4d9e-9d23-fbe73ca984f8");
        config.put("username", username);
        config.put("password",password);
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", igUrl);
        return config;
    }

    @Override
    public boolean hcxGenerate() throws Exception {

        File payloadFile= new ClassPathResource("input/message.txt").getFile();
        String payload=FileUtils.readFileToString(payloadFile);

        HCXIntegrator.init(setConfig());
        Operations operation = Operations.COVERAGE_ELIGIBILITY_CHECK;
        String recipient_code = "1-434d79f6-aad8-48bc-b408-980a4dbd90e2";
        Map<String,Object> output = new HashMap<>();
        HCXOutgoingRequest hcxOutgoingRequest = new HCXOutgoingRequest();
        Boolean response = hcxOutgoingRequest.generate(payload,operation,recipient_code,output);
        log.info("output {}",output);
        return response;

    }
}
