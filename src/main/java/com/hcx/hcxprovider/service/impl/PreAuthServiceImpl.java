package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.dto.PreAuthResDTO;
import com.hcx.hcxprovider.enums.ErrorMessage;
import com.hcx.hcxprovider.enums.Status;
import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.model.PreAuthResponse;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.repository.PreAuthResponseRepo;
import com.hcx.hcxprovider.service.PreAuthService;
import io.hcxprotocol.impl.HCXIncomingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.JSONUtils;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
   public String savePreAuthRequest( PreAuthRequest preAuthRequest)  {
        try {
            preAuthRequest.setStatus(Status.INIATED);
            preAuthRequestRepo.save(preAuthRequest);
        }
        catch (Exception e){
            log.error("error in saving the preAuth request", e);
        }
        log.info("preAuth  req saved");
        PreAuthReqDTO preAuthReqDTO = new PreAuthReqDTO();
        preAuthReqDTO.setReferenceId(preAuthRequest.getId());
        preAuthReqDTO.setMessageType(preAuthRequest.getRequestType());
        preAuthReqDTO.setSenderCode(preAuthRequest.getSenderCode());
        preAuthReqDTO.setInsurerCode(preAuthRequest.getInsurerCode());
        log.info("preAuthReqDTO {} ",preAuthReqDTO);
        if( preAuthReqDTO!=null) {
            rabbitTemplate.convertAndSend(exchange, reqroutingKey, preAuthReqDTO);
            try {
                preAuthRequest.setStatus(Status.ENQUEUED);
                preAuthRequestRepo.save(preAuthRequest);
            }
            catch (Exception e) {
                log.error("error in updating the preAuth request", e);
            }

        }

        return "PreAuth request pushed to Queue";
    }
    public Map<String, Object> setConfig() throws IOException {
        Map<String, Object> config = new HashMap<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/keys/vitraya-mock-provider-private-key.pem");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String privateKey = reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            config.put("protocolBasePath", protocolBasePath);
            config.put("authBasePath", authBasePath);
            config.put("participantCode", participantCode);
            config.put("username", username);
            config.put("password", password);
            config.put("encryptionPrivateKey", privateKey);
            config.put("igUrl", igUrl);
        }
        catch(Exception e){
            log.error(e.getMessage());
        }
        return config;
    }

    @Override
    public String savePreAuthResponse(String pre) throws Exception {
        Operations operation = Operations.PRE_AUTH_ON_SUBMIT;
        HCXIntegrator.init(setConfig());
        Map<String,Object> output = new HashMap<>();
        Map<String,Object> input = new HashMap<>();
        Map<String,Object> headers;
        input.put("payload",pre);
        HCXIncomingRequest hcxIncomingRequest = new HCXIncomingRequest();
        hcxIncomingRequest.process(JSONUtils.serialize(input),operation,output);
        log.info("Incoming Request: {}",output);
        headers = (Map<String, Object>) output.get("headers");
        log.info("headers {}",headers);
        String correlationId = (String) headers.get("x-hcx-correlation_id");
        String fhirPayload = (String) output.get("fhirPayload");
        PreAuthRequest preAuthRequest = new PreAuthRequest();
        try {
            preAuthRequest = preAuthRequestRepo.findPreAuthRequestByCorrelationId(correlationId);
        }
        catch(Exception e){
            log.error("Error in fetching preAuthRequest",e);
        }
        if(preAuthRequest!=null) {
            PreAuthResponse preAuthResponse = new PreAuthResponse();
            if (preAuthRequest.getCorrelationId().equalsIgnoreCase(correlationId)) {
                preAuthRequest.setStatus(Status.COMPLETED);
                preAuthRequestRepo.save(preAuthRequest);
            }
            if (fhirPayload != null) {
                preAuthResponse.setResponseType("preAuthResponse");
                preAuthResponse.setFhirPayload(fhirPayload);
                preAuthResponseRepo.save(preAuthResponse);
                log.info("PreAuth Response saved");
            }
            PreAuthResDTO preAuthResDTO = new PreAuthResDTO();
            preAuthResDTO.setReferenceId(preAuthResponse.getResponseId());
            preAuthResDTO.setMessageType(preAuthResponse.getResponseType());
            preAuthResDTO.setSenderCode(preAuthResponse.getSenderCode());
            preAuthResDTO.setInsurerCode(preAuthResponse.getInsurerCode());
            if (preAuthResDTO != null) {
                try {
                    rabbitTemplate.convertAndSend(exchange, resroutingKey, preAuthResDTO);
                    return "PreAuth response pushed to Queue";
                } catch (Exception e) {
                  log.error(e.getMessage());
                }
            }
            else{
                throw new ProviderException(ErrorMessage.RABBITMQ_ERROR);
            }
        }
        else{
            throw new ProviderException(ErrorMessage.RESOURCE_NOT_FOUND);
        }
       return null;
    }
}
