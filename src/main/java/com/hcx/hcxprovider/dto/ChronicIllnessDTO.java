package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChronicIllnessDTO {
    private int illnessId;
    private String diagnosisDate;
    private int numberOfMonths;
    private String illnessName;
    private String illnessIcdCode;
}
