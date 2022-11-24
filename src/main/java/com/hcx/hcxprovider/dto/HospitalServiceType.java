package com.hcx.hcxprovider.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalServiceType {
    private int id;
    private int hospitalId;
    private VitrayaRoomCategory vitrayaRoomCategory;
    private String roomType;
    private String insurerRoomType;
    private boolean singlePrivateAC;
    private BigDecimal roomTariffPerDay;
    private ServiceType serviceType;
    private String createTime;
    private String updateTime;
    private String serviceCode;
}
