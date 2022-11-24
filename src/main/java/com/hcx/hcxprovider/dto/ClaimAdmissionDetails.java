package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimAdmissionDetails {
    private long claimId;
    private Date admissionDate;
    private Date dischargeDate;
    private String roomType;
    private int hospitalServiceTypeId;
    private int stayDuration;
    private String costEstimation;
    private BigDecimal packageAmount;
    private boolean icuStay;
    private int icuStayDuration;
    private int icuServiceTypeId;
}
