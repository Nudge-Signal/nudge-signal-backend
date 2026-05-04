# Rule

이 프로젝트에서 Codex 가 작업할 때 **반드시 따라야 하는 규칙**. 위반은 코드 리뷰에서 막힌다.

## 1. 워크플로

- **(2인 팀) 작업 시작 전 main 동기화 체크.** 새 브랜치를 따거나 진행 중인 브랜치에서 다시 작업할 때, 먼저 `git fetch origin` 후 `git log --oneline HEAD..origin/main` 으로 따라잡아야 할 커밋이 있는지 확인. 있으면 rebase 한 뒤 시작.
- **Plan Mode 필수.** 어떠한 작업이든 시작 전 반드시 Codex Plan Mode 로 진입해 계획을 세운 뒤 진행한다. 단순 문서 수정, 한 줄 변경, 질문성 조사도 예외 없이 Plan Mode 를 먼저 사용한다.
- **feature 브랜치에서 작업.** 브랜치 명: `feat/<scope>`, `fix/<scope>`, `chore/<scope>`. **main 에 직접 커밋·푸시 금지.** 변경은 항상 PR.
- **머지 전 `./gradlew clean build` 그린 확인.**
- **작업 완료 시 문서 동기화.** 변경이 영향을 주는 문서(`README.md`, `AGENTS.md`, `.codex/Rule.md`, `.codex/Skill.md`, `docs/TROUBLESHOOTING.md` 등)를 같은 PR 에 함께 업데이트.
- **리뷰 요청 시 `.codex/Review.md` 기준을 따른다.** findings 우선, 심각도 순 정렬, 파일/라인 근거, 테스트 공백 명시가 기본이다.
- **에러·트레이드오프·성능 개선은 즉시 `docs/TROUBLESHOOTING.md` 에 기록.** 형식은 `.codex/Skill.md` 의 "트러블슈팅 기록" 섹션 참조.

## 2. Git

- **커밋 메시지 / PR 본문 / 이슈에 Claude·Codex·AI 도구 흔적을 절대 남기지 않는다.**
  - `Co-Authored-By: Claude ...` 또는 `Co-Authored-By: Codex ...` 트레일러 금지
  - "Generated with ..." 류 푸터 금지
  - AI 마커 이모지 금지
