package com.opdinna.error_vault.backend.model.mapper;

import com.opdinna.error_vault.backend.model.domain.ErrorFile;
import com.opdinna.error_vault.backend.model.dto.ErrorFileDTO;

public class ErrorFileDTOMapper {
    public static ErrorFileDTO toDto(ErrorFile error) {
        return new ErrorFileDTO(error.getHeading(), error.getProjectName(), error.getProblemDescription(),
                error.getSolutionText(), error.getCodeBlockBeforeFix(), error.getCodeBlockAfterFix(),
                error.getReferenceLinks(), error.getImageLinkList(), error.getLabels());
    }

    public static ErrorFile toErrorFile(ErrorFileDTO error) {
        return new ErrorFile(error.getTitle(), error.getProjectTitle(), error.getLabels(), error.getCodeBeforeFix(),
                error.getCodeAfterFix(), error.getProblemDescription(), error.getSolutionDescription(),
                error.getImageList(), error.getReferenceLinks());
    }
}
