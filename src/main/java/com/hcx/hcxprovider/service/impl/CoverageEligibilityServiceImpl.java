package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.CoverageEligibilityDTO;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.repository.CoverageEligibilityRequestRepo;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CoverageEligibilityServiceImpl implements CoverageEligibilityService {
    @Value("${queue.name}")
    String queueName;

    @Value("${queue.exchange}")
    String exchange;

    @Value("${queue.routingKey}")
    private String routingkey;

    @Autowired
    PreAuthRequestRepo preAuthRequestRepo;

    @Autowired
    CoverageEligibilityRequestRepo coverageEligibilityRequestRepo;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String saveCoverageEligibilityRequest(CoverageEligibilityRequest coverageEligibilityRequest){

        coverageEligibilityRequestRepo.save(coverageEligibilityRequest);

        CoverageEligibilityDTO coverageEligibilityDTO= new CoverageEligibilityDTO();
        coverageEligibilityDTO.setRequestId(coverageEligibilityRequest.getRequestId());
        coverageEligibilityDTO.setRecipientCode(coverageEligibilityRequest.getRecipientCode());
        rabbitTemplate.convertAndSend(exchange,routingkey,coverageEligibilityDTO);
        return "Updated Successfully";
    }
}
