package com.example.recruitermatching.exception;

/**
 * Thrown when a requested Candidate does not exist in the database.
 * Caught by GlobalExceptionHandler and converted to HTTP 404.
 */
public class CandidateNotFoundException extends RuntimeException {

    private final Long candidateId;

    public CandidateNotFoundException(Long candidateId) {
        super("Candidate not found with id: " + candidateId);
        this.candidateId = candidateId;
    }

    public Long getCandidateId() {
        return candidateId;
    }
}
