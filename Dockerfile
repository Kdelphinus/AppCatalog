# 1. Java 17 환경을 기반으로 빌드 환경을 구성
FROM openjdk:17-alpine as builder

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 관련 파일들을 먼저 복사하여 의존성을 캐싱
COPY build.gradle settings.gradle /workspace/app/
COPY gradle /workspace/app/gradle
COPY gradlew /workspace/app/

# Gradle을 실행하여 의존성을 다운로드
# RUN ./gradlew build || return 0 # 주석 처리된 부분은 필요 시 사용
COPY . /workspace/app/

# Gradle 빌드를 실행하여 jar 파일 생성
RUN ./gradlew build

# 2. 실제 실행 환경 구성 (더 가벼운 이미지 사용)
FROM openjdk:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일을 실행 환경으로 복사
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "/app/app.jar"]