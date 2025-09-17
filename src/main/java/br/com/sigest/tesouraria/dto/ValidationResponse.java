package br.com.sigest.tesouraria.dto;

import java.util.HashMap;
import java.util.Map;

public class ValidationResponse {

    private boolean success;
    private String message;
    private Map<String, String> fieldErrors;

    // Constructors
    public ValidationResponse() {
        this.fieldErrors = new HashMap<>();
    }

    public ValidationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.fieldErrors = new HashMap<>();
    }

    // Static factory methods
    public static ValidationResponse success(String message) {
        return new ValidationResponse(true, message);
    }

    public static ValidationResponse error(String message) {
        return new ValidationResponse(false, message);
    }

    public static ValidationResponse validationError(String message, Map<String, String> fieldErrors) {
        ValidationResponse response = new ValidationResponse(false, message);
        response.setFieldErrors(fieldErrors);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    public void addFieldError(String field, String error) {
        if (this.fieldErrors == null) {
            this.fieldErrors = new HashMap<>();
        }
        this.fieldErrors.put(field, error);
    }
}