- 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`, `ci:`.
- 한 커밋에 여러 의도를 섞지 않는다 (리팩토링 + 기능 추가 혼합 금지).
- PR 본문은 한국어. **개요 / 변경 사항 / 테스트 / 관련 이슈** 섹션 유지. 자세한 템플릿은 `.codex/agents/create-pr.toml` 참고.

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

### 4.1 패키지·레이어

- 패키지: `com.nudgesignal.backend.*`. 레이어: `api` (controller) / `service` / `domain` (entity, repository) / `infra` (외부 연동) / `config`.
- Lombok 사용 OK (`@Getter`, `@RequiredArgsConstructor`, `@Slf4j`). **`@Data` / `@Setter` 는 엔티티에 쓰지 말 것** (불변성 파괴).
- JPA 엔티티 기본 생성자는 `protected`. 필드 직접 노출 금지.
- 컨트롤러는 DTO 반환. 엔티티 직접 노출 금지.
- 트랜잭션 경계는 서비스 레이어 (`@Transactional`). 조회 전용 메서드는 `@Transactional(readOnly = true)` 명시.
- `open-in-view: false`. 컨트롤러/뷰에서 lazy loading 금지.

### 4.2 객체 생성과 소멸

- 의미 있는 이름이 있을 때 정적 팩토리 메서드 우선 (`User.of(...)`, `EmailAddress.from(...)`, `UserResponse.from(user)`). public 생성자는 의도가 단순 매핑일 때만.
- 매개변수가 4개 이상이거나 선택값이 섞이면 빌더(`@Builder`) 또는 별도 객체로 묶는다. 텔레스코핑 생성자 금지.
- 싱글톤이 필요하면 enum 또는 private 생성자 + 정적 인스턴스. 스프링 빈은 기본이 싱글톤이므로 별도 패턴 불필요.
- 유틸 클래스(정적 메서드만 가진 클래스) 는 private 생성자로 인스턴스화 차단.
- 불필요한 객체 생성 금지: `new String("...")`, 박싱된 정수 캐싱 우회, 반복문 안 정규식 컴파일 등.
- 자원 해제는 `try-with-resources`. `try-finally` 로 close 호출 금지.
- `finalize()` / `Cleaner` 사용 금지. 자원은 명시적으로 close 하거나 `AutoCloseable` 로 감싼다.

### 4.3 모든 객체의 공통 메서드

- `equals` 를 재정의하면 `hashCode` 도 함께 재정의. JPA 엔티티는 **식별자 기반**으로 (자동 생성 ID 가 채워진 후에만 의미). Lombok `@EqualsAndHashCode(of = "id")` 명시적 지정.
- `toString` 은 디버깅·로그용으로 가급적 재정의. 단, 비밀번호·토큰·이메일 등 민감 정보 / 양방향 연관관계 필드는 제외.
- `Comparable` 은 자연 순서가 명확할 때만 구현. 그 외는 `Comparator` 를 별도 제공.
- `clone()` 사용 금지. 복사가 필요하면 복사 생성자 또는 정적 팩토리.

### 4.4 클래스와 인터페이스

- 접근 수준 최소화. 기본은 `private`, 필요한 만큼만 열어준다 (`package-private` -> `protected` -> `public`).
- 가변성 최소화. 도메인 객체·값 객체·DTO 는 불변 선호 (`final` 필드, setter 없음). 상태 변경은 도메인 메서드로 (`user.changeNickname(...)`).
- 상속보다 컴포지션. 프레임워크가 강제하지 않는 한 `extends` 대신 협력 객체 주입.
- 상속용으로 설계되지 않은 클래스는 `final` 또는 package-private. 상속을 의도했다면 protected 훅 메서드와 자기 호출 규약을 문서화.
- 의존성은 구현 클래스가 아니라 인터페이스 타입으로 받는다 (`List`, `Map`, 도메인 추상화 등).
- public 클래스의 가변 필드는 노출 금지. 접근자(getter) 사용.
- 한 파일에는 한 개의 top-level 클래스만.

### 4.5 제네릭

- raw 타입 금지 (`List` 대신 `List<User>`).
- 비검사 경고는 가능한 한 제거. 남겨야 한다면 가장 좁은 범위에 `@SuppressWarnings("unchecked")` + 사유 주석.
- 배열보다 리스트. 제네릭 배열 생성 금지.
- API 유연성을 위해 한정 와일드카드 활용 (PECS: producer `extends`, consumer `super`).

### 4.6 열거 타입과 애노테이션

- 정수·문자열 상수 묶음 대신 `enum`. (예: 매칭 상태, 알림 종류)
- `ordinal()` 에 의존 금지. 순서가 의미 있다면 enum 필드로 명시.
- 비트 필드 / 정수 키 맵 대신 `EnumSet` / `EnumMap`.
- 명명 패턴 / 문자열 키 대신 애노테이션으로 메타데이터 표현.

### 4.7 람다와 스트림

- 익명 내부 클래스 대신 람다. 람다는 3줄 이내로 짧게. 길어지면 메서드로 추출.
- 표준 함수형 인터페이스 우선 (`Function`, `Predicate`, `Consumer`, `Supplier`, `BiFunction`, ...). 같은 시그니처를 새로 정의하지 않는다.
- 메서드 참조가 더 읽기 좋다면 람다 대신 사용 (`users.stream().map(User::getId)`).
- 스트림은 가독성에 도움될 때만. 복잡한 분기 / 누적 / side effect 가 끼면 for 문이 낫다.
- 스트림 파이프라인 안에서 외부 상태 변경 / I/O 등 부수효과 금지. `forEach` 는 결과 수집이 아니라 종료 액션 용도.

### 4.8 메서드

- 매개변수 유효성을 메서드 진입 직후 검증. 컨트롤러 진입은 Bean Validation (`@Valid`, `@NotNull`, ...), 도메인 / 서비스는 `Objects.requireNonNull`, `Assert.isTrue` 등 명시적 가드.
- 가변 객체를 외부로부터 받거나 외부로 내보낼 때는 방어적 복사 또는 불변 뷰 (`List.copyOf`, `Collections.unmodifiableList`).
- `null` 대신 빈 컬렉션 / `Optional` 반환. 컬렉션 반환 메서드는 절대 `null` 을 돌려주지 않는다.
- `Optional` 은 **반환 타입 전용**. 필드 / 매개변수 / 컬렉션 요소로 사용 금지.
- 매개변수가 4개를 넘기면 객체(파라미터 객체 / Command / Query) 로 묶는다.
- boolean 매개변수 연속 금지 (호출부에서 의미가 사라진다). enum / 객체로 표현.

### 4.9 일반 프로그래밍

- 지역 변수의 범위는 최초 사용 직전까지 좁힌다. 선언과 초기화는 함께.
- 인덱스가 필요 없다면 for-each 우선.
- 표준 라이브러리 우선. 직접 구현 전에 JDK / Spring / Apache Commons 에 같은 기능이 있는지 확인.
- 정확한 계산은 `BigDecimal` 또는 정수형. 금액 / 점수 / 매칭 비율 등에 `float` / `double` 금지.
- 박싱 타입(`Integer`, `Long`, ...) 보다 기본형. 컬렉션 / 제네릭이 아니면 박싱하지 않는다. `==` 로 박싱 비교 금지.
- 문자열로 만물을 표현하지 않는다. 상태 / 종류 / 식별자는 enum 또는 도메인 VO.
- 반복적인 문자열 연결은 `StringBuilder` / `String.join` / `Stream#collect`.
- 객체는 가능한 한 인터페이스 타입으로 참조 (`List<User> users = new ArrayList<>();`).

