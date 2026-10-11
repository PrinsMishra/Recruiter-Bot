-- =====================================================================================
-- RECRUITERFLOW INTERNSHIP ASSIGNMENT — SECTION B (BACKEND)
-- PART 2: THE SQL DETECTIVE CHALLENGE
-- File: sql_detective.sql
-- =====================================================================================
-- Description:
--   Independent hiring operations dataset and solutions to the 5 challenge questions.
--   Compatible with PostgreSQL and standard ANSI SQL databases.
--
-- How to run this file:
--   psql -U postgres -d recruiter_db -f sql_detective.sql
--   or execute directly in any SQL client (pgAdmin, DBeaver, IntelliJ Database Tool, etc.)
-- =====================================================================================

-- =====================================================================================
-- SETUP: SCHEMA & SEED DATA (Loaded as-is per assignment instructions)
-- =====================================================================================

DROP TABLE IF EXISTS interviews CASCADE;
DROP TABLE IF EXISTS applicants CASCADE;
DROP TABLE IF EXISTS job_postings CASCADE;
DROP TABLE IF EXISTS recruiters CASCADE;

CREATE TABLE recruiters (
    id     INT PRIMARY KEY,
    name   VARCHAR(100),
    region VARCHAR(50)
);

CREATE TABLE job_postings (
    id           INT PRIMARY KEY,
    title        VARCHAR(100),
    recruiter_id INT,
    department   VARCHAR(50),
    opened_date  DATE,
    status       VARCHAR(20) -- 'open', 'closed', 'on_hold'
);

CREATE TABLE applicants (
    id           INT PRIMARY KEY,
    full_name    VARCHAR(100),
    email        VARCHAR(100),
    source       VARCHAR(50), -- nullable
    applied_date DATE
);

CREATE TABLE interviews (
    id             INT PRIMARY KEY,
    applicant_id   INT,
    job_posting_id INT,
    stage          VARCHAR(20), -- 'Screen', 'Technical', 'Final'
    scheduled_date DATE,
    outcome        VARCHAR(20)  -- 'passed', 'failed', 'no_show', NULL (pending)
);

-- Seed Data
INSERT INTO recruiters VALUES
(1, 'Priya Shah', 'APAC'),
(2, 'Daniel Ortiz', 'NA'),
(3, 'Fatima Al-Sayed', 'EMEA'),
(4, 'Wei Zhang', 'APAC'),
(5, 'Grace Okafor', 'EMEA');

INSERT INTO job_postings VALUES
(1, 'Backend Engineer I', 1, 'Engineering', '2026-01-05', 'open'),
(2, 'Backend Engineer I', 2, 'Engineering', '2026-01-10', 'open'),
(3, 'Data Analyst', 3, 'Data', '2026-01-08', 'closed'),
(4, 'Backend Engineer II', 1, 'Engineering', '2026-02-01', 'open'),
(5, 'DevOps Engineer', 4, 'Infrastructure', '2026-01-20', 'open'),
(6, 'Data Analyst', 5, 'Data', '2026-02-05', 'on_hold'),
(7, 'Backend Engineer I', 2, 'Engineering', '2026-02-15', 'closed');

INSERT INTO applicants VALUES
(1, 'Ananya Rao', 'ananya.rao@mail.com', 'LinkedIn', '2026-01-06'),
(2, 'Ananya Rao', 'Ananya.Rao@mail.com', 'Referral', '2026-02-04'),
(3, 'Ben Turner', 'ben.turner@mail.com', 'Job Board', '2026-01-07'),
(4, 'Chloe Martin', 'chloe.martin@mail.com', NULL, '2026-01-09'),
(5, 'Devansh Patel', 'devansh.patel@mail.com', 'LinkedIn', '2026-01-11'),
(6, 'Elena Popescu', 'elena.popescu@mail.com', 'Referral', '2026-01-12'),
(7, 'Farid Haidari', 'farid.haidari@mail.com', 'Job Board', '2026-01-13'),
(8, 'Grace Lin', 'grace.lin@mail.com', 'LinkedIn', '2026-01-15'),
(9, 'Hassan Malik', 'hassan.malik@mail.com', NULL, '2026-01-16'),
(10, 'Isla Fraser', 'isla.fraser@mail.com', 'Referral', '2026-01-18'),
(11, 'Jonas Weber', 'jonas.weber@mail.com', 'Job Board', '2026-02-10');

