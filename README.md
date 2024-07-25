# 콘서트 예매 서버 구축

## 프로젝트 개요

`콘서트 예약 서비스`는 사용자들이 콘서트 좌석을 예약하고, 예약을 위한 잔액을 충전하며, 결제를 완료할 수 있는 시스템입니다. 이 서비스는 다수의 사용자가 동시에 접근할 수 있는 대기열 시스템을 포함하며, 동시성 이슈를 고려하여 설계됩니다.

## 주요 기능 및 제약사항

1. **유저 토큰 발급 API**: 사용자가 대기열에 들어가서 서비스를 이용할 수 있도록 토큰을 발급합니다.
2. **예약 가능 날짜 / 좌석 API**: 예약 가능한 날짜와 해당 날짜의 좌석 정보를 조회합니다.
3. **좌석 예약 요청 API**: 사용자가 좌석을 임시로 예약하고 결제가 이루어지지 않으면 임시 배정을 해제합니다.
4. **잔액 충전 / 조회 API**: 사용자가 예약에 사용할 금액을 충전하고, 현재 잔액을 조회합니다.
5. **결제 API**: 사용자가 좌석 예약 후 결제를 완료하면 좌석을 최종 배정합니다.
6. **대기열 확인 API**: 사용자가 현재 대기열에서 자신의 위치를 확인할 수 있습니다.
7. **대기열 추가 API:** 사용자가 현재 대기열에 자신을 추가할 수 있습니다.

## 1. 목표 설정

- 유저가 대기열을 통해 콘서트 좌석을 예약하고 결제까지 완료할 수 있는 시스템 구축.
- 동시성 이슈를 고려하여 다수의 사용자가 동시에 접근할 때도 안정적으로 서비스 제공.

## 2. 기술 스택

- **언어**: Java
- **프레임워크**: Spring Boot
- **데이터베이스**: MySQL (대기열 관리 및 데이터 저장)
- **테스트 프레임워크**: JUnit, Mockito
- **인증**: JWT

## 3. 아키텍처 설계

- **모듈 구성**:
    - **User Module**: 사용자 관리 및 인증
    - **Token Module**: 토큰 및 대기열 관리
    - **Reservation Module**: 예약 처리
    - **Payment Module**: 결제 처리
- **데이터베이스 설계**:
    - **Users**: 유저 정보
    - **Seats**: 좌석 정보
    - **Reservations**: 예약 정보
    - **Payments**: 결제 내역
    - **Tokens**: 토큰 및 대기열 정보

## 4. API Spec

### 4.1 유저 토큰 발급 API

- **Endpoint**: `/api/v1/tokens`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "userId": "UUID"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "token": "string",
      "status": "string",
      "queuePosition": "int",
      "remainingTime": "long"
    }
    ```
    
- **Error Codes** :
    - `400 Bad Request`: Invalid request payload.
    - `404 Not Found`: User not found.
    - `409 Conflict`: User already in the queue.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

### 4.2 예약 가능 날짜 / 좌석 API

- **Endpoint**: `/api/v1/reservations/dates`
- **Method**: GET
- **Response**:
    
    ```json
    [
      {
        "concertId": "UUID",
        "concertName": "Concert Name",
        "date": "2024-07-03"
      },
      {
        "concertId": "UUID",
        "concertName": "Concert Name",
        "date": "2024-07-04"
      },
      ...
    ]
    ```
    
- **Error Codes**:
    - `500 Internal Server Error`: Server encountered an unexpected condition.
- **Endpoint**: `/api/v1/reservations/{concertId}/seats`
- **Method**: GET
- **Response**:
    
    ```json
    [
      {
        "seatNumber": 1,
        "available": true
      },
      {
        "seatNumber": 2,
        "available": false
      },
      ...
    ]
    ```
    
- **Error Codes**:
    - `400 Bad Request`: Invalid concert ID.
    - `404 Not Found`: Concert not found.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

### 4.3 좌석 예약 요청 API

- **Endpoint**: `/api/v1/reservations`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "concertId": "UUID",
      "seatNumber": 1,
      "userId": "UUID"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "reservationId": "UUID",
      "expiresAt": "timestamp"
    }
    ```
    
- **Error Codes**:
    - `400 Bad Request`: Invalid request payload.
    - `401 Unauthorized`: User not authenticated.
    - `403 Forbidden`: User not allowed to make a reservation (e.g., token not in ACTIVE status).
    - `404 Not Found`: Concert or seat not found.
    - `409 Conflict`: Seat already reserved.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

