-- users 테이블 데이터
INSERT INTO users (id, username, password, balance, created_at) VALUES
('user1', 'John Doe', 'password123', 1000.00, CURRENT_TIMESTAMP),
('user2', 'Jane Smith', 'password456', 1500.00, CURRENT_TIMESTAMP),
('user3', 'Bob Johnson', 'password789', 2000.00, CURRENT_TIMESTAMP);

-- concerts 테이블 데이터
INSERT INTO concerts (id, concert_name, date) VALUES
('concert1', 'Summer Rock Festival', '2024-08-15'),
('concert2', 'Classical Night', '2024-09-20'),
('concert3', 'Jazz in the Park', '2024-10-05');

-- seats 테이블 데이터
INSERT INTO seats (concert_id, seat_number, status, reserved_by, reserved_until) VALUES
('concert1', 1, 'AVAILABLE', NULL, NULL),
('concert1', 2, 'AVAILABLE', NULL, NULL),
('concert1', 3, 'RESERVED', 'user1', DATEADD('MINUTE', 5, CURRENT_TIMESTAMP)),
('concert1', 4, 'SOLD', 'user2', NULL),
('concert1', 5, 'AVAILABLE', NULL, NULL),
('concert2', 1, 'AVAILABLE', NULL, NULL),
('concert2', 2, 'RESERVED', 'user3', DATEADD('MINUTE', 5, CURRENT_TIMESTAMP)),
('concert2', 3, 'AVAILABLE', NULL, NULL),
('concert2', 4, 'SOLD', 'user1', NULL),
('concert2', 5, 'AVAILABLE', NULL, NULL),
('concert3', 1, 'AVAILABLE', NULL, NULL),
('concert3', 2, 'AVAILABLE', NULL, NULL),
('concert3', 3, 'AVAILABLE', NULL, NULL),
('concert3', 4, 'RESERVED', 'user2', DATEADD('MINUTE', 5, CURRENT_TIMESTAMP)),
('concert3', 5, 'SOLD', 'user3', NULL);

-- reservations 테이블 데이터
INSERT INTO reservations (id, user_id, concert_id, seat_number, reservation_status, reserved_at, performance_date) VALUES
('res1', 'user1', 'concert1', 3, 'TEMPORARY', CURRENT_TIMESTAMP, '2024-08-15'),
('res2', 'user2', 'concert1', 4, 'COMPLETED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP), '2024-08-15'),
('res3', 'user3', 'concert2', 2, 'TEMPORARY', CURRENT_TIMESTAMP, '2024-09-20'),
('res4', 'user1', 'concert2', 4, 'COMPLETED', DATEADD('DAY', -1, CURRENT_TIMESTAMP), '2024-09-20'),
('res5', 'user2', 'concert3', 4, 'TEMPORARY', CURRENT_TIMESTAMP, '2024-10-05');

-- payments 테이블 데이터
-- INSERT INTO payments (id, user_id, reservation_id, amount, payment_status, paid_at) VALUES
-- (1, 'user2', 'res2', 100.00, 'COMPLETED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
-- (2, 'user1', 'res4', 150.00, 'COMPLETED', DATEADD('DAY', -1, CURRENT_TIMESTAMP));

-- tokens 테이블 데이터
-- INSERT INTO tokens (id, user_id, queue_position, status, created_at, expires_at, last_updated_at) VALUES
-- ('token1', 'user1', 1, 'ACTIVE', CURRENT_TIMESTAMP, DATEADD('HOUR', 1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
-- ('token2', 'user2', 2, 'WAITING', DATEADD('MINUTE', -5, CURRENT_TIMESTAMP), DATEADD('HOUR', 1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
-- ('token3', 'user3', 3, 'EXPIRED', DATEADD('HOUR', -2, CURRENT_TIMESTAMP), DATEADD('HOUR', -1, CURRENT_TIMESTAMP), DATEADD('HOUR', -1, CURRENT_TIMESTAMP));