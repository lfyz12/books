package ru.urfu.bookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Запись журнала действий пользователя (таблица user_actions).
 */
@Entity
@Table(name = "user_actions")
@Getter
@Setter
@NoArgsConstructor
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @Column(nullable = false)
    private String description;

    /** Кто выполнил действие. */
    private String username;

    public UserAction(LocalDateTime dateAction, String description, String username) {
        this.dateAction = dateAction;
        this.description = description;
        this.username = username;
    }
}