### 4.4 잔액 충전 / 조회 API

- **Endpoint**: `/api/v1/payments/recharge`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "userId": "string",
      "amount": "BigDecimal"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "newBalance": "int",
      "currentBalance": "int"
    }
    ```
    
- **Error Codes**:
    - `400 Bad Request`: Invalid request payload.
    - `404 Not Found`: User not found.
    - `500 Internal Server Error`: Server encountered an unexpected condition.
- **Endpoint**: `/api/v1/payments/balance/{userId}`
- **Method**: GET
- **Response**:
    
    ```json
    {
      "newBalance": "int",
      "currentBalance": "int"
    }
    ```
    
- **Error Codes**:
    - `404 Not Found`: User not found.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

### 4.5 결제 API

- **Endpoint**: `/api/v1/payments/process`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "reservationId": "UUID",
      "amount": 100,
      "userId": "UUID"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "paymentId": "UUID",
      "status": "success"
    }
    ```
    
- **Error Codes**:
    - `400 Bad Request`: Invalid request payload.
    - `401 Unauthorized`: User not authenticated.
    - `403 Forbidden`: User not allowed to make a payment (e.g., token not in ACTIVE status).
    - `404 Not Found`: Reservation not found.
    - `409 Conflict`: Payment already processed or insufficient balance.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

### 4.6 토큰 상태 확인 API

- **Endpoint**: `/api/v1/tokens/{userId}`
- **Method**: GET
- **Response**:
    
    ```json
    {
      "token": "string",
      "status": "string",
      "queuePosition": "int",
      "remainingTime": "long"
    }
    ```
    
- **Error Codes**:
    - `400 Bad Request`: Invalid request payload.
    - `401 Unauthorized`: User not authenticated.
    - `404 Not Found`: Token not found.
    - `410 Gone`: Token expired.
    - `500 Internal Server Error`: Server encountered an unexpected condition.

## 5. Gantt

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

## 6. 상세 일정

### 프로젝트 마일스톤 및 상세 일정 (수정)

### 프로젝트 기간

2024년 7월 1일 ~ 2024년 7월 19일

### 마일스톤

1. **설계 단계** (7월 1일 ~ 7월 6일)
2. **기능 구현 및 테스트** (7월 7일 ~ 7월 19일)

### 설계 단계 (7월 1일 ~ 7월 6일)

- **7월 1일**
    - 프로젝트 요구사항 분석
    - 주요 기능 정의
    - 기술 스택 확정
- **7월 2일**
    - 시퀀스 다이어그램 설계
- **7월 3일**
    - 데이터베이스 설계
    - 테이블 구조 정의 (Users, Seats, Reservations, Payments, Queue)
- **7월 4일**
    - API 설계 (유저 토큰 발급, 예약 가능 날짜/좌석, 좌석 예약 요청, 잔액 충전/조회, 결제)
    - 엔드포인트 정의 및 Request/Response 설계
- **7월 5일**
    - 시스템 아키텍처 설계
    - 모듈화 구조 정의 (User Module, Queue Module, Reservation Module, Payment Module)
- **7월 6일**
    - 전체 설계 리뷰 및 피드백 반영
    - 최종 설계 문서 작성

### 기능 구현 및 테스트 단계 (7월 7일 ~ 7월 19일)

- **7월 7일 ~ 7월 9일**
    - 유저 토큰 발급 API 구현
    - 단위 테스트 작성 및 테스트
- **7월 10일 ~ 7월 11일**
    - 예약 가능 날짜/좌석 API 구현
    - 단위 테스트 작성 및 테스트
- **7월 12일 ~ 7월 13일**
    - 좌석 예약 요청 API 구현
    - 단위 테스트 작성 및 테스트
- **7월 14일 ~ 7월 15일**
    - 잔액 충전/조회 API 구현
    - 단위 테스트 작성 및 테스트
- **7월 16일 ~ 7월 17일**
    - 결제 API 구현
    - 단위 테스트 작성 및 테스트
- **7월 18일**
    - 통합 테스트 및 전체 시스템 테스트
    - 버그 수정 및 최종 조정
- **7월 19일**
    - 최종 검토 및 배포 준비
    - 프로젝트 문서화 및 마무리

