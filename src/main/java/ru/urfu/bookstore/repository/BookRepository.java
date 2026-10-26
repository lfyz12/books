package ru.urfu.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.entity.Store;
import ru.urfu.bookstore.entity.User;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByTitleAsc();

    /** Книги, созданные конкретным пользователем. */
    List<Book> findByOwnerOrderByTitleAsc(User owner);

    /** Есть ли в магазине книги (нужно перед удалением магазина). */
    boolean existsByStore(Store store);
}
