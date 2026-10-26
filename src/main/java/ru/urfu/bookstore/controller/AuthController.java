package ru.urfu.bookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.urfu.bookstore.dto.RegistrationForm;
import ru.urfu.bookstore.service.UserService;

import java.util.Objects;

/**
 * Вход и регистрация. Сама проверка логина и пароля выполняется Spring Security,
 * здесь только показывается страница входа.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Пароли не совпадают");
        }
        if (userService.exists(form.getUsername())) {
            bindingResult.rejectValue("username", "exists", "Такой логин уже занят");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.register(form.getUsername(), form.getPassword());
        return "redirect:/login?registered";
    }
}
