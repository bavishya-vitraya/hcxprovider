package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.dto.PreAuthResDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.model.PreAuthResponse;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.repository.PreAuthResponseRepo;
import com.hcx.hcxprovider.service.PreAuthService;
import io.hcxprotocol.impl.HCXIncomingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PreAuthServiceImpl implements PreAuthService {

    @Value("${queue.exchange.name}")
    private String exchange;

    @Value("${queue.reqrouting.key}")
    private String reqroutingKey;

    @Value("${queue.resrouting.key}")
    private String resroutingKey;
    @Autowired
    private  PreAuthRequestRepo preAuthRequestRepo;

    @Autowired
    private PreAuthResponseRepo preAuthResponseRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("classpath:resources/keys/vitraya-mock-provider-private-key.pem")
    String privateKeyPath;

    @Value("${hcx.protocolBasePath}")
    String protocolBasePath;

    @Value("${hcx.authBasePath}")
    String authBasePath;

    @Value("${hcx.participantCode}")
    String participantCode;

    @Value("${hcx.recipientCode}")
    String recipientCode;

    @Value("${hcx.username}")
    String username;

    @Value("${hcx.password}")
    String password;

    @Value("${hcx.igUrl}")
    String igUrl;

    @Override
   public String savePreAuthRequest( PreAuthRequest preAuthRequest){
        preAuthRequestRepo.save(preAuthRequest);
        log.info("preAuth  req saved");
        PreAuthReqDTO preAuthReqDTO = new PreAuthReqDTO();
        preAuthReqDTO.setRequestId(preAuthRequest.getId());
        preAuthReqDTO.setRequestType(preAuthRequest.getRequestType());
        preAuthReqDTO.setSenderCode(preAuthRequest.getSenderCode());
        preAuthReqDTO.setInsurerCode(preAuthRequest.getInsurerCode());
        log.info("preAuthReqDTO {} ",preAuthReqDTO);
        rabbitTemplate.convertAndSend(exchange,reqroutingKey,preAuthReqDTO);
        return "PreAuth request pushed to Queue";
    }
    public Map<String, Object> setConfig() throws IOException {
        Map<String, Object> config = new HashMap<>();
        File file = new ClassPathResource("keys/vitraya-mock-provider-private-key.pem").getFile();
        String privateKey= FileUtils.readFileToString(file);
        config.put("protocolBasePath", protocolBasePath);
        config.put("authBasePath", authBasePath);
        config.put("participantCode",participantCode);
        config.put("username", username);
        config.put("password",password);
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", igUrl);
        return config;
    }
    public Map<String, Object> setPayorConfig() throws IOException {
        Map<String, Object> config = new HashMap<>();
        File file = new ClassPathResource("keys/vitraya-mock-payor-private-key.pem").getFile();
        String privateKey= FileUtils.readFileToString(file);
        config.put("protocolBasePath", protocolBasePath);
        config.put("authBasePath", authBasePath);
        config.put("participantCode","1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
        config.put("username", "vitrayahcxpayor1@vitrayatech.com");
        config.put("password","BkYJHwm64EEn8B8");
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", igUrl);
        return config;
    }
    @Override
    public String savePreAuthResponse(String pre) throws Exception {
        File payloadFile = new ClassPathResource("input/jwePayload").getFile();
        String preAuthRes = FileUtils.readFileToString(payloadFile);
        Operations operation = Operations.PRE_AUTH_ON_SUBMIT;
        HCXIntegrator.init(setConfig());
        Map<String,Object> output = new HashMap<>();
        HCXIncomingRequest hcxIncomingRequest = new HCXIncomingRequest();
        hcxIncomingRequest.process(preAuthRes,operation,output);
        //hcxIncomingRequest.decryptPayload(preAuthRes,output);
        log.info("Incoming Request: {}",output);
        String fhirPayload = (String) output.get("fhirPayload");
        PreAuthResponse preAuthResponse=new PreAuthResponse();
        preAuthResponse.setResult(fhirPayload);
        preAuthResponseRepo.save(preAuthResponse);
        log.info("preAuth  req saved");
        PreAuthResDTO preAuthResDTO= new PreAuthResDTO();
        preAuthResDTO.setResponseId(preAuthResponse.getResponseId());
        preAuthResDTO.setResponseType(preAuthResponse.getResponseType());
        preAuthResDTO.setSenderCode(preAuthResponse.getSenderCode());
        preAuthResDTO.setInsurerCode(preAuthResponse.getInsurerCode());
        rabbitTemplate.convertAndSend(exchange,resroutingKey,preAuthResDTO);
        return "PreAuth response pushed to Queue";
    }
}
