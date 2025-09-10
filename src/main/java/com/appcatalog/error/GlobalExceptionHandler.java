package com.appcatalog.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 이 클래스가 모든 컨트롤러의 예외를 담당
public class GlobalExceptionHandler {

  // TargetNotFoundException 이 들어오면, 이 메소드를 실행
  @ExceptionHandler(TargetNotFoundException.class)
  public ResponseEntity<String> handleTargetNotFound(TargetNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(JobNotFoundException.class)
  public ResponseEntity<String> handleJobNotFound(JobNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }
}
