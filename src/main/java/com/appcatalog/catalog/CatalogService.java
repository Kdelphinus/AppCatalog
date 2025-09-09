package com.appcatalog.catalog;

import com.appcatalog.catalog.dto.NexusApiResponse;
import com.appcatalog.catalog.dto.NexusArtifact;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

  private final RestTemplate restTemplate;

  // application.properties에서 nexus.api.url 값 가져옴
  @Value("${nexus.api.url}")
  private String nexusApiUrl;

  public List<NexusArtifact> getArtifactsByRepository(String repository) {
    String url = nexusApiUrl + "/service/rest/v1/components?repository=" + repository;
    NexusApiResponse jsonResponse = restTemplate.getForObject(url, NexusApiResponse.class);
    if (jsonResponse != null && jsonResponse.getItems() != null) {
      return jsonResponse.getItems();
    }
    return Collections.emptyList(); // 자바 공용 빈 배열 반환(매번 새로 만들지 않아서 메모리 절약 가능)
  }
}
