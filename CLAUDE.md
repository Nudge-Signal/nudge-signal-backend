# CLAUDE.md

이 파일은 이 저장소에서 작업하는 Claude (또는 다른 AI 코딩 도우미) 가 따라야 할 가이드입니다.

## 프로젝트 개요

**Nudge-Signal** 소개팅 앱의 백엔드 서비스.

- 언어/런타임: Java 17
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

## 설정 / 환경변수

- 모든 DB/Redis 접속 정보는 환경변수로 주입 (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `SPRING_PROFILES_ACTIVE`, `SERVER_PORT`).
- **저장소 안에는 어떠한 환경변수 값도 두지 않는다.** `.env`, `.env.example`, `application-*.yml` 의 fallback 디폴트, `docker-compose.yml` 의 `${VAR:-default}` 등 모든 형태 금지. `${VAR}` 만 사용.
- env 파일은 저장소 **밖**에 보관 (예: `~/.config/nudge-signal/backend.local.env`). README 의 "환경변수" 섹션 참조.
- 새 환경변수를 추가할 때는 README 의 환경변수 표에 키 이름만 추가 (값/예시 절대 금지).

## 코딩 컨벤션

- 패키지 네임스페이스는 `com.nudgesignal.backend.*`.
- Lombok 사용 OK (`@Getter`, `@RequiredArgsConstructor`, `@Slf4j`). `@Data` / `@Setter` 는 엔티티에 쓰지 말 것 (불변성 깨짐).
- JPA 엔티티의 기본 생성자는 `protected`, 필드 직접 노출 금지.
- 컨트롤러는 DTO 반환. 엔티티 직접 노출 금지.
- 트랜잭션 경계는 서비스 레이어 (`@Transactional`).
- `open-in-view: false` — 컨트롤러/뷰에서 lazy loading 시도 금지.

## 테스트

- 단위/슬라이스 테스트 우선 (`@WebMvcTest`, `@DataJpaTest`).
- 풀 컨텍스트 통합 테스트는 인프라 의존을 명시적으로 다룬다 (Testcontainers 도입 시 별도 모듈로).
- 현재 `NudgeSignalBackendApplicationTests` 는 인프라 없이 떠야 한다 — DB/Redis auto-config 를 `@EnableAutoConfiguration(exclude = ...)` 로 제외해 둔 상태.

## Git / PR 규칙 (중요)

- **커밋 메시지와 PR 본문에 AI 도구 흔적을 절대 남기지 않는다.**
  - `Co-Authored-By: Claude ...` 트레일러 금지
  - "Generated with Claude Code" 류의 푸터 금지
  - 🤖 등 AI 마커 이모지 금지
- 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/) 형식 권장: `feat:`, `fix:`, `chore:`, `refactor:`, `docs:`, `test:`.
- 브랜치 명: `feat/<scope>`, `fix/<scope>`, `chore/<scope>`.
- PR 은 한국어로 작성. Summary + Test plan 섹션 유지.
- 작업 단위로 브랜치를 따고, 머지 전 `./gradlew clean build` 그린 확인.

## 작업 시 주의

- 백워드 호환을 위한 사용하지 않는 코드 / 주석 / 플래그를 남기지 않는다. 필요 없으면 지운다.
- 추측성 추상화 금지. 같은 패턴이 3회 이상 반복될 때 추출.
- 불필요한 주석 금지. "왜" 가 비자명할 때만 한 줄로 남긴다.
