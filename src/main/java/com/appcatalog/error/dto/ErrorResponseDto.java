package com.appcatalog.error.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder // 빌더 패턴을 사용하여 객체 생성 용이
public class ErrorResponseDto {
  @Builder.Default // 빌더 패턴 사용 시 기본값 설정
  private final LocalDateTime timestamp = LocalDateTime.now(); // 에러 발생 시간

  private final String status; // 예: "ERROR" 또는 HTTP 상태 코드 문자열
  private final int code; // HTTP 상태 코드 숫자 (예: 404, 500)
  private final String message; // 사용자에게 보여줄 간략한 에러 메시지
  private final String path; // 요청 경로 (선택 사항)
  private final List<ErrorDetail> errors; // 필드 유효성 검증 오류 시 상세 정보

  @Getter
  @Builder
  public static class ErrorDetail {
    private final String field;
    private final String defaultMessage;
  }
}
