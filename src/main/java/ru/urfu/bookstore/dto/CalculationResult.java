package ru.urfu.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Результат расчёта стоимости покупки, который показывается на форме расчёта.
 */
@Getter
@AllArgsConstructor
public class CalculationResult {

    private String bookTitle;
    /** Цена одной книги. */
    private double price;
    private int quantity;
    /** Стоимость без скидки: цена × количество. */
    private double subtotal;
    private int discountPercent;
    /** Размер скидки в рублях. */
    private double discountSum;
    /** Стоимость доставки (0 - если доставка не нужна или бесплатная). */
    private double deliveryPrice;
    /** Итого к оплате. */
    private double total;
}