INSERT INTO interviews VALUES
(1, 1, 1, 'Screen', '2026-01-10', 'passed'),
(2, 1, 1, 'Technical', '2026-01-15', 'passed'),
(3, 1, 1, 'Final', '2026-01-22', 'passed'),
(4, 2, 4, 'Screen', '2026-02-05', 'passed'),
(5, 3, 1, 'Screen', '2026-01-11', 'passed'),
(6, 3, 1, 'Technical', '2026-01-16', 'failed'),
(7, 3, 3, 'Final', '2026-01-28', 'passed'),
(8, 4, 1, 'Screen', '2026-01-12', 'passed'),
(9, 4, 1, 'Final', '2026-01-24', 'failed'),
(10, 5, 2, 'Screen', '2026-01-14', 'passed'),
(11, 5, 2, 'Technical', '2026-01-19', 'passed'),
(12, 5, 2, 'Final', '2026-01-25', 'passed'),
(13, 6, 2, 'Screen', '2026-01-15', 'passed'),
(14, 6, 2, 'Final', '2026-01-26', 'failed'),
(15, 7, 2, 'Screen', '2026-01-16', 'no_show'),
(16, 8, 4, 'Screen', '2026-02-03', 'passed'),
(17, 8, 4, 'Technical', '2026-02-08', 'passed'),
(18, 8, 4, 'Final', '2026-02-12', 'passed'),
(19, 9, 4, 'Screen', '2026-02-04', 'passed'),
(20, 9, 4, 'Final', '2026-02-14', 'passed'),
(21, 10, 5, 'Screen', '2026-01-22', 'passed'),
(22, 10, 5, 'Technical', '2026-01-28', NULL),
(23, 11, 7, 'Final', '2026-02-20', 'passed'),
(24, 6, 7, 'Final', '2026-02-21', 'failed');


-- =====================================================================================
-- Question 1:
-- List all currently open job postings, along with the name of the recruiter who owns each one.
-- =====================================================================================
-- Detective Notes:
-- - Filters job_postings where status = 'open'.
-- - Joins with recruiters to display the responsible recruiter's name.

SELECT 
    jp.id AS job_posting_id,
    jp.title AS job_title,
    jp.department,
    r.name AS recruiter_name
FROM job_postings jp
INNER JOIN recruiters r ON jp.recruiter_id = r.id
WHERE jp.status = 'open'
ORDER BY jp.id ASC;

/*
Expected Output:
 job_posting_id |      job_title      |   department   | recruiter_name 
----------------+---------------------+----------------+----------------
              1 | Backend Engineer I  | Engineering    | Priya Shah
              2 | Backend Engineer I  | Engineering    | Daniel Ortiz
              4 | Backend Engineer II | Engineering    | Priya Shah
              5 | DevOps Engineer     | Infrastructure | Wei Zhang
*/


-- =====================================================================================
-- Question 2:
-- For each job posting, how many applicants reached the Final interview stage?
-- =====================================================================================
-- Detective Notes:
-- 1. Using a LEFT JOIN from job_postings preserves jobs that have 0 applicants reaching 
--    the Final stage (e.g., job 5 has only Screen/Technical, and job 6 has no interviews).
--    An INNER JOIN would incorrectly omit these jobs entirely.
-- 2. Placing the condition `i.stage = 'Final'` inside the LEFT JOIN ON clause ensures 
--    we don't inadvertently convert the outer join into an inner join.
-- 3. COUNT(DISTINCT i.applicant_id) guarantees that each candidate is counted at most once
--    per job, even if multiple interview records exist.

