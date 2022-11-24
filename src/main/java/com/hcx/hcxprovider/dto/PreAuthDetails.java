package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreAuthDetails {
    private ClaimFlowType claimFlowType;
    private int serviceTypeId;
    private Claim claim;
    private ClaimIllnessTreatmentDetails claimIllnessTreatmentDetails;
    private ClaimAdmissionDetails claimAdmissionDetails;
    private HospitalServiceType hospitalServiceType;
    private Procedure procedure;
    private ProcedureMethod procedureMethod;
    private List<DocumentMaster> documentMasterList;
    private Illness illness;
}
