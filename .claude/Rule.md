# Rule

이 프로젝트에서 작업할 때 **반드시 따라야 하는 규칙**. 위반은 코드 리뷰에서 막힌다.

## 1. 워크플로

- **(2인 팀) 작업 시작 전 main 동기화 체크.** 새 브랜치를 따거나 진행 중인 브랜치에서 다시 작업할 때, 먼저 `git fetch origin && git log --oneline HEAD..origin/main` 으로 따라잡아야 할 커밋이 있는지 확인. 있으면 rebase 한 뒤 시작.
- **플랜 모드 진입 필수.** 작업 시작 시 `EnterPlanMode` 툴을 호출해 계획을 세우고, `ExitPlanMode` 로 사용자 승인을 받은 뒤 구현. **텍스트로만 계획을 나열하는 것은 플랜 모드가 아니다 — 반드시 툴을 호출.** (사소한 한 줄 수정 등 위험이 거의 없는 변경은 예외.)
- **feature 브랜치에서 작업.** 브랜치 명: `feat/<scope>`, `fix/<scope>`, `chore/<scope>`. **main 에 직접 커밋·푸시 금지.** 변경은 항상 PR.
- **머지 전 `./gradlew clean build` 그린 확인.**
- **작업 완료 시 문서 동기화.** 변경이 영향을 주는 문서(`README.md`, `CLAUDE.md`, `docs/TROUBLESHOOTING.md` 등)를 같은 PR 에 함께 업데이트.
- **에러·트레이드오프·성능 개선은 즉시 `docs/TROUBLESHOOTING.md` 에 기록.** 형식은 `Skill.md` 의 "트러블슈팅 기록" 섹션 참조.

## 2. Git

- **커밋 메시지 / PR 본문 / 이슈에 Claude·Codex·AI 도구 흔적을 절대 남기지 않는다.**
  - `Co-Authored-By: Claude ...` 트레일러 금지
  - "Generated with Claude Code" 류 푸터 금지
  - 🤖 등 AI 마커 이모지 금지
- 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`, `ci:`.
- 한 커밋에 여러 의도를 섞지 않는다 (리팩토링 + 기능 추가 혼합 금지).
- PR 본문은 한국어. **개요 / 변경 사항 / 테스트 / 관련 이슈** 섹션 유지. 자세한 템플릿은 `.claude/agents/create-pr.md` 참고.

## 3. 보안 / 환경변수

- **저장소 안에 어떠한 환경변수 값도 두지 않는다.** 다음은 모두 금지:
  - `.env`, `.env.example` 등 어떤 형태의 env 파일도 커밋 금지
  - `application-*.yml` 의 fallback 디폴트
  - `docker-compose.yml` 의 `${VAR:-default}` 형태
  - placeholder 값(`changeme`, `your-password-here` 등) 도 금지
- `${VAR}` 형태로 **참조만** 한다. 값은 저장소 밖에 보관 (예: `~/.config/nudge-signal/backend.local.env`).
- 운영 환경은 GitHub Actions secrets 로 주입.
- 민감한 값(비밀번호 / 시크릿 / OAuth 크리덴셜) 은 문서·커밋 메시지·이슈·PR 본문 등 git 기록 어디에도 노출하지 않는다.
- 새 환경변수 추가 시 README 의 환경변수 표에 **키 이름만** 추가 (값/예시 절대 금지).

## 4. 코딩 컨벤션

- 패키지: `com.nudgesignal.backend.*`. 레이어: `api` (controller) / `service` / `domain` (entity, repository) / `infra` (외부 연동) / `config`.
- Lombok 사용 OK (`@Getter`, `@RequiredArgsConstructor`, `@Slf4j`). **`@Data` / `@Setter` 는 엔티티에 쓰지 말 것** (불변성 파괴).
- JPA 엔티티 기본 생성자는 `protected`. 필드 직접 노출 금지.
- 컨트롤러는 DTO 반환. 엔티티 직접 노출 금지.
- 트랜잭션 경계는 서비스 레이어 (`@Transactional`).
- `open-in-view: false`. 컨트롤러/뷰에서 lazy loading 금지.

## 5. 코드 품질

- **테스트 가능한 설계**: 생성자 주입 (DI) 우선. 인터페이스 기반 협력. `static`/싱글톤 직접 호출 지양.
- **기능 추가 시 테스트 코드 동반 작성** — 같은 PR 에 들어간다.
- 외부 의존(DB / Redis / 외부 API) 은 명시적 경계(`infra/`) 뒤로 격리.
- 안정성 우선: 입력 검증, 트랜잭션 경계, 에러 처리는 해피 패스보다 먼저 잡는다.
- 단위/슬라이스 테스트 우선 (`@WebMvcTest`, `@DataJpaTest`).
- 풀 컨텍스트 통합 테스트는 인프라 의존을 명시적으로 다룬다 (Testcontainers 도입 시 별도 모듈로).
- `NudgeSignalBackendApplicationTests` 는 인프라 없이 떠야 한다 (현재 DB/Redis auto-config 제외 처리).

## 6. 작업 시 주의

- 백워드 호환을 위한 사용하지 않는 코드 / 주석 / 플래그를 남기지 않는다. 필요 없으면 지운다.
- 추측성 추상화 금지. 같은 패턴이 3회 이상 반복될 때 추출.
- 불필요한 주석 금지. "왜" 가 비자명할 때만 한 줄로 남긴다.
