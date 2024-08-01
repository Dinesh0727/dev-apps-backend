package com.opdinna.error_vault.backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "errors")
@NoArgsConstructor
@Getter
public class ErrorFile {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "heading")
    private String heading;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "labels")
    private List<String> labels;

    @Column(name = "code_block_before_fix")
    private String codeBlockBeforeFix;

    @Column(name = "code_block_after_fix")
    private String codeBlockAfterFix;

    @Column(name = "problem_description")
    private String problemDescription;

    @Column(name = "solution_text")
    private String solutionText;

    @Column(name = "image_link_list")
    private List<String> imageLinkList;

    @Column(name = "reference_links")
    private List<String> referenceLinks;

    public ErrorFile(String heading, String projectName, List<String> labels, String codeBlockBeforeFix,
            String codeBlockAfterFix, String problemDescription, String solutionText, List<String> imageLinkList,
            List<String> referenceLinks) {
        this.heading = heading;
        this.projectName = projectName;
        this.labels = labels;
        this.codeBlockBeforeFix = codeBlockBeforeFix;
        this.codeBlockAfterFix = codeBlockAfterFix;
        this.problemDescription = problemDescription;
        this.solutionText = solutionText;
        this.imageLinkList = imageLinkList;
        this.referenceLinks = referenceLinks;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
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

    public void setImageLinkList(List<String> imageLinkList) {
        this.imageLinkList = imageLinkList;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

}
