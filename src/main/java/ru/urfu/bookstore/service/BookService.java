package ru.urfu.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.entity.User;
import ru.urfu.bookstore.repository.BookRepository;

import java.util.List;

/**
 * Бизнес-логика для книг.
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserService userService;
    private final UserActionService userActionService;

    /** USER видит только свои книги, READ_ONLY и ADMIN - все. */
    public List<Book> findVisible() {
        User current = userService.getCurrentUser();
        if (current.getRole() == Role.USER) {
            return bookRepository.findByOwnerOrderByTitleAsc(current);
        }
        return bookRepository.findAllByOrderByTitleAsc();
    }

    /** Книга для просмотра (например, для расчёта стоимости). */
    public Book getForView(Long id) {
        Book book = findById(id);
        User current = userService.getCurrentUser();
        if (current.getRole() == Role.USER && !book.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Нет доступа к этой книге");
        }
        return book;
    }

    /** Книга для редактирования (с проверкой прав). */
    public Book getForEdit(Long id) {
        Book book = findById(id);
        userService.checkCanModify(book.getOwner());
        return book;
    }

    public void save(Book form) {
        // Книгу можно положить только в магазин, который пользователь может изменять
        userService.checkCanModify(form.getStore().getOwner());

        if (form.getId() == null) {
            // Новая запись: владелец - текущий пользователь
            form.setOwner(userService.getCurrentUser());
            bookRepository.save(form);
            userActionService.logAction("Добавлена книга «" + form.getTitle() + "»");
        } else {
            // Изменение: переносим поля из формы в запись из БД
            Book book = getForEdit(form.getId());
            book.setTitle(form.getTitle());
            book.setAuthor(form.getAuthor());
            book.setGenre(form.getGenre());
            book.setPublishYear(form.getPublishYear());
            book.setPrice(form.getPrice());
            book.setQuantity(form.getQuantity());
            book.setStore(form.getStore());
            bookRepository.save(book);
            userActionService.logAction("Изменена книга «" + book.getTitle() + "»");
        }
    }

    public void delete(Long id) {
        Book book = getForEdit(id);
        bookRepository.delete(book);
        userActionService.logAction("Удалена книга «" + book.getTitle() + "»");
    }

    /** Общая стоимость книг в наличии: сумма (цена × количество). */
    public double totalValue(List<Book> books) {
        return books.stream()
                .mapToDouble(book -> book.getPrice() * book.getQuantity())
                .sum();
    }

    private Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Книга не найдена"));
    }
}
