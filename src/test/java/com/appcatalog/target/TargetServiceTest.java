package com.appcatalog.target;

import com.appcatalog.error.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import com.appcatalog.target.domain.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// JUnit5에게 "이번 테스트에서는 Mockito 확장 기능을 사용할게!" 라고 알려주는 선언
@ExtendWith(MockitoExtension.class)
class TargetServiceTest {

  // --- 테스트 준비 ---

  // @Mock: 가짜(Mock) 객체를 만드는 어노테이션.
  // 진짜 DB에 연결되는 대신, 우리가 원하는 대로 행동하게 만들 가짜 Repository.
  @Mock private TargetEnvironmentRepository targetRepository;

  // @InjectMocks: 테스트 대상(TargetService)을 만드는 어노테이션.
  // 위에서 @Mock으로 만든 가짜 객체들을 이 TargetService에 자동으로 주입해줘요.
  @InjectMocks private TargetService targetService;

  @DisplayName("모든 배포 대상을 조회하면, Target 리스트를 반환한다.")
  @Test
  void findAllTargets_ShouldReturnTargetList() {
    // given (준비): 이런 상황이 주어졌을 때
    // 가짜 targetRepository가 findAll() 메소드를 호출받으면,
    // 가짜 TargetEnvironment 객체가 2개 들어있는 리스트를 반환하라고 "미리 행동을 정의"해 둡니다.
    TargetEnvironment target1 = new TargetEnvironment(); // 가짜 데이터 1
    target1.setName("Test Server 1");
    TargetEnvironment target2 = new TargetEnvironment(); // 가짜 데이터 2
    target2.setName("Test Server 2");
    given(targetRepository.findAll()).willReturn(List.of(target1, target2));

    // when (실행): 테스트하려는 메소드를 실제로 호출하면
    List<TargetEnvironment> result = targetService.findAllTargets();

    // then (검증): 결과는 이래야 한다
    // 결과 리스트는 null이 아니어야 하고,
    assertThat(result).isNotNull();
    // 리스트의 크기는 2여야 한다.
    assertThat(result.size()).isEqualTo(2);
  }

  @DisplayName("올바른 데이터로 Target을 생성하면, 저장 후 객체를 반환한다.")
  @Test
  void createTarget_ShouldReturnTarget() {
    // given
    TargetEnvironment target1 = new TargetEnvironment();
    target1.setName("Test Server 1");

    // "만약 targetRepository의 save 메소드가 어떤 TargetEnvironment 객체든(any) 받으면,
    //  그냥 그 객체를 그대로 돌려주도록 연기해줘!" 라고 대본을 줍니다.
    given(targetRepository.save(any(TargetEnvironment.class))).willReturn(target1);

    // when
    TargetEnvironment result = targetService.createTarget(target1);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(target1.getName());
  }

  @DisplayName("존재하는 ID로 조회하면, 해당 Target 객체를 반환한다.")
  @Test
  void findTarget_ShouldReturnTarget() {
    // given (준비)
    // 1. "DB에 이미 저장되어 있다고 가정할" 가짜 객체를 만듭니다.
    TargetEnvironment existingTarget = new TargetEnvironment();
    existingTarget.setId(1L); // ID도 직접 설정해줍니다.
    existingTarget.setName("Test Server 1");

    // 2. 리허설: "만약 가짜 repository에게 ID가 1L인 Target을 찾아달라고 요청하면,
    //            Optional로 감싼 existingTarget 객체를 반환해줘!" 라고 시킵니다.
    given(targetRepository.findById(1L)).willReturn(Optional.of(existingTarget));

    // when (실행)
    // 이제 진짜 findTargetById를 호출합니다.
    TargetEnvironment result = targetService.findTargetById(1L);

    // then (검증)
    // 결과가 우리가 "가정했던" 객체와 일치하는지 확인합니다.
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(existingTarget.getId());
    assertThat(result.getName()).isEqualTo(existingTarget.getName());
  }

  @DisplayName("존재하지 않는 ID로 조회하면, 예외를 던진다.")
  @Test
  void findTarget_ShouldThrowException() {
    // given
    given(targetRepository.findById(1L)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> targetService.findTargetById(1L))
        .isInstanceOf(TargetNotFoundException.class)
        .hasMessage("Target not found with id: 1");
  }

  @DisplayName("존재하는 ID로 Target을 수정하면, 수정된 Target 객체를 반환한다.")
  @Test
  void updateTarget_ShouldReturnUpdatedTarget() {
    // given
    Long targetId = 1L;
    TargetEnvironment existingTarget = new TargetEnvironment();
    existingTarget.setId(targetId);
    existingTarget.setName("Old Name");
    existingTarget.setType(TargetType.VM);
    existingTarget.setHost("old.host.com");
    existingTarget.setPort(8080);
    existingTarget.setCredentialId("old_cred");

    TargetEnvironment updatedDetails = new TargetEnvironment();
    updatedDetails.setName("New Name");
    updatedDetails.setType(TargetType.KUBERNETES);
    updatedDetails.setHost("new.host.com");
    updatedDetails.setPort(9090);
    updatedDetails.setCredentialId("new_cred");

    given(targetRepository.findById(targetId)).willReturn(Optional.of(existingTarget));
    given(targetRepository.save(any(TargetEnvironment.class))).willReturn(existingTarget);

    // when
    TargetEnvironment result = targetService.updateTarget(targetId, updatedDetails);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(targetId);
    assertThat(result.getName()).isEqualTo(updatedDetails.getName());
    assertThat(result.getType()).isEqualTo(updatedDetails.getType());
    assertThat(result.getHost()).isEqualTo(updatedDetails.getHost());
    assertThat(result.getPort()).isEqualTo(updatedDetails.getPort());
    assertThat(result.getCredentialId()).isEqualTo(updatedDetails.getCredentialId());
  }

  @DisplayName("존재하지 않는 ID로 Target을 수정하면, 예외를 던진다.")
  @Test
  void updateTarget_ShouldThrowExceptionWhenNotFound() {
    // given
    Long targetId = 1L;
    TargetEnvironment updatedDetails = new TargetEnvironment();
    given(targetRepository.findById(targetId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> targetService.updateTarget(targetId, updatedDetails))
        .isInstanceOf(TargetNotFoundException.class)
        .hasMessage("Target not found with id: " + targetId);
  }

  @DisplayName("존재하는 ID로 Target을 삭제하면, 성공적으로 삭제한다.")
  @Test
  void deleteTarget_ShouldDeleteSuccessfully() {
    // given
    Long targetId = 1L;
    given(targetRepository.existsById(targetId)).willReturn(true);

    // when
    targetService.deleteTarget(targetId);

    // then
    verify(targetRepository, times(1)).deleteById(targetId); // deleteById가 1번 호출되었는지 확인
  }

  @DisplayName("존재하지 않는 ID로 Target을 삭제하면, 예외를 던진다.")
  @Test
  void deleteTarget_ShouldThrowExceptionWhenNotFound() {
    // given
    Long targetId = 1L;
    given(targetRepository.existsById(targetId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> targetService.deleteTarget(targetId))
        .isInstanceOf(TargetNotFoundException.class)
        .hasMessage("Target not found with id: " + targetId);
  }
}
