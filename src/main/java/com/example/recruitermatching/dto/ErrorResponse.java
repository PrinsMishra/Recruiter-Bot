package com.example.recruitermatching.dto;

/**
 * Standard error response DTO returned for 4xx/5xx errors.
 * Keeps error messages structured and consistent across the API.
 */
public class ErrorResponse {

    private String error;
    private Long resourceId;

    public ErrorResponse() {}

    public ErrorResponse(String error, Long resourceId) {
        this.error = error;
        this.resourceId = resourceId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
