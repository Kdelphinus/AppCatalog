package com.appcatalog.orchestrator.deployment.strategy;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;
import com.appcatalog.orchestrator.target.domain.TargetEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VmDeployer implements Deployer {

    private static final Logger logger = LoggerFactory.getLogger(VmDeployer.class);

    @Override
    public void deploy(DeploymentJob job) {
        logger.info("Executing VM deployment strategy for job ID: {}", job.getId());
        // TODO: Implement VM-specific deployment logic using SSH (e.g., JSch)
        // 1. Fetch target environment details
        // 2. Connect via SSH
        // 3. Transfer artifact
        // 4. Execute deployment script
        logger.info("VM deployment for job ID {} completed.", job.getId());
    }

    @Override
    public boolean supports(String targetType) {
        return TargetEnvironment.TargetType.VM.name().equalsIgnoreCase(targetType);
    }
}
