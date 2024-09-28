package com.opdinna.error_vault.backend.model.domain;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "errors")
public class ErrorFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "heading")
    private String heading;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "labels")
    private String labels;

    @Column(name = "code_block_before_fix")
    private String codeBlockBeforeFix;

    @Column(name = "code_block_after_fix")
    private String codeBlockAfterFix;

    @Column(name = "problem_description")
    private String problemDescription;

    @Column(name = "solution_text")
    private String solutionText;

    @Column(name = "image_link_list")
    @ElementCollection
    @CollectionTable(name = "error_file_image_link_list", joinColumns = @JoinColumn(name = "error_file_id"))
    private List<ImageLinkElement> imageLinkList;

    @Column(name = "reference_links")
    private String referenceLinks;

    public ErrorFile() {

    }

    public ErrorFile(String heading, String projectName, List<String> labels, String codeBlockBeforeFix,
            String codeBlockAfterFix, String problemDescription, String solutionText,
            List<ImageLinkElement> imageLinkList,
            List<String> referenceLinks) {
        this.heading = heading;
        this.projectName = projectName;
        this.labels = String.join(",", labels);
        this.codeBlockBeforeFix = codeBlockBeforeFix;
        this.codeBlockAfterFix = codeBlockAfterFix;
        this.problemDescription = problemDescription;
        this.solutionText = solutionText;
        this.imageLinkList = imageLinkList;
        this.referenceLinks = String.join(";", referenceLinks);
    }

    public ErrorFile(int id, String heading, String projectName, List<String> labels, String codeBlockBeforeFix,
            String codeBlockAfterFix, String problemDescription, String solutionText,
            List<ImageLinkElement> imageLinkList,
            List<String> referenceLinks) {
        this.heading = heading;
        this.projectName = projectName;
        this.labels = String.join(",", labels);
        this.codeBlockBeforeFix = codeBlockBeforeFix;
        this.codeBlockAfterFix = codeBlockAfterFix;
        this.problemDescription = problemDescription;
        this.solutionText = solutionText;
        this.imageLinkList = imageLinkList;
        this.referenceLinks = String.join(";", referenceLinks);
        this.id = id;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setLabels(List<String> labels) {
        this.labels = String.join(",", labels);
    }

    public void setCodeBlockBeforeFix(String codeBlockBeforeFix) {
        this.codeBlockBeforeFix = codeBlockBeforeFix;
    }

    public void setCodeBlockAfterFix(String codeBlockAfterFix) {
        this.codeBlockAfterFix = codeBlockAfterFix;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public void setImageLinkList(List<ImageLinkElement> imageLinkList) {
        this.imageLinkList = imageLinkList;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = String.join(";", referenceLinks);
    }

    @Override
    public String toString() {
        return "ErrorFile [id=" + id + ", heading=" + heading + ", projectName=" + projectName + ", labels=" + labels
                + ", codeBlockBeforeFix=" + codeBlockBeforeFix + ", codeBlockAfterFix=" + codeBlockAfterFix
                + ", problemDescription=" + problemDescription + ", solutionText=" + solutionText + ", imageLinkList="
                + imageLinkList + ", referenceLinks=" + referenceLinks + "]";
    }

    public int getId() {
        return id;
    }

    public String getHeading() {
        return heading;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<String> getLabels() {
        return Arrays.asList(labels.split(","));
    }

    public String getCodeBlockBeforeFix() {
        return codeBlockBeforeFix;
    }

    public String getCodeBlockAfterFix() {
        return codeBlockAfterFix;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public String getSolutionText() {
        return solutionText;
    }

    public List<ImageLinkElement> getImageLinkList() {
        return imageLinkList;
    }

    public List<String> getReferenceLinks() {
        return Arrays.asList(referenceLinks.split(";"));
    }

}
