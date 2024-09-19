package com.vipa.medlabel.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginUserInfo {
    private String username;
    private String email;
    private String phone;
    private String profileLink;
    private String token;
}
