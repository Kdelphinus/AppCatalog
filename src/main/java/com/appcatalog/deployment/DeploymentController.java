package com.appcatalog.deployment;

import com.appcatalog.deployment.domain.DeploymentJob;
import com.appcatalog.deployment.dto.DeploymentRequest;
import com.appcatalog.deployment.dto.DeploymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deployments")
@RequiredArgsConstructor
public class DeploymentController {

  private final DeploymentService deploymentService;

  @PostMapping
  public ResponseEntity<DeploymentResponse> createDeployment(
      @RequestBody DeploymentRequest request) {
    DeploymentJob createdJob = deploymentService.createAndQueueJob(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(DeploymentResponse.fromEntity(createdJob));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DeploymentResponse> getDeploymentById(@PathVariable Long id) {
    DeploymentJob job = deploymentService.findJobById(id);
    return ResponseEntity.ok(DeploymentResponse.fromEntity(job));
  }
}