## 7. SequenceDiagram

### 유저 토큰 발급 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant DB

    User->>API Gateway: 토큰 발급 요청
    API Gateway->>ConcertFacade: issueToken(userId)
    ConcertFacade->>TokenService: getOrCreateTokenForUser(userId)
    TokenService->>DB: 토큰 상태 조회
    alt 토큰이 없음
        TokenService->>DB: 새 토큰 생성
        TokenService->>TokenService: JWT 토큰 생성
        alt 토큰 수 < 11
            TokenService->>DB: 토큰 상태를 ACTIVE로 설정
        else 토큰 수 >= 11
            TokenService->>DB: 토큰 상태를 WAITING으로 설정
        end
    end
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: Token
    ConcertFacade-->>API Gateway: TokenDTO
    API Gateway-->>User: 토큰 및 대기열 정보 전달
```

### 예약 가능 날짜 조회 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ReservationController
    participant ConcertFacade
    participant ConcertService
    participant DB

    User->>API Gateway: 예약 가능 날짜 조회 요청
    API Gateway->>ReservationController: getAvailableConcertDates()
    ReservationController->>ConcertFacade: getAllConcerts()
    ConcertFacade->>ConcertService: getAllConcerts()
    ConcertService->>DB: 예약 가능 날짜 정보 조회
    DB-->>ConcertService: 예약 가능 날짜 정보 반환
    ConcertService-->>ConcertFacade: List<Concert>
    ConcertFacade-->>ReservationController: List<ConcertDTO>
    ReservationController->>ReservationController: Convert to List<ConcertDateResponse>
    ReservationController-->>API Gateway: List<ConcertDateResponse>
    API Gateway-->>User: 예약 가능 날짜 정보 전달
```

### 예약 가능 콘서트 조회 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Reservation Service
    participant DB

    User->>API Gateway: 예약 가능 콘서트 조회 요청
    API Gateway->>Reservation Service: 예약 가능 콘서트 조회
    Reservation Service->>DB: 예약 가능 콘서트 정보 조회
    DB-->>Reservation Service: 예약 가능 콘서트 정보 반환
    Reservation Service-->>API Gateway: 예약 가능 콘서트 정보 반환
    API Gateway-->>User: 예약 가능 콘서트 정보 전달

    Note over Reservation Service,DB: 실패 시
    DB-->>Reservation Service: 조회 실패
    Reservation Service-->>API Gateway: 예약 가능 콘서트 조회 실패
    API Gateway-->>User: 예약 가능 콘서트 조회 실패 메시지 전달
```

### 좌석 예약 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant ReservationService
    participant DB

    User->>API Gateway: 좌석 예약 요청
    API Gateway->>ConcertFacade: reserveSeat(SeatReservationRequest)
    ConcertFacade->>TokenService: getTokenStatus(userId)
    TokenService->>DB: 토큰 상태 조회
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: Token
    alt Token.status == ACTIVE
        ConcertFacade->>ReservationService: reserveSeat(concertId, seatNumber, userId, performanceDate)
        ReservationService->>DB: 좌석 예약 상태 업데이트 및 임시 배정 정보 저장
        DB-->>ReservationService: 좌석 예약 정보 반환
        ReservationService-->>ConcertFacade: Reservation (임시 예약 정보 포함)
        ConcertFacade-->>API Gateway: ReservationResponse (예약 ID 및 만료 시간 포함)
        API Gateway-->>User: 좌석 예약 정보 전달
    else Token.status != ACTIVE
        ConcertFacade-->>API Gateway: TokenInvalidStatusException
        API Gateway-->>User: 예약 불가 메시지 전달
    end
```

### 잔액 충전 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant PaymentService
    participant DB

    User->>API Gateway: 잔액 충전 요청
    API Gateway->>ConcertFacade: rechargeBalance(userId, amount)
    ConcertFacade->>TokenService: isTokenValid(userId)
    TokenService->>DB: 토큰 상태 조회
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: 토큰 유효성 결과
    alt 토큰 유효
        ConcertFacade->>PaymentService: rechargeBalance(userId, amount)
        PaymentService->>DB: 잔액 업데이트
        DB-->>PaymentService: 업데이트된 잔액 정보
        PaymentService-->>ConcertFacade: 충전 결과
        ConcertFacade-->>API Gateway: 충전 결과 반환
        API Gateway-->>User: 충전 결과 전달
    else 토큰 무효
        ConcertFacade-->>API Gateway: 충전 불가 (토큰 무효)
        API Gateway-->>User: 충전 불가 메시지 전달
    end
