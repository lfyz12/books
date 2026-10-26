package ru.urfu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение «Книжный каталог».
 * Аннотация @SpringBootApplication включает автоконфигурацию Spring Boot
 * и поиск компонентов (контроллеров, сервисов, репозиториев) в этом пакете.
 */
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
