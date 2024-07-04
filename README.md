### 프로젝트 개요

`콘서트 예약 서비스`는 사용자들이 콘서트 좌석을 예약하고, 예약을 위한 잔액을 충전하며, 결제를 완료할 수 있는 시스템입니다. 이 서비스는 다수의 사용자가 동시에 접근할 수 있는 대기열 시스템을 포함하며, 동시성 이슈를 고려하여 설계됩니다.

### 주요 기능 및 제약사항

1. **유저 토큰 발급 API**: 사용자가 대기열에 들어가서 서비스를 이용할 수 있도록 토큰을 발급합니다.
2. **예약 가능 날짜 / 좌석 API**: 예약 가능한 날짜와 해당 날짜의 좌석 정보를 조회합니다.
3. **좌석 예약 요청 API**: 사용자가 좌석을 임시로 예약하고 결제가 이루어지지 않으면 임시 배정을 해제합니다.
4. **잔액 충전 / 조회 API**: 사용자가 예약에 사용할 금액을 충전하고, 현재 잔액을 조회합니다.
5. **결제 API**: 사용자가 좌석 예약 후 결제를 완료하면 좌석을 최종 배정합니다.

### 프로젝트 계획

### 1. 목표 설정

- 유저가 대기열을 통해 콘서트 좌석을 예약하고 결제까지 완료할 수 있는 시스템 구축.
- 동시성 이슈를 고려하여 다수의 사용자가 동시에 접근할 때도 안정적으로 서비스 제공.

### 2. 기술 스택

- **언어**: Java
- **프레임워크**: Spring Boot
- **데이터베이스**: MySQL (대기열 관리 및 데이터 저장)
- **메시지 브로커**: Kafka (동시성 처리)
- **테스트 프레임워크**: JUnit, Mockito

### 3. 아키텍처 설계

- **모듈 구성**:
    - **User Module**: 사용자 관리 및 인증
    - **Queue Module**: 대기열 관리
    - **Reservation Module**: 예약 처리
    - **Payment Module**: 결제 처리
- **데이터베이스 설계**:
    - **Users**: 유저 정보
    - **Seats**: 좌석 정보
    - **Reservations**: 예약 정보
    - **Payments**: 결제 내역
    - **Queue**: 대기열 정보

### 4. API 설계

**1️⃣ 유저 토큰 발급 API**

- **Endpoint**: `/api/v1/token`
- **Method**: POST
- **Request**: { "userId": "UUID" }
- **Response**: { "token": "string", "queuePosition": "int", "remainingTime": "int" }

**2️⃣ 예약 가능 날짜 / 좌석 API**

- **Endpoint**: `/api/v1/reservations/dates`
- **Method**: GET
- **Response**: [ "2024-07-03", "2024-07-04", ... ]
- **Endpoint**: `/api/v1/reservations/seats`
- **Method**: GET
- **Request**: { "date": "2024-07-03" }
- **Response**: [ { "seatNumber": 1, "available": true }, { "seatNumber": 2, "available": false }, ... ]

**3️⃣ 좌석 예약 요청 API**

- **Endpoint**: `/api/v1/reservations`
- **Method**: POST
- **Request**: { "date": "2024-07-03", "seatNumber": 1, "token": "string" }
- **Response**: { "reservationId": "UUID", "expiresAt": "timestamp" }

**4️⃣ 잔액 충전 / 조회 API**

- **Endpoint**: `/api/v1/balance`
- **Method**: POST
- **Request**: { "userId": "UUID", "amount": 100 }
- **Response**: { "newBalance": 200 }
- **Endpoint**: `/api/v1/balance`
- **Method**: GET
- **Request**: { "userId": "UUID" }
- **Response**: { "currentBalance": 200 }

**5️⃣ 결제 API**

- **Endpoint**: `/api/v1/payments`
- **Method**: POST
- **Request**: { "reservationId": "UUID", "amount": 100, "token": "string" }
- **Response**: { "paymentId": "UUID", "status": "success" }

