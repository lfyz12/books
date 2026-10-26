package ru.urfu.bookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.service.BookService;
import ru.urfu.bookstore.service.StoreService;

/**
 * Список книг и форма добавления/изменения книги.
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final StoreService storeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findVisible());
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("stores", storeService.findVisible());
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getForEdit(id));
        model.addAttribute("stores", storeService.findVisible());
        return "books/form";
    }

    /** Сохранение и новой, и изменённой книги (отличаются наличием id). */
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult,
                       Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Есть ошибки ввода - показываем форму ещё раз с сообщениями
            model.addAttribute("stores", storeService.findVisible());
            return "books/form";
        }
        bookService.save(book);
        redirectAttributes.addFlashAttribute("message", "Книга «" + book.getTitle() + "» сохранена");
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Книга удалена");
        return "redirect:/books";
    }
}
