package com.appcatalog.deployment.strategy;

import com.appcatalog.deployment.domain.DeploymentJob;

public interface Deployer {

  /**
   * 주어진 배포 작업을 실행합니다.
   *
   * @param job 실행할 배포 작업 정보
   */
  void deploy(DeploymentJob job);

  /**
   * 해당 Deployer가 특정 타겟 타입을 지원하는지 여부를 반환합니다.
   *
   * @param targetType 확인할 타켓 타입의 문자열
   * @return 지원하면 true, 그렇지 않으면 false
   */
  boolean supports(String targetType);
}
