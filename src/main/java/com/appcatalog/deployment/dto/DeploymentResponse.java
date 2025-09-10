package com.appcatalog.deployment.dto;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.domain.DeploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // JSON 변환 시 Jackson 라이브러리가 사용하기 위한 빈 생성자
@AllArgsConstructor // 모든 필드를 포함하는 생성자 (객체 생성 시 편의용)
public class DeploymentResponse {

  private Long id;
  private String serviceName;
  private String serviceVersion;
  private Long targetId;
  private DeploymentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static DeploymentResponse fromEntity(DeploymentJob job) {
    return new DeploymentResponse(
        job.getId(),
        job.getServiceName(),
        job.getServiceVersion(),
        job.getTargetId(),
        job.getStatus(),
        job.getCreatedAt(),
        job.getUpdatedAt());
  }
}
