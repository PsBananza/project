package com.dunice.project.controller;

import com.dunice.project.config.exeption.CustomException;
import com.dunice.project.config.exeption.ErrorCodes;
import com.dunice.project.config.logger.ControllerLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    @ControllerLog
    public ResponseEntity<String> authUser() {
        if (true) {
            throw new CustomException(ErrorCodes.MUST_NOT_BE_NULL);
        }
        return ResponseEntity.ok("ok");
    }

}
