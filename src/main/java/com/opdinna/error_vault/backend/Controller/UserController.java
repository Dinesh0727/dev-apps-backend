package com.opdinna.error_vault.backend.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.opdinna.error_vault.backend.model.domain.User;
import com.opdinna.error_vault.backend.service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{email}")
    public User getUser(@PathVariable String email) {
        return userService.getUser(email);
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody User user) {
        userService.addUser(user);
        return user.toString();
    }

    @DeleteMapping("/deleteUser/{email}")
    public String deleteUser(@PathVariable String email) {
        User user = getUser(email);
        userService.deleteUser(email);
        return "Deleted the user : " + user.toString();
    }

}
