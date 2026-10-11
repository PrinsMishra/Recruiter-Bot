package com.example.recruitermatching.controller;

import com.example.recruitermatching.dto.CandidateMatchResponse;
import com.example.recruitermatching.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JobController: Handles HTTP requests related to jobs.
 *
 * Responsibilities:
 *   - Parse the path parameter {id}
 *   - Delegate to MatchingService
 *   - Return HTTP response
 *
 * No business logic here. Controller stays thin.
 */
@RestController
@RequestMapping("/jobs")
public class JobController {

    private final MatchingService matchingService;

    public JobController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    /**
     * GET /jobs/{id}/matches
     *
     * Returns all candidates ranked by their compatibility score for the given job.
     * Candidates are sorted from best match (highest score) to worst match.
     *
     * @param id  the job ID
     * @return    200 OK with sorted list of CandidateMatchResponse
     *            404 NOT FOUND if the job does not exist (handled by GlobalExceptionHandler)
     */
    @GetMapping("/{id}/matches")
    public ResponseEntity<List<CandidateMatchResponse>> getCandidateMatchesForJob(
            @PathVariable("id") Long id
    ) {
        List<CandidateMatchResponse> matches = matchingService.findCandidatesForJob(id);
        return ResponseEntity.ok(matches);
    }
}
