package ru.urfu.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Данные формы регистрации. Это не сущность БД, а просто объект для формы.
 */
@Data
public class RegistrationForm {

    @NotBlank(message = "Введите логин")
    @Size(min = 3, max = 30, message = "Логин должен быть от 3 до 30 символов")
    private String username;

    @NotBlank(message = "Введите пароль")
    @Size(min = 4, max = 50, message = "Пароль должен быть от 4 до 50 символов")
    private String password;

    @NotBlank(message = "Повторите пароль")
    private String confirmPassword;
}
