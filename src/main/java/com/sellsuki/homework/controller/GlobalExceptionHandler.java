package com.sellsuki.homework.controller;

import com.sellsuki.homework.exceptions.BadRequestException;
import com.sellsuki.homework.exceptions.InternalServerErrorException;
import com.sellsuki.homework.response.ErrorResponse;
import com.sellsuki.homework.response.Response;
import com.sellsuki.homework.types.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // capture all exception in application
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new Response<ErrorResponse>(
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(error -> new ErrorResponse("400", error.getDefaultMessage()))
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    private ResponseEntity<?> badRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorType().getCode(),
                ex.getErrorType().getMessage()
        );

        List<ErrorResponse> errors = new ArrayList<>();
        errors.add(error);

        Response<?> response = new Response<>(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseBody
    private ResponseEntity<?> InternalException(InternalServerErrorException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorType().getCode(),
                ex.getErrorType().getMessage()
        );

        List<ErrorResponse> errors = new ArrayList<>();
        errors.add(error);

        Response<?> response = new Response<>(errors);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Response> defaultExceptionHandler(Exception ex) {
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                ErrorType.INTERNAL_SERVER_ERROR.getCode(),
                ErrorType.INTERNAL_SERVER_ERROR.getMessage()
        );

        List<ErrorResponse> errors = new ArrayList<>();
        errors.add(error);

        Response<?> response = new Response<>(errors);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
