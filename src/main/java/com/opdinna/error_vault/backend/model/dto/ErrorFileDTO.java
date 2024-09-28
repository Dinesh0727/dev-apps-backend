package com.opdinna.error_vault.backend.model.dto;

import java.util.List;

import com.opdinna.error_vault.backend.model.domain.ImageLinkElement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ErrorFileDTO {
    @Size(min = 1, message = "Atleast 1 character required")
    @NotNull
    private String title;

    @Size(min = 1, message = "Atleast 1 character required")
    @NotNull
    private String projectTitle;

    @Size(min = 1, message = "Atleast 1 character required")
    @NotNull
    private String problemDescription;

    @Size(min = 1, message = "Atleast 1 character required")
    @NotNull
    private String solutionDescription;

    private String codeBeforeFix;

    private String codeAfterFix;

    private List<String> referenceLinks;

    private List<ImageLinkElement> imageList;

    private List<String> labels;

    public ErrorFileDTO(@Size(min = 1, message = "Atleast 1 character required") String title,
            @Size(min = 1, message = "Atleast 1 character required") String projectTitle,
            @Size(min = 1, message = "Atleast 1 character required") String problemDescription,
            @Size(min = 1, message = "Atleast 1 character required") String solutionDescription, String codeBeforeFix,
            String codeAfterFix, List<String> referenceLinks, List<ImageLinkElement> imageList, List<String> labels) {
        this.title = title;
        this.projectTitle = projectTitle;
        this.problemDescription = problemDescription;
        this.solutionDescription = solutionDescription;
        this.codeBeforeFix = codeBeforeFix;
        this.codeAfterFix = codeAfterFix;
        this.referenceLinks = referenceLinks;
        this.imageList = imageList;
        this.labels = labels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
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

    public String getCodeBeforeFix() {
        return codeBeforeFix;
    }

    public void setCodeBeforeFix(String codeBeforeFix) {
        this.codeBeforeFix = codeBeforeFix;
    }

    public String getCodeAfterFix() {
        return codeAfterFix;
    }

    public void setCodeAfterFix(String codeAfterFix) {
        this.codeAfterFix = codeAfterFix;
    }

    public List<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

    public List<ImageLinkElement> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageLinkElement> imageList) {
        this.imageList = imageList;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "ErrorFileDTO [title=" + title + ", projectTitle=" + projectTitle + ", problemDescription="
                + problemDescription + ", solutionDescription=" + solutionDescription + ", codeBeforeFix="
                + codeBeforeFix + ", codeAfterFix=" + codeAfterFix + ", referenceLinks=" + referenceLinks
                + ", imageList=" + imageList + ", labels=" + labels + "]";
    }
}
