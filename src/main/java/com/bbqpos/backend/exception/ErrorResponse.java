package com.bbqpos.backend.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String type;
    private String cause;
    private String location;
}