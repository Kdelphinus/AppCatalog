package com.appcatalog.error;

import com.appcatalog.error.dto.ErrorResponseDto;
import com.appcatalog.error.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice // 이 클래스가 모든 컨트롤러의 예외를 담당
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponseDto> handleAllUncaughtExceptions(
      RuntimeException ex, HttpServletRequest request) {
    log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponseDto.builder()
                .status("Internal server error")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .path(request.getRequestURI()) // 요청 경로
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    List<ErrorResponseDto.ErrorDetail> errors = new ArrayList<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              ErrorResponseDto.ErrorDetail errorDetail =
                  ErrorResponseDto.ErrorDetail.builder()
                      .field(error.getField())
                      .defaultMessage(error.getDefaultMessage())
                      .build();
              errors.add(errorDetail);
            });
    ErrorResponseDto errorResponse =
        ErrorResponseDto.builder()
            .status("BAD REQUEST")
            .code(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .errors(errors)
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(TargetNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleTargetNotFound(TargetNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ErrorResponseDto.builder()
                .status("Not found")
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler(JobNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleJobNotFound(JobNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ErrorResponseDto.builder()
                .status("Not found")
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ErrorResponseDto> handleDataConflict(DataConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponseDto.builder()
                .status("Conflict")
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler(CannotDeployException.class)
  public ResponseEntity<ErrorResponseDto> handleCannotDeploy(CannotDeployException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ErrorResponseDto.builder()
                .status("Bad request")
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ErrorResponseDto.builder()
                .status("Unauthorized")
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build());
  }
}
