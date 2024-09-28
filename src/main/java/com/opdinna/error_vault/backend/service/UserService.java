package com.opdinna.error_vault.backend.service;

import java.util.List;

import com.opdinna.error_vault.backend.model.domain.User;

public interface UserService {

    public void addUser(User user);

    public List<User> getUsers();

    public User getUser(String email);

    public void deleteUser(String email);

}
