package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountDetailsDTO {
    private Double claimedAmountAfterDiscount;
    private Double consideredAmountTotal;
    private Double discountInHospitalBill;
    private List<ClaimedAmountBreakupDTO> claimedAmountBreakup;
}
