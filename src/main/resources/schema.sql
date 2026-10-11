-- ============================================================
-- SCHEMA: Recruiter Matching Backend
-- All DDL is handwritten raw SQL (no ORM/Hibernate generation)
-- Database: PostgreSQL (RF_labs)
-- ============================================================

-- Drop existing tables in reverse dependency order (CASCADE handles indexes/constraints)
DROP TABLE IF EXISTS candidate_skills CASCADE;
DROP TABLE IF EXISTS job_skills CASCADE;
DROP TABLE IF EXISTS candidates CASCADE;
DROP TABLE IF EXISTS jobs CASCADE;
DROP TABLE IF EXISTS skills CASCADE;

-- ============================================================
-- TABLE: skills
-- Normalized skill names (lowercase) stored here once.
-- Both candidates and jobs reference this table.
-- ============================================================
CREATE TABLE skills (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_skills_name UNIQUE (name)
);

CREATE INDEX idx_skills_name ON skills(name);

-- ============================================================
-- TABLE: jobs
-- A job posting with a title, description, and experience req.
-- ============================================================
CREATE TABLE jobs (
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    description        TEXT,
    minimum_experience NUMERIC(4, 1) NOT NULL DEFAULT 0
);

-- ============================================================
-- TABLE: job_skills
-- Many-to-many join: a job requires many skills.
-- ============================================================
CREATE TABLE job_skills (
    job_id   BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    CONSTRAINT pk_job_skills PRIMARY KEY (job_id, skill_id),
    CONSTRAINT fk_job_skills_job   FOREIGN KEY (job_id)   REFERENCES jobs(id)   ON DELETE CASCADE,
    CONSTRAINT fk_job_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE INDEX idx_job_skills_job_id   ON job_skills(job_id);
CREATE INDEX idx_job_skills_skill_id ON job_skills(skill_id);

-- ============================================================
-- TABLE: candidates
-- A candidate with name, email, and years of experience.
-- ============================================================
CREATE TABLE candidates (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    experience_years NUMERIC(4, 1) NOT NULL DEFAULT 0,
    CONSTRAINT uq_candidates_email UNIQUE (email)
);

CREATE INDEX idx_candidates_experience ON candidates(experience_years);

-- ============================================================
-- TABLE: candidate_skills
-- Many-to-many join: a candidate has many skills.
-- ============================================================
CREATE TABLE candidate_skills (
    candidate_id BIGINT NOT NULL,
    skill_id     BIGINT NOT NULL,
    CONSTRAINT pk_candidate_skills PRIMARY KEY (candidate_id, skill_id),
    CONSTRAINT fk_candidate_skills_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate_skills_skill     FOREIGN KEY (skill_id)     REFERENCES skills(id)     ON DELETE CASCADE
);

CREATE INDEX idx_candidate_skills_candidate_id ON candidate_skills(candidate_id);
CREATE INDEX idx_candidate_skills_skill_id     ON candidate_skills(skill_id);
