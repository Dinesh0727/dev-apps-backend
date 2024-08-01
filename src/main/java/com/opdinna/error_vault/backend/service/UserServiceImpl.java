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

    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("There is no user with id : " + id));
    }

}
