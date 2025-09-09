package com.appcatalog.catalog;

import com.appcatalog.catalog.dto.NexusApiResponse;
import com.appcatalog.catalog.dto.NexusArtifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

  @Mock // 가짜 "전화기"
  private RestTemplate restTemplate;

  @InjectMocks // 주인공: 가짜 전화기를 주입받은 서비스
  private CatalogService catalogService;

  // @BeforeEach: 각각의 @Test가 실행되기 "직전에" 항상 먼저 실행되는 설정 메소드
  @BeforeEach
  void setUp() {
    // 단위 테스트에서는 @Value가 동작하지 않으므로, ReflectionTestUtils를 사용해
    // private 필드인 nexusApiUrl에 직접 값을 강제로 주입합니다.
    ReflectionTestUtils.setField(catalogService, "nexusApiUrl", "http://fake-nexus:8081");
  }

  @DisplayName("Nexus API를 호출하여 아티팩트 목록을 성공적으로 반환한다.")
  @Test
  void getArtifactsByRepository_ShouldReturnArtifactList() {
    // given (준비)
    String repository = "maven-releases";
    String expectedUrl =
        "http://fake-nexus:8081/service/rest/v1/components?repository=" + repository;

    // 1. Nexus가 반환할 "가짜 응답 데이터"를 미리 만들어 둡니다.
    NexusArtifact fakeArtifact = new NexusArtifact();
    fakeArtifact.setName("my-cool-app");
    NexusApiResponse fakeResponse = new NexusApiResponse();
    fakeResponse.setItems(List.of(fakeArtifact));

    // 2. 리허설: "만약 가짜 restTemplate에게 'expectedUrl' 주소로
    //            'NexusApiResponse.class' 타입을 요청하는 전화가 오면,
    //            우리가 만든 'fakeResponse'를 반환하도록 연기해줘!"
    given(restTemplate.getForObject(eq(expectedUrl), eq(NexusApiResponse.class)))
        .willReturn(fakeResponse);

    // when (실행)
    List<NexusArtifact> result = catalogService.getArtifactsByRepository(repository);

    // then (검증)
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getName()).isEqualTo("my-cool-app");
  }
}
