package com.opdinna.error_vault.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.opdinna.error_vault.backend.model.ErrorFile;
import com.opdinna.error_vault.backend.repository.ErrorFileRepository;

import jakarta.transaction.Transactional;

@Service
public class ErrorFileServiceImpl implements ErrorFileService {

    private ErrorFileRepository errorFileRepository;

    public ErrorFileServiceImpl(ErrorFileRepository errorFileRepository) {
        this.errorFileRepository = errorFileRepository;
    }

    @Override
    @Transactional
    public void addErrorFile(ErrorFile e) {
        errorFileRepository.save(e);
    }

    @Override
    @Transactional
    public void deleteErrorFile(int id) {
        errorFileRepository.deleteById(id);
    }

    @Override
    public List<ErrorFile> getAllErrorFiles() {
        return errorFileRepository.findAll();
    }

    @Override
    public ErrorFile getErrorFile(int id) {
        return errorFileRepository.findById(id).orElseThrow(() -> new NullPointerException());
    }

}
