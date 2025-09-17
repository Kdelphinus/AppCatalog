package com.appcatalog.target.dto;

import com.appcatalog.credential.dto.CredentialRequestDto;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TargetRequestDto {
  @NotBlank(message = "Target name cannot be empty")
  @Size(min = 2, max = 50, message = "Target name must be between 2 and 50 characters")
  private String name;

  @NotNull(message = "Target type cannot be empty")
  private TargetType type;

  @NotBlank(message = "Target name cannot be empty")
  private String host;

  @Min(value = 1, message = "Port must be at least 1")
  @Max(value = 65535, message = "Port must be at most 65535")
  private int port;

  @Valid
  @NotNull(message = "Credential cannot be empty")
  private CredentialRequestDto credential;

  public TargetEnvironment toEntity() {
    TargetEnvironment targetEnvironment = new TargetEnvironment();
    targetEnvironment.setName(this.name);
    targetEnvironment.setType(this.type);
    targetEnvironment.setHost(this.host);
    targetEnvironment.setPort(this.port);
    if (this.credential != null) {
      targetEnvironment.setCredential(this.credential.toEntity());
    }
    return targetEnvironment;
  }
}
