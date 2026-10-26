package ru.urfu.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.bookstore.entity.Store;
import ru.urfu.bookstore.entity.User;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByOrderByNameAsc();

    /** Магазины, созданные конкретным пользователем. */
    List<Store> findByOwnerOrderByNameAsc(User owner);
}
