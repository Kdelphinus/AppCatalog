package com.appcatalog.orchestrator.deployment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "DEPLOYMENT_JOBS")
@Getter
@Setter
public class DeploymentJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String serviceVersion;
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private DeploymentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for deployment status
    public enum DeploymentStatus {
        PENDING,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