SELECT 
    jp.id AS job_posting_id,
    jp.title AS job_title,
    COUNT(DISTINCT i.applicant_id) AS final_stage_applicants_count
FROM job_postings jp
LEFT JOIN interviews i 
    ON jp.id = i.job_posting_id 
   AND i.stage = 'Final'
GROUP BY jp.id, jp.title
ORDER BY jp.id ASC;

/*
Expected Output:
 job_posting_id |      job_title      | final_stage_applicants_count 
----------------+---------------------+------------------------------
              1 | Backend Engineer I  |                            2
              2 | Backend Engineer I  |                            2
              3 | Data Analyst        |                            1
              4 | Backend Engineer II |                            2
              5 | DevOps Engineer     |                            0
              6 | Data Analyst        |                            0
              7 | Backend Engineer I  |                            2
*/


-- =====================================================================================
-- Question 3:
-- Find every person who effectively applied to more than one job posting. 
-- Careful — the data isn't as clean as it looks.
-- =====================================================================================
-- Detective Notes:
-- The dirty data traps in the dataset:
-- 1. Case-Sensitivity & Duplicate Person Profiles:
--    Ananya Rao is entered under two different applicant IDs:
--      - id 1: email 'ananya.rao@mail.com' (applied/interviewed for job 1)
--      - id 2: email 'Ananya.Rao@mail.com' (applied/interviewed for job 4)
--    In relational databases, raw string comparisons are case-sensitive by default.
--    Grouping strictly by applicant_id would fail to detect Ananya as a multi-job applicant!
--    Normalizing with LOWER(TRIM(a.email)) unifies her identity across records.
-- 2. Multiple Interview Stages vs. Multiple Job Applications:
--    An applicant typically has multiple interview rows for the SAME job posting
--    (e.g., Screen, Technical, Final). Counting raw interview rows (COUNT(i.id)) would 
--    falsely treat multiple stages of one job as multiple applications.
--    We must count distinct job postings: COUNT(DISTINCT i.job_posting_id) > 1.
-- 3. We also aggregate applicant_name (using MIN/MAX) and group by the normalized email.

SELECT 
    LOWER(TRIM(a.email)) AS normalized_email,
    MIN(a.full_name) AS applicant_name,
    COUNT(DISTINCT i.job_posting_id) AS distinct_jobs_applied
FROM applicants a
INNER JOIN interviews i ON a.id = i.applicant_id
GROUP BY LOWER(TRIM(a.email))
HAVING COUNT(DISTINCT i.job_posting_id) > 1
ORDER BY distinct_jobs_applied DESC, applicant_name ASC;

/*
Expected Output:
  normalized_email   | applicant_name | distinct_jobs_applied 
---------------------+----------------+-----------------------
 ananya.rao@mail.com | Ananya Rao     |                     2  (Job 1 via id=1, Job 4 via id=2)
 ben.turner@mail.com | Ben Turner     |                     2  (Job 1, Job 3)
 elena.popescu@mail.com | Elena Popescu  |                  2  (Job 2, Job 7)
*/


-- =====================================================================================
-- Question 4:
-- For each recruiter, compute their Final-stage conversion rate 
-- (passed ÷ total Final-stage interviews), but only include recruiters 
-- with at least 3 Final-stage interviews.
-- =====================================================================================
-- Detective Notes:
-- 1. In PostgreSQL / SQL standard, integer division (e.g. 3 / 4) results in 0 due to integer truncation.
--    We cast or multiply by 1.0 (or cast to NUMERIC) before division:
--    ROUND(1.0 * SUM(CASE WHEN i.outcome = 'passed' THEN 1 ELSE 0 END) / COUNT(i.id), 4)
-- 2. Stage filter `i.stage = 'Final'` isolates the Final round.
-- 3. The HAVING clause `COUNT(i.id) >= 3` excludes recruiters with small sample sizes:
--    - Priya Shah: 4 Final interviews, 3 passed -> 0.7500 (75.0%)
--    - Daniel Ortiz: 4 Final interviews, 2 passed -> 0.5000 (50.0%)
--    - Fatima Al-Sayed: 1 Final interview (excluded by HAVING)
--    - Wei Zhang: 0 Final interviews (excluded by HAVING)
--    - Grace Okafor: 0 Final interviews (excluded by HAVING)

