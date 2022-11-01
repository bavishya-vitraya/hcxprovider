package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.ClaimRequestDTO;
import com.hcx.hcxprovider.model.ClaimRequest;
import com.hcx.hcxprovider.repository.ClaimRequestRepo;
import com.hcx.hcxprovider.service.ClaimRequestService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClaimRequestServiceImpl implements ClaimRequestService {

    @Value("${queue.name}")
    String queueName;

    @Value("${queue.exchange}")
    String exchange;

    @Value("${queue.routingKey}")
    private String routingkey;

    @Autowired
    ClaimRequestRepo claimRequestRepo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String saveClaimRequest(ClaimRequest claimRequest) {

        claimRequestRepo.save(claimRequest);
        ClaimRequestDTO claimRequestDTO = new ClaimRequestDTO();
        claimRequestDTO.setRequestId(claimRequest.getId());
        claimRequestDTO.setReqType(claimRequest.getRequestType());
        claimRequestDTO.setHospitalCode(claimRequest.getHospitalCode());
        claimRequestDTO.setInsurerCode(claimRequest.getInsurerCode());
        rabbitTemplate.convertAndSend(exchange,routingkey,claimRequestDTO);
        return "Claim Request sent successfully";
    }
}
