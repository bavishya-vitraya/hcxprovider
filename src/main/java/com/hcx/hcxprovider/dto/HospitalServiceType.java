package com.hcx.hcxprovider.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

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
    private Date createTime;
    private Date updateTime;
    private String serviceCode;
}
