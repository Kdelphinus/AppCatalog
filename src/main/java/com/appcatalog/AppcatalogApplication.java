package com.appcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // 비동기 작업을 위한 어노테이션
public class AppcatalogApplication {

  public static void main(String[] args) {
    SpringApplication.run(AppcatalogApplication.class, args);
  }
}
