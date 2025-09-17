package com.appcatalog.target;

import com.appcatalog.error.DataConflictException;
import com.appcatalog.error.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로는 읽기 전용으로 설정
public class TargetService {

  private final TargetEnvironmentRepository targetRepository;

  /** 새로운 배포 대상을 생성 (Create) */
  @Transactional // 데이터를 변경하므로 쓰기 가능 트랜잭션 적용
  public TargetEnvironment createTarget(TargetEnvironment target) {
    if (targetRepository.existsByName(target.getName())) {
      throw new DataConflictException("Target already exists with name: " + target.getName());
    }
    return targetRepository.save(target);
  }

  /** 모든 배포 대상을 조회 (Read) */
  public List<TargetEnvironment> findAllTargets() {
    return targetRepository.findAll();
  }

  /** ID로 특정 배포 대상을 조회 (Read) ID에 해당하는 대상이 없으면 예외를 발생시키는 비즈니스 규칙 포함 */
  public TargetEnvironment findTargetById(Long id) {
    return targetRepository
        .findById(id)
        .orElseThrow(() -> new TargetNotFoundException("Target not found with id: " + id));
  }

  /** 배포 대상 정보를 수정 (Update) ID에 해당하는 대상이 없으면 예외를 발생시키는 비즈니스 규칙 포함 */
  @Transactional // 데이터를 변경하므로 쓰기 가능 트랜잭션 적용
  public TargetEnvironment updateTarget(Long id, TargetEnvironment updatedDetails) {
    TargetEnvironment existingTarget = findTargetById(id); // 먼저 존재하는지 확인 (없으면 예외 발생)

    existingTarget.setName(updatedDetails.getName());
    existingTarget.setType(updatedDetails.getType());
    existingTarget.setHost(updatedDetails.getHost());
    existingTarget.setPort(updatedDetails.getPort());
    existingTarget.setCredential(updatedDetails.getCredential());

    return targetRepository.save(existingTarget);
  }

  /** ID로 특정 배포 대상을 삭제 (Delete) */
  @Transactional // 데이터를 변경하므로 쓰기 가능 트랜잭션 적용
  public void deleteTarget(Long id) {
    if (!targetRepository.existsById(id)) {
      throw new TargetNotFoundException("Target not found with id: " + id);
    }
    targetRepository.deleteById(id);
  }
}
