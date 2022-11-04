package com.hcx.hcxprovider.model;

import com.hcx.hcxprovider.dto.FinalEnhanceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "claimrequests")
@AllArgsConstructor
@Data
public class ClaimRequest {
    @Id
    private String id;
    private String insurerCode;
    private String hospitalCode;
    private String hospitalName;
    private String requestType;
    private FinalEnhanceDTO claimRequest;
}
