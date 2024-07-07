### 프로젝트 개요

콘서트 예약 서비스는 사용자들이 콘서트 좌석을 예약하고, 예약을 위한 잔액을 충전하며, 결제를 완료할 수 있는 시스템입니다. 
이 서비스는 다수의 사용자가 동시에 접근할 수 있는 대기열 시스템을 포함하며, 동시성 이슈를 고려하여 설계하였습니다.

### 주요 기능 및 제약사항

1. 유저 토큰 발급 API: 사용자가 대기열에 들어가서 서비스를 이용할 수 있도록 JWT 토큰을 발급합니다.
2. 예약 가능 날짜 / 좌석 API: 예약 가능한 날짜와 해당 날짜의 좌석 정보를 조회합니다.
3. 좌석 예약 요청 API: 사용자가 좌석을 임시로 예약하고 결제가 이루어지지 않으면 임시 배정을 해제합니다.
4. 잔액 충전 / 조회 API: 사용자가 예약에 사용할 금액을 충전하고, 현재 잔액을 조회합니다.
5. 결제 API: 사용자가 좌석 예약 후 결제를 완료하면 좌석을 최종 배정합니다.
6. 대기열 확인 API: 사용자가 현재 대기열에서 자신의 위치를 확인할 수 있습니다.

### 프로젝트 계획

### 1. 목표 설정

- 유저가 대기열을 통해 콘서트 좌석을 예약하고 결제까지 완료할 수 있는 시스템 구축.
- 동시성 이슈를 고려하여 다수의 사용자가 동시에 접근할 때도 안정적으로 서비스 제공.

### 2. 기술 스택

- **언어**: Java
- **프레임워크**: Spring Boot
- **데이터베이스**: MySQL (대기열 관리 및 데이터 저장)
- **테스트 프레임워크**: JUnit, Mockito
- **인증**: JWT

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
      "queuePosition": "int",
      "remainingTime": "int"
    }
    ```
    

### 4.2 예약 가능 날짜 / 좌석 API

- **Endpoint**: `/api/v1/concerts`
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
    
- **Endpoint**: `/api/v1/concerts/{concertId}/seats`
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
    

### 4.3 좌석 예약 요청 API

- **Endpoint**: `/api/v1/reservations`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "concertId": "UUID",
      "seatNumber": 1,
      "token": "string"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "reservationId": "UUID",
      "expiresAt": "timestamp"
    }
    ```
    

### 4.4 잔액 충전 / 조회 API

- **Endpoint**: `/api/v1/users/{userId}/balance`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "userId": "UUID",
      "amount": 100
    }
    ```
    
- **Response**:
    
    ```json
    {
      "newBalance": 200
    }
    ```
    
- **Endpoint**: `/api/v1/balances`
- **Method**: GET
- **Request**:
    
    ```json
    {
      "userId": "UUID"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "currentBalance": 200
    }
    ```
    

### 4.5 결제 API

- **Endpoint**: `/api/v1/payments`
- **Method**: POST
- **Request**:
    
    ```json
    {
      "reservationId": "UUID",
      "amount": 100,
      "token": "string"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "paymentId": "UUID",
      "status": "success"
    }
    ```
    

### 4.6 대기열 확인 API

- **Endpoint**: `/api/v1/queue/status`
- **Method**: GET
- **Request**:
    
    ```json
    {
      "token": "string"
    }
    ```
    
- **Response**:
    
    ```json
    {
      "queuePosition": "int",
      "remainingTime": "int"
    }
    ```

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
### 유저 토큰 발급 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Auth Service
    participant DB

    User->>API Gateway: 토큰 발급 요청
    API Gateway->>Auth Service: Authorization 헤더 확인
    Auth Service->>DB: Queue 테이블에서 userId 조회
    alt userId가 없음
        DB-->>Auth Service: 조회 실패
        Auth Service-->>API Gateway: 토큰 발급 실패
        API Gateway-->>User: 토큰 발급 실패 메시지 전달
    else userId가 있음
        alt 상태가 ACTIVE
            Auth Service-->>API Gateway: 토큰 발급
            API Gateway-->>User: 토큰 및 대기열 정보 전달
        else 상태가 WAITING
            Auth Service-->>API Gateway: 대기 중
            API Gateway-->>User: 대기 중 메시지 전달
        else 상태가 EXPIRED
            Auth Service->>DB: 새 Queue 데이터 생성
            DB-->>Auth Service: 생성 완료
            Auth Service-->>API Gateway: 토큰 발급
            API Gateway-->>User: 토큰 및 대기열 정보 전달
        end
    end
```

### 예약 가능 날짜 조회 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Reservation Service
    participant DB

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
    participant Reservation Service
    participant DB

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
```

### 좌석 예약 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Reservation Service
    participant DB

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
```

### 잔액 충전 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Payment Service
    participant DB

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

```

### 잔액 조회 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Payment Service
    participant DB

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

```

### 결제 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Payment Service
    participant DB

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

### 대기열 확인 요청 Use Case

```mermaid
sequenceDiagram
    participant User
    participant API Gateway
    participant Auth Service
    participant DB

    User->>API Gateway: 대기열 확인 요청
    API Gateway->>Auth Service: 대기열 정보 조회
    Auth Service->>DB: 대기열 정보 조회
    DB-->>Auth Service: 대기열 정보 반환
    Auth Service-->>API Gateway: 대기열 정보 반환
    API Gateway-->>User: 대기열 정보 전달

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

    CONCERTS {
        STRING id PK "NOT NULL"
        STRING concertName "NOT NULL"
        DATE date "NOT NULL"
        INT qauntity "NOT NULL"
    }

    QUEUE {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        STRING token "NOT NULL"
        INT queuePosition "NOT NULL"
        STRING status "NOT NULL / 상태: ACTIVE, WAITING, EXPIRED" 
        TIMESTAMP createdAt "NOT NULL"
        TIMESTAMP expiresAt "NOT NULL"
    }

    SEATS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING concertId "NOT NULL"
        INT seatNumber UK "NOT NULL"
        BOOLEAN isReserved UK "NOT NULL"
        STRING reservedBy "NOT NULL"
        TIMESTAMP reservedUntil "NOT NULL"
    }

    RESERVATIONS {
        BIGINT id PK "AUTO_INCREMENT"
        STRING userId "NOT NULL"
        STRING concertId UK "NOT NULL"
        INT seatNumber UK "NOT NULL"
        STRING reservationStatus UK "NOT NULL / 상태: ACTIVE, WAITING, EXPIRED"
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
        STRING token PK "NOT NULL"
        STRING userId "NOT NULL"
        TIMESTAMP createdAt "NOT NULL"
        TIMESTAMP expiresAt "NOT NULL"
    }

    USERS ||--o{ QUEUE : "가짐"
    USERS ||--o{ RESERVATIONS : "생성"
    USERS ||--o{ PAYMENTS : "생성"
    USERS ||--o{ SEATS : "예약"
    USERS ||--o{ TOKENS : "발급"

    CONCERTS ||--o{ SEATS : "가짐"
    QUEUE }o--|| USERS : "속함"
    TOKENS }o--|| USERS : "속함"
    SEATS }o--|| USERS : "예약된 사람"
    SEATS }o--|| CONCERTS : "속함"
    RESERVATIONS }o--|| USERS : "속함"
    RESERVATIONS }o--|| CONCERTS : "속함"
    PAYMENTS }o--|| USERS : "속함"
    PAYMENTS }o--|| RESERVATIONS : "관련"
```