CREATE TABLE users (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE concerts (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    concert_name VARCHAR(255) NOT NULL,
    date DATE NOT NULL
);

CREATE TABLE seats (
    concert_id VARCHAR(255) NOT NULL,
    seat_number INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reserved_by VARCHAR(255),
    reserved_until TIMESTAMP,
    PRIMARY KEY (concert_id, seat_number, status)
);

CREATE TABLE reservations (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    concert_id VARCHAR(255) NOT NULL,
    seat_number INT NOT NULL,
    reservation_status VARCHAR(50) NOT NULL,
    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    performance_date DATE NOT NULL
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    reservation_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    queue_position INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL
);