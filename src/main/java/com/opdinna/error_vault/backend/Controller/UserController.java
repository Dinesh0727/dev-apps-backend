package com.opdinna.error_vault.backend.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.opdinna.error_vault.backend.model.User;
import com.opdinna.error_vault.backend.service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @GetMapping("/")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getMethodName(@PathVariable int id) {
        return userService.getUser(id);
    }

}
