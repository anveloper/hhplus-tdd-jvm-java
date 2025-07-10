## TDD로 개발하기

### 기본과제

- API 요구사항

  - [x] PATCH `/point/{id}/charge` : 포인트를 충전한다.
  - [x] PATCH `/point/{id}/use` : 포인트를 사용한다.
  - [x] GET `/point/{id}` : 포인트를 조회한다.

- 기능 요구사항

  - [x] GET `/point/{id}/histories/` : 포인트 내역을 조회한다.
  - [x] 잔고가 부족할 경우, 초인트 사용은 실패해야한다.

- 제약 조건 추가
  - [x] 포인트 충전은 양수만 가능하다.~~(비즈니스 로직)~~(입력 유효성)
  - [x] 포인트 충전 시 10만 포인트를 초과해서 충전할 수 없다.(비즈니스 로직) 

### 심화과제

  - 같은 사용자가 동시에 충전할 경우, 해당 요청 모두 정상적으로 반영되어야 합니다.

## 리뷰 포인트

이번 과제는 **테스트 작성을 숙달**하고, **TDD 개발 기법을 준수**하며 과제를 수행하였습니다.

1. 테스트 범위 선정

  - 단위 테스트 `Unit Test`: `UserPoint`의 비즈니스 로직 메서드 `PointService`에 대한 테스트를 대상으로 선정했습니다.
  - 통합 테스트 `Integration Test`: API를 호출하는 것으로 `Controller -> Service -> Record` 전체 흐름을 블랙박스 관점에서 검증하는 것으로 테스트를 작성했습니다.
  
