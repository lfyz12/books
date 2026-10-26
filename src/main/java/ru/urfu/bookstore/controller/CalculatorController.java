package ru.urfu.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.urfu.bookstore.dto.CalculationResult;
import ru.urfu.bookstore.entity.Book;
import ru.urfu.bookstore.service.BookService;
import ru.urfu.bookstore.service.CalculatorService;
import ru.urfu.bookstore.service.UserActionService;

/**
 * Форма расчёта стоимости покупки.
 */
@Controller
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final BookService bookService;
    private final CalculatorService calculatorService;
    private final UserActionService userActionService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("books", bookService.findVisible());
        return "calculator";
    }

    @PostMapping
    public String calculate(@RequestParam Long bookId,
                            @RequestParam int quantity,
                            @RequestParam int discount,
                            @RequestParam(defaultValue = "false") boolean delivery,
                            Model model) {
        // Возвращаем введённые значения, чтобы они остались в форме
        model.addAttribute("books", bookService.findVisible());
        model.addAttribute("bookId", bookId);
        model.addAttribute("quantity", quantity);
        model.addAttribute("discount", discount);
        model.addAttribute("delivery", delivery);

        if (quantity < 1 || quantity > 1000 || discount < 0 || discount > 100) {
            model.addAttribute("error", "Количество должно быть от 1 до 1000, скидка - от 0 до 100%");
            return "calculator";
        }

        Book book = bookService.getForView(bookId);
        CalculationResult result = calculatorService.calculate(book, quantity, discount, delivery);
        model.addAttribute("result", result);
        userActionService.logAction(String.format("Расчёт стоимости: «%s» × %d, скидка %d%% = %.2f руб.",
                book.getTitle(), quantity, discount, result.getTotal()));
        return "calculator";
    }
}
