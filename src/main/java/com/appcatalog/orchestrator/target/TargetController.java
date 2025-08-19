package com.appcatalog.orchestrator.target;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appcatalog.orchestrator.target.domain.TargetEnvironment;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/targets")
public class TargetController {

  // Placeholder for TargetService
  // @Autowired
  // private TargetService targetService;

  @GetMapping
  public ResponseEntity<List<TargetEnvironment>> getTargets() {
    // TODO: Implement logic to fetch all target environments
    return ResponseEntity.ok(Collections.emptyList());
  }
}
