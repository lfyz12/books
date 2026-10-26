package ru.urfu.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.bookstore.entity.Role;
import ru.urfu.bookstore.service.UserService;

/**
 * Форма назначения ролей. Доступна только ADMIN (см. SecurityConfig).
 */
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", Role.values());
        model.addAttribute("currentUser", userService.getCurrentUser());
        return "admin/users";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role,
                             RedirectAttributes redirectAttributes) {
        if (id.equals(userService.getCurrentUser().getId())) {
            // Иначе администратор может случайно лишить себя доступа
            redirectAttributes.addFlashAttribute("error", "Нельзя изменить роль самому себе");
        } else {
            userService.changeRole(id, role);
            redirectAttributes.addFlashAttribute("message",
                    "Роль изменена. Она начнёт действовать после повторного входа пользователя.");
        }
        return "redirect:/admin/users";
    }
}
