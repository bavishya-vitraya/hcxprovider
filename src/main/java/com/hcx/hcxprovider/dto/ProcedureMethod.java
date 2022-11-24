package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureMethod {
    private int id;
    private int procedureId;
    private String procedureMethodName;
    private String procedureMethodDisplayName;
    private String procedureCode;
    private String procedureNameInsurer;
    private boolean enabled;
    private String createTime;
    private String updateTime;
    private String procedureMethodCode;
}
