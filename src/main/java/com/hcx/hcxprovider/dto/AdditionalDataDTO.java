package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalDataDTO {
    private String roomCategory;
    private String dateOfDischarge;
    private String patientStatus;
    private String managementType;
    private boolean includesFinalBill;
    private String ipNumber;
    private String dateOfAdmission;
    private List<TreatingDoctorDetailsDTO> treatingDoctorDetails;
    private List<SpecialityDetailsDTO> specialtyDetails;
    private AmountDetailsDTO amountDetails;
}
