package ru.urfu.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.entity.User;
import ru.urfu.bookstore.entity.UserAction;
import ru.urfu.bookstore.repository.UserActionRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Журнал действий пользователей.
 * Каждое действие записывается в таблицу user_actions и в файл logs/bookstore.log.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository userActionRepository;

    /** Записать действие текущего (вошедшего) пользователя. */
    public void logAction(String description) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        logAction(username, description);
    }

    /** Записать действие указанного пользователя. */
    public void logAction(String username, String description) {
        userActionRepository.save(new UserAction(LocalDateTime.now(), description, username));
        log.info("Пользователь '{}': {}", username, description);
    }

    /** ADMIN видит весь журнал, остальные - только свои действия. */
    public List<UserAction> findForUser(User user) {
        if (user.getRole() == Role.ADMIN) {
            return userActionRepository.findAllByOrderByDateActionDesc();
        }
        return userActionRepository.findByUsernameOrderByDateActionDesc(user.getUsername());
    }

    /** Spring Security сам публикует это событие после успешного входа. */
    @EventListener
    public void onLogin(AuthenticationSuccessEvent event) {
        logAction(event.getAuthentication().getName(), "Вход в систему");
    }

    /** Событие после выхода из системы. */
    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        logAction(event.getAuthentication().getName(), "Выход из системы");
    }
}
