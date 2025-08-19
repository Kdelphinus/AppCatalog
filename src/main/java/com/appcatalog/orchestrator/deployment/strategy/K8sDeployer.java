package com.appcatalog.orchestrator.deployment.strategy;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;
import com.appcatalog.orchestrator.target.domain.TargetEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class K8sDeployer implements Deployer {

    private static final Logger logger = LoggerFactory.getLogger(K8sDeployer.class);

    @Override
    public void deploy(DeploymentJob job) {
        logger.info("Executing Kubernetes deployment strategy for job ID: {}", job.getId());
        // TODO: Implement Kubernetes-specific deployment logic (e.g., Fabric8 client)
        // 1. Fetch target environment details (Kubeconfig/API server URL)
        // 2. Create/update Kubernetes resources (Deployment, Service, etc.)
        logger.info("Kubernetes deployment for job ID {} completed.", job.getId());
    }

    @Override
    public boolean supports(String targetType) {
        return TargetEnvironment.TargetType.KUBERNETES.name().equalsIgnoreCase(targetType);
    }
}
