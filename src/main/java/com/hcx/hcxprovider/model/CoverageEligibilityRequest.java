package com.hcx.hcxprovider.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CoverageEligibilityRequests")
@AllArgsConstructor
@Data
public class CoverageEligibilityRequest {

  @Id
  private String id;
  private String requestType;
  private String hospitalName;
  private String insurerCode;
  private String senderCode;
}
