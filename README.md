# Nudge-Signal Backend

소개팅 앱 **Nudge-Signal** 의 백엔드 서버.

## 기술 스택

- Java 17
- Spring Boot 3.5.x (Web, Data JPA, Validation, Actuator)
- PostgreSQL 16
- Redis 7
- Gradle (with wrapper)
- Docker / docker-compose

## 사전 준비

- JDK 17 (Temurin 권장)
- Docker / Docker Desktop
- IntelliJ IDEA (권장 IDE)

## 시작하기 (로컬 개발)

### 1. 인프라 기동 (PostgreSQL + Redis)

```bash
cp .env.example .env
docker compose up -d postgres redis
```

기본 접속 정보 (`.env` 에서 변경 가능):

| 항목         | 값              |
|------------|-----------------|
| DB host    | localhost:5432  |
| DB name    | nudge_signal    |
| DB user/pw | nudge / nudge   |
| Redis      | localhost:6379  |

### 2. 애플리케이션 실행

IntelliJ 에서 `NudgeSignalBackendApplication` 을 실행하거나 CLI 에서:

```bash
./gradlew bootRun
```

기본 활성 프로파일은 `local` 입니다 (`SPRING_PROFILES_ACTIVE` 로 변경 가능).

### 3. 헬스체크

```bash
curl http://localhost:8080/api/ping
curl http://localhost:8080/actuator/health
```

## 프로파일

| Profile | 용도              | DDL    |
|---------|-----------------|--------|
| local   | 로컬 개발 (Docker)  | update |
| dev     | 개발 서버           | validate |
| prod    | 운영              | validate |

DB / Redis 접속 정보는 모두 환경변수로 주입 (`DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`).

## Docker 로 앱 빌드 & 실행

```bash
docker build -t nudge-signal-backend:local .
docker run --rm -p 8080:8080 --env-file .env nudge-signal-backend:local
```

## 빌드 & 테스트

```bash
./gradlew clean build      # 컴파일 + 테스트 + bootJar
./gradlew test             # 테스트만
./gradlew bootJar          # 실행 가능한 jar
```

## 디렉터리 구조

```
src/main/java/com/nudgesignal/backend
├── NudgeSignalBackendApplication.java
└── api/                  # REST 컨트롤러
src/main/resources
├── application.yml       # 공통 설정
├── application-local.yml # 로컬 프로파일
├── application-dev.yml   # 개발 프로파일
└── application-prod.yml  # 운영 프로파일
```
