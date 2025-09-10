package com.appcatalog.deployment;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.domain.DeploymentJobRepository;
import com.appcatalog.deployment.dto.DeploymentRequest;
import com.appcatalog.error.JobNotFoundException;
import com.appcatalog.error.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeploymentService {

  private final DeploymentJobRepository deploymentJobRepository;
  private final TargetEnvironmentRepository targetEnvironmentRepository;

  @Transactional
  public DeploymentJob createAndQueueJob(DeploymentRequest request) {
    if (!targetEnvironmentRepository.existsById(request.getTargetId())) {
      throw new TargetNotFoundException("Target with ID: " + request.getTargetId() + " not found.");
    }
    DeploymentJob job = new DeploymentJob();
    job.setServiceName(request.getServiceName());
    job.setServiceVersion(request.getServiceVersion());
    job.setTargetId(request.getTargetId());
    job.initializeStatus(); // 초기 상태 설정
    return deploymentJobRepository.save(job);
  }

  public DeploymentJob findJobById(Long id) {
    return deploymentJobRepository
        .findById(id)
        .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
  }
}
