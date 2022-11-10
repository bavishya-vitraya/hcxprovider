package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedure {

   private Integer id;
   private String createTime;
   private boolean enabled;
   private String description;
   private Integer departmentId;
   private String updateTime;
   private String name;
   private String procedureCode;
}
