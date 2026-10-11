package com.example.recruitermatching.matcher;

import com.example.recruitermatching.model.Candidate;
import com.example.recruitermatching.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for MatchingAlgorithm.
 *
 * Tests the core matching logic in complete isolation — no Spring context,
 * no database, no HTTP. Pure JUnit 5.
 *
 * Test Cases covered:
 *   1. Perfect match          → score = 100
 *   2. Partial skill match    → 4/5 skills, full experience → 86
 *   3. Below experience       → all skills, 3/4 years → 92.5
 *   4. No matching skills     → score = 30 (only experience contributes)
 *   5. Exceeds experience     → candidate 10y, required 3y → capped at 100
 *   6. No required experience → defaults safely to 100 experience score
 *   7. No required skills     → defaults safely to 100 skill score
 *   8. Skill alias matching   → "JS" normalized to "javascript"
 */
class MatchingAlgorithmTest {

    private MatchingAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new MatchingAlgorithm(new SkillNormalizer());
    }

    // ─────────────────────────────────────────────────────────────
    // Helper builders
    // ─────────────────────────────────────────────────────────────

    private Candidate candidate(double exp, String... skills) {
        Candidate c = new Candidate(1L, "Test Candidate", "test@test.com", exp);
        c.setSkills(Arrays.asList(skills));
        return c;
    }

    private Job job(double minExp, String... requiredSkills) {
        Job j = new Job(1L, "Test Job", "Test description", minExp);
        j.setRequiredSkills(Arrays.asList(requiredSkills));
        return j;
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 1 — Perfect match
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 1: Perfect match — all skills match, experience meets requirement → score = 100")
    void testPerfectMatch() {
        Candidate c = candidate(4.0, "java", "spring boot", "sql", "docker", "git");
        Job j = job(4.0, "java", "spring boot", "sql", "docker", "git");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getSkillScore(),      0.01, "Skill score should be 100");
        assertEquals(100.0, result.getExperienceScore(), 0.01, "Experience score should be 100");
        assertEquals(100.0, result.getFinalScore(),      0.01, "Final score should be 100");
        assertTrue(result.getReason().contains("all"), "Reason should mention matching all skills");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 2 — Partial skill match
    // 4/5 skills matched, experience fully met
    // skillScore = 4/5 * 100 = 80
    // expScore   = 100
    // final      = 0.7*80 + 0.3*100 = 56 + 30 = 86
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 2: 4/5 skills matched, full experience → score = 86")
    void testPartialSkillMatch() {
        Candidate c = candidate(5.0, "java", "spring boot", "sql", "docker", "react");
        Job j = job(4.0, "java", "spring boot", "sql", "docker", "kafka");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(80.0, result.getSkillScore(),      0.01, "Skill score should be 80");
        assertEquals(100.0, result.getExperienceScore(), 0.01, "Experience score should be 100");
        assertEquals(86.0, result.getFinalScore(),       0.01, "Final score should be 86");
        assertEquals(4, result.getMatchedSkills(), "Should match 4 skills");
        assertEquals(5, result.getTotalRequiredSkills(), "Total required should be 5");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 3 — Experience below requirement
    // All skills match, but candidate has 3 of 4 required years
    // skillScore = 100
    // expScore   = 3/4 * 100 = 75
    // final      = 0.7*100 + 0.3*75 = 70 + 22.5 = 92.5
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 3: All skills match, experience 3/4 years → score = 92.5")
    void testExperienceBelowRequirement() {
        Candidate c = candidate(3.0, "java", "sql", "docker", "git");
        Job j = job(4.0, "java", "sql", "docker", "git");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getSkillScore(),      0.01, "Skill score should be 100");
        assertEquals(75.0,  result.getExperienceScore(), 0.01, "Experience score should be 75");
        assertEquals(92.5,  result.getFinalScore(),      0.01, "Final score should be 92.5");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 4 — No matching skills
    // skillScore = 0
    // expScore   = 100 (meets experience)
    // final      = 0.7*0 + 0.3*100 = 30
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 4: No skills matched, experience met → score = 30")
    void testNoMatchingSkills() {
        Candidate c = candidate(5.0, "python", "react", "css");
        Job j = job(4.0, "java", "spring boot", "sql", "kafka");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(0.0,  result.getSkillScore(),      0.01, "Skill score should be 0");
        assertEquals(100.0, result.getExperienceScore(), 0.01, "Experience score should be 100");
        assertEquals(30.0,  result.getFinalScore(),      0.01, "Final score should be 30");
        assertEquals(0, result.getMatchedSkills(), "No skills should match");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 5 — Candidate greatly exceeds experience
    // Candidate: 10 years, Required: 3 years
    // experienceScore = min(10/3, 1.0) * 100 = 100 (capped)
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 5: Candidate vastly exceeds experience requirement → experience score capped at 100")
    void testExperienceExceeded() {
        Candidate c = candidate(10.0, "java", "sql");
        Job j = job(3.0, "java", "sql");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getExperienceScore(), 0.01, "Experience score should be capped at 100");
        assertEquals(100.0, result.getFinalScore(),      0.01, "Final score should be 100");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 6 — No minimum experience required
    // Job minimum_experience = 0 → experience score defaults to 100
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 6: Job has no minimum experience → experience score = 100 (no division by zero)")
    void testNoMinimumExperience() {
        Candidate c = candidate(0.0, "java");
        Job j = job(0.0, "java");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getExperienceScore(), 0.01, "Experience score should be 100 when no minimum");
        assertFalse(result.getReason().isEmpty(), "Reason should not be empty");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 7 — No required skills
    // Job has 0 required skills → skill score defaults to 100
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 7: Job has no required skills → skill score = 100 safely")
    void testNoRequiredSkills() {
        Candidate c = candidate(3.0, "java", "sql");
        Job j = job(3.0); // No required skills

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getSkillScore(), 0.01, "Skill score should be 100 when no requirements");
        assertEquals(0, result.getTotalRequiredSkills(), "Total required skills should be 0");
    }

    // ─────────────────────────────────────────────────────────────
    // Test Case 8 — Skill alias normalization
    // Candidate has "JS", job requires "javascript" → should match
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Case 8: Skill aliases normalized correctly — 'JS' matches 'javascript'")
    void testSkillAliasNormalization() {
        Candidate c = candidate(2.0, "JS", "react", "html");
        Job j = job(2.0, "javascript", "react", "html");

        MatchingAlgorithm.MatchResult result = algorithm.compute(c, j);

        assertEquals(100.0, result.getSkillScore(), 0.01, "'JS' should normalize and match 'javascript'");
        assertEquals(3, result.getMatchedSkills(), "All 3 skills should match after normalization");
    }

    // ─────────────────────────────────────────────────────────────
    // Score component unit tests
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("computeSkillScore: 4/6 = 66.7")
    void testComputeSkillScore() {
        assertEquals(66.7, algorithm.computeSkillScore(4, 6), 0.05);
    }

    @Test
    @DisplayName("computeSkillScore: 0 total skills = 100 (safe default)")
    void testComputeSkillScoreZeroTotal() {
        assertEquals(100.0, algorithm.computeSkillScore(0, 0), 0.01);
    }

    @Test
    @DisplayName("computeExperienceScore: candidate 2 years, required 4 years = 50")
    void testComputeExperienceScore() {
        assertEquals(50.0, algorithm.computeExperienceScore(2.0, 4.0), 0.01);
    }

    @Test
    @DisplayName("computeExperienceScore: candidate exceeds required → capped at 100")
    void testComputeExperienceScoreCapped() {
        assertEquals(100.0, algorithm.computeExperienceScore(8.0, 4.0), 0.01);
    }

    @Test
    @DisplayName("computeExperienceScore: no requirement (0 years) → 100")
    void testComputeExperienceScoreZeroRequired() {
        assertEquals(100.0, algorithm.computeExperienceScore(0.0, 0.0), 0.01);
    }

    // ─────────────────────────────────────────────────────────────
    // Assignment Official Scenario Tests: Sherlock Holmes & Michael Scott
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Assignment Scenario: Sherlock H. matches Backend Detective with 100% score")
    void testSherlockHolmesBackendDetective() {
        // Sherlock H.: deduction, pattern-recognition, forensics | Exp: 8 yrs
        Candidate sherlock = candidate(8.0, "deduction", "pattern-recognition", "forensics");
        // Backend Detective: deduction, pattern-recognition, forensics | Min Exp: 3 yrs
        Job backendDetective = job(3.0, "deduction", "pattern-recognition", "forensics");

        MatchingAlgorithm.MatchResult result = algorithm.compute(sherlock, backendDetective);

        assertEquals(100.0, result.getSkillScore(), 0.01);
        assertEquals(100.0, result.getExperienceScore(), 0.01);
        assertEquals(100.0, result.getFinalScore(), 0.01);
        assertEquals(3, result.getMatchedSkills());
        assertTrue(result.getReason().contains("Matched all 3 required skills"));
    }

    @Test
    @DisplayName("Assignment Scenario: Michael Scott matches Sales Engineer")
    void testMichaelScottSalesEngineer() {
        // Michael S.: sales, public-speaking, team-building | Exp: 11 yrs
        Candidate michael = candidate(11.0, "sales", "public-speaking", "team-building");
        // Sales Engineer: sales, public-speaking, negotiation | Min Exp: 3 yrs
        Job salesEngineer = job(3.0, "sales", "public-speaking", "negotiation");

        MatchingAlgorithm.MatchResult result = algorithm.compute(michael, salesEngineer);

        // 2 of 3 skills matched: sales, public-speaking -> 66.7%
        assertEquals(66.7, result.getSkillScore(), 0.05);
        // Experience: 11 >= 3 -> 100.0%
        assertEquals(100.0, result.getExperienceScore(), 0.01);
        // Final score: (0.7 * 66.7) + (0.3 * 100) = 46.69 + 30 = 76.7
        assertEquals(76.7, result.getFinalScore(), 0.1);
        assertEquals(2, result.getMatchedSkills());
    }
}

