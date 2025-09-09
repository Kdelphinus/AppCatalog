package com.appcatalog.catalog;

import com.appcatalog.catalog.dto.NexusArtifact;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

  private final CatalogService catalogService;

  @GetMapping("/{repository}")
  public ResponseEntity<List<NexusArtifact>> getArtifact(@PathVariable String repository) {
    List<NexusArtifact> artifacts = catalogService.getArtifactsByRepository(repository);
    return ResponseEntity.ok(artifacts);
  }
}
