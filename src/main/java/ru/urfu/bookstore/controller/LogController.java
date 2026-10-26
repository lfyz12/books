package ru.urfu.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.urfu.bookstore.service.UserActionService;
import ru.urfu.bookstore.service.UserService;

/**
 * Просмотр журнала действий пользователей (таблица user_actions).
 */
@Controller
@RequiredArgsConstructor
public class LogController {

    private final UserActionService userActionService;
    private final UserService userService;

    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("actions", userActionService.findForUser(userService.getCurrentUser()));
        return "logs";
    }
}
