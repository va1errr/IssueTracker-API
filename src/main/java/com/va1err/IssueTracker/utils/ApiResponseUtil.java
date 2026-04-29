package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponseUtil {

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static ErrorResponse fail(HttpStatus status, Map<String, String> errors, String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .details(errors)
                .build();
    }

}
