package ru.urfu.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверка запуска приложения и правил доступа для разных ролей.
 * Для тестов используется отдельная БД в памяти, рабочая БД не затрагивается.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class BookstoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void anonymousIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails("reader")
    void readOnlyCanViewButNotEdit() throws Exception {
        mockMvc.perform(get("/books")).andExpect(status().isOk());
        mockMvc.perform(get("/books/new")).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("user")
    void userCanAddButIsNotAdmin() throws Exception {
        mockMvc.perform(get("/books/new")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    void adminHasFullAccess() throws Exception {
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isOk());
    }
}