```

### 결제 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant PaymentService
    participant ReservationService
    participant DB

    User->>API Gateway: 결제 요청
    API Gateway->>ConcertFacade: processPayment(userId, reservationId, amount)
    ConcertFacade->>TokenService: isTokenValid(userId)
    TokenService->>DB: 토큰 상태 조회
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: 토큰 유효성 결과
    alt 토큰 유효
        ConcertFacade->>ReservationService: checkReservationStatus(reservationId)
        ReservationService->>DB: 예약 상태 조회
        DB-->>ReservationService: 예약 상태 반환
        ReservationService-->>ConcertFacade: 예약 상태
        alt 예약 유효
            ConcertFacade->>PaymentService: processPayment(userId, reservationId, amount)
            PaymentService->>DB: 결제 처리 및 좌석 소유권 업데이트
            DB-->>PaymentService: 결제 처리 결과
            PaymentService-->>ConcertFacade: 결제 결과
            ConcertFacade-->>API Gateway: 결제 결과 반환
            API Gateway-->>User: 결제 결과 전달
        else 예약 무효
            ConcertFacade-->>API Gateway: 결제 실패 (예약 무효)
            API Gateway-->>User: 결제 실패 메시지 전달
        end
    else 토큰 무효
        ConcertFacade-->>API Gateway: 결제 불가 (토큰 무효)
        API Gateway-->>User: 결제 불가 메시지 전달
    end
```

### 대기열 확인 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant DB

    User->>API Gateway: 토큰 상태 확인 요청
    API Gateway->>ConcertFacade: checkTokenStatus(userId)
    ConcertFacade->>TokenService: getTokenStatus(userId)
    TokenService->>DB: 토큰 정보 조회
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: Token
    ConcertFacade-->>API Gateway: TokenDTO
    API Gateway-->>User: 토큰 상태 정보 전달
```

## 8. DDL

**USERS 테이블**:

```sql
CREATE TABLE USERS (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**CONCERT 테이블**:

```sql
CREATE TABLE CONCERTS (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    concertName VARCHAR(255) NOT NULL,
    date DATE NOT NULL
);
```

**Seats 테이블**:

```sql
CREATE TABLE SEATS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concertId VARCHAR(255) NOT NULL,
    seatNumber INT NOT NULL,
    isReserved BOOLEAN NOT NULL,
    reservedBy VARCHAR(255),
    reservedUntil TIMESTAMP,
    UNIQUE (concertId, seatNumber)
);
```

**Reservations 테이블**:

```sql
CREATE TABLE RESERVATIONS (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    concertId VARCHAR(255) NOT NULL,
    seatNumber INT NOT NULL,
    reservationStatus VARCHAR(50) NOT NULL,
    reservedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    performanceDate DATE NOT NULL
);
```

**Payments 테이블**:

```sql
CREATE TABLE PAYMENTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    reservationId VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paymentStatus VARCHAR(50) NOT NULL,
    paidAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Tokens 테이블**:

```sql
CREATE TABLE TOKENS (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    queuePosition INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiresAt TIMESTAMP NOT NULL,
    lastUpdatedAt TIMESTAMP NOT NULL
);
```

## 9. ERD

```mermaid
erDiagram
    USERS {
        STRING id PK "NOT NULL"
        STRING username "NOT NULL"
        STRING password "NOT NULL"
        DECIMAL balance "NOT NULL"
        TIMESTAMP createdAt "NOT NULL"
    }

    CONCERTS {
        STRING id PK "NOT NULL"
        STRING concertName "NOT NULL"
        DATE date "NOT NULL"
    }

    TOKENS {
        STRING id PK "NOT NULL"
        STRING userId "NOT NULL"
        INT queuePosition "NOT NULL"
        STRING status "NOT NULL / 상태: ACTIVE, WAITING, EXPIRED"
        TIMESTAMP createdAt "NOT NULL"
        TIMESTAMP expiresAt "NOT NULL"
        TIMESTAMP lastUpdatedAt "NOT NULL"
    }

    SEATS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING concertId "NOT NULL"
        INT seatNumber "NOT NULL"
        BOOLEAN isReserved "NOT NULL"
        STRING reservedBy "NULL"
        TIMESTAMP reservedUntil "NULL"
    }

    RESERVATIONS {
        STRING id PK "NOT NULL"
        STRING userId "NOT NULL"
        STRING concertId "NOT NULL"
        INT seatNumber "NOT NULL"
        STRING reservationStatus "NOT NULL"
        TIMESTAMP reservedAt "NOT NULL"
        DATE performanceDate "NOT NULL"
    }

    PAYMENTS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        STRING reservationId "NOT NULL"
        DECIMAL amount "NOT NULL"
        STRING paymentStatus "NOT NULL"
        TIMESTAMP paidAt "NOT NULL"
    }

    USERS ||--o{ TOKENS : "가짐"
    USERS ||--o{ RESERVATIONS : "생성"
    USERS ||--o{ PAYMENTS : "생성"
    USERS ||--o{ SEATS : "예약"

    CONCERTS ||--o{ SEATS : "가짐"
    TOKENS }o--|| USERS : "속함"
    SEATS }o--|| USERS : "예약된 사람"
    SEATS }o--|| CONCERTS : "속함"
    RESERVATIONS }o--|| USERS : "속함"
    RESERVATIONS }o--|| CONCERTS : "속함"
    PAYMENTS }o--|| USERS : "속함"
    PAYMENTS }o--|| RESERVATIONS : "관련"
