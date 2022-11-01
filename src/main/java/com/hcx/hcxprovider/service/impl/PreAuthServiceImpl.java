package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PreAuthServiceImpl implements PreAuthService {
    @Value("${queue.name}")
    String queueName;

    @Value("${queue.exchange}")
    String exchange;

    @Value("${queue.routingKey}")
    private String routingkey;

    @Autowired
    PreAuthRequestRepo preAuthRequestRepo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
   public String savePreAuthRequest( PreAuthRequest preAuthRequest){
        preAuthRequestRepo.save(preAuthRequest);
        PreAuthReqDTO preAuthReqDTO = new PreAuthReqDTO();
        preAuthReqDTO.setRequestId(preAuthRequest.getId());
        preAuthReqDTO.setRequestType(preAuthRequest.getRequestType());
        preAuthReqDTO.setHospitalCode(preAuthRequest.getHospitalCode());
        preAuthReqDTO.setInsurerCode(preAuthRequest.getInsurerCode());
        log.info("preAuthReqDTO {}",preAuthReqDTO);
        rabbitTemplate.convertAndSend(exchange,routingkey,preAuthReqDTO);
        return "PreAuth request sent successfully";
    }
}
