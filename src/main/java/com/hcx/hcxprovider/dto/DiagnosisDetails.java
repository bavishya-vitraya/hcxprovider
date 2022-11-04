package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisDetails {
    private String reasonForNotPaying;
    private String sublimitName;
    private boolean pedImpactOnDiagnosis;
    private String hospitalDiagnosis;
    private String insuranceDiagnosis;
    private boolean considerForPayment;
    private boolean sublimitApplicable;
}
