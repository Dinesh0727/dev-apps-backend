package com.opdinna.error_vault.backend.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdinna.error_vault.backend.model.domain.ErrorFile;
import com.opdinna.error_vault.backend.model.domain.ImageLinkElement;
import com.opdinna.error_vault.backend.model.dto.ErrorCardDTO;
import com.opdinna.error_vault.backend.model.dto.ErrorFileDTO;
import com.opdinna.error_vault.backend.model.mapper.ErrorFileDTOMapper;
import com.opdinna.error_vault.backend.service.ErrorFileService;

@RestController
@RequestMapping("/errors")
public class ErrorFileController {

    private final ErrorFileService errorFileService;

    public ErrorFileController(ErrorFileService errorFileService) {
        this.errorFileService = errorFileService;
    }

    @GetMapping("/allErrors")
    public List<ErrorFile> getAllErrorFiles() {
        System.out.println("Got a call to fetch all error files " +
                System.currentTimeMillis());
        List<ErrorFile> allErrorFiles = errorFileService.getAllErrorFiles();
        return allErrorFiles;
    }

    @GetMapping("")
    public List<ErrorCardDTO> getAllErrorFileCards() {
        System.out.println("Got a call to fetch all error cards " +
                System.currentTimeMillis());
        List<ErrorCardDTO> allErrorCards = errorFileService.getAllErrorCards();
        System.out.println(allErrorCards);
        return allErrorCards;
    }

    @GetMapping("/{id}")
    public ErrorFile getErrorFileById(@PathVariable int id) {
        return errorFileService.getErrorFile(id);
    }

    @PostMapping("/createErrorFile")
    public String createErrorFile(@RequestBody ErrorFileDTO requestBody) {

        System.out.println("Response Body : " + requestBody.toString());
        String errorTitle = requestBody.getTitle();
        String projectTitle = requestBody.getProjectTitle();
        String problemText = (String) requestBody.getProblemDescription();
        String solutionText = (String) requestBody.getSolutionDescription();
        // For lists, you might need to cast to List<Object> and then to List<String> if
        // needed
        String beforeFixText = (String) requestBody.getCodeBeforeFix();
        String afterFixText = (String) requestBody.getCodeAfterFix();
        List<ImageLinkElement> imageList = (List<ImageLinkElement>) requestBody.getImageList();
        List<String> referenceList = (List<String>) requestBody.getReferenceLinks();
        List<String> labelList = (List<String>) requestBody.getLabels();

        // Log or process the values as needed
        System.out.println("Error Title: " + errorTitle);
        System.out.println("Project Title: " + projectTitle);
        System.out.println("Problem Text: " + problemText);
        System.out.println("Solution Text: " + solutionText);
        System.out.println("Image List: " + imageList);
        System.out.println("Reference List: " + referenceList);
        System.out.println("Label List: " + labelList);
        System.out.println("Before CodeFix: " + beforeFixText);
        System.out.println("After CodeFix: " + afterFixText);

        ErrorFile file = ErrorFileDTOMapper.toErrorFile(requestBody);
        ErrorFile addedFile = errorFileService.addErrorFile(file);

        // Return a response or further processing
        return String.valueOf(addedFile.getId());
    }

    @DeleteMapping("/deleteErrorFile/{id}")
    public String deleteErrorFile(@PathVariable int id) {
        errorFileService.deleteErrorFile(id);

        return "Deleted the error file with id " + id;
    }

}
