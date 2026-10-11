package com.example.recruitermatching.exception;

/**
 * Thrown when a requested Job does not exist in the database.
 * Caught by GlobalExceptionHandler and converted to HTTP 404.
 */
public class JobNotFoundException extends RuntimeException {

    private final Long jobId;

    public JobNotFoundException(Long jobId) {
        super("Job not found with id: " + jobId);
        this.jobId = jobId;
    }

    public Long getJobId() {
        return jobId;
    }
}