## Milestone
```mermaid
gantt
    title Concert 프로젝트 마일스톤 및 상세 일정
    dateFormat  YYYY-MM-DD
    section 설계 단계
    요구사항 분석            :done,  des1, 2024-07-01, 1d
    시퀀스 다이어그램 설계    :done,  des2, 2024-07-02, 1d
    데이터베이스 설계        :done,  des3, 2024-07-03, 1d
    API 설계                :done,  des4, 2024-07-04, 1.5d
    시스템 아키텍쳐 설계     :done,  des5, 2024-07-05, 1.5d
    설계 리뷰 및 문서 작성   :done,  des6, 2024-07-06, 2d

    section 기능 구현 및 테스트 단계
    유저 토큰 발급 API 구현  :done,  dev1, 2024-07-07, 3d
    예약 가능 날짜/좌석 API 구현 :done, dev2, 2024-07-10, 2d
    좌석 예약 요청 API 구현  :done,  dev3, 2024-07-12, 2d
    잔액 충전/조회 API 구현  :done,  dev4, 2024-07-14, 2d
    결제 API 구현           :done,  dev5, 2024-07-16, 2d
    통합 테스트 및 시스템 테스트 :done, test1, 2024-07-18, 1d
    최종 검토 및 배포 준비   :done,  release1, 2024-07-19, 1d

```

## Sequence Diagram
```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Auth Service
    participant Reservation Service
    participant Payment Service
    participant DB
    participant Redis

    User->>API Gateway: 토큰 발급 요청
    API Gateway->>Auth Service: 유저 인증 및 토큰 발급 요청
    Auth Service->>DB: 유저 정보 및 대기열 위치 저장
    DB-->>Auth Service: 저장 완료
    Auth Service->>Redis: 토큰 저장 및 만료 시간 설정
    Redis-->>Auth Service: 저장 완료
    Auth Service-->>API Gateway: 토큰 및 대기열 정보 반환
    API Gateway-->>User: 토큰 및 대기열 정보 전달

    Note over Auth Service,DB: 실패 시
    DB-->>Auth Service: 저장 실패
    Auth Service-->>API Gateway: 토큰 발급 실패
    API Gateway-->>User: 토큰 발급 실패 메시지 전달

    User->>API Gateway: 예약 가능 날짜 조회 요청
    API Gateway->>Reservation Service: 예약 가능 날짜 조회
    Reservation Service->>DB: 예약 가능 날짜 정보 조회
    DB-->>Reservation Service: 예약 가능 날짜 정보 반환
    Reservation Service-->>API Gateway: 예약 가능 날짜 정보 반환
    API Gateway-->>User: 예약 가능 날짜 정보 전달

    Note over Reservation Service,DB: 실패 시
    DB-->>Reservation Service: 조회 실패
    Reservation Service-->>API Gateway: 예약 가능 날짜 조회 실패
    API Gateway-->>User: 예약 가능 날짜 조회 실패 메시지 전달

    User->>API Gateway: 예약 가능 좌석 조회 요청
    API Gateway->>Reservation Service: 예약 가능 좌석 조회
    Reservation Service->>DB: 예약 가능 좌석 정보 조회
    DB-->>Reservation Service: 예약 가능 좌석 정보 반환
    Reservation Service-->>API Gateway: 예약 가능 좌석 정보 반환
    API Gateway-->>User: 예약 가능 좌석 정보 전달

    Note over Reservation Service,DB: 실패 시
    DB-->>Reservation Service: 조회 실패
    Reservation Service-->>API Gateway: 예약 가능 좌석 조회 실패
    API Gateway-->>User: 예약 가능 좌석 조회 실패 메시지 전달

    User->>API Gateway: 좌석 예약 요청
    API Gateway->>Reservation Service: 좌석 예약 요청 처리
    Reservation Service->>DB: 좌석 예약 상태 업데이트 및 임시 배정 정보 저장
    DB-->>Reservation Service: 좌석 예약 정보 반환
    Reservation Service-->>API Gateway: 좌석 예약 정보 반환
    API Gateway-->>User: 좌석 예약 정보 전달

    Note over Reservation Service,DB: 실패 시
    DB-->>Reservation Service: 예약 실패
    Reservation Service-->>API Gateway: 좌석 예약 실패
    API Gateway-->>User: 좌석 예약 실패 메시지 전달

    User->>API Gateway: 잔액 충전 요청
    API Gateway->>Payment Service: 잔액 충전 요청 처리
    Payment Service->>DB: 유저 잔액 업데이트
    DB-->>Payment Service: 업데이트 완료
    Payment Service-->>API Gateway: 충전 결과 반환
    API Gateway-->>User: 충전 결과 전달

    Note over Payment Service,DB: 실패 시
    DB-->>Payment Service: 충전 실패
    Payment Service-->>API Gateway: 잔액 충전 실패
    API Gateway-->>User: 잔액 충전 실패 메시지 전달

    User->>API Gateway: 잔액 조회 요청
    API Gateway->>Payment Service: 잔액 조회 요청 처리
    Payment Service->>DB: 유저 잔액 조회
    DB-->>Payment Service: 잔액 정보 반환
    Payment Service-->>API Gateway: 잔액 정보 반환
    API Gateway-->>User: 잔액 정보 전달

    Note over Payment Service,DB: 실패 시
    DB-->>Payment Service: 조회 실패
    Payment Service-->>API Gateway: 잔액 조회 실패
    API Gateway-->>User: 잔액 조회 실패 메시지 전달

    User->>API Gateway: 결제 요청
    API Gateway->>Payment Service: 결제 요청 처리
    Payment Service->>DB: 결제 내역 저장 및 좌석 소유권 업데이트
    DB-->>Payment Service: 결제 및 업데이트 완료
    Payment Service-->>API Gateway: 결제 결과 반환
    API Gateway-->>User: 결제 결과 전달

    Note over Payment Service,DB: 실패 시
    DB-->>Payment Service: 결제 실패
    Payment Service-->>API Gateway: 결제 실패
    API Gateway-->>User: 결제 실패 메시지 전달

```

