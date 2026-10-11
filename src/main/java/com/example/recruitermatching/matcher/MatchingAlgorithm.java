package com.example.recruitermatching.matcher;

import com.example.recruitermatching.model.Candidate;
import com.example.recruitermatching.model.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MatchingAlgorithm: Core business logic for computing candidate-job compatibility.
 *
 * This is a deterministic, rule-based weighted scoring algorithm.
 * No ML, no LLM, no vector databases — purely explainable math.
 *
 * ─────────────────────────────────────────────────
 * SCORING FORMULA:
 *
 *   Skill Score (0–100):
 *     = (matched required skills / total required skills) × 100
 *     → Measures required-skill coverage (NOT Jaccard — extra skills don't penalize)
 *     → If no skills required, defaults to 100.
 *
 *   Experience Score (0–100):
 *     = min(candidateExperience / requiredExperience, 1.0) × 100
 *     → Capped at 100 — exceeding minimum does not inflate score
 *     → If no experience required (0), defaults to 100.
 *
 *   Final Score:
 *     = (0.70 × skillScore) + (0.30 × experienceScore)
 *     → Skill overlap is the primary signal (70%)
 *     → Experience fit is secondary (30%)
 *
 * Scores are rounded to 1 decimal place.
 * ─────────────────────────────────────────────────
 */
@Component
public class MatchingAlgorithm {

    private static final double SKILL_WEIGHT      = 0.70;
    private static final double EXPERIENCE_WEIGHT = 0.30;

    private final SkillNormalizer skillNormalizer;

    public MatchingAlgorithm(SkillNormalizer skillNormalizer) {
        this.skillNormalizer = skillNormalizer;
    }

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Computes the final match score between a candidate and a job.
     *
     * @param candidate  the candidate to evaluate
     * @param job        the job to match against
     * @return           MatchResult containing score, skill breakdown, and reason
     */
    public MatchResult compute(Candidate candidate, Job job) {

        // Normalize all skill sets before comparison
        Set<String> normalizedCandidateSkills = normalizeAll(candidate.getSkills());
        Set<String> normalizedRequiredSkills  = normalizeAll(job.getRequiredSkills());

        // Find matched skills (intersection)
        Set<String> matchedSkills = normalizedCandidateSkills.stream()
                .filter(normalizedRequiredSkills::contains)
                .collect(Collectors.toSet());

        int totalRequired = normalizedRequiredSkills.size();
        int totalMatched  = matchedSkills.size();

        // --- Skill Score ---
        double skillScore = computeSkillScore(totalMatched, totalRequired);

        // --- Experience Score ---
        double experienceScore = computeExperienceScore(
                candidate.getExperienceYears(),
                job.getMinimumExperience()
        );

        // --- Final Weighted Score ---
        double finalScore = round1dp((SKILL_WEIGHT * skillScore) + (EXPERIENCE_WEIGHT * experienceScore));

        // --- Human-Readable Reason ---
        String reason = buildReason(
                totalMatched, totalRequired,
                candidate.getExperienceYears(), job.getMinimumExperience(),
                skillScore, experienceScore
        );

        return new MatchResult(finalScore, skillScore, experienceScore, totalMatched, totalRequired, reason);
    }

    // ─────────────────────────────────────────────────────────────
    // Score Calculations (package-visible for unit testing)
    // ─────────────────────────────────────────────────────────────

    /**
     * Required-skill coverage score.
     * Extra candidate skills do NOT reduce the score.
     *
     * @param matched  number of matched required skills
     * @param total    total number of required skills in the job
     * @return         skill score 0–100
     */
    double computeSkillScore(int matched, int total) {
        if (total == 0) {
            return 100.0; // No requirements → any candidate qualifies fully on skills
        }
        return round1dp(((double) matched / total) * 100.0);
    }

    /**
     * Capped experience ratio score.
     * Candidate meeting or exceeding the minimum scores 100.
     * Candidate below minimum scores proportionally less.
     *
     * @param candidateExp  candidate's years of experience
     * @param requiredExp   job's minimum required experience
     * @return              experience score 0–100
     */
    double computeExperienceScore(double candidateExp, double requiredExp) {
        if (requiredExp <= 0) {
            return 100.0; // No experience requirement → any candidate qualifies fully
        }
        double ratio = candidateExp / requiredExp;
        return round1dp(Math.min(ratio, 1.0) * 100.0);
    }

    // ─────────────────────────────────────────────────────────────
    // Reason Generation
    // ─────────────────────────────────────────────────────────────

    /**
     * Builds a short, deterministic human-readable explanation of the score.
     * Generated entirely from the raw scoring data — no LLM involved.
     */
    private String buildReason(
            int matched, int total,
            double candidateExp, double requiredExp,
            double skillScore, double experienceScore
    ) {
        StringBuilder sb = new StringBuilder();

        // --- Skill explanation ---
        if (total == 0) {
            sb.append("No specific skills were required for this job.");
        } else if (matched == total) {
            sb.append("Matched all ").append(total).append(" required skill").append(total == 1 ? "" : "s").append(".");
        } else if (matched == 0) {
            sb.append("Matched none of the ").append(total).append(" required skill").append(total == 1 ? "" : "s").append(".");
        } else {
            sb.append("Matched ").append(matched).append(" of ").append(total)
              .append(" required skill").append(total == 1 ? "" : "s").append(".");
        }

        sb.append(" ");

        // --- Experience explanation ---
        if (requiredExp <= 0) {
            sb.append("No minimum experience is required for this role.");
        } else if (candidateExp >= requiredExp) {
            sb.append("Has ").append(formatExp(candidateExp))
              .append(" of experience, meeting the minimum requirement of ")
              .append(formatExp(requiredExp)).append(".");
        } else {
            sb.append("Has ").append(formatExp(candidateExp))
              .append(" of experience against the minimum requirement of ")
              .append(formatExp(requiredExp)).append(".");
        }

        return sb.toString().trim();
    }

    // ─────────────────────────────────────────────────────────────
    // Utility Methods
    // ─────────────────────────────────────────────────────────────

    private Set<String> normalizeAll(List<String> skills) {
        return skills.stream()
                .map(skillNormalizer::normalize)
                .collect(Collectors.toSet());
    }

    private double round1dp(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String formatExp(double exp) {
        if (exp == Math.floor(exp)) {
            return (int) exp + " year" + ((int) exp == 1 ? "" : "s");
        }
        return exp + " years";
    }

    // ─────────────────────────────────────────────────────────────
    // Result Record
    // ─────────────────────────────────────────────────────────────

    /**
     * Immutable result object returned from compute().
     * Contains the final score, component scores, matched skill count, and reason.
     */
    public static class MatchResult {
        private final double finalScore;
        private final double skillScore;
        private final double experienceScore;
        private final int matchedSkills;
        private final int totalRequiredSkills;
        private final String reason;

        public MatchResult(double finalScore, double skillScore, double experienceScore,
                           int matchedSkills, int totalRequiredSkills, String reason) {
            this.finalScore = finalScore;
            this.skillScore = skillScore;
            this.experienceScore = experienceScore;
            this.matchedSkills = matchedSkills;
            this.totalRequiredSkills = totalRequiredSkills;
            this.reason = reason;
        }

        public double getFinalScore()         { return finalScore; }
        public double getSkillScore()         { return skillScore; }
        public double getExperienceScore()    { return experienceScore; }
        public int getMatchedSkills()         { return matchedSkills; }
        public int getTotalRequiredSkills()   { return totalRequiredSkills; }
        public String getReason()             { return reason; }
    }
}
