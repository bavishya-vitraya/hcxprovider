package com.hcx.hcxprovider.model;

import com.hcx.hcxprovider.dto.PreAuthDetails;
import com.hcx.hcxprovider.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "preAuthRequests")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreAuthRequest {
    @Id
    private String id;
    private String senderCode;
    private String insurerCode;
    private String requestType;
    private Status status;
    private String correlationId;
    private PreAuthDetails preAuthReq;
}
