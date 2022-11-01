package com.hcx.hcxprovider.consumer;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.hcx.hcxprovider.service.ListenerService;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class VHIRequestListener {
    @Autowired
    PreAuthRequestRepo preAuthRequestRepo;

    @Autowired
    ListenerService listenerService;



    @RabbitListener(queues = "${queue.name}")
    public void recievedMessage(PreAuthReqDTO preAuthReqDTO) throws Exception {
      log.info("preAuthReqDTO {}" , preAuthReqDTO);
       boolean result= listenerService.hcxGenerate();

     log.info("result {} ", result);
    }
}
