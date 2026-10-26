package ru.urfu.bookstore.entity;

/**
 * Роли пользователей.
 * READ_ONLY - только просмотр (выдаётся при регистрации),
 * USER - работает только со своими записями,
 * ADMIN - полный доступ ко всем данным.
 */
public enum Role {
    READ_ONLY,
    USER,
    ADMIN
}
