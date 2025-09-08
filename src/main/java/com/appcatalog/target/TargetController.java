package com.appcatalog.target;

import com.appcatalog.target.domain.TargetEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/targets")
@RequiredArgsConstructor
public class TargetController {

  private final TargetService targetService;

  // --- 생성 API ---
  @PostMapping
  public ResponseEntity<TargetEnvironment> createTarget(@RequestBody TargetEnvironment newTarget) {
    TargetEnvironment createdTarget = targetService.createTarget(newTarget);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTarget); // 201 Created
  }

  // --- 조회 API ---
  @GetMapping
  public ResponseEntity<List<TargetEnvironment>> getAllTargets() {
    List<TargetEnvironment> targets = targetService.findAllTargets();
    return ResponseEntity.ok(targets); // 200 OK
  }

  @GetMapping("/{id}")
  public ResponseEntity<TargetEnvironment> getTargetById(@PathVariable Long id) {
    TargetEnvironment target = targetService.findTargetById(id);
    return ResponseEntity.ok(target); // 200 OK
  }

  // --- 수정 API ---
  @PutMapping("/{id}")
  public ResponseEntity<TargetEnvironment> updateTargetById(
      @PathVariable Long id, @RequestBody TargetEnvironment updatedTarget) {
    TargetEnvironment target = targetService.updateTarget(id, updatedTarget);
    return ResponseEntity.ok(target); // 200 OK
  }

  // --- 삭제 API ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTargetById(@PathVariable Long id) {
    targetService.deleteTarget(id);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
