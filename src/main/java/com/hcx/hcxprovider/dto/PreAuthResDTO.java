package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreAuthResDTO
{
    private String responseId;
    private String senderCode;
    private String insurerCode;
    private String responseType;
}
