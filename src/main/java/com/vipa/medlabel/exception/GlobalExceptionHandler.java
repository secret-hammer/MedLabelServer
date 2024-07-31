package com.vipa.medlabel.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.vipa.medlabel.dto.response.ResponseResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ResponseResult<Object>> handleCustomException(CustomException ex, WebRequest request) {
                ResponseResult<Object> errorResponse = new ResponseResult<>(ex.getErrorCode().getCode(),
                                ex.getErrorCode().getMessage());

                log.error("An CustomException occurred: {},{}", ex.getErrorCode().getCode(),
                                ex.getErrorCode().getMessage(),
                                ex);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ResponseResult<Object>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException e, WebRequest request) {
                // 获取所有的验证错误
                List<ObjectError> errors = e.getBindingResult().getAllErrors();
                // 将错误信息拼接成一个字符串
                String errorMessage = errors.stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.joining(", "));

                ResponseResult<Object> errorResponse = new ResponseResult<>(CustomError.ARGUMENT_NOT_VALID.getCode(),
                                errorMessage);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ResponseResult<Object>> handleAllExceptions(Exception ex, WebRequest request) {
                ResponseResult<Object> errorResponse = new ResponseResult<>(CustomError.INTERNAL_SERVER_ERROR.getCode(),
                                ex.getMessage());

                log.error("An Exception occurred", ex);

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