## er Diagram
```mermaid
erDiagram
    USERS {
        STRING id PK "NOT NULL"
        STRING username "NOT NULL"
        STRING password "NOT NULL"
        DECIMAL balance "NOT NULL"
        TIMESTAMP createdAt "NOT NULL"
    }

    QUEUE {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        STRING token "NOT NULL"
        INT queuePosition "NOT NULL"
        INT remainingTime "NOT NULL"
        TIMESTAMP createdAt "NOT NULL"
    }

    SEATS {
        BIGINT id PK "AUTO_INCREMENT"
        DATE date "NOT NULL"
        INT seatNumber "NOT NULL"
        BOOLEAN isReserved "NOT NULL"
        STRING reservedBy "NOT NULL"
        TIMESTAMP reservedUntil "NOT NULL"
    }

    RESERVATIONS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        DATE date "NOT NULL"
        INT seatNumber "NOT NULL"
        STRING reservationStatus "NOT NULL"
        TIMESTAMP reservedAt "NOT NULL"
    }

    PAYMENTS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        BIGINT reservationId "NOT NULL"
        DECIMAL amount "NOT NULL"
        STRING paymentStatus "NOT NULL"
        TIMESTAMP paidAt "NOT NULL"
    }
    
    TOKENS {
        VARCHAR(255) token PK "NOT NULL"
        VARCHAR(255) userId "NOT NULL"
        TIMESTAMP createdAt "NOT NULL"
        TIMESTAMP expiresAt "NOT NULL"
    }

    USERS ||--o{ QUEUE : "has"
    USERS ||--o{ RESERVATIONS : "makes"
    USERS ||--o{ PAYMENTS : "makes"
    USERS ||--o{ SEATS : "reserves"
    USERS ||--o{ TOKENS : "generates"

    QUEUE }o--|| USERS : "belongs to"
    TOKENS }o--|| USERS : "belongs to"
    SEATS }o--|| USERS : "is reserved by"
    RESERVATIONS }o--|| USERS : "belongs to"
    PAYMENTS }o--|| USERS : "belongs to"
    PAYMENTS }o--|| RESERVATIONS : "for"

```
