package com.hcx.hcxprovider.model;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CoverageEligibilityRequests")
@AllArgsConstructor
public class CoverageEligibilityRequest {

  @Id
  private String id;
  private String resourceType;
  private String requestId;
  private String name;

  private String recipientCode;

  private String participantCode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRecipientCode() {
    return recipientCode;
  }

  public void setRecipientCode(String recipientCode) {
    this.recipientCode = recipientCode;
  }

  public String getParticipantCode() {
    return participantCode;
  }

  public void setParticipantCode(String participantCode) {
    this.participantCode = participantCode;
  }
}
