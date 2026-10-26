package ru.urfu.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Книга (таблица books). Каждая книга продаётся в одном из магазинов.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите название книги")
    @Size(max = 150, message = "Не длиннее 150 символов")
    private String title;

    @NotBlank(message = "Введите автора")
    @Size(max = 100, message = "Не длиннее 100 символов")
    private String author;

    @Size(max = 50, message = "Не длиннее 50 символов")
    private String genre;

    @NotNull(message = "Введите год издания")
    @Min(value = 1450, message = "Год не может быть меньше 1450")
    @Max(value = 2100, message = "Год не может быть больше 2100")
    private Integer publishYear;

    /** Цена одного экземпляра в рублях. */
    @NotNull(message = "Введите цену")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private Double price;

    /** Количество экземпляров в наличии. */
    @NotNull(message = "Введите количество")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer quantity;

    /** Магазин, в котором продаётся книга (много книг - один магазин). */
    @NotNull(message = "Выберите магазин")
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    /** Пользователь, который создал запись. */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Book(String title, String author, String genre, Integer publishYear,
                Double price, Integer quantity, Store store, User owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publishYear = publishYear;
        this.price = price;
        this.quantity = quantity;
        this.store = store;
        this.owner = owner;
    }
}
