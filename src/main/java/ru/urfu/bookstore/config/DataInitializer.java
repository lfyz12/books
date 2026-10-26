package ru.urfu.bookstore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.entity.Store;
import ru.urfu.bookstore.entity.User;
import ru.urfu.bookstore.repository.BookRepository;
import ru.urfu.bookstore.repository.StoreRepository;
import ru.urfu.bookstore.repository.UserRepository;

/**
 * Заполняет пустую базу демонстрационными данными при первом запуске.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // данные уже есть
        }

        User admin = userRepository.save(new User("admin", passwordEncoder.encode("admin"), Role.ADMIN));
        User user = userRepository.save(new User("user", passwordEncoder.encode("user"), Role.USER));
        userRepository.save(new User("reader", passwordEncoder.encode("reader"), Role.READ_ONLY));

        Store bookWorld = storeRepository.save(new Store("Книжный мир", "Екатеринбург", "ул. Ленина, 10", admin));
        Store bookHouse = storeRepository.save(new Store("Дом книги", "Екатеринбург", "ул. Малышева, 25", admin));
        Store reader = storeRepository.save(new Store("Читатель", "Москва", "ул. Тверская, 7", user));

        bookRepository.save(new Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", 1967, 650.0, 12, bookWorld, admin));
        bookRepository.save(new Book("Преступление и наказание", "Фёдор Достоевский", "Роман", 1866, 480.0, 8, bookWorld, admin));
        bookRepository.save(new Book("Чистый код", "Роберт Мартин", "Программирование", 2008, 1490.0, 5, bookHouse, admin));
        bookRepository.save(new Book("Spring в действии", "Крейг Уоллс", "Программирование", 2022, 2100.0, 3, bookHouse, admin));
        bookRepository.save(new Book("Евгений Онегин", "Александр Пушкин", "Поэзия", 1833, 390.0, 15, reader, user));
        bookRepository.save(new Book("Война и мир", "Лев Толстой", "Роман", 1869, 1200.0, 6, reader, user));
        bookRepository.save(new Book("Гарри Поттер и философский камень", "Джоан Роулинг", "Фэнтези", 1997, 790.0, 10, reader, user));

        log.info("База данных заполнена демонстрационными данными");
    }
}
