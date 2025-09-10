package com.appcatalog.deployment;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.domain.DeploymentJobRepository;
import com.appcatalog.deployment.dto.DeploymentRequest;
import com.appcatalog.deployment.strategy.Deployer;
import com.appcatalog.error.JobNotFoundException;
import com.appcatalog.error.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

  private final DeploymentJobRepository deploymentJobRepository;
  private final TargetEnvironmentRepository targetEnvironmentRepository;
  private final List<Deployer>
      deployers; // Spring 실행 시, @Component 가 붙은 모든 Deployer 를 자동으로 리스트에 담아줌

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

    DeploymentJob savedJob = deploymentJobRepository.save(job);
    executeDeployment(savedJob.getId());

    return savedJob;
  }

  public DeploymentJob findJobById(Long id) {
    return deploymentJobRepository
        .findById(id)
        .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
  }

  @Async
  @Transactional // Dirty Check 덕분에 수정에선 save 메소드 호출 생략 가능
  public void executeDeployment(Long jobId) {
    DeploymentJob job = findJobById(jobId);
    TargetEnvironment target =
        targetEnvironmentRepository
            .findById(job.getTargetId())
            .orElseThrow(
                () ->
                    new TargetNotFoundException("Target not found with id: " + job.getTargetId()));

    // 배포 작업 시작
    job.startProgress();

    try {
      // 배포 시도
      Deployer deployer = findDeployerFor(target);
      deployer.deploy(job);

      // 배포 작업 성공
      job.markAsSuccess();

    } catch (Exception e) {
      // 배포 실패 시
      log.error("Deployment failed for Job ID: {}", jobId, e);
      job.markAsFailed();
    }
  }

  private Deployer findDeployerFor(TargetEnvironment target) {
    String targetType = target.getType().name();
    return deployers.stream()
        .filter(d -> d.supports(targetType))
        .findFirst()
        .orElseThrow(
            () -> new UnsupportedOperationException("Unsupported target type: " + targetType));
  }
}