```

## 10. Project 구조

```markdown
├─main
│  ├─java
│  │  └─io
│  │      └─hhplus
│  │          └─concert
│  │              └─reservation
│  │                  │  Application.java
│  │                  │
│  │                  ├─application
│  │                  │  ├─dto
│  │                  │  │      ConcertDTO.java
│  │                  │  │      PaymentDTO.java
│  │                  │  │      ReservationDTO.java
│  │                  │  │      SeatDTO.java
│  │                  │  │      TokenDTO.java
│  │                  │  │      UserDTO.java
│  │                  │  │
│  │                  │  ├─exception
│  │                  │  │      ConcertNotFoundException.java
│  │                  │  │      GlobalExceptionHandler.java
│  │                  │  │      InsufficientBalanceException.java
│  │                  │  │      ReservationNotFoundException.java
│  │                  │  │      SeatAlreadyReservedException.java
│  │                  │  │      SeatNotFoundException.java
│  │                  │  │      TokenExpiredException.java
│  │                  │  │      TokenInvalidStatusException.java
│  │                  │  │      TokenNotFoundException.java
│  │                  │  │      UserAlreadyInQueueException.java
│  │                  │  │      UserNotFoundException.java
│  │                  │  │      UserNotInQueueException.java
│  │                  │  │
│  │                  │  └─facade
│  │                  │          ConcertFacade.java
│  │                  │          ConcertFacadeImpl.java
│  │                  │
│  │                  ├─config
│  │                  │      SecurityConfig.java
│  │                  │      SwaggerConfig.java
│  │                  │      WebMvcConfig.java
│  │                  │
│  │                  ├─domain
│  │                  │  ├─model
│  │                  │  │      Concert.java
│  │                  │  │      Payment.java
│  │                  │  │      Reservation.java
│  │                  │  │      Seat.java
│  │                  │  │      Token.java
│  │                  │  │      User.java
│  │                  │  │
│  │                  │  └─service
│  │                  │          ConcertService.java
│  │                  │          ConcertServiceImpl.java
│  │                  │          PaymentService.java
│  │                  │          PaymentServiceImpl.java
│  │                  │          ReservationService.java
│  │                  │          ReservationServiceImpl.java
│  │                  │          TokenService.java
│  │                  │          TokenServiceImpl.java
│  │                  │          UserService.java
│  │                  │          UserServiceImpl.java
│  │                  │
│  │                  ├─infrastructure
│  │                  │  ├─entity
│  │                  │  │      ConcertEntity.java
│  │                  │  │      PaymentEntity.java
│  │                  │  │      ReservationEntity.java
│  │                  │  │      SeatEntity.java
│  │                  │  │      TokenEntity.java
│  │                  │  │      UserEntity.java
│  │                  │  │
│  │                  │  ├─mapper
│  │                  │  │      ConcertMapper.java
│  │                  │  │      PaymentMapper.java
│  │                  │  │      ReservationMapper.java
│  │                  │  │      ResponseMapper.java
│  │                  │  │      SeatMapper.java
│  │                  │  │      TokenMapper.java
│  │                  │  │
│  │                  │  └─repository
│  │                  │          ConcertRepository.java
│  │                  │          PaymentRepository.java
│  │                  │          ReservationRepository.java
│  │                  │          SeatRepository.java
│  │                  │          TokenRepository.java
│  │                  │          UserRepository.java
│  │                  │
│  │                  └─presentation
│  │                      ├─controller
│  │                      │      PaymentController.java
│  │                      │      ReservationController.java
│  │                      │      TokenController.java
│  │                      │      UserController.java
│  │                      │
│  │                      ├─filter
│  │                      │      CommonFilter.java
│  │                      │
│  │                      ├─interceptor
│  │                      │      ApiInterceptor.java
│  │                      │
│  │                      ├─request
│  │                      │      BalanceRequest.java
│  │                      │      PaymentRequest.java
│  │                      │      ReservationRequest.java
│  │                      │      SeatReservationRequest.java
│  │                      │      TokenRequest.java
│  │                      │      UserRequest.java
│  │                      │      UserTokenRequest.java
│  │                      │
│  │                      └─response
│  │                              BalanceResponse.java
│  │                              ConcertDateResponse.java
│  │                              PaymentResponse.java
│  │                              ReservationResponse.java
│  │                              SeatResponse.java
│  │                              TokenResponse.java
│  │                              UserResponse.java
│  │
│  └─resources
│          application.properties
│          schema.sql
│
└─test
    └─java
        └─io
            └─hhplus
                └─concert
                    └─reservation
                        │  ApplicationTests.java
                        │
                        ├─application
                        │  └─facade
                        │          ConcertFacadeTest.java
                        │
                        ├─config
                        │      TestSecurityConfig.java
                        │
                        └─presentation
                                PaymentControllerTest.java
                                ReservationControllerTest.java
                                TokenControllerTest.java
                                UserControllerTest.java