2. 테스트 대역 사용

  - 테스트 작성 초반에는 Mockito를 사용하지 않고, Service와 Table을 직접 선언하였습니다.
    - [`PointServiceTest.java` 파일링크](https://github.com/anveloper/hhplus-tdd-jvm-java/blob/develop/src/test/java/io/hhplus/tdd/point/PointServiceTest.java)
  - Q&A 이후 실제 객체를 선언하는 것의 불편함(파라미터의 나열, 세팅)을 확인하고, 대역을 사용하여 테스트를 수정했습니다.
    - [`PointServiceTestWithMockito.java` 파일링크](https://github.com/anveloper/hhplus-tdd-jvm-java/blob/develop/src/test/java/io/hhplus/tdd/point/PointServiceTestWithMockito.java)

3. TDD로 개발

  - 호출할 빈 함수를 선언하는 것을 제외하고, TDD 개발 기법을 준수하기 위해 노력하였습니다.
    - PointService 작성, ~~PointServiceImpl 작성~~(삭제)
  - 요구사항 별 `PointService` 테스트 `RED -> GREEN -> REFACTOR` (커밋순)

    |순번| 요구사항 | RED | GREEN | REFACTOR |
    |---|----------|-----|--------|----------|
    |1-1|`포인트_충전_성공()`|[4b92656](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/4b92656e59a1e7232bde4c78414032d18500b40e)|[d674e9b](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/d674e9b8bfc6eddf4a37a4aa1b86476f452f97ed)|        |
    |2-1|`포인트_사용_성공()`|[4b92656](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/4b92656e59a1e7232bde4c78414032d18500b40e)|[2394a78](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/2394a786376d243047132750ef11b7ab7c20a826)|          |
    |2-2|`포인트_사용_실패()`|[4b92656](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/4b92656e59a1e7232bde4c78414032d18500b40e)|[2394a78](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/2394a786376d243047132750ef11b7ab7c20a826)|          |
    |3|`포인트_조회()`|[11e1402](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/11e14020165acfb5247db3a606297a3bca6fbee1)|[acf5b36](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/acf5b3629fe02e9c1c97e0caf150388a0a672d39)|          |
    |4|`포인트_히스토리_조회()`|[c1d6827](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/c1d6827b3ee85e9a930e09dcc4e35aaa560d2eb5)|[16a5e90](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/16a5e900a5b6574e93535183c1e5c47e87966a25)|          |
    |1-2|`포인트_충전_실패_0보다_작은_금액()`|[71d3641](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/71d3641ca3bf41da9008d1a1d3749235fd4f83bc)|[7ece964](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/7ece964c5c10b2b9cf9ebb4fecb0fda6bf48e0d3)|        |
    |1-3|`포인트_충전_실패_최대_제한_금액()`|[fa9c042](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/fa9c042ed08f2c79a078addb1e12c0b29968ea98)|[3ce3d9d](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/3ce3d9d935e22e86aa84af0bbd40140d1bb5ca40)|        |

  - 요구사항 별 `PointController` 테스트 (커밋순)
    - 주어진 `PointController`에 `return` 이 `new UserPoint(0, 0, 0)`이 고정되어있어, 충전 테스트를 먼저 작성하였으나 `isOk`가 통과가 되는 현상을 확인했습니다.
    - 충전 GREEN 코드 작성 이전에 조회 RED -> GREEN을 진행하였습니다. 
    - 조회 GREEN 수행 이후에 충전 RED에 종속된 결과 조회의 id는 조회되나 `Controller` 미구현으로 point는 0인 것을 확인 후 GREEN 진행하였습니다.

    |순번| 요구사항 | RED | GREEN | REFACTOR |
    |---|----------|-----|--------|----------|
    |1-1|`포인트_충전_정상_동작()`|[f9fb924](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/f9fb924b7cbcd241edc33e4837e007056a1484fb)|[658fd79](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/658fd79cf0e9f61d243ed58ef15eedd09303f77d)|      |
    |2|`포인트_조회_동작()`|[50488a9](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/50488a9d413053ec13c357d4dc003478c3be0121)|[0d3f66b](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/0d3f66b26beab166475c995a07327a1086f8c68b)|      |
    |1-2|`포인트_충전_실패_0보다_작은_금액()`|[d084247](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/d084247d8ce1718316aae447f2f1bbae6d46c640)|(즉시 통과)|      |
    |1-3|`포인트_충전_실패_최대_제한_금액()`|[d084247](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/d084247d8ce1718316aae447f2f1bbae6d46c640)|(즉시 통과)|      |
    |3-1|`포인트_사용_성공()`|[f0140eb](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/f0140ebbb4487e659927e71fdca670b7ea47fdd7)|[b1ecac4](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/b1ecac42eb7a9d569198de58c5e051717cc98b52)|      |
    |3-2|`포인트_사용_실패_포인트_부족()`|[e6cce35](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/e6cce35435e970d708033aaed747fc0159890667)|(즉시 통과)|      |
    |4|`포인트_히스토리_조회()`|[4973f8d](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/4973f8df9259e9dbb244112af83de04037e1657c)|[d112070](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/d112070d5880dca4e25f2770e6ffaff3a426ef02)|      |

    - 실패 케이스를 작성하였으나, 앞서 성공 케이스에서 바로 Service 코드로 연결되어 즉시 통과되었습니다.


4. 테스트 리팩토링

- 초반에는 REFACTOR를 단순 코드 정리 수준으로만 생각하고 커밋을 남겼습니다.
- 이후에는 테스트 코드 작성의 관점에서 반복되는 세팅 함수들의 재사용까지를 포함하기 위해 별도로 전체 REFACTOR를 수행하였습니다.

  |순번|파일명|대상|REFACTOR|
  |----|-----|----|-----------| 
  |1|`PointServiceTest.java`|`충전()`, `사용()`|[438ae4a](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/438ae4a8eef690e6893edb28c0d4b2065271411d)|
  |2|`PointServiceTestWithMockito.java`|`조회_응답_설정()`, `저장_응답_설정()`|[5d81377](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/5d8137719e91bee3cc735fdaf24258bafbe6a9ad)|
  |3|`PointControllerTest.java`|`충전()`, `사용()`|[46765eb](https://github.com/anveloper/hhplus-tdd-jvm-java/commit/46765eb85ea5d9769880e1c93e10fa2edba0bb81)|

- 주요 로직 코드와 검증 코드는 가독성을 위해 그대로 두어 크게 코드량이 줄지는 않았습니다.
- 다만, 추후 테스트 코드를 작성할 때, 더 유용하게 사용할 수 있을 것 같다고 느꼈습니다.

## 동시성

- 보고서는 평가에서 제외되었지만, 학습을 필요할 것 같아 정리하였습니다.

### 동시성 문제가 발생하는 원인

> 동시성 문제는 여러 스레드, 혹은 여러 요청이 공유자원(해당 프로젝트에선 `UserPoint`)에 동시에 접근할 때,
> 과정중에 읽고 > 연산하고 > 쓰는 과정이 분리된 상태에서 Lock 없이 진행될 때 발생합니다.

- 동시에 한 `userId`로 요청을 보내는 경우 읽는 과정에서 동일한 데이터를 확인하고, 연산 후 쓰는 과정에서 하나가 다른 하나를 덮어쓰는 갱신 손실을 발생시키는 문제가 있습니다.
- 다만, 현재 `JUnit` 테스트 환경에서는 단일 함수들이 단일 스레드에서 직렬적으로 실행되기 때문에 동시성 문제가 감춰질 수도 있습니다.

### 동시성 문제를 해결하기 위한 이론

> 동시성 문제가 발생하는 근본적인 원인이 공유 자원에 대한 비원자적인 접근에 의한 것이기 때문에,
> Lock 또는 원자적 연산으로 임계 구역을 보호해야 합니다.

- 임계 구역(`Critical Section`) 보호: 여러 프로세스에 의해 공유되는 데이터 또는 자원에 대하여 한 프로세스만 사용하도록 제한하는 방법을 말합니다.
- 상호 배제(`Mutual Exclusion`):  공유된 자원에 대하여 하나의 프로세스만 진입할 수 있도록 하는 것을 의미합니다.
- 상호 배제 3대 조건
  - 상호 배제(`Mutual Exclusion`): 하나의 프로세스만 임계 구역에 진입할 수 있어야 한다.
  - 진행(`Progress`): 임계 구역이 비어있는 경우, 대기중인 프로세스 중 하나가 언젠가 진입해야 한다.
  - 유한한 대기(`Bounded Waiting`): 한 프로세스가 무한히 대기하지 않도록 보장해야 한다.(기아상태 방지)

### 동시성 문제를 해결하기 위한 방법

<details><summary>1. `synchronized`, `ReentrantLock` (Java 동기화 키워드/클래스) << 현재 적용한 방법(`@Synchronized`)</summary>

- 장점
  - JVM 내부에서 가장 간단한 동기화 수단
  - 사용하기 쉽고 코드 수정만으로 적용 가능
- 단점
  - 단일 인스턴스 환경에서만 유효
  - 모든 요청 직렬 처리 → 성능 저하 가능
</details>

<details><summary>2. 데이터베이스 원자 연산</summary>

- 예: `UPDATE user_point SET point = point + ? WHERE id = ?`
- 장점
  - 조회 없이 한 줄로 처리 → 빠르고 안전
  - 정합성 보장 + 트랜잭션과 함께 사용 가능
- 단점
  - 복잡한 조건/비즈니스 로직 표현 어려움
</details>

<details><summary>3. 트랜잭션 + SELECT FOR UPDATE</summary>

- 장점
  - 정합성 철저히 보장
  - 복잡한 연산이나 조건을 트랜잭션 안에서 안전하게 처리 가능
- 단점
  - 락 경합 시 대기 시간 증가, 데드락 발생 가능성 존재
  - 트랜잭션 유지 비용 큼
</details>

<details><summary>4. 분산 락 (Redis, ZooKeeper 등)</summary>

- 장점
  - 다중 서버(WAS) 간에도 임계 구역 보호 가능
  - 마이크로서비스, 수평 확장 시스템에 적합
- 단점
  - 락 획득/해제 실패 시 정합성 무너질 수 있음
  - 네트워크 병목, 락 설정 오류로 시스템 불안정 가능
</details>

<details><summary>5. 메시지 큐 기반 직렬화 (Kafka, RabbitMQ 등)</summary>

- 장점
  - 모든 요청을 순서대로 처리 → 동시성 문제 원천 제거
  - 정합성 강력 보장
- 단점
  - 실시간 처리에는 부적합 (지연 발생)
  - 설계, 운영 복잡도 높음
</details>

<details><summary>6. 낙관적 락 (Optimistic Lock)</summary>

- 예: 버전 번호(version)를 기반으로 충돌 검출
- 장점
  - 락 없이 병렬 처리 가능 → 성능 우수
  - 충돌이 드문 경우 이상적
- 단점
  - 충돌 시 예외 발생 + 재시도 로직 필요
  - 충돌이 많으면 오히려 성능 저하
</details>

<details><summary>7. `CQRS` + 이벤트 소싱</summary>

- 읽기/쓰기 모델을 분리하고, 상태를 이벤트로만 저장
- 장점
  - 확장성 우수
  - 변경 이력 추적, 감사 로깅 가능
- 단점
  - 도입/설계 난이도 높음
  - 일관성 유지 복잡
</details>

## KPT 회고

### KEEP

- 금주부터 다시 시작한 항해99에 집중하는 습관을 들이는 것을 목표로 하였고, 잘 지킨 것 같습니다.
- 적어도 하루에 2시간은 프로젝트 코드를 계속 들여다 보고, 작성하며, 더 나은 방향을 찾기 위해 노력한 것 같습니다.

### PROBLEM

- 입사 이후 간만에 사용하는 Java와 처음 사용해보는 IntelliJ 환경이 약간의 어색함이 들었었지만,
- 과제의 집중도를 높히기 위한 예제코드와 왜 많이들 사용하는 지 알 것 같은 IDE의 편리함 덕분에 금새 극복할 수 있던 것 같습니다.
- 이번에 동시성에 대하여 다시 공부하면서 회사 코드에 빈틈이 얼마나 많을까 고민을 하게 되었습니다.
- 여태까지 문제가 없었던건 그냥 사용자가 많지 않아서 였다는 것을 다시한번 깨달았습니다.

### TRY

- 하루 2시간 코딩 습관
- 학습을 위한 구체적인 커밋 기록
- 커밋과 함께 코드에 주석 기록 열심히 하기
- 나중에 복습해도 커밋만으로 이해할 수 있도록 노력하기
 
