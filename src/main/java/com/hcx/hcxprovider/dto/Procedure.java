package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Procedure {
    private long id;
    private String createTime;
    private boolean enabled;
    private String description;
    private int departmentId;
    private String updateTime;
    private String name;
    private String procedureCode;
}
