package com.opdinna.error_vault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdinna.error_vault.backend.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
