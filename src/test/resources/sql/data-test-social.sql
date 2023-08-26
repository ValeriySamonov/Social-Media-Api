-- Вставка тестовых пользователей
INSERT INTO users (username, email, password)
VALUES ('user1', 'user1@example.com', '$2a$12$BFV7OVGwgriHtAeb3QD/Ze0RVi5TSVcO5gK2/9/vCdkCnDvj5UTF2'),
       ('user2', 'user2@example.com', '$2a$12$4GntsWHEmY49H/tCGWzlUuAiNgTV6JVN1XXw6bJ8bgwul7eLUwklW'),
       ('user3', 'user3@example.com', '$2a$12$0zXmSKGJeQwhQPNulEOYaex2cxIl9b0MB7K3YxXm5Xnbg6HAsuONG');

INSERT INTO posts (title, text, user_id, created_at)
VALUES ('Title', 'Text', 1, '2023-08-15 23:34:05.262185');

INSERT INTO post_images (file_name, post_id)
VALUES ('test1.jpg', 1),
       ('test2.jpg', 1);

INSERT INTO subscriptions (subscriber_id, target_user_id, friend_status, subs_status)
VALUES (2, 1, 0, 1),
       (1 ,3, 1, 2);