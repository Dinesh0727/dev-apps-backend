package com.opdinna.error_vault.backend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdinna.error_vault.backend.model.ErrorFile;
import com.opdinna.error_vault.backend.service.ErrorFileService;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/errors")
public class ErrorFileController {

    private ErrorFileService errorFileService;

    public ErrorFileController(ErrorFileService errorFileService) {
        this.errorFileService = errorFileService;
    }

    @GetMapping("/")
    public List<ErrorFile> getAllErrorFiles(@RequestParam String param) {
        return errorFileService.getAllErrorFiles();
    }

    @GetMapping("/{id}")
    public ErrorFile getErrorFileById(@PathVariable int id) {
        return errorFileService.getErrorFile(id);
    }

    @PutMapping("/createErrorFile")
    public void createErrorFile(@RequestBody ErrorFile errorFile) {
        errorFileService.addErrorFile(errorFile);
    }

    @DeleteMapping("/deleteErrorFile/{id}")
    public String deleteErrorFile(@PathVariable int id) {
        errorFileService.deleteErrorFile(id);

        return "Deleted the error file with id " + id;
    }

}