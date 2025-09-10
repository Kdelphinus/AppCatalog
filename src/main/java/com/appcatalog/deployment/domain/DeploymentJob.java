package com.appcatalog.deployment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DEPLOYMENT_JOBS")
@Setter // TODO 테스트를 위한 임시 추가
@Getter
public class DeploymentJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter private String serviceName; // 소프트웨어 이름
  @Setter private String serviceVersion; // 소프트웨어 버전
  @Setter private Long targetId; // 배포될 대상 서버의 ID

  @Enumerated(EnumType.STRING)
  private DeploymentStatus status; // 배포 상태

  @CreationTimestamp private LocalDateTime createdAt; // 생성 시간
  @UpdateTimestamp private LocalDateTime updatedAt; // 변경 시간

  public void startProgress() {
    this.status = DeploymentStatus.IN_PROGRESS;
  }

  public void markAsSuccess() {
    this.status = DeploymentStatus.SUCCESS;
  }

  public void markAsFailed() {
    this.status = DeploymentStatus.FAILED;
  }

  public void initializeStatus() { // 초기 상태를 설정하는 메소드
    this.status = DeploymentStatus.PENDING;
  }
}
