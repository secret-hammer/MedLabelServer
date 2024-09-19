package com.vipa.medlabel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vipa.medlabel.dto.request.user.LoginRequest;
import com.vipa.medlabel.dto.request.user.RegisterRequest;
import com.vipa.medlabel.dto.response.ResponseResult;
import com.vipa.medlabel.dto.response.user.LoginUserInfo;
import com.vipa.medlabel.service.user.AuthService;
import com.vipa.medlabel.service.user.UserService;

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
    public ResponseEntity<ResponseResult<LoginUserInfo>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginUserInfo loginUserInfo = authService.login(loginRequest);

        ResponseResult<LoginUserInfo> response = new ResponseResult<>(200, "User logged in successfully", loginUserInfo);

        return ResponseEntity.ok(response);
    }
}