package com.hcx.hcxprovider.service.impl;

import com.google.gson.Gson;
import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.dto.PreAuthResDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.model.PreAuthResponse;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.repository.PreAuthResponseRepo;
import com.hcx.hcxprovider.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Override
    public String savePreAuthResponse(String preAuth) {
        Gson json = new Gson();
        PreAuthResponse preAuthResponse=new PreAuthResponse();
        preAuthResponse=(PreAuthResponse) json.fromJson(preAuth, PreAuthResponse.class);
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
