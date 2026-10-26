package ru.urfu.bookstore.service;

import org.junit.jupiter.api.Test;
import ru.urfu.bookstore.dto.CalculationResult;
import ru.urfu.bookstore.entity.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Модульный тест расчёта стоимости (без запуска Spring).
 */
class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    private Book bookWithPrice(double price) {
        Book book = new Book();
        book.setTitle("Тестовая книга");
        book.setPrice(price);
        return book;
    }

    @Test
    void discountAndPaidDelivery() {
        // 500 × 2 = 1000, скидка 10% = 100, после скидки 900 < 2000 -> доставка 300
        CalculationResult result = calculatorService.calculate(bookWithPrice(500), 2, 10, true);

        assertEquals(1000, result.getSubtotal(), 0.001);
        assertEquals(100, result.getDiscountSum(), 0.001);
        assertEquals(300, result.getDeliveryPrice(), 0.001);
        assertEquals(1200, result.getTotal(), 0.001);
    }

    @Test
    void freeDeliveryForLargeOrder() {
        // 1000 × 3 = 3000, без скидки, 3000 >= 2000 -> доставка бесплатная
        CalculationResult result = calculatorService.calculate(bookWithPrice(1000), 3, 0, true);

        assertEquals(0, result.getDeliveryPrice(), 0.001);
        assertEquals(3000, result.getTotal(), 0.001);
    }

    @Test
    void withoutDelivery() {
        CalculationResult result = calculatorService.calculate(bookWithPrice(300), 1, 0, false);

        assertEquals(0, result.getDeliveryPrice(), 0.001);
        assertEquals(300, result.getTotal(), 0.001);
    }
}
