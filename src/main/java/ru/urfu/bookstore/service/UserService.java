package ru.urfu.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.entity.User;
import ru.urfu.bookstore.repository.UserRepository;

import java.util.List;

/**
 * Работа с пользователями: вход, регистрация, роли, проверка прав.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActionService userActionService;

    /**
     * Вызывается Spring Security при входе: находит пользователя в БД
     * и сообщает его пароль (хэш) и роль.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }

    /** Регистрация: по умолчанию выдаётся роль READ_ONLY. */
    public User register(String username, String rawPassword) {
        User user = new User(username, passwordEncoder.encode(rawPassword), Role.READ_ONLY);
        userRepository.save(user);
        userActionService.logAction(username, "Регистрация нового пользователя");
        return user;
    }

    /** Пользователь, который сейчас вошёл в систему. */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByUsernameAsc();
    }

    public void changeRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Role oldRole = user.getRole();
        user.setRole(role);
        userRepository.save(user);
        userActionService.logAction("Изменена роль пользователя " + user.getUsername() + ": " + oldRole + " -> " + role);
    }

    /**
     * Может ли текущий пользователь изменять запись, созданную owner.
     * ADMIN - любую запись, USER - только свою, READ_ONLY - никакую.
     */
    public boolean canModify(User owner) {
        User current = getCurrentUser();
        if (current.getRole() == Role.ADMIN) {
            return true;
        }
        return current.getRole() == Role.USER
                && owner != null
                && owner.getId().equals(current.getId());
    }

    /** То же, что canModify, но при отсутствии прав выбрасывает исключение (ответ 403). */
    public void checkCanModify(User owner) {
        if (!canModify(owner)) {
            throw new AccessDeniedException("Нет прав на изменение этой записи");
        }
    }
}
