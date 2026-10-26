package ru.urfu.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.bookstore.entity.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByOrderByDateActionDesc();

    List<UserAction> findByUsernameOrderByDateActionDesc(String username);
}
