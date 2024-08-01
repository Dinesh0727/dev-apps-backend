package com.opdinna.error_vault.backend.service;

import java.util.List;

import com.opdinna.error_vault.backend.model.ErrorFile;

public interface ErrorFileService {

    public List<ErrorFile> getAllErrorFiles();

    public ErrorFile getErrorFile(int id);

    public void addErrorFile(ErrorFile e);

    public void deleteErrorFile(int id);
}
