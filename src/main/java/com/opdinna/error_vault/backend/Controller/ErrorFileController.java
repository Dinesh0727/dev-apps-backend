package com.opdinna.error_vault.backend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdinna.error_vault.backend.model.ErrorFile;
import com.opdinna.error_vault.backend.service.ErrorFileService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/errors")
public class ErrorFileController {

    private ErrorFileService errorFileService;

    public ErrorFileController(ErrorFileService errorFileService) {
        this.errorFileService = errorFileService;
    }

    @GetMapping("/")
    public List<ErrorFile> getAllErrorFiles() {
        System.out.println("Got a call to fetch all error files " + System.currentTimeMillis());
        List<ErrorFile> allErrorFiles = errorFileService.getAllErrorFiles();
        return allErrorFiles;
    }

    @GetMapping("/{id}")
    public ErrorFile getErrorFileById(@PathVariable int id) {
        return errorFileService.getErrorFile(id);
    }

    @PostMapping("/createErrorFile")
    public String createErrorFile(@RequestBody ErrorFile errorFile) {
        errorFileService.addErrorFile(errorFile);
        return errorFile.toString();
    }

    @DeleteMapping("/deleteErrorFile/{id}")
    public String deleteErrorFile(@PathVariable int id) {
        errorFileService.deleteErrorFile(id);

        return "Deleted the error file with id " + id;
    }

}
