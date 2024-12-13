package com.opdinna.error_vault.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.opdinna.error_vault.backend.model.domain.User;
import com.opdinna.error_vault.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }
}
