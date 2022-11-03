package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimRequestDTO {
    private String insurerCode;
    private String hospitalCode;
    private String requestType;
    private String requestId;
}
