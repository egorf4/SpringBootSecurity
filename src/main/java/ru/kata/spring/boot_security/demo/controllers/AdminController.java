package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "new-user";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds, BindingResult bindingResult) {
        Collection<Role> roles = roleRepository.findAllById(roleIds);
        user.setRoles(roles);
        if (bindingResult.hasErrors()) {
            return "new-user";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        System.out.println("USER: " + user);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        List<Role> allRoles = roleRepository.findAll();
        System.out.println("ROLES: " + allRoles);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "edit-user";
    }


    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user) {

        System.out.println("userR ID: " + user.getId());
        System.out.println("Role id" + user.getRoles());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        } else {
            User existingUser = userService.findById(user.getId());
            user.setPassword(existingUser.getPassword());
        }

        Set<Role> roles = user.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName())) // Найти роли в БД
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.update(user);

        return "redirect:/admin";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
