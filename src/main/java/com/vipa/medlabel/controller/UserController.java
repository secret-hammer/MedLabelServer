package com.vipa.medlabel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vipa.medlabel.dto.request.LoginRequest;
import com.vipa.medlabel.dto.request.RegisterRequest;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.service.AuthService;
import com.vipa.medlabel.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private AuthService authService;
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseResult<Object>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        userService.register(registerRequest);
        ResponseResult<Object> response = new ResponseResult<>(200, "User registered successfully");

        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseResult<String>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);

        ResponseResult<String> response = new ResponseResult<>(200, "User logged in successfully", token);

        return ResponseEntity.ok(response);
    }
}