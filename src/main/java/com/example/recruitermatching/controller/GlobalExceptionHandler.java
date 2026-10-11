package com.example.recruitermatching.controller;

import com.example.recruitermatching.dto.ErrorResponse;
import com.example.recruitermatching.exception.CandidateNotFoundException;
import com.example.recruitermatching.exception.JobNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler: Centralized exception-to-HTTP response mapping.
 *
 * Catches domain exceptions thrown by the Service layer and converts them
 * to structured JSON error responses. Keeps controllers clean.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles JobNotFoundException → HTTP 404
     *
     * Example response:
     * {
     *   "error": "Job not found",
     *   "resourceId": 999
     * }
     */
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJobNotFound(JobNotFoundException ex) {
        ErrorResponse body = new ErrorResponse("Job not found", ex.getJobId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles CandidateNotFoundException → HTTP 404
     *
     * Example response:
     * {
     *   "error": "Candidate not found",
     *   "resourceId": 999
     * }
     */
    @ExceptionHandler(CandidateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCandidateNotFound(CandidateNotFoundException ex) {
        ErrorResponse body = new ErrorResponse("Candidate not found", ex.getCandidateId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Catch-all for unexpected runtime exceptions → HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse body = new ErrorResponse("Internal server error: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
