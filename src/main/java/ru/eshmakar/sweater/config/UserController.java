package ru.eshmakar.sweater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.eshmakar.sweater.domain.Role;
import ru.eshmakar.sweater.domain.User;
import ru.eshmakar.sweater.repos.UserRepo;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user") //ссылка над классом, означает, что у всех методов будет добавлятся /user/ к адресу
@PreAuthorize("hasAuthority('ADMIN')") //разрешаем выполнять эти методы только админам
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userRepo.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());//получаем все значении ролей
        return "userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ){

        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key: form.keySet()){
            if (roles.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }

        userRepo.save(user); //сохраняем в базе

        return "redirect:/user";
    }
}