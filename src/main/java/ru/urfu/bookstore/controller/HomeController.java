package ru.urfu.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.service.BookService;
import ru.urfu.bookstore.service.StoreService;
import ru.urfu.bookstore.service.UserService;

import java.util.List;

/**
 * Главная страница и страница «О приложении».
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final BookService bookService;
    private final StoreService storeService;

    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookService.findVisible();
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("bookCount", books.size());
        model.addAttribute("storeCount", storeService.findVisible().size());
        model.addAttribute("totalValue", bookService.totalValue(books));
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
