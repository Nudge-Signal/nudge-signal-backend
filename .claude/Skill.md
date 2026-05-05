# Skill

자주 쓰는 절차와 도구 사용법. `Rule.md` 가 "무엇을 해야 한다" 라면 이 파일은 "어떻게 한다" 다.

## 작업 시작 루틴

1. **main 동기화 체크** (2인 팀 필수)
   ```bash
   git fetch origin
   git log --oneline HEAD..origin/main
   ```
   결과가 비어 있지 않으면:
   - 새 작업: `git switch main && git pull --ff-only` 후 새 브랜치 분기
   - 진행 중 브랜치: `git rebase origin/main`
2. **플랜 모드 진입**: 어떠한 작업이든 시작 전 반드시 Plan Mode 로 계획을 세운다. 작업 시작 시 `EnterPlanMode` 툴을 호출해 계획을 세우고, `ExitPlanMode` 로 사용자 승인을 받은 뒤 구현한다. 단순 문서 수정, 한 줄 변경, 질문성 조사도 예외 없이 Plan Mode 를 먼저 사용한다.
3. feature 브랜치에서 구현 시작 (`feat/<scope>` 등).

## 검증 루틴

```bash
./gradlew clean build   # 컴파일 + 테스트 + bootJar 까지 그린이어야 함
```

부분 검증:
```bash
./gradlew compileJava   # 컴파일만
./gradlew test          # 테스트만
```

## 작업 마무리 루틴 (커밋 → 푸시 → PR)

1. 영향받는 문서를 같은 변경 안에 갱신 (`README.md` / `CLAUDE.md` / `docs/TROUBLESHOOTING.md` 등).
2. `./gradlew clean build` 그린 확인.
3. Conventional Commits 형식으로 커밋. **AI 마커 / `Co-Authored-By` 절대 금지.**
4. push 후 PR 생성. 본문은 한국어, 형식은 `.claude/agents/create-pr.md` 의 템플릿 참고.
5. 직접 호출이 필요하면 `Task` 로 `create-pr` 에이전트 사용.

## 트러블슈팅 기록

에러·의식적 트레이드오프·성능 개선을 만나면 즉시 `docs/TROUBLESHOOTING.md` **하단**에 항목 추가:

    ## YYYY-MM-DD · [TROUBLE | TRADEOFF | PERF] 제목
    - 상황: 어떤 맥락에서 무슨 일이 일어났나
    - 원인: 왜 그런가 (메커니즘까지)
    - 해결: 무엇을 어떻게 했나
    - 학습/메모: 다음에 떠올려야 할 것

자동 기록 도우미: `.claude/agents/troubleshooting-recorder.md` (Task 로 호출). 보수적 판단 — 단순 코드 작성·정상 빌드는 기록 대상 아님.

## 환경변수 추가 시

1. 로컬 env 파일(`~/.config/nudge-signal/backend.local.env`) 에 키=값 추가.
2. `application-*.yml` 에는 `${VAR}` 만 사용 (디폴트 / fallback 금지).
3. `docker-compose.yml` 에서 컨테이너에 필요하면 `${VAR}` 만 참조.
4. README 의 환경변수 표에 **키 이름만** 추가 (값 / 예시 / placeholder 모두 금지).
5. 운영용은 GitHub Actions secrets 에도 등록 (PR 메모로 알린다).

## 자주 쓰는 슬래시 / 스킬

- `/fewer-permission-prompts` — 트랜스크립트 분석해 안전한 명령을 자동 허용 목록에 추가.
- `/review`, `/security-review` — PR 리뷰 보조.
- `Task` 로 `create-pr` 에이전트 — PR 생성 보조.
- `Task` 로 `troubleshooting-recorder` 에이전트 — 세션 종료 시 트러블슈팅 자동 기록.
- `Task` 로 `architecture-alignment` 에이전트 — 신규 코드 / 리팩토링이 레이어 / DDD 원칙에 맞는지 점검.
