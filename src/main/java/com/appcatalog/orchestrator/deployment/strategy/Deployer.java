package com.appcatalog.orchestrator.deployment.strategy;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;

public interface Deployer {
    void deploy(DeploymentJob job);
    boolean supports(String targetType); // A way to determine which deployer to use
}
