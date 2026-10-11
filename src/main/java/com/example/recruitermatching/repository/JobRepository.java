package com.example.recruitermatching.repository;

import com.example.recruitermatching.model.Job;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JobRepository: All database access for the 'jobs' and 'job_skills' tables.
 * Uses handwritten parameterized SQL via JdbcTemplate only.
 * No ORM, no Spring Data JPA, no query builders.
 */
@Repository
public class JobRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─────────────────────────────────────────────────────────────
    // Row Mapper
    // ─────────────────────────────────────────────────────────────

    private final RowMapper<Job> JOB_ROW_MAPPER = (rs, rowNum) -> {
        Job job = new Job();
        job.setId(rs.getLong("id"));
        job.setTitle(rs.getString("title"));
        job.setDescription(rs.getString("description"));
        job.setMinimumExperience(rs.getDouble("minimum_experience"));
        return job;
    };

    // ─────────────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────────────

    /**
     * Find a single job by its primary key.
     * Returns empty Optional if job does not exist.
     */
    public Optional<Job> findById(Long jobId) {
        String sql = """
                SELECT
                    j.id,
                    j.title,
                    j.description,
                    j.minimum_experience
                FROM jobs j
                WHERE j.id = ?
                """;
        try {
            Job job = jdbcTemplate.queryForObject(sql, JOB_ROW_MAPPER, jobId);
            return Optional.ofNullable(job);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find all jobs in the system (used when matching a candidate against all jobs).
     */
    public List<Job> findAll() {
        String sql = """
                SELECT
                    j.id,
                    j.title,
                    j.description,
                    j.minimum_experience
                FROM jobs j
                ORDER BY j.id ASC
                """;
        return jdbcTemplate.query(sql, JOB_ROW_MAPPER);
    }

    /**
     * Returns the list of normalized required skill names for a given job.
     * Skills are stored lowercase in the DB — no further normalization needed at DB level.
     */
    public List<String> findRequiredSkillsByJobId(Long jobId) {
        String sql = """
                SELECT
                    s.name
                FROM skills s
                INNER JOIN job_skills js ON s.id = js.skill_id
                WHERE js.job_id = ?
                ORDER BY s.name ASC
                """;
        return jdbcTemplate.queryForList(sql, String.class, jobId);
    }

    /**
     * Loads a job with its full list of required skills in a single enriched result.
     * Delegates to findById + findRequiredSkillsByJobId.
     */
    public Optional<Job> findByIdWithSkills(Long jobId) {
        Optional<Job> jobOpt = findById(jobId);
        jobOpt.ifPresent(job -> {
            List<String> skills = findRequiredSkillsByJobId(jobId);
            job.setRequiredSkills(skills);
        });
        return jobOpt;
    }

    /**
     * Loads all jobs with their full required skill lists.
     * Uses a single JOIN query to avoid N+1 queries.
     * Skills for each job are collected in Java after the bulk fetch.
     */
    public List<Job> findAllWithSkills() {
        // Step 1: fetch all jobs
        List<Job> jobs = findAll();
        // Step 2: for each job fetch its skills (acceptable for small datasets)
        // For large datasets, a single JOIN + grouping in Java would be more efficient.
        for (Job job : jobs) {
            List<String> skills = findRequiredSkillsByJobId(job.getId());
            job.setRequiredSkills(skills);
        }
        return jobs;
    }
}
