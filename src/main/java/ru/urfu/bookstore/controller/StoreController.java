package ru.urfu.bookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.bookstore.entity.Store;
import ru.urfu.bookstore.service.StoreService;

/**
 * Список магазинов и форма добавления/изменения магазина.
 */
@Controller
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stores", storeService.findVisible());
        return "stores/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("store", new Store());
        return "stores/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("store", storeService.getForEdit(id));
        return "stores/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("store") Store store, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "stores/form";
        }
        storeService.save(store);
        redirectAttributes.addFlashAttribute("message", "Магазин «" + store.getName() + "» сохранён");
        return "redirect:/stores";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (storeService.delete(id)) {
            redirectAttributes.addFlashAttribute("message", "Магазин удалён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Нельзя удалить магазин, в котором есть книги");
        }
        return "redirect:/stores";
    }
}
