package com.opdinna.error_vault.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.opdinna.error_vault.backend.model.domain.ErrorFile;
import com.opdinna.error_vault.backend.model.dto.ErrorCardDTO;

public interface ErrorFileRepository extends JpaRepository<ErrorFile, Integer> {

    @Query("SELECT new com.opdinna.error_vault.backend.model.dto.ErrorCardDTO(e.id, e.heading, e.problemDescription, e.solutionText, e.labels) FROM ErrorFile e")
    public List<ErrorCardDTO> getAllErrorFileCards();
}
