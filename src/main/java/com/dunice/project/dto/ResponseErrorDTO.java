package com.dunice.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseErrorDTO {
    private String message;
    private List<Integer> codes;
    @Builder.Default
    private int statusCode = 1;
    @Builder.Default
    private boolean success = true;
    private String timeStamp;
}