package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjudicationDetailsRequest {
    private Boolean relapseOfIllness;
    private String adjudicationCategory;
    private String adjudicationRemarks;
    private String adjudicationDecision;
    private String adjudicationCategoryRemarks;
    private BigDecimal adjudicationApprovedAmount;
    private List<DiagnosisDetails> diagnosisDetails;
    private List<ProcedureDetails> procedureDetails;
    private List<MedicalDecisionDetails> medicalDecisionDetails;
}
