package com.appcatalog.deployment.strategy;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.error.exception.TargetNotFoundException;
import com.appcatalog.target.domain.TargetEnvironment;
import com.appcatalog.target.domain.TargetEnvironmentRepository;
import com.appcatalog.target.domain.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j // logger 추가
@Component // 빈으로 등록
@RequiredArgsConstructor
public class VmDeployer implements Deployer {

  private final TargetEnvironmentRepository targetEnvironmentRepository;

  @Override
  public void deploy(DeploymentJob job) {
    log.info("Executing VM deployment strategy for Job ID: {}", job.getId());

    // 배포 대상 서버 정보 조회
    TargetEnvironment target =
        targetEnvironmentRepository
            .findById(job.getTargetId())
            .orElseThrow(
                () ->
                    new TargetNotFoundException("Target not found with id: " + job.getTargetId()));

    // SSH 클라이언트 생성
    final SSHClient ssh = new SSHClient();

    try {
      // 보안 설정(처음 보는 서버도 일단 믿는 설정 -> 테스트용)
      ssh.addHostKeyVerifier(new PromiscuousVerifier());

      // 서버에 연결
      log.info("Connecting to host: {} on port: {}", target.getHost(), target.getPort());
      ssh.connect(target.getHost(), target.getPort());

      // 로그인 (사용자 인증)
      // docker-compose.yml 에서 설정한 값 사용(테스트용)
      // 실제는 vault 같은 곳에서 안전하게 가져와야 함
      ssh.authPassword("testuser", "testpassword");

      try (Session session = ssh.startSession()) {
        // 원격으로 실행할 명령어 정의
        final String command =
            "echo 'Deployment successful on $(date)' > /tmp/deployment_receipt.txt";
        log.info("Executing command: {}", command);

        // 명령어 실행 및 결과 확인
        final Session.Command cmd = session.exec(command);
        cmd.join(5, TimeUnit.SECONDS);

        // 결과 로그 출력
        log.info("Command finished with exit status: {}", cmd.getExitStatus());
        log.info("Command output: \n{}", IOUtils.readFully(cmd.getInputStream()).toString());
      }
    } catch (IOException e) {
      // IO 예외 발생 시 로그 기록 및 런타임 예외로 던짐
      log.error("SSH operation failed for job ID: {}", job.getId(), e);
      throw new RuntimeException("Deployment failed due to SSH connection error", e);
    } finally {
      // 어떤 경우에도 연결 종료
      try {
        if (ssh.isConnected()) {
          ssh.disconnect();
          log.info("SSH connection closed for job ID: {}", job.getId());
        }
      } catch (IOException e) {
        log.error("Error while disconnecting SSH client for job ID: {}", job.getId(), e);
      }
    }
  }

  @Override
  public boolean supports(String targetType) {
    // VM 타입만 지원
    return TargetType.VM.name().equals(targetType);
  }
}
