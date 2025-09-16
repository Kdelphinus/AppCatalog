package com.appcatalog.target.dto;

import com.appcatalog.target.domain.TargetEnvironment;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TargetRequestDto {
  @NotBlank(message = "Target name cannot be empty")
  private String name;

  private String type;

  @NotBlank(message = "Target name cannot be empty")
  private String host;

  @NotBlank(message = "Target name cannot be empty")
  private int port;

  private String credentialId;

  public TargetEnvironment toEntity() {
    TargetEnvironment targetEnvironment = new TargetEnvironment();
    targetEnvironment.setName(this.name);
    targetEnvironment.setType(
        com.appcatalog.target.domain.TargetType.valueOf(this.type.toUpperCase()));
    targetEnvironment.setHost(this.host);
    targetEnvironment.setPort(this.port);
    targetEnvironment.setCredentialId(this.credentialId);
    return targetEnvironment;
  }
}
