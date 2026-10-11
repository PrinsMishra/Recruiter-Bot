package com.example.recruitermatching.dto;

/**
 * Response DTO for GET /jobs/{id}/matches
 * Represents a candidate ranked by their match score against a job.
 */
public class CandidateMatchResponse {

    private Long candidateId;
    private String candidateName;
    private double score;
    private String reason;

    public CandidateMatchResponse() {}

    public CandidateMatchResponse(Long candidateId, String candidateName, double score, String reason) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.score = score;
        this.reason = reason;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
