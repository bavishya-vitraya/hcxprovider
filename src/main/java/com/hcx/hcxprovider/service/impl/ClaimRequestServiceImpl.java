package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.ClaimRequestDTO;
import com.hcx.hcxprovider.error.ErrorMessage;
import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.ClaimRequest;
import com.hcx.hcxprovider.repository.ClaimRequestRepo;
import com.hcx.hcxprovider.service.ClaimRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClaimRequestServiceImpl implements ClaimRequestService {

    @Value("${queue.exchange.name}")
    private String exchange;

    @Value("${queue.reqrouting.key}")
    private String reqroutingKey;

    @Value("${queue.resrouting.key}")
    private String resroutingKey;

    @Autowired
    ClaimRequestRepo claimRequestRepo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String saveClaimRequest(ClaimRequest claimRequest) throws ProviderException {
        try {
            claimRequestRepo.save(claimRequest);
        }
        catch(Exception e){
           log.error("error in saving the claim request", e);
        }
        log.info("saved claim request");
        ClaimRequestDTO claimRequestDTO = new ClaimRequestDTO();
        claimRequestDTO.setReferenceId(claimRequest.getId());
        claimRequestDTO.setMessageType(claimRequest.getRequestType());
        claimRequestDTO.setSenderCode(claimRequest.getSenderCode());
        claimRequestDTO.setInsurerCode(claimRequest.getInsurerCode());
        log.info("Claim Request: {}",claimRequest.getClaimRequest());
        if(claimRequestDTO!=null) {
            rabbitTemplate.convertAndSend(exchange, reqroutingKey, claimRequestDTO);
            return "Claim Request sent successfully";
        }
        else{
            throw  new ProviderException(ErrorMessage.REQUEST_NOT_FOUND);
        }
    }
}
