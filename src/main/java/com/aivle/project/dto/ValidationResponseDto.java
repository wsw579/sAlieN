package com.aivle.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResponseDto {
    // Getter
    private String message;

    public ValidationResponseDto(String message) {
        this.message = message;
    }

}

