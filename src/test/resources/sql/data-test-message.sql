-- Вставка тестовых пользователей
INSERT INTO users (username, email, password, roles)
VALUES ('user1', 'user1@example.com', 'password1', '{1}'),
       ('user2', 'user2@example.com', 'password2', '{1}'),
       ('user3', 'user3@example.com', 'password3', '{1}');

INSERT INTO messages (sender_id, receiver_id, content)
VALUES (1, 2, 'message1'),
       (2, 1, 'message2');

INSERT INTO subscriptions (subscriber_id, target_user_id, created_at, friend_status, subs_status)
VALUES (2, 1, '2023-08-15 23:34:05.262185', 0, 1),
       (1 ,3, '2023-08-15 23:34:05.262185', 1, 2);