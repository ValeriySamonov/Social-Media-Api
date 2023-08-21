# SocialMedia API

![GitHub license](https://img.shields.io/github/license/your-username/SocialMedia-API)
![GitHub stars](https://img.shields.io/github/stars/your-username/SocialMedia-API?style=social)

## О проекте

Проект SocialMedia API представляет собой платформу для социальных медиа, позволяющую пользователям взаимодействовать друг с другом. Основной функционал включает в себя регистрацию, аутентификацию, создание постов, обмен сообщениями, подписку на других пользователей и получение ленты активности.

## Технологии

- Spring Boot 3.0.6
- Spring Security
- JWT (JSON Web Token)
- Liquibase
- PostgreSQL
- Docker
- Maven

## Запуск проекта

1. Клонируйте репозиторий:

```
git clone https://github.com/your-username/SocialMedia-API.git
```

2. Установите Docker, если он еще не установлен.


3. Запустите приложение с помощью Maven:

```
mvn spring-boot:run
```

4. Приложение будет доступно по адресу [http://localhost:8080](http://localhost:8080).

## Интеграционные тесты

Проект включает в себя интеграционные тесты для всех контроллеров. Тесты запускаются в отдельном Docker-контейнере с PostgreSQL, что позволяет проверить корректность работы всего приложения в реальной среде.

Для запуска тестов выполните следующую команду:

```
mvn test
```

Документация API
Файл social_media_api.yaml в папке resources проекта содержит описание API. Вы можете открыть этот файл в редакторе Swagger для детального изучения доступных запросов.

Лицензия
Проект доступен под лицензией MIT. Подробную информацию смотрите в файле LICENSE.
