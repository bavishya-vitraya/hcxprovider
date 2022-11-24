package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChronicIllnessDTO {
    private int illnessId;
    private Date diagnosisDate;
    private int numberOfMonths;
    private String illnessName;
    private String illnessIcdCode;
}
