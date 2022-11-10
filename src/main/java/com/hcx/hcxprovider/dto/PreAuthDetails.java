package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthDetails {
    private String claimFlowType;
    private Integer serviceTypeId;
    private Claim claim;
    private ClaimIllnessTreatmentDetails claimIllnessTreatmentDetails;
    private ClaimAdmissionDetails claimAdmissionDetails;
    private HospitalServiceType hospitalServiceType;
    private Procedure procedure;
    private ProcedureMethod procedureMethod;
    private List<DocumentMaster> documentMasterList;
    private Illness illness;

}
