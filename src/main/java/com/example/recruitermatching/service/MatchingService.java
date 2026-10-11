package com.example.recruitermatching.service;

import com.example.recruitermatching.dto.CandidateMatchResponse;
import com.example.recruitermatching.dto.JobMatchResponse;
import com.example.recruitermatching.exception.CandidateNotFoundException;
import com.example.recruitermatching.exception.JobNotFoundException;
import com.example.recruitermatching.matcher.MatchingAlgorithm;
import com.example.recruitermatching.matcher.MatchingAlgorithm.MatchResult;
import com.example.recruitermatching.model.Candidate;
import com.example.recruitermatching.model.Job;
import com.example.recruitermatching.repository.CandidateRepository;
import com.example.recruitermatching.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MatchingService: Orchestrates the full matching workflow.
 *
 * Responsibilities:
 *   1. Fetch job or candidate data from repositories (with skills loaded)
 *   2. Fetch the full list of candidates or jobs to match against
 *   3. Run the MatchingAlgorithm for each pair
 *   4. Sort results deterministically (score DESC, then id ASC as tiebreaker)
 *   5. Map internal results to response DTOs
 *
 * All matching business logic lives in MatchingAlgorithm — this service
 * only coordinates the workflow.
 */
@Service
public class MatchingService {

    private final JobRepository       jobRepository;
    private final CandidateRepository candidateRepository;
    private final MatchingAlgorithm   matchingAlgorithm;

    public MatchingService(
            JobRepository jobRepository,
            CandidateRepository candidateRepository,
            MatchingAlgorithm matchingAlgorithm
    ) {
        this.jobRepository       = jobRepository;
        this.candidateRepository = candidateRepository;
        this.matchingAlgorithm   = matchingAlgorithm;
    }

    // ─────────────────────────────────────────────────────────────
    // API 1: GET /jobs/{id}/matches
    // Returns all candidates ranked best-to-worst for a given job.
    // ─────────────────────────────────────────────────────────────

    public List<CandidateMatchResponse> findCandidatesForJob(Long jobId) {

        // 1. Fetch the target job with its required skills — throw 404 if not found
        Job job = jobRepository.findByIdWithSkills(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        // 2. Fetch all candidates with their skill lists
        List<Candidate> allCandidates = candidateRepository.findAllWithSkills();

        // 3. Score every candidate against this job
        // 4. Sort: score DESC, then candidate_id ASC (deterministic tiebreaker)
        // 5. Map to response DTOs
        return allCandidates.stream()
                .map(candidate -> {
                    MatchResult result = matchingAlgorithm.compute(candidate, job);
                    return new CandidateMatchResponse(
                            candidate.getId(),
                            candidate.getName(),
                            result.getFinalScore(),
                            result.getReason()
                    );
                })
                .sorted(
                    Comparator.comparingDouble(CandidateMatchResponse::getScore).reversed()
                              .thenComparingLong(CandidateMatchResponse::getCandidateId)
                )
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // API 2: GET /candidates/{id}/matches
    // Returns all jobs ranked best-to-worst for a given candidate.
    // ─────────────────────────────────────────────────────────────

    public List<JobMatchResponse> findJobsForCandidate(Long candidateId) {

        // 1. Fetch the target candidate with their skills — throw 404 if not found
        Candidate candidate = candidateRepository.findByIdWithSkills(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException(candidateId));

        // 2. Fetch all jobs with their required skill lists
        List<Job> allJobs = jobRepository.findAllWithSkills();

        // 3. Score every job against this candidate
        // 4. Sort: score DESC, then job_id ASC (deterministic tiebreaker)
        // 5. Map to response DTOs
        return allJobs.stream()
                .map(job -> {
                    MatchResult result = matchingAlgorithm.compute(candidate, job);
                    return new JobMatchResponse(
                            job.getId(),
                            job.getTitle(),
                            result.getFinalScore(),
                            result.getReason()
                    );
                })
                .sorted(
                    Comparator.comparingDouble(JobMatchResponse::getScore).reversed()
                              .thenComparingLong(JobMatchResponse::getJobId)
                )
                .collect(Collectors.toList());
    }
}
