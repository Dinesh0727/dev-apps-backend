package com.opdinna.error_vault.backend.service;

import java.util.List;

import com.opdinna.error_vault.backend.model.domain.ErrorFile;
import com.opdinna.error_vault.backend.model.dto.ErrorCardDTO;

public interface ErrorFileService {

    public List<ErrorFile> getAllErrorFiles();

    // Can replace with the ErrorFileDTO Datatype later or can change the return
    // type in Controller
    public ErrorFile getErrorFile(int id);

    public void addErrorFile(ErrorFile e);

    public void deleteErrorFile(int id);

    public List<ErrorCardDTO> getAllErrorCards();
}
