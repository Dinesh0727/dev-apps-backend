package com.opdinna.error_vault.backend.service;

import java.util.List;
import com.opdinna.error_vault.backend.model.User;

public interface UserService {

    // I have to make this class as Interface and create an implementation
    // class

    public void addUser(User user);

    public List<User> getUsers();

    public User getUser(String email);

    public void deleteUser(String email);

}
