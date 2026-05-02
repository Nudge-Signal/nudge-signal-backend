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

## 환경변수

이 저장소는 **어떠한 환경변수 값도 포함하지 않는다**. 모든 값(호스트, 포트, 계정, 비밀번호 등)은 저장소 **밖**에서 주입한다.

필요한 환경변수 (값은 본인이 안전한 위치에 보관):

| 키                         | 설명                          |
|---------------------------|-----------------------------|
| `SPRING_PROFILES_ACTIVE`  | `local` / `dev` / `prod`    |
| `SERVER_PORT`             | 애플리케이션 포트                  |
| `DB_HOST`                 | PostgreSQL 호스트              |
| `DB_PORT`                 | PostgreSQL 포트               |
| `DB_NAME`                 | DB 이름                       |
| `DB_USERNAME`             | DB 계정                       |
| `DB_PASSWORD`             | DB 비밀번호                     |
| `REDIS_HOST`              | Redis 호스트                   |
| `REDIS_PORT`              | Redis 포트                    |
| `REDIS_PASSWORD`          | Redis 비밀번호 (없으면 빈 문자열)      |

### 권장 보관 위치 (예시)

저장소 디렉터리 밖에 env 파일을 두고 사용한다. 예:

```
~/.config/nudge-signal/backend.local.env
~/.config/nudge-signal/backend.dev.env
```

해당 파일 내용 형식:

```
SPRING_PROFILES_ACTIVE=...
SERVER_PORT=...
DB_HOST=...
...
```

이 파일은 **절대 저장소 안으로 옮기지 않는다**.

## 시작하기 (로컬 개발)

### 1. env 파일 준비

위에 제시한 위치에 `backend.local.env` 작성.

### 2. 인프라 기동 (PostgreSQL + Redis)

```bash
docker compose --env-file ~/.config/nudge-signal/backend.local.env up -d postgres redis
```

### 3. 애플리케이션 실행

**IntelliJ IDEA (권장)**
1. `Run > Edit Configurations` 에서 `NudgeSignalBackendApplication` 선택
2. `Environment variables` 에 위 키들을 직접 입력하거나, [EnvFile 플러그인](https://plugins.jetbrains.com/plugin/7861-envfile) 으로 `~/.config/nudge-signal/backend.local.env` 지정
3. Run

**CLI**
```bash
set -a && source ~/.config/nudge-signal/backend.local.env && set +a
./gradlew bootRun
```

### 4. 헬스체크

```bash
curl http://localhost:$SERVER_PORT/api/ping
curl http://localhost:$SERVER_PORT/actuator/health
```

## 프로파일

| Profile | 용도              | DDL      |
|---------|-----------------|----------|
| local   | 로컬 개발 (Docker)  | update   |
| dev     | 개발 서버           | validate |
| prod    | 운영              | validate |

## Docker 로 앱 빌드 & 실행

```bash
docker build -t nudge-signal-backend:local .
docker run --rm -p 8080:8080 \
  --env-file ~/.config/nudge-signal/backend.local.env \
  nudge-signal-backend:local
```

## 빌드 & 테스트

```bash
./gradlew clean build      # 컴파일 + 테스트 + bootJar
./gradlew test             # 테스트만
./gradlew bootJar          # 실행 가능한 jar
```

## Codex 작업 문서

- `AGENTS.md`: Codex 가 우선 읽는 프로젝트 가이드
- `.codex/Rule.md`: 필수 규칙 (워크플로 / Git / 보안 / 컨벤션 / 코드 품질)
- `.codex/Skill.md`: 자주 쓰는 작업 절차
- `.codex/agents/*.toml`: PR 생성, 아키텍처 점검, 트러블슈팅 기록용 에이전트 정의
- `docs/TROUBLESHOOTING.md`: 에러·트레이드오프·성능 개선 이력

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
