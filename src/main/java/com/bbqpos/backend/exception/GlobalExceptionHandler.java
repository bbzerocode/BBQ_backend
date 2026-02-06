package com.bbqpos.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        System.out.println("=== GlobalExceptionHandler caught exception ===");
        System.out.println("Exception type: " + e.getClass().getSimpleName());
        System.out.println("Exception message: " + e.getMessage());
        
        if (e.getCause() != null) {
            System.out.println("Cause: " + e.getCause().getMessage());
        }
        
        System.out.println("Stack trace:");
        e.printStackTrace();
        
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setType(e.getClass().getSimpleName());
        
        if (e.getCause() != null) {
            error.setCause(e.getCause().getMessage());
        }
        
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            error.setLocation(stackTrace[0].toString());
        }
        
        System.out.println("=== Returning error response ===");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}