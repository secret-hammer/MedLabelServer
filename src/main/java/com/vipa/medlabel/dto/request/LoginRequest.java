package com.vipa.medlabel.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {
    @NotNull(message = "Username or email cannot be null")
    private String usernameOrEmail;

    @NotNull(message = "Password cannot be null")
    private String password;
}
