# CLAUDE.md

이 파일은 이 저장소에서 작업하는 Claude (또는 다른 AI 코딩 도우미) 가 따라야 할 가이드입니다. 행동 규칙과 절차는 분리해서 관리합니다 — 이 파일은 **프로젝트 사실** (스택 / 구조 / 명령) 만 둡니다.

- @.claude/Rule.md — 반드시 따라야 하는 규칙 (워크플로 / Git / 보안 / 컨벤션 / 코드 품질)
- @.claude/Skill.md — 자주 쓰는 절차·루틴 (작업 시작 / 검증 / 마무리 / 트러블슈팅 기록 / 환경변수 추가)

## 프로젝트 개요

**Nudge-Signal** 소개팅 앱의 백엔드 서비스. 2인 팀.

## 작업 원칙

- 어떠한 작업이든 시작 전 반드시 Plan Mode 로 진입해 계획을 세운 뒤 진행한다. 작업 시작 시 `EnterPlanMode` 툴을 호출해 계획을 세우고, `ExitPlanMode` 로 사용자 승인을 받은 뒤 구현한다. 단순 문서 수정, 한 줄 변경, 질문성 조사도 예외 없이 Plan Mode 를 먼저 사용한다.

- 언어/런타임: Java 21
- 프레임워크: Spring Boot 3.5.x (Web / Data JPA / Validation / Actuator)
- 저장소: PostgreSQL 16, Redis 7
- 빌드: Gradle (wrapper 포함)
- 컨테이너: Docker / docker-compose
- IDE: IntelliJ IDEA

## 디렉터리 구조

```
src/main/java/com/nudgesignal/backend
├── NudgeSignalBackendApplication.java   # 엔트리포인트
└── api/                                 # REST 컨트롤러
src/main/resources
├── application.yml                      # 공통 설정
├── application-local.yml                # 로컬(Docker) 프로파일
├── application-dev.yml                  # 개발 서버 프로파일
└── application-prod.yml                 # 운영 프로파일
docs/
└── TROUBLESHOOTING.md                   # 에러/트레이드오프/성능 이력
```

신규 패키지 추가 시 레이어 구분을 따른다: `api` (controller) / `service` / `domain` (entity, repository) / `infra` (외부 연동) / `config`.

## 자주 쓰는 명령

```bash
./gradlew clean build       # 컴파일 + 테스트 + bootJar
./gradlew test              # 테스트만
./gradlew bootRun           # 로컬 기동
docker compose up -d        # Postgres + Redis 기동
docker compose down         # 인프라 종료
```

## 환경변수

- 모든 외부 의존 접속 정보는 환경변수로 주입: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `SPRING_PROFILES_ACTIVE`, `SERVER_PORT`.
- 로컬 env 파일은 저장소 **밖**에 둔다 (예: `~/.config/nudge-signal/backend.local.env`).
- 운영은 GitHub Actions secrets 로 주입.
- 그 외 자세한 키 / 보안 / fallback 금지 규칙은 `.claude/Rule.md` 의 **보안** 섹션 참조.
