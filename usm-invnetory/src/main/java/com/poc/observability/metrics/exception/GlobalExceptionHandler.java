package com.poc.observability.metrics.exception;

import com.poc.observability.metrics.api.dto.AssetResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<AssetResponse> handleAssetNotFound(AssetNotFoundException ex) {
        log.error("Asset not found exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Asset not found", ex.getMessage());
    }

    @ExceptionHandler(AssetCreationException.class)
    public ResponseEntity<AssetResponse> handleAssetCreation(AssetCreationException ex) {
        log.error("Asset creation exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create asset", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<AssetResponse> handleExternalService(ExternalServiceException ex) {
        log.error("External service exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "External service failed", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AssetResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation error: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AssetResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AssetResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create or retrieve asset", "Unexpected error occurred");
    }

    private ResponseEntity<AssetResponse> buildErrorResponse(HttpStatus status, String message, String detail) {
        AssetResponse response = AssetResponse.builder()
                .status("FAILED")
                .message(message + ": " + detail)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
