package com.appcatalog.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.domain.DeploymentJobRepository;
import com.appcatalog.deployment.domain.DeploymentStatus;
import com.appcatalog.deployment.dto.DeploymentRequest;
import com.appcatalog.deployment.strategy.Deployer;
import com.appcatalog.error.exception.JobNotFoundException;
import com.appcatalog.error.exception.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import java.util.List;
import java.util.Optional;

import com.appcatalog.target.domain.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceTest {

  @Mock private DeploymentJobRepository deploymentJobRepository;
  @Mock private TargetEnvironmentRepository targetEnvironmentRepository;
  @Mock private Deployer vmDeployer; // VmDeployer를 Mock으로 주입

  // DeploymentService 자체를 Spy로 만듭니다.
  // 이렇게 하면 서비스의 실제 메소드를 호출하면서, 필요한 부분만 Mocking할 수 있습니다.
  @InjectMocks @Spy private DeploymentService deploymentService;

  @Captor private ArgumentCaptor<Long> jobIdCaptor;
  @Captor private ArgumentCaptor<DeploymentJob> jobCaptor;

  private DeploymentRequest testRequest;
  private TargetEnvironment testTarget;
  private DeploymentJob testJob;

  @BeforeEach
  void setUp() {
    testRequest = new DeploymentRequest();
    testRequest.setServiceName("test-service");
    testRequest.setServiceVersion("1.0.0");
    testRequest.setTargetId(1L);

    testTarget = new TargetEnvironment();
    testTarget.setId(1L);
    testTarget.setName("Test VM");
    testTarget.setHost("localhost");
    testTarget.setPort(22);
    testTarget.setType(TargetType.VM); // VmDeployer가 지원하는 타입

    testJob = new DeploymentJob();
    testJob.setId(1L); // Mocking된 job에 ID 부여
    testJob.setServiceName(testRequest.getServiceName());
    testJob.setServiceVersion(testRequest.getServiceVersion());
    testJob.setTargetId(testRequest.getTargetId());
    testJob.initializeStatus(); // PENDING 상태로 초기화
  }

  @DisplayName("올바른 요청 시, 배포 작업이 생성되고 PENDING 상태로 저장된다.")
  @Test
  void createDeploymentJob_ShouldCreateAndSavePendingJob() {
    // given
    // targetId 존재 확인
    given(targetEnvironmentRepository.existsById(testRequest.getTargetId())).willReturn(true);
    // save 호출 시 ID가 부여된 job 반환
    given(deploymentJobRepository.save(any(DeploymentJob.class))).willReturn(testJob);

    // executeDeployment는 Spy 객체의 실제 메소드가 호출되므로
    // 그 안에서 필요한 findById 호출들도 Mocking해야 합니다.
    // 하지만 현재 테스트는 createAndQueueJob이 executeDeployment를 '호출했다'는 것에 집중
    // executeDeployment 내부 로직은 아래에서 별도로 Mocking하여 실제 실행을 방지
    doNothing().when(deploymentService).executeDeployment(anyLong());

    // when
    DeploymentJob createdJob = deploymentService.createAndQueueJob(testRequest);

    // then
    assertThat(createdJob).isNotNull();
    assertThat(createdJob.getId()).isEqualTo(testJob.getId());
    assertThat(createdJob.getStatus())
        .isEqualTo(DeploymentStatus.PENDING); // createAndQueueJob은 PENDING으로 저장
    assertThat(createdJob.getServiceName()).isEqualTo(testRequest.getServiceName());

    // verify
    // save가 한 번 호출되었는지 확인
    then(deploymentJobRepository).should(times(1)).save(jobCaptor.capture());
    assertThat(jobCaptor.getValue().getStatus()).isEqualTo(DeploymentStatus.PENDING);

    // executeDeployment가 정확한 jobId로 호출되었는지 확인
    then(deploymentService).should(times(1)).executeDeployment(jobIdCaptor.capture());
    assertThat(jobIdCaptor.getValue()).isEqualTo(testJob.getId());
  }

  @DisplayName("DeploymentService가 Deployer를 찾아 배포를 실행하고 성공 상태로 변경한다.")
  @Test
  void executeDeployment_ShouldFindDeployerAndMarkAsSuccess() {
    // given
    // findById(jobId) 호출 시 testJob 반환
    given(deploymentJobRepository.findById(testJob.getId())).willReturn(Optional.of(testJob));
    // targetRepository.findById(targetId) 호출 시 testTarget 반환
    given(targetEnvironmentRepository.findById(testJob.getTargetId()))
        .willReturn(Optional.of(testTarget));

    // VmDeployer의 supports가 VM 타입을 지원한다고 Mocking
    given(vmDeployer.supports(testTarget.getType().name())).willReturn(true);

    // Deployer 목록을 주입 (@InjectMocks는 @Mock과 @Spy를 자동으로 주입하지만, List는 수동으로 설정 필요)
    ReflectionTestUtils.setField(deploymentService, "deployers", List.of(vmDeployer));

    // when
    // executeDeployment는 @Async이므로, Spy 객체를 통해 직접 호출
    deploymentService.executeDeployment(testJob.getId());

    // then
    // Deployer의 deploy 메소드가 호출되었는지 확인
    then(vmDeployer).should(times(1)).deploy(jobCaptor.capture());
    assertThat(jobCaptor.getValue().getId()).isEqualTo(testJob.getId());

    // job 상태가 IN_PROGRESS -> SUCCESS로 변경되었는지 확인
    assertThat(testJob.getStatus()).isEqualTo(DeploymentStatus.SUCCESS);
  }

  @DisplayName("Deployer 실패 시, 배포 작업이 FAILED 상태로 변경된다.")
  @Test
  void executeDeployment_ShouldMarkAsFailedOnDeployerFailure() {
    // given
    given(deploymentJobRepository.findById(testJob.getId())).willReturn(Optional.of(testJob));
    given(targetEnvironmentRepository.findById(testJob.getTargetId()))
        .willReturn(Optional.of(testTarget));
    given(vmDeployer.supports(testTarget.getType().name())).willReturn(true);

    // Deployer 목록 주입
    ReflectionTestUtils.setField(deploymentService, "deployers", List.of(vmDeployer));

    // deployer.deploy 호출 시 예외 발생하도록 Mocking
    doThrow(new RuntimeException("Deployment simulated failure"))
        .when(vmDeployer)
        .deploy(any(DeploymentJob.class));

    // when
    deploymentService.executeDeployment(testJob.getId());

    // then
    // deploy가 한 번 호출되었는지 확인
    then(vmDeployer).should(times(1)).deploy(jobCaptor.capture());
    // job 상태가 IN_PROGRESS -> FAILED로 변경되었는지 확인
    assertThat(testJob.getStatus()).isEqualTo(DeploymentStatus.FAILED);
    // save가 호출되지 않았음을 확인 (더티 체킹 덕분)
    then(deploymentJobRepository).should(never()).save(any(DeploymentJob.class));
  }

  @DisplayName("Target이 존재하지 않으면 createAndQueueJob은 TargetNotFoundException을 발생시킨다.")
  @Test
  void createAndQueueJob_ShouldThrowTargetNotFoundException() {
    // given
    given(targetEnvironmentRepository.existsById(testRequest.getTargetId())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> deploymentService.createAndQueueJob(testRequest))
        .isInstanceOf(TargetNotFoundException.class)
        .hasMessageContaining("Target with ID: " + testRequest.getTargetId() + " not found.");

    // executeDeployment가 호출되지 않았는지 확인
    then(deploymentService).should(never()).executeDeployment(anyLong());
  }

  @DisplayName("Job ID로 Job을 찾을 수 없으면 findJobById는 JobNotFoundException을 발생시킨다.")
  @Test
  void findJobById_ShouldThrowJobNotFoundExceptionWhenJobNotFound() {
    // given
    given(deploymentJobRepository.findById(anyLong())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> deploymentService.findJobById(1L))
        .isInstanceOf(JobNotFoundException.class)
        .hasMessageContaining("Job not found with id: 1");
  }
}
