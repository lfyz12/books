# Книжный каталог — курсовой проект на Spring Boot

Веб-приложение для учёта **книг** и **книжных магазинов** (вариант 1: «Список книг + список книжных магазинов, стоимость книг»).

## Технологии
Java 21, Spring Boot 3.5, Spring MVC, Thymeleaf, Spring Data JPA (Hibernate), H2, Spring Security, Spring Boot Actuator, Lombok, Bootstrap 5, Maven.

## Запуск в VS Code
1. Открыть папку проекта в VS Code (нужно расширение **Extension Pack for Java**; VS Code сам предложит его установить).
2. Дождаться, пока Java-расширение загрузит проект (внизу строка «Java: Ready»).
3. Нажать **F5** (конфигурация «Run BookstoreApplication») или открыть `BookstoreApplication.java` и нажать **Run** над методом `main`.

Или в терминале VS Code:
```
.\mvnw.cmd spring-boot:run
```
Тесты: `.\mvnw.cmd test`. Установленный Maven не нужен — его скачает Maven Wrapper (`mvnw.cmd`).

После запуска открыть http://localhost:8080

## Демо-аккаунты
Создаются автоматически при первом запуске (класс `DataInitializer`):

| Логин | Пароль | Роль |
|---|---|---|
| admin | admin | ADMIN |
| user | user | USER |
| reader | reader | READ_ONLY |

Новый пользователь, зарегистрированный через форму «Зарегистрироваться», получает роль **READ_ONLY**.

## Роли
| Возможность | READ_ONLY | USER | ADMIN |
|---|---|---|---|
| Просмотр книг и магазинов | все записи | только свои | все записи |
| Добавление / изменение / удаление | нет | только свои записи | любые записи |
| Расчёт стоимости, главная, «О приложении» | да | да | да |
| Журнал действий | свои действия | свои действия | все действия |
| Назначение ролей, Actuator, H2-консоль | нет | нет | да |

## Формы приложения
| № | Форма | Адрес |
|---|---|---|
| 1 | Вход (и регистрация) | `/login`, `/register` |
| 2 | Списки: книги, магазины | `/books`, `/stores` |
| 3 | Ввод данных: книга, магазин | `/books/new`, `/books/{id}/edit`, `/stores/new`, `/stores/{id}/edit` |
| 4 | Просмотр логов | `/logs` |
| 5 | Главная страница | `/` |
| 6 | О приложении | `/about` |
| 7 | Расчёт стоимости покупки | `/calculator` |
| 8 | Назначение ролей (ADMIN) | `/admin/users` |

**Расчёт стоимости:** сумма = цена × количество; скидка = сумма × % / 100; доставка 300 ₽ (бесплатно, если сумма со скидкой ≥ 2000 ₽); итого = сумма − скидка + доставка.

## База данных
H2 в файле `data/bookstore.mv.db`, таблицы создаёт Hibernate по классам-сущностям:

- `users` — id, username, password (BCrypt-хэш), role
- `stores` — id, name, city, address, owner_id → users
- `books` — id, title, author, genre, publish_year, price, quantity, store_id → stores, owner_id → users
- `user_actions` — id, date_action, description, username (журнал действий)

Посмотреть БД: http://localhost:8080/h2-console (вход под admin), JDBC URL `jdbc:h2:file:./data/bookstore`, пользователь `sa`, пароль пустой.
Чтобы начать с чистой базы, остановите приложение и удалите папку `data`.

## Логи
Каждое действие пользователя (вход, выход, регистрация, добавление/изменение/удаление, расчёт, смена роли) записывается:
- в таблицу `user_actions` (видно на странице «Журнал»),
- в файл `logs/bookstore.log`.

## Actuator
- http://localhost:8080/actuator/health — состояние приложения (доступно всем)
- http://localhost:8080/actuator/info — информация о приложении (доступно всем)
- http://localhost:8080/actuator — список всех эндпоинтов: metrics, loggers, logfile, mappings, beans (только ADMIN)

## Структура проекта
```
src/main/java/ru/urfu/bookstore/
  BookstoreApplication.java  — точка входа
  config/       — SecurityConfig (роли и доступ), DataInitializer (демо-данные)
  entity/       — классы-сущности (таблицы БД): User, Role, Book, Store, UserAction
  repository/   — интерфейсы Spring Data JPA для работы с таблицами
  dto/          — объекты для форм: RegistrationForm, CalculationResult
  service/      — бизнес-логика и проверка прав
  controller/   — обработка запросов и выбор HTML-страницы
src/main/resources/
  application.properties — настройки (БД, логи, Actuator)
  templates/    — HTML-страницы Thymeleaf
  static/css/   — стили
src/test/java/  — тесты (расчёт стоимости, права доступа ролей)
```

## Как проходит запрос (Spring MVC)
Браузер → Spring Security (проверка входа и роли) → **Controller** → **Service** (логика, проверка «своя/чужая запись», запись в журнал) → **Repository** → БД H2. Контроллер кладёт данные в `Model` и возвращает имя шаблона → Thymeleaf формирует HTML → браузер.
