package ru.urfu.bookstore.service;

import org.springframework.stereotype.Service;
import ru.urfu.bookstore.dto.CalculationResult;
import ru.urfu.bookstore.entity.Book;

/**
 * Расчёт стоимости покупки книг.
 */
@Service
public class CalculatorService {

    /** Стоимость доставки, руб. */
    public static final double DELIVERY_PRICE = 300;
    /** Сумма заказа (после скидки), начиная с которой доставка бесплатная, руб. */
    public static final double FREE_DELIVERY_FROM = 2000;

    /**
     * сумма    = цена × количество
     * скидка   = сумма × процент / 100
     * доставка = 300 руб., но бесплатно, если (сумма - скидка) >= 2000 руб.
     * итого    = сумма - скидка + доставка
     */
    public CalculationResult calculate(Book book, int quantity, int discountPercent, boolean delivery) {
        double subtotal = book.getPrice() * quantity;
        double discountSum = subtotal * discountPercent / 100;
        double afterDiscount = subtotal - discountSum;

        double deliveryPrice = 0;
        if (delivery && afterDiscount < FREE_DELIVERY_FROM) {
            deliveryPrice = DELIVERY_PRICE;
        }

        double total = afterDiscount + deliveryPrice;
        return new CalculationResult(book.getTitle(), book.getPrice(), quantity,
                subtotal, discountPercent, discountSum, deliveryPrice, total);
    }
}
