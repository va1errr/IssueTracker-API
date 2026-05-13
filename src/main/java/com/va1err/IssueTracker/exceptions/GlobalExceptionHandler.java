package com.va1err.IssueTracker.exceptions;

import com.va1err.IssueTracker.dto.responses.ErrorResponse;
import com.va1err.IssueTracker.utils.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(
                (err) -> errors.put(err.getField(), err.getDefaultMessage())
        );

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.BAD_REQUEST, errors, "Validation failed"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotValid(HttpMessageNotReadableException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", e.getMessage());

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.BAD_REQUEST, errors, "Invalid values"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", e.getMessage());

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.NOT_FOUND, errors, "Not Found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(ProjectNotFoundException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", e.getMessage());

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.NOT_FOUND, errors, "Not Found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IssueNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(IssueNotFoundException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", e.getMessage());

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.NOT_FOUND, errors, "Not Found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(InvalidPasswordException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", e.getMessage());

        ErrorResponse response = ApiResponseUtil.fail(
                HttpStatus.BAD_REQUEST, errors, "Invalid"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
