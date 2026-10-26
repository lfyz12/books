package ru.urfu.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Книжный магазин (таблица stores).
 */
@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите название магазина")
    @Size(max = 100, message = "Не длиннее 100 символов")
    private String name;

    @NotBlank(message = "Введите город")
    @Size(max = 50, message = "Не длиннее 50 символов")
    private String city;

    @NotBlank(message = "Введите адрес")
    @Size(max = 200, message = "Не длиннее 200 символов")
    private String address;

    /** Пользователь, который создал запись. */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Store(String name, String city, String address, User owner) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.owner = owner;
    }
}
