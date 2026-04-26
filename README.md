# bloomtoget 🌸

> 그룹과 함께 목표를 만들고, 매일 달성 여부를 체크하는 그룹형 Todo 서비스

---

## 목차

- [서비스 소개](#서비스-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [모듈 구조](#모듈-구조)
- [로컬 실행 방법](#로컬-실행-방법)
- [설계 결정](#설계-결정)
- [추가 예정 기능](#추가-예정-기능)

---

## 서비스 소개

bloomtoget은 혼자서는 지속하기 어려운 계획 및 목표 달성 과정을 **그룹의 힘으로 함께 달성**하는 서비스입니다.

그룹에 참여한 멤버들이 같은 목표를 공유하고, 매일 달성 여부를 체크하면서 서로의 진행 상황을 확인할 수 있습니다. 몇 명이 오늘 목표를 달성했는지 실시간으로 볼 수 있어 자연스러운 동기부여가 됩니다.

> 이 프로젝트는 [구 버전(Java 8 + Spring MVC + MyBatis)](https://github.com/JBGeum/bloomtoget)을 Java 21 + Spring Boot 3 + 헥사고날 아키텍처로 전면 재설계한 버전입니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| 그룹 탐색 및 가입 신청 | 공개 상태이며 최대 인원(6명) 미만인 그룹을 찾아 가입 신청 |
| 가입 승인 관리 | 그룹 관리자가 신청 목록을 확인하고 승인/거절 |
| 목표 생성 | 그룹 내 멤버가 목표를 만들고 다른 멤버에게 공개 |
| 목표 참여/중단 | 원하는 목표에 참여하거나 언제든 중단 가능 |
| 일일 달성 체크 | 매일 참여 중인 목표 리스트에서 달성 여부를 체크 |
| 달성 현황 확인 | 같은 목표 참여자 중 오늘 달성한 인원 수 확인 |
| 프로필 관리 | 회원 가입 후 프로필 사진 업로드 및 정보 수정 |

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Architecture | Hexagonal Architecture (Ports & Adapters) |
| Build | Gradle (Multi-module + Convention Plugins) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + QueryDSL |
| Test | JUnit 5 + Testcontainers |
| Migration | Flyway |
| Container | Docker / Docker Compose |
| CI | GitHub Actions |

---

## 아키텍처

3모듈 헥사고날 아키텍처를 적용했습니다. 비즈니스 로직이 외부 기술(DB, 웹 프레임워크)에 의존하지 않도록 경계를 명확하게 분리했습니다.

```
┌─────────────────────────────────────────────┐
│                   app                        │
│           (실행 진입점, 설정)                  │
└───────────────────┬─────────────────────────┘
                    │ depends on
┌───────────────────▼─────────────────────────┐
│              infrastructure                  │
│  ┌──────────────┐  ┌──────────────────────┐ │
│  │  web adapter │  │ persistence adapter  │ │
│  │ (Controller) │  │  (JPA Repository)    │ │
│  └──────┬───────┘  └──────────┬───────────┘ │
└─────────┼────────────────────┼──────────────┘
          │ implements          │ implements
┌─────────▼────────────────────▼──────────────┐
│                  domain                      │
│   ┌─────────────┐   ┌────────────────────┐  │
│   │  Use Cases  │   │  Domain Entities   │  │
│   │  (Ports)    │   │  (순수 비즈니스 로직) │  │
│   └─────────────┘   └────────────────────┘  │
└─────────────────────────────────────────────┘
```

**의존성 방향:** `infrastructure` → `domain` ← `app`
domain 모듈은 외부 기술에 대한 의존성이 없습니다.

---

## 모듈 구조

```
bloomtoget/
├── app/                    # 실행 진입점, Spring Boot Application, 전체 설정
├── domain/                 # 비즈니스 로직, Use Case, Port 인터페이스, 도메인 엔티티
├── infrastructure/         # 어댑터 구현체 (Web, Persistence, Security)
├── buildSrc/               # Convention Plugins (모듈 공통 Gradle 설정)
├── docker-compose.yml      # 로컬 개발 환경 (PostgreSQL)
└── settings.gradle
```

### 각 모듈의 책임

**domain** — 외부 의존성 없이 순수 Java로만 구성됩니다. 그룹·목표·달성 체크 등 비즈니스 규칙이 여기 있습니다. Use Case 인터페이스(Port)를 정의하며, infrastructure가 이를 구현합니다.

**infrastructure** — 웹 요청을 처리하는 Controller, JPA를 이용한 Persistence 어댑터, Spring Security 설정이 포함됩니다. domain의 Port를 구현합니다.

**app** — Spring Boot 실행 진입점과 전체 설정을 담당합니다. 모든 모듈을 조립하는 역할입니다.

---


## 설계 결정

### 헥사고날 아키텍처를 선택한 이유

구 버전(Spring MVC + MyBatis)은 Controller → Service → Mapper가 강하게 결합되어 있었습니다. DB 기술을 교체하거나 테스트를 작성할 때 전체 레이어를 함께 수정해야 했습니다.

헥사고날 아키텍처를 적용하면 domain이 외부 기술(JPA, PostgreSQL, HTTP)을 전혀 모릅니다. Use Case 테스트를 DB 없이 순수 Java로 작성할 수 있고, JPA를 다른 기술로 교체해도 domain 코드는 바뀌지 않습니다.

### MySQL → PostgreSQL로 전환한 이유

구 버전은 MySQL 8을 사용했습니다. PostgreSQL로 전환한 이유는 두 가지입니다. 첫째, JSONB와 배열 타입으로 달성 데이터를 유연하게 저장할 수 있습니다. 둘째, 향후 목표 검색 기능 추가 시 Full-Text Search를 네이티브로 지원합니다.

### Testcontainers를 도입한 이유

구 버전은 테스트가 없었습니다. 새 버전에서는 H2 인메모리 DB 대신 Testcontainers로 실제 PostgreSQL 16 컨테이너를 띄워 테스트합니다. H2는 PostgreSQL 방언을 완벽히 지원하지 않아 실제 환경과 차이가 생깁니다. Testcontainers를 쓰면 운영 환경과 동일한 DB로 테스트할 수 있습니다.

### Flyway를 도입한 이유

구 버전은 DDL을 수동으로 실행했습니다. 누가 언제 스키마를 변경했는지 추적이 어렵고, 팀 간 환경 불일치가 발생했습니다. Flyway를 도입하면 스키마 변경 이력이 버전 파일로 관리되고, `docker-compose up` 후 자동으로 스키마가 최신 상태로 맞춰집니다.

### Records를 도입한 이유

domain 모듈의 Command/Query 객체(입출력 데이터)를 Java 21 Record로 작성합니다. 불변성이 컴파일 타임에 보장되고, 보일러플레이트(getter, equals, hashCode, toString)가 제거됩니다. `public record CreateGroupCommand(String name, int maxMembers) {}` 한 줄로 DTO 클래스를 대체할 수 있습니다.

---

## ERD

> (ERD 이미지 추가 예정)

구 버전 ERD 참고: https://imgur.com/lcpGmjA

---

## 추가 예정 기능

- [ ] 캘린더 형식으로 달성률 확인 및 시각화
- [ ] 그룹 내 게시판 (타임라인)
- [ ] 목표 달성으로 그룹 경험치 획득 및 레벨업
- [ ] 회원 정보 수정 및 커스텀 설정

---

## 업데이트 내역

| 날짜         | 내용                                             |
|------------|------------------------------------------------|
| 2022.04.13 | 구 버전(Java 8 + Spring MVC) 최초 배포                |
| 2025.10.23 | Java 21 + Spring Boot 3 + 헥사고날 아키텍처로 전면 재설계 시작 |
| 2026.04.26 | README 업데이트                                 |
