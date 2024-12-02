package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService{

    User findById(Long id);

    List<User> findAll();

    void createUser(User user, List<Long> roleIds);

    void updateUser(User user, List<Long> roleIds);

    void deleteById(Long id);

    List<Role> findAllRoles();

    UserDetails loadUserByUsername(String email);

    User findByUsername(String email);
}
