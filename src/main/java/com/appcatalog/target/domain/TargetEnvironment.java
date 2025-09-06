package com.appcatalog.target.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TARGET_ENVIRONMENTS")
@Getter
@Setter
public class TargetEnvironment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name; // 설치할 서버의 이름

  @Enumerated(EnumType.STRING)
  private TargetType type; // 서버의 종류

  private String host; // 실제 서버 호스트
  private int port; // 서버 포트
  private String credentialId; // 인증 정보 ID
}
