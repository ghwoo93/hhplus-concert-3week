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
