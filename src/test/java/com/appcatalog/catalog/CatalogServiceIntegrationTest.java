package com.appcatalog.catalog;

import com.appcatalog.catalog.dto.NexusArtifact;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // <-- 이 임포트 추가

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled // gradlew build 시 테스트 생략
@SpringBootTest
@ActiveProfiles("test") // <-- 이 어노테이션 추가: application-test.properties 파일을 사용하도록 지시
class CatalogServiceIntegrationTest {

  @Autowired // Spring이 만든 진짜 CatalogService를 주입받음
  private CatalogService catalogService;

  // @Value("${catalog.nexus-url}") // <-- NexusService가 이 값을 받도록 설정되어 있다면,
  // private String nexusUrl; //      여기서 직접 주입받을 필요는 없습니다. NexusService 내부에서 받도록 합니다.

  @DisplayName("Nexus API를 호출하여 maven-releases 저장소의 아티팩트 목록을 가져온다.")
  @Test
  void getArtifacts_ByRepository_IntegrationTest() {
    // given
    String repository = "maven-releases";

    // when
    // 이 테스트는 docker-compose up 으로 Nexus가 실행 중이어야 성공합니다.
    // @ActiveProfiles("test") 덕분에 catalogService 내부의 NexusService는
    // 'http://nexus:8081' 주소를 사용하여 Nexus에 연결을 시도할 것입니다.
    List<NexusArtifact> artifacts = catalogService.getArtifactsByRepository(repository);

    // then
    // Nexus가 비어있다면, 비어있는 리스트가 반환되는 것을 기대합니다.
    assertThat(artifacts).isNotNull();
    // 실제로는 더 구체적인 검증이 필요하겠지만, 현재는 연결 성공 여부만 확인합니다.
    // assertThat(artifacts).isEmpty(); // Nexus에 아무것도 없다면
  }
}
