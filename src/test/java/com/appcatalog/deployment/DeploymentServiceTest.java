package com.appcatalog.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.domain.DeploymentJobRepository;
import com.appcatalog.deployment.domain.DeploymentStatus;
import com.appcatalog.deployment.dto.DeploymentRequest;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceTest {

  // 테스트를 위한 가짜 레포지토리
  @Mock private DeploymentJobRepository deploymentJobRepository;
  @Mock private TargetEnvironmentRepository targetEnvironmentRepository;

  // 가짜 레포지토리를 사용하는 서비스
  @InjectMocks private DeploymentService deploymentService;

  @DisplayName("올바른 요청 시, 배포 작업이 생성되고 PENDING 상태로 저장된다.")
  @Test
  void createDeploymentJob_ShouldCreateAndSavePendingJob() {
    // given
    DeploymentRequest request = new DeploymentRequest();
    request.setServiceName("test1");
    request.setServiceVersion("1.0.0");
    request.setTargetId(1L);

    DeploymentJob job = new DeploymentJob();
    job.setServiceName(request.getServiceName());
    job.setServiceVersion(request.getServiceVersion());
    job.setTargetId(request.getTargetId());
    job.initializeStatus(); // 초기 상태 설정

    given(targetEnvironmentRepository.existsById(request.getTargetId())).willReturn(true);
    given(deploymentJobRepository.save(any(DeploymentJob.class))).willReturn(job);

    // when
    DeploymentJob createdJob = deploymentService.createAndQueueJob(request);

    // then
    assertThat(createdJob).isNotNull();
    assertThat(createdJob.getStatus()).isEqualTo(DeploymentStatus.PENDING);
    assertThat(createdJob.getServiceName()).isEqualTo(request.getServiceName());
    assertThat(createdJob.getServiceVersion()).isEqualTo(request.getServiceVersion());
    assertThat(createdJob.getTargetId()).isEqualTo(request.getTargetId());
  }
}
