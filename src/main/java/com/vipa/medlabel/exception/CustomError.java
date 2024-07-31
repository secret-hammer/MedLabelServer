package com.vipa.medlabel.exception;

public enum CustomError {
    INVALID_AUTHENTIFICATION_ERROR(700, "Invalid Authorization header"),
    USERNAME_NOT_FOUND_ERROR(701, "User not exists by Username or Email"),
    USERNAME_ALREADY_EXISTS(702, "Username already exists"),
    EMAIL_ALREADY_EXISTS(703, "Email already exists"),
    INVALID_JWT_TOKEN(704, "Invalid JWT token"),
    JWT_TOKEN_EXPIRED(705, "JWT token is expired"),
    UNSUPPORTED_JWT_TOKEN(706, "Unsupported JWT token"),
    EMPTY_JWT_CLAIM(707, "Empty JWT claim"),
    UNAUTHORIZED_REQUEST(708, "Unauthorized request"),

    ARGUMENT_NOT_VALID(709, "Argument not valid"),
    INTERNAL_SERVER_ERROR(800, "Internal server error");

    private final String message;
    private final Integer code;

    CustomError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