### 4.10 예외

- 예외는 예외 상황에만. 정상 흐름 / 분기 제어에 throw-catch 사용 금지.
- 도메인 예외 / 비즈니스 예외는 `RuntimeException` 계열로 통일 (checked 예외로 호출자에게 강제 부담 주지 않는다).
- 표준 예외 우선 (`IllegalArgumentException`, `IllegalStateException`, `NoSuchElementException`). 같은 의미를 새로 만들지 않는다.
- 추상화 수준에 맞는 예외를 던진다. 인프라 예외(JDBC / IO) 를 컨트롤러 응답으로 그대로 흘리지 않고, 서비스 / `@RestControllerAdvice` 에서 도메인·응답 예외로 변환.
- 던지는 예외는 javadoc 또는 메서드 시그니처에 드러낸다.
- 예외를 무시하지 않는다. 빈 `catch` 금지. 의도적으로 무시한다면 사유를 한 줄 주석 + 변수명 `ignored`.

### 4.11 동시성

- 공유 가변 상태는 동기화하거나 처음부터 불변으로 만든다. 가능한 한 후자.
- `Thread` 직접 생성 금지. `Executor` / `TaskExecutor` / Spring `@Async` 사용.
- 저수준 `wait` / `notify` 대신 `CompletableFuture`, `BlockingQueue`, `ConcurrentHashMap` 등 동시성 유틸.
- 락 범위 최소화. 락 안에서 외부 메서드 / 알 수 없는 콜백 호출 금지 (데드락 / 라이브락 위험).
- 시간 / 만료가 의미 있는 곳에는 `Instant` / `Duration` 사용. `System.currentTimeMillis()` 직접 사용 자제.

## 5. 코드 품질

- **테스트 가능한 설계**: 생성자 주입 (DI) 우선. 인터페이스 기반 협력. `static`/싱글톤 직접 호출 지양.
- **기능 추가 시 테스트 코드 동반 작성**. 같은 PR 에 들어간다.
- 외부 의존(DB / Redis / 외부 API) 은 명시적 경계(`infra/`) 뒤로 격리.
- 안정성 우선: 입력 검증, 트랜잭션 경계, 에러 처리는 해피 패스보다 먼저 잡는다.
- 단위/슬라이스 테스트 우선 (`@WebMvcTest`, `@DataJpaTest`).
- 풀 컨텍스트 통합 테스트는 인프라 의존을 명시적으로 다룬다 (Testcontainers 도입 시 별도 모듈로).
- `NudgeSignalBackendApplicationTests` 는 인프라 없이 떠야 한다 (현재 DB/Redis auto-config 제외 처리).

## 6. 작업 시 주의

- 백워드 호환을 위한 사용하지 않는 코드 / 주석 / 플래그를 남기지 않는다. 필요 없으면 지운다.
- 추측성 추상화 금지. 같은 패턴이 3회 이상 반복될 때 추출.
- 불필요한 주석 금지. "왜" 가 비자명할 때만 한 줄로 남긴다.
