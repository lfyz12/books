package ru.urfu.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.bookstore.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Доступ к таблице users. Реализацию методов Spring Data JPA
 * создаёт сам по их названиям.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByOrderByUsernameAsc();
}
