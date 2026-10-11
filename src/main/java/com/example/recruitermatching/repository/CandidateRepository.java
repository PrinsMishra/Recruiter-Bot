package com.example.recruitermatching.repository;

import com.example.recruitermatching.model.Candidate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CandidateRepository: All database access for the 'candidates' and 'candidate_skills' tables.
 * Uses handwritten parameterized SQL via JdbcTemplate only.
 * No ORM, no Spring Data JPA, no query builders.
 */
@Repository
public class CandidateRepository {

    private final JdbcTemplate jdbcTemplate;

    public CandidateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─────────────────────────────────────────────────────────────
    // Row Mapper
    // ─────────────────────────────────────────────────────────────

    private final RowMapper<Candidate> CANDIDATE_ROW_MAPPER = (rs, rowNum) -> {
        Candidate candidate = new Candidate();
        candidate.setId(rs.getLong("id"));
        candidate.setName(rs.getString("name"));
        candidate.setEmail(rs.getString("email"));
        candidate.setExperienceYears(rs.getDouble("experience_years"));
        return candidate;
    };

    // ─────────────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────────────

    /**
     * Find a single candidate by primary key.
     * Returns empty Optional if not found.
     */
    public Optional<Candidate> findById(Long candidateId) {
        String sql = """
                SELECT
                    c.id,
                    c.name,
                    c.email,
                    c.experience_years
                FROM candidates c
                WHERE c.id = ?
                """;
        try {
            Candidate candidate = jdbcTemplate.queryForObject(sql, CANDIDATE_ROW_MAPPER, candidateId);
            return Optional.ofNullable(candidate);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find all candidates in the system.
     */
    public List<Candidate> findAll() {
        String sql = """
                SELECT
                    c.id,
                    c.name,
                    c.email,
                    c.experience_years
                FROM candidates c
                ORDER BY c.id ASC
                """;
        return jdbcTemplate.query(sql, CANDIDATE_ROW_MAPPER);
    }

    /**
     * Returns the list of normalized skill names a candidate possesses.
     * Skills are stored lowercase in the DB — consistent with skill normalization.
     */
    public List<String> findSkillsByCandidateId(Long candidateId) {
        String sql = """
                SELECT
                    s.name
                FROM skills s
                INNER JOIN candidate_skills cs ON s.id = cs.skill_id
                WHERE cs.candidate_id = ?
                ORDER BY s.name ASC
                """;
        return jdbcTemplate.queryForList(sql, String.class, candidateId);
    }

    /**
     * Loads a candidate with their full skill list.
     * Delegates to findById + findSkillsByCandidateId.
     */
    public Optional<Candidate> findByIdWithSkills(Long candidateId) {
        Optional<Candidate> candidateOpt = findById(candidateId);
        candidateOpt.ifPresent(candidate -> {
            List<String> skills = findSkillsByCandidateId(candidateId);
            candidate.setSkills(skills);
        });
        return candidateOpt;
    }

    /**
     * Loads all candidates with their full skill lists.
     * Skills fetched per candidate to keep SQL simple and readable.
     */
    public List<Candidate> findAllWithSkills() {
        List<Candidate> candidates = findAll();
        for (Candidate candidate : candidates) {
            List<String> skills = findSkillsByCandidateId(candidate.getId());
            candidate.setSkills(skills);
        }
        return candidates;
    }
}
