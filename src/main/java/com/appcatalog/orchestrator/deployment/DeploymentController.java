package com.appcatalog.orchestrator.deployment;

import com.appcatalog.orchestrator.deployment.domain.DeploymentJob;
import com.appcatalog.orchestrator.deployment.dto.DeploymentRequest;
import com.appcatalog.orchestrator.deployment.dto.DeploymentResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deployments")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping
    public ResponseEntity<DeploymentResponse> createDeployment(@Valid @RequestBody DeploymentRequest request) {
        DeploymentJob newJob = deploymentService.createAndQueueJob(request);
        return ResponseEntity.accepted().body(DeploymentResponse.fromEntity(newJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeploymentResponse> getDeploymentStatus(@PathVariable Long id) {
        // TODO: Implement logic to fetch deployment status
        // DeploymentJob job = deploymentService.getJobStatus(id);
        // return ResponseEntity.ok(DeploymentResponse.fromEntity(job));
        return ResponseEntity.ok().build(); // Placeholder
    }
}
