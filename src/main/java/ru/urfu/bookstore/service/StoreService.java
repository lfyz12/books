package ru.urfu.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.entity.Store;
import ru.urfu.bookstore.entity.User;
import ru.urfu.bookstore.repository.BookRepository;
import ru.urfu.bookstore.repository.StoreRepository;

import java.util.List;

/**
 * Бизнес-логика для книжных магазинов.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final UserActionService userActionService;

    /** USER видит только свои магазины, READ_ONLY и ADMIN - все. */
    public List<Store> findVisible() {
        User current = userService.getCurrentUser();
        if (current.getRole() == Role.USER) {
            return storeRepository.findByOwnerOrderByNameAsc(current);
        }
        return storeRepository.findAllByOrderByNameAsc();
    }

    /** Магазин для редактирования (с проверкой прав). */
    public Store getForEdit(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Магазин не найден"));
        userService.checkCanModify(store.getOwner());
        return store;
    }

    public void save(Store form) {
        if (form.getId() == null) {
            // Новая запись: владелец - текущий пользователь
            form.setOwner(userService.getCurrentUser());
            storeRepository.save(form);
            userActionService.logAction("Добавлен магазин «" + form.getName() + "»");
        } else {
            // Изменение: переносим поля из формы в запись из БД
            Store store = getForEdit(form.getId());
            store.setName(form.getName());
            store.setCity(form.getCity());
            store.setAddress(form.getAddress());
            storeRepository.save(store);
            userActionService.logAction("Изменён магазин «" + store.getName() + "»");
        }
    }

    /**
     * Удаление магазина. Возвращает false, если в магазине есть книги
     * (такой магазин удалять нельзя).
     */
    public boolean delete(Long id) {
        Store store = getForEdit(id);
        if (bookRepository.existsByStore(store)) {
            return false;
        }
        storeRepository.delete(store);
        userActionService.logAction("Удалён магазин «" + store.getName() + "»");
        return true;
    }
}
