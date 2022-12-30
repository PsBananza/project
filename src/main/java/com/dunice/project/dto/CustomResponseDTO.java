package com.dunice.project.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomResponseDTO {
    private String message;
    private int statusCode;
    private String timestamp;

}
