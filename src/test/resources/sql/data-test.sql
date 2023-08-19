-- Вставка тестовых пользователей
INSERT INTO users (username, email, password_hash)
VALUES ('user1', 'user1@example.com', 'password1'),
       ('user2', 'user2@example.com', 'password2'),
       ('user3', 'user3@example.com', 'password3');

INSERT INTO posts (title, text, user_id, created_at)
VALUES ('Title', 'Text', 1, '2023-08-15 23:34:05.262185');

INSERT INTO post_image (file_name, post_id)
VALUES ('test1.jpg', 1),
       ('test2.jpg', 1);

INSERT INTO messages (sender_id, receiver_id, content)
VALUES (1, 2, 'message1'),
       (2, 1, 'message2');

INSERT INTO subscriptions (subscriber_id, target_user_id, created_at, friend_status, subs_status)
VALUES (2, 1, '2023-08-15 23:34:05.262185', 0, 1),
       (1 ,3, '2023-08-15 23:34:05.262185', 1, 2)