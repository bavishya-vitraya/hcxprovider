package com.hcx.hcxprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMaster {
    private long id;
    private long parentTableId;
    private int illnessDocumentId;
    private String documentType;
    private String fileName;
    private String storageFileName;
    private String fileType;
    private boolean fileSupported;
    private String createTime;
    private String updateTime;
    private String note;
    private int documentStatus;
}
