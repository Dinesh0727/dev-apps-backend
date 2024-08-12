package com.opdinna.error_vault.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.opdinna.error_vault.backend.model.User;
import com.opdinna.error_vault.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NullPointerException("There is no user with email : " + email));
    }

    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }
}
