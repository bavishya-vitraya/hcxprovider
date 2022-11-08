package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.CoverageEligibilityDTO;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.repository.CoverageEligibilityRequestRepo;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoverageEligibilityServiceImpl implements CoverageEligibilityService {

    @Value("${queue.exchange.name}")
    private String exchange;

    @Value("${queue.reqrouting.key}")
    private String reqroutingKey;

    @Value("${queue.resrouting.key}")
    private String resroutingKey;
    @Autowired
    CoverageEligibilityRequestRepo coverageEligibilityRequestRepo;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String saveCoverageEligibilityRequest(CoverageEligibilityRequest coverageEligibilityRequest){

        coverageEligibilityRequestRepo.save(coverageEligibilityRequest);
        log.info("Coverage Eligibility Request Saved");
        CoverageEligibilityDTO coverageEligibilityDTO= new CoverageEligibilityDTO();
        coverageEligibilityDTO.setRequestId(coverageEligibilityRequest.getId());
        coverageEligibilityDTO.setInsurerCode(coverageEligibilityRequest.getInsurerCode());
        coverageEligibilityDTO.setSenderCode(coverageEligibilityRequest.getSenderCode());
        coverageEligibilityDTO.setRequestType(coverageEligibilityRequest.getRequestType());
        rabbitTemplate.convertAndSend(exchange,reqroutingKey,coverageEligibilityDTO);
        return "Coverage Eligibility request sent successfully";
    }
}
