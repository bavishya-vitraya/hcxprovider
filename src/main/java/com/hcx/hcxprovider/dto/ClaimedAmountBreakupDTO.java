package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimedAmountBreakupDTO {
    private Double componentNumberOfDaysClaimed;
    private Double componentNumberOfDaysAllowed;
    private Double componentPerDayAmountClaimed;
    private Double componentPerDayAmountEligible;
    private Double componentPerDayAmountConsidered;
    private String componentName;
    private Double componentTotalClaimedAmount;
    private Double componentNetAmountClaimed;
    private Double componentDeductibleAmount;
    private Double componentNonPayables;
    private Double componentPayableAmount;
    private String componentDeductibleOrNonPayablesReason;
}
