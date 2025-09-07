# Bank REST API

REST API для управления банковскими картами и пользователями с авторизацией.

---

## Требования

- Docker
- Docker Compose
- Java 21 
- Maven Wrapper (в проекте уже есть)

---

## Структура проекта

- `Dockerfile` — сборка и запуск приложения
- `docker-compose.yml` — сервисы для БД и приложения
- `src/` — исходный код приложения

---

## Запуск через Docker

docker-compose up --build

---

## Запуск Swagger / OpenAPI
После запуска приложения Swagger UI доступен по адресу:
http://localhost:8080/swagger-ui.html

---

## Таблицы в бд

- bank_card - информация о банковских картах
- status_card - статусы карт (ACTIVE, BLOCKED, EXPIRED)
- user_account - данные пользователей
- role_user - роли пользователей (ADMIN, USER)
