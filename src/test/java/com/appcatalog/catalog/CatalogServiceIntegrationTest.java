package com.appcatalog.catalog;

import com.appcatalog.catalog.dto.NexusArtifact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest: 테스트를 위해 실제 Spring 애플리케이션 환경을 모두 로드!
@SpringBootTest
class CatalogServiceIntegrationTest {

  @Autowired // Spring이 만든 진짜 CatalogService를 주입받음
  private CatalogService catalogService;

  @DisplayName("Nexus API를 호출하여 maven-releases 저장소의 아티팩트 목록을 가져온다.")
  @Test
  void getArtifacts_ByRepository_IntegrationTest() {
    // given
    String repository = "maven-releases";

    // when
    // 이 테스트는 docker-compose up 으로 Nexus가 실행 중이어야 성공합니다.
    List<NexusArtifact> artifacts = catalogService.getArtifactsByRepository(repository);

    // then
    // 지금은 Nexus에 아무것도 없으니, 비어있는 리스트가 반환되는 것을 기대할 수 있습니다.
    assertThat(artifacts).isNotNull();
  }
}
