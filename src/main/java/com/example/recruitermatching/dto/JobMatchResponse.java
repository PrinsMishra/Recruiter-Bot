package com.example.recruitermatching.dto;

/**
 * Response DTO for GET /candidates/{id}/matches
 * Represents a job ranked by its match score against a candidate.
 */
public class JobMatchResponse {

    private Long jobId;
    private String jobTitle;
    private double score;
    private String reason;

    public JobMatchResponse() {}

    public JobMatchResponse(Long jobId, String jobTitle, double score, String reason) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.score = score;
        this.reason = reason;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