SELECT 
    r.id AS recruiter_id,
    r.name AS recruiter_name,
    COUNT(i.id) AS total_final_interviews,
    SUM(CASE WHEN i.outcome = 'passed' THEN 1 ELSE 0 END) AS passed_final_interviews,
    ROUND(
        1.0 * SUM(CASE WHEN i.outcome = 'passed' THEN 1 ELSE 0 END) / COUNT(i.id), 
        4
    ) AS final_conversion_rate
FROM recruiters r
INNER JOIN job_postings jp ON r.id = jp.recruiter_id
INNER JOIN interviews i ON jp.id = i.job_posting_id
WHERE i.stage = 'Final'
GROUP BY r.id, r.name
HAVING COUNT(i.id) >= 3
ORDER BY final_conversion_rate DESC, total_final_interviews DESC;

/*
Expected Output:
 recruiter_id | recruiter_name | total_final_interviews | passed_final_interviews | final_conversion_rate 
--------------+----------------+------------------------+-------------------------+-----------------------
            1 | Priya Shah     |                      4 |                       3 |                0.7500
            2 | Daniel Ortiz   |                      4 |                       2 |                0.5000
*/


-- =====================================================================================
-- Question 5 (Bonus):
-- Using a window function, find the recruiter with the most successful placements 
-- (passed at Final stage) per department.
-- =====================================================================================
-- Detective Notes:
-- 1. Successful placement definition: an interview where `stage = 'Final'` AND `outcome = 'passed'`.
-- 2. Common Table Expression (CTE) aggregates successful placements per department and recruiter.
-- 3. We use the window function `DENSE_RANK() OVER (PARTITION BY jp.department ORDER BY COUNT(i.id) DESC)`.
--    Why DENSE_RANK()? In real recruitment operations, ties for top performer can occur.
--    DENSE_RANK() ensures that if two recruiters share the top placement count in a department,
--    both are fairly recognized rather than arbitrarily dropping one.
-- 4. Outer query filters `WHERE rank_in_dept = 1`.
--
-- Breakdown:
-- - Engineering: 
--     * Priya Shah (recruiter 1): 3 placements (interview 3 for job 1, interviews 18 & 20 for job 4)
--     * Daniel Ortiz (recruiter 2): 2 placements (interview 12 for job 2, interview 23 for job 7)
--     -> Winner: Priya Shah
-- - Data:
--     * Fatima Al-Sayed (recruiter 3): 1 placement (interview 7 for job 3)
--     -> Winner: Fatima Al-Sayed
-- - Infrastructure:
--     * Wei Zhang: 0 passed Final interviews (no placements)

WITH department_recruiter_placements AS (
    SELECT 
        jp.department,
        r.id AS recruiter_id,
        r.name AS recruiter_name,
        COUNT(i.id) AS successful_placements,
        DENSE_RANK() OVER (
            PARTITION BY jp.department 
            ORDER BY COUNT(i.id) DESC
        ) AS rank_in_dept
    FROM recruiters r
    INNER JOIN job_postings jp ON r.id = jp.recruiter_id
    INNER JOIN interviews i ON jp.id = i.job_posting_id
    WHERE i.stage = 'Final' 
      AND i.outcome = 'passed'
    GROUP BY jp.department, r.id, r.name
)
SELECT 
    department,
    recruiter_id,
    recruiter_name,
    successful_placements
FROM department_recruiter_placements
WHERE rank_in_dept = 1
ORDER BY department ASC;

/*
Expected Output:
 department  | recruiter_id |  recruiter_name  | successful_placements 
-------------+--------------+------------------+-----------------------
 Data        |            3 | Fatima Al-Sayed  |                     1
 Engineering |            1 | Priya Shah       |                     3
*/
