package com.opdinna.error_vault.backend.model.dto;

import java.util.Arrays;
import java.util.List;

public class ErrorCardDTO {

    private int id;

    private String errorTitle;

    private String problemDescription;

    private String solutionDescription;

    private List<String> labels;

    public ErrorCardDTO(int id, String errorTitle, String problemDescription, String solutionDescription,
            String labels) {
        this.id = id;
        this.errorTitle = errorTitle;
        this.problemDescription = problemDescription;
        this.solutionDescription = solutionDescription;
        this.labels = Arrays.asList(labels.split(","));
    }

    public int getId() {
        return this.id;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getSolutionDescription() {
        return solutionDescription;
    }

    public void setSolutionDescription(String solutionDescription) {
        this.solutionDescription = solutionDescription;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "ErrorCardDTO [errorTitle=" + errorTitle + ", problemDescription=" + problemDescription
                + ", solutionDescription=" + solutionDescription + ", labels=" + labels + "]";
    }

}
