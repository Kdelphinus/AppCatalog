package com.appcatalog.deployment.dto;

import lombok.Data;

@Data
public class DeploymentRequest {

  private String serviceName;
  private String serviceVersion;
  private Long targetId;
}
