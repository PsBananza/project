package com.dunice.project.config.exeption;

import com.dunice.project.dto.CustomResponseDTO;
import com.dunice.project.dto.ResponseErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;



@ControllerAdvice
public class HandlerException {
    @ExceptionHandler(value = CustomException.class)
    public final ResponseEntity<CustomResponseDTO> handleAccessDeniedException(CustomException customExeption) {
        var errorDetails = new CustomResponseDTO();
        errorDetails.setTimestamp(LocalDateTime
                        .now()
                        .toString())
                .setStatusCode(Integer.parseInt(String.valueOf(customExeption.getCode())))
                .setMessage(String.valueOf(customExeption.getMessage()));

        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> bodyValidationExceptionHandler(MethodArgumentNotValidException ex) {
        String message = ex
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();
        List<Integer> errorCodes = Collections
                .singletonList(ErrorCodes
                        .errorCodes
                        .get(message));

        ResponseErrorDTO responseErrorDTO = ResponseErrorDTO.builder()
                .message(message)
                .codes(errorCodes)
                .statusCode(errorCodes
                        .stream()
                        .findFirst()
                        .get())
                .timeStamp(LocalDateTime
                        .now()
                        .toString())
                .build();

        return ResponseEntity
                .badRequest()
                .body(responseErrorDTO);
    }

}

