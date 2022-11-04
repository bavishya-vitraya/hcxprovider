package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.dto.PreAuthEnhanceDTO;
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
        log.info("preAuth  req saved");
        PreAuthReqDTO preAuthReqDTO = new PreAuthReqDTO();
        preAuthReqDTO.setRequestId(preAuthRequest.getId());
        preAuthReqDTO.setRequestType(preAuthRequest.getRequestType());
        preAuthReqDTO.setHospitalCode(preAuthRequest.getSenderCode());
        preAuthReqDTO.setInsurerCode(preAuthRequest.getInsurerCode());
        log.info("preAuthReqDTO {} ",preAuthReqDTO);
        rabbitTemplate.convertAndSend(exchange,routingkey,preAuthReqDTO);
        return "PreAuth request pushed to Queue";
    }

    @Override
    public String updatePreAuthRequest(PreAuthEnhanceDTO preAuthEnhanceDTO){
        String id=preAuthEnhanceDTO.getPreAuthid();
        PreAuthRequest preAuthRequest=preAuthRequestRepo.findPreAuthRequestById(id);
        log.info("Before change dateOfAdmission{} ::", preAuthRequest.getPreAuthReq().getAdditionalData().getDateOfAdmission());
        preAuthRequest.getPreAuthReq().setRequestedAmount(Double.valueOf(preAuthEnhanceDTO.getRequestedAmount()));
        preAuthRequest.getPreAuthReq().getAdditionalData().setDateOfAdmission(preAuthEnhanceDTO.getDateOfAdmission());
        preAuthRequest.getPreAuthReq().getAdditionalData().setDateOfDischarge(preAuthEnhanceDTO.getDateOfDischarge());
        preAuthRequest.getPreAuthReq().getAdditionalData().setIncludesFinalBill(preAuthEnhanceDTO.isIncludesFinalBill());
        preAuthRequest.getPreAuthReq().getAdditionalData().setRoomCategory(preAuthEnhanceDTO.getRoomCategory());
        preAuthRequest.setDiagnosis(preAuthEnhanceDTO.getDiagnosis());
      //  preAuthRequest.getPreAuthReq().getAdditionalData().getSpecialityDetails().get(0).setProcedureName(preAuthEnhanceDTO.getProcedure());
        //amount breakup
        preAuthRequest.getPreAuthReq().setFiles(preAuthEnhanceDTO.getFiles());
        preAuthRequest.getPreAuthReq().setVitrayaReferenceNumber(preAuthEnhanceDTO.getVitrayaReferenceNumber());

        preAuthRequestRepo.save(preAuthRequest);
        log.info("After change dateOfAdmission{}  ", preAuthRequest.getPreAuthReq().getAdditionalData().getDateOfAdmission());
        return "PreAuth request updated";
    }
}
