package com.example.recruitermatching.controller;

import com.example.recruitermatching.dto.JobMatchResponse;
import com.example.recruitermatching.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CandidateController: Handles HTTP requests related to candidates.
 *
 * Responsibilities:
 *   - Parse the path parameter {id}
 *   - Delegate to MatchingService
 *   - Return HTTP response
 *
 * No business logic here. Controller stays thin.
 */
@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final MatchingService matchingService;

    public CandidateController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    /**
     * GET /candidates/{id}/matches
     *
     * Returns all jobs ranked by their compatibility score for the given candidate.
     * Jobs are sorted from best match (highest score) to worst match.
     *
     * @param id  the candidate ID
     * @return    200 OK with sorted list of JobMatchResponse
     *            404 NOT FOUND if the candidate does not exist (handled by GlobalExceptionHandler)
     */
    @GetMapping("/{id}/matches")
    public ResponseEntity<List<JobMatchResponse>> getJobMatchesForCandidate(
            @PathVariable("id") Long id
    ) {
        List<JobMatchResponse> matches = matchingService.findJobsForCandidate(id);
        return ResponseEntity.ok(matches);
    }
}
