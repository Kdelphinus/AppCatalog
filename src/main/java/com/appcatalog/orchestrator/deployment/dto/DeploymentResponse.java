package com.appcatalog.orchestrator.deployment.dto;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;
import java.time.LocalDateTime;

public record DeploymentResponse(
    Long jobId,
    String serviceName,
    String serviceVersion,
    Long targetId,
    DeploymentJob.DeploymentStatus status,
    LocalDateTime createdAt
) {
    public static DeploymentResponse fromEntity(DeploymentJob job) {
        return new DeploymentResponse(
            job.getId(),
            job.getServiceName(),
            job.getServiceVersion(),
            job.getTargetId(),
            job.getStatus(),
            job.getCreatedAt()
        );
    }
}