```

### (별첨) 토큰 발급 후 대기열 프로세스 설계

- 스케줄러 설계 변경:
    - 좌석의 임시 배정은 매 1분마다 스케줄러가 조회하여 임시 배정된 좌석을 해제하는 배치 처리로 구현합니다. (변경 없음)
    - 대기열 순번은 10초마다 스케줄러가 TokenStatus가 WAITING인 Token을 createdAt으로 정렬하여 계산합니다.
- 클라이언트 폴링:
    - 클라이언트는 폴링 방식으로 주기적으로 토큰 상태 정보를 조회하여 자신의 위치를 확인할 수 있습니다.
- 인터페이스 구현:
    - TokenService 인터페이스에서 구현되어야 합니다.

### 1단계: 요구사항 분석 및 기본 설계

- 유저가 서비스에 접근할 때 고유한 토큰을 발급받아야 합니다.
- 모든 API 호출 시 토큰을 사용하여 유효성 검증을 수행해야 합니다.
- 토큰 시스템은 다수의 인스턴스에서도 일관성을 유지해야 합니다.
- 토큰의 상태(ACTIVE, WAITING, EXPIRED)를 관리하고, 일정 시간이 지나면 토큰이 만료되어야 합니다.

### 2단계: 데이터베이스 테이블 설계

**TOKENS 테이블**:

```sql
CREATE TABLE TOKENS (
    id VARCHAR(255) PRIMARY KEY NOT NULL,
    userId VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    queuePosition INT,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiresAt TIMESTAMP NOT NULL,
    lastUpdatedAt TIMESTAMP NOT NULL
);
```

### 3단계: 토큰 발급 프로세스

1. Authorization 헤더를 보냄 -> `TOKENS` 테이블에서 해당 `userId`를 조회.
2. 조건 분기:
    - 없으면 새 토큰 생성.
    - 있으면 상태 확인:
        - ACTIVE: 서비스에 들어가게 함.
        - WAITING: 대기 상태 유지.
        - EXPIRED: 새로운 토큰을 생성.

### 4단계: 데이터베이스 설계 (예약 테이블 복합키)

`RESERVATIONS` 테이블에서 `(concert_option_id, seat_id, status)`를 복합키로 설정하여 동시성 문제를 해결합니다.

### 5단계: 성능 저하/운영 확장성 저하 해결

**물리적 FK 제거 및 트랜잭션 관리**

- 물리적 FK를 제거하여 성능을 최적화합니다. 대신 트랜잭션을 통해 데이터의 일관성을 유지합니다.
- 예를 들어, 어플리케이션 레벨에서 예약 생성 및 결제는 하나의 트랜잭션으로 묶어 원자성을 보장합니다.

### 6단계: 임시 배정 스케줄러 구현

**임시 배정 스케줄러 설계**

- 좌석의 임시 배정은 매 1분마다 스케줄러가 조회하여 임시 배정된 좌석을 해제하는 배치 처리로 구현합니다.
- Spring의 `@Scheduled` 어노테이션을 사용하여 스케줄러를 설정합니다.
    
    ```java
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Component;
    
    @Component
    public class SeatReservationScheduler {
    
        @Scheduled(fixedRate = 60000) // 1분마다 실행
        public void releaseExpiredReservations() {
            // 임시 배정된 좌석 조회 및 해제 로직
            List<Seat> expiredSeats = seatRepository.findExpiredReservations();
            for (Seat seat : expiredSeats) {
                seat.setReserved(false);
                seat.setReservedBy(null);
                seat.setReservedUntil(null);
                seatRepository.save(seat);
            }
        }
    }
    ```
    

**임시 배정 조회 및 해제 쿼리**

- 어플리케이션 레벨에서 해당 쿼리를 실행합니다
    
    ```sql
    -- 임시 배정된 좌석 조회
    SELECT * FROM SEATS WHERE reservedUntil < NOW();
    
    -- 좌석 해제 업데이트
    UPDATE SEATS SET isReserved = FALSE, reservedBy = NULL, reservedUntil = NULL WHERE reservedUntil < NOW();
    ```
    

### 7단계: 대기열 조회 API 구현

### 대기열 조회 API 설계

클라이언트는 폴링 방식으로 주기적으로 대기열 정보를 조회하여 자신의 위치를 확인할 수 있습니다.

**대기열 조회 API**:

- **Endpoint**: `/api/v1/tokens/{userId}`
- **Method**: GET
- **Response**:

```json
{
  "token": "string",
  "status": "string",
  "queuePosition": "int",
  "remainingTime": "long"
}
```

### 8단계: 대기열 순번 처리 방법

1. 토큰 발급 시 대기열 추가:
    - 유저가 서비스에 접근할 때 토큰을 발급받고 `TOKENS` 테이블에 새로운 레코드를 추가합니다.
    - `createdAt` 컬럼을 사용하여 대기열에 추가된 시점을 기록합니다.
2. 대기열 순번 계산:
    - 10초마다 스케줄러가 실행되어 WAITING 상태의 토큰들의 순번을 재계산합니다.
    - `createdAt`을 기준으로 정렬하여 순번을 계산합니다.

**순번 계산 예제 쿼리**

```sql
UPDATE TOKENS t1
JOIN (
    SELECT id, ROW_NUMBER() OVER (ORDER BY createdAt) AS new_position
    FROM TOKENS
    WHERE status = 'WAITING'
) t2 ON t1.id = t2.id
SET t1.queuePosition = t2.new_position
WHERE t1.status = 'WAITING'
```

### 9단계: 시퀀스 다이어그램

대기열 시스템의 주요 시퀀스를 다이어그램으로 작성합니다.

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant ConcertFacade
    participant TokenService
    participant DB

    User->>API Gateway: 토큰 발급 요청
    API Gateway->>ConcertFacade: issueToken(userId)
    ConcertFacade->>TokenService: getOrCreateTokenForUser(userId)
    TokenService->>DB: 토큰 상태 조회
    alt 토큰이 없음
        TokenService->>DB: 새 토큰 생성
    end
    DB-->>TokenService: 토큰 정보 반환
    TokenService-->>ConcertFacade: Token
    alt Token.status == ACTIVE
        ConcertFacade-->>API Gateway: TokenDTO (ACTIVE)
    else Token.status == WAITING
        ConcertFacade-->>API Gateway: TokenDTO (WAITING)
    else Token.status == EXPIRED
        ConcertFacade->>TokenService: createNewToken(userId)
        TokenService->>DB: 새 토큰 생성
        DB-->>TokenService: 새 토큰 정보
        TokenService-->>ConcertFacade: 새 Token
        ConcertFacade-->>API Gateway: TokenDTO (ACTIVE)
    end
    API Gateway-->>User: 토큰 및 대기열 정보 전달
```
