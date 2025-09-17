package com.appcatalog.target.domain;

import com.appcatalog.credential.Credential;
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

  // TargetEnvironment가 저장/삭제 시 Credential도 같이 저장/삭제 되도록 설정
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  // 외래키 컬럼명 지정
  @JoinColumn(name = "credential_id", referencedColumnName = "id")
  private Credential credential; // 인증 정보 ID
}
