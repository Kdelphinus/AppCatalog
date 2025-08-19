package com.appcatalog.orchestrator.deployment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Using record for immutable data transfer objects
public record DeploymentRequest(
    @NotEmpty String serviceName,
    @NotEmpty String serviceVersion,
    @NotNull Long targetId
) {}
