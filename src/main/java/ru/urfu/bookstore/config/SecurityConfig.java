package ru.urfu.bookstore.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Настройки безопасности: какие страницы каким ролям доступны,
 * форма входа, выход, шифрование паролей.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Пароли хранятся в БД в виде BCrypt-хэша. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Доступно без входа
                        .requestMatchers("/login", "/register", "/css/**", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // Только для администратора
                        .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")
                        .requestMatchers("/admin/**", "/actuator/**").hasRole("ADMIN")
                        // Добавление, изменение, удаление - для USER и ADMIN (READ_ONLY нельзя)
                        .requestMatchers("/books/new", "/books/*/edit", "/books/save", "/books/*/delete",
                                "/stores/new", "/stores/*/edit", "/stores/save", "/stores/*/delete")
                        .hasAnyRole("USER", "ADMIN")
                        // Всё остальное - для любого вошедшего пользователя
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // H2-консоль работает во фрейме и отправляет формы без CSRF-токена
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
