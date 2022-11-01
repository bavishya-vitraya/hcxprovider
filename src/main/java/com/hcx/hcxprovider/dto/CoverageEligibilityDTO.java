package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverageEligibilityDTO {

    private String insurerCode;
    private String hospitalCode;
    private String reqType;
    private String requestId;
}