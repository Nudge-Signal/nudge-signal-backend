# TROUBLESHOOTING

개발/운영 중에 만난 **에러(TROUBLE)** · **의식적 트레이드오프(TRADEOFF)** · **성능 개선(PERF)** 사례를 누적 기록. 이력서·포트폴리오 작성 시 근거 자료로 활용.

## 작성 형식

각 항목은 아래 형태로 추가한다. **하단에 추가 (시간 순 누적)**, 카테고리 태그는 대괄호 안에 정확히 하나만 (`TROUBLE` / `TRADEOFF` / `PERF`).

    ## YYYY-MM-DD · [TROUBLE | TRADEOFF | PERF] 제목
    - 상황: 어떤 맥락에서 무슨 일이 일어났나
    - 원인: 왜 그런가 (메커니즘까지)
    - 해결: 무엇을 어떻게 했나
    - 학습/메모: 다음에 떠올려야 할 것

## 기록 기준

**기록한다**
- 에러 메시지 + 디버깅 + 해결 과정 (TROUBLE)
- 옵션 비교 후 의식적으로 한쪽을 택한 결정 (TRADEOFF)
- 측정/비교로 입증된 개선 (PERF)

**기록하지 않는다**
- 단순 코드 작성, 리팩토링, 문서 갱신
- 정상 빌드 / 정상 테스트 통과
- PR 머지, CI 통과 등 운영 잡무

자동 기록 도우미: `.claude/agents/troubleshooting-recorder.md` (Task 로 호출).

---

<!-- 아래에 항목을 추가한다. 최신 항목이 아래로. -->

## 2026-05-01 · [TROUBLE] @WebMvcTest 컨텍스트 로드 실패 — 프로파일 / 포트 placeholder 미해결

- 상황: `HealthController` 슬라이스 테스트 (`@WebMvcTest(HealthController.class)`) 추가 후 `./gradlew test` 가 `Profile '${SPRING_PROFILES_ACTIVE}' must contain a letter, digit or allowed char` 로 실패.
- 원인: `application.yml` 이 보안 규칙 §3 에 따라 `spring.profiles.active: ${SPRING_PROFILES_ACTIVE}` / `server.port: ${SERVER_PORT}` 를 **fallback 없이** 참조한다. 일반 실행은 환경변수 / GitHub Actions secrets 로 채우지만, `@WebMvcTest` 는 외부 env 를 자동 주입하지 않으므로 placeholder 가 그대로 남아 `ProfilesValidator` 가 거부.
- 해결: 테스트 클래스에 `@TestPropertySource(properties = {"SPRING_PROFILES_ACTIVE=test", "SERVER_PORT=0"})` 추가. `NudgeSignalBackendApplicationTests` 가 이미 같은 패턴을 쓰고 있어 동일하게 맞춤.
- 학습/메모: 보안 규칙(§3) 으로 yml 디폴트 금지 → 슬라이스 테스트 추가 시 매번 env 주입 필요. 컨트롤러 / 서비스 슬라이스가 늘어나면 공통 기반 클래스 또는 `@TestPropertySource` 를 모은 메타애노테이션 도입 검토. 통합 테스트(`@SpringBootTest`) 도 동일 문제 — 같은 기준으로 처리.

