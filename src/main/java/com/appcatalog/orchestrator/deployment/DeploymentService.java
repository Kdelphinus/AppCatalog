package com.appcatalog.orchestrator.deployment;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;
import com.appcatalog.orchestrator.deployment.domain.DeploymentJobRepository;
import com.appcatalog.orchestrator.deployment.dto.DeploymentRequest;
import com.appcatalog.orchestrator.deployment.strategy.Deployer;
import com.appcatalog.orchestrator.target.domain.TargetEnvironment;
import com.appcatalog.orchestrator.target.domain.TargetEnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentService.class);

    private final DeploymentJobRepository deploymentJobRepository;
    private final TargetEnvironmentRepository targetEnvironmentRepository;
    private final List<Deployer> deployers;

    @Autowired
    public DeploymentService(DeploymentJobRepository deploymentJobRepository,
                             TargetEnvironmentRepository targetEnvironmentRepository,
                             List<Deployer> deployers) {
        this.deploymentJobRepository = deploymentJobRepository;
        this.targetEnvironmentRepository = targetEnvironmentRepository;
        this.deployers = deployers;
    }

    @Transactional
    public DeploymentJob createAndQueueJob(DeploymentRequest request) {
        // Ensure target environment exists
        targetEnvironmentRepository.findById(request.targetId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid target ID: " + request.targetId()));

        DeploymentJob job = new DeploymentJob();
        job.setServiceName(request.serviceName());
        job.setServiceVersion(request.serviceVersion());
        job.setTargetId(request.targetId());
        job.setStatus(DeploymentJob.DeploymentStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        DeploymentJob savedJob = deploymentJobRepository.save(job);
        
        // Trigger the asynchronous deployment process
        executeDeployment(savedJob.getId());

        return savedJob;
    }

    @Async
    public void executeDeployment(Long jobId) {
        logger.info("Starting async deployment for job ID: {}", jobId);
        DeploymentJob job = deploymentJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        TargetEnvironment target = targetEnvironmentRepository.findById(job.getTargetId())
                .orElseThrow(() -> new RuntimeException("Target not found for job: " + jobId));

        try {
            updateJobStatus(job, DeploymentJob.DeploymentStatus.IN_PROGRESS);

            Deployer deployer = findDeployer(target.getType().name());
            deployer.deploy(job);

            updateJobStatus(job, DeploymentJob.DeploymentStatus.SUCCESS);
            logger.info("Successfully finished async deployment for job ID: {}", jobId);
        } catch (Exception e) {
            logger.error("Deployment failed for job ID: {}", jobId, e);
            updateJobStatus(job, DeploymentJob.DeploymentStatus.FAILED);
        }
    }

    private Deployer findDeployer(String targetType) {
        return deployers.stream()
                .filter(d -> d.supports(targetType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported target type: " + targetType));
    }

    private void updateJobStatus(DeploymentJob job, DeploymentJob.DeploymentStatus status) {
        job.setStatus(status);
        job.setUpdatedAt(LocalDateTime.now());
        deploymentJobRepository.save(job);
    }
}
