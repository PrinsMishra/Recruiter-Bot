-- ============================================================
-- SEED DATA: Recruiterflow Recruiter Bot (Core Project)
-- Official assignment dataset: 15 Fictional Candidates & 6 Jobs
-- All skills are normalized (lowercase, trimmed)
-- ============================================================

-- ============================================================
-- 1. Skills
-- ============================================================
INSERT INTO skills (name) VALUES
    ('deduction'),
    ('pattern-recognition'),
    ('forensics'),
    ('research'),
    ('time-management'),
    ('public-speaking'),
    ('systems-design'),
    ('rapid-prototyping'),
    ('leadership'),
    ('project-management'),
    ('stakeholder-management'),
    ('woodworking'),
    ('minimalism'),
    ('negotiation'),
    ('chemistry'),
    ('persuasion'),
    ('resourcefulness'),
    ('theoretical-analysis'),
    ('precision'),
    ('strategy'),
    ('crisis-management'),
    ('sales'),
    ('team-building'),
    ('optimism'),
    ('mentorship'),
    ('security'),
    ('loyalty');

-- ============================================================
-- 2. Jobs (6 Fictional Jobs from Assignment Brief)
-- ============================================================
INSERT INTO jobs (id, title, description, minimum_experience) VALUES
    (
        1,
        'Backend Detective',
        'Tagline: We have a bug. We have no leads. We have you. Culture: analytical, autonomous.',
        3.0
    ),
    (
        2,
        'Rapid Prototyping Engineer',
        'Tagline: Ship first, document never (kidding — please document). Culture: innovative, fast-paced.',
        2.0
    ),
    (
        3,
        'Developer Relations Lead',
        'Tagline: Explain complex things to confused humans, cheerfully. Culture: energetic, curious.',
        2.0
    ),
    (
        4,
        'Engineering Manager, Chaos Team',
        'Tagline: Herd cats. The cats are senior engineers. Culture: empathetic, organized.',
        5.0
    ),
    (
        5,
        'Incident Commander',
        'Tagline: 3am page. You''re the one who picks up. Culture: decisive, calm-under-pressure.',
        4.0
    ),
    (
        6,
        'Sales Engineer',
        'Tagline: Sell the vision, then go build it. Culture: enthusiastic, persistent.',
        3.0
    );

-- Reset sequence for jobs if needed (for databases like PostgreSQL)
ALTER SEQUENCE IF EXISTS jobs_id_seq RESTART WITH 7;

-- ============================================================
-- 3. Job Required Skills
-- ============================================================

-- Job 1: Backend Detective -> deduction, pattern-recognition, forensics
INSERT INTO job_skills (job_id, skill_id)
SELECT 1, id FROM skills WHERE name IN ('deduction', 'pattern-recognition', 'forensics');

-- Job 2: Rapid Prototyping Engineer -> rapid-prototyping, systems-design
INSERT INTO job_skills (job_id, skill_id)
SELECT 2, id FROM skills WHERE name IN ('rapid-prototyping', 'systems-design');

-- Job 3: Developer Relations Lead -> public-speaking, research
INSERT INTO job_skills (job_id, skill_id)
SELECT 3, id FROM skills WHERE name IN ('public-speaking', 'research');

-- Job 4: Engineering Manager, Chaos Team -> team-building, stakeholder-management, leadership
INSERT INTO job_skills (job_id, skill_id)
SELECT 4, id FROM skills WHERE name IN ('team-building', 'stakeholder-management', 'leadership');

-- Job 5: Incident Commander -> crisis-management, strategy, negotiation
INSERT INTO job_skills (job_id, skill_id)
SELECT 5, id FROM skills WHERE name IN ('crisis-management', 'strategy', 'negotiation');

-- Job 6: Sales Engineer -> sales, public-speaking, negotiation
INSERT INTO job_skills (job_id, skill_id)
SELECT 6, id FROM skills WHERE name IN ('sales', 'public-speaking', 'negotiation');

-- ============================================================
-- 4. Candidates (15 Fictional Candidates from Assignment Brief)
-- ============================================================
INSERT INTO candidates (id, name, email, experience_years) VALUES
    (1,  'Sherlock H.', 'sherlock.h@recruiterbot.local', 8.0),
    (2,  'Hermione G.', 'hermione.g@recruiterbot.local', 4.0),
    (3,  'Tony S.',     'tony.s@recruiterbot.local',     12.0),
    (4,  'Leslie K.',   'leslie.k@recruiterbot.local',   6.0),
    (5,  'Ron S.',      'ron.s@recruiterbot.local',      15.0),
    (6,  'Rick S.',     'rick.s@recruiterbot.local',     20.0),
    (7,  'Elle W.',     'elle.w@recruiterbot.local',     3.0),
    (8,  'MacGyver',    'macgyver@recruiterbot.local',   10.0),
    (9,  'Sheldon C.',  'sheldon.c@recruiterbot.local',  9.0),
    (10, 'Katniss E.',  'katniss.e@recruiterbot.local',  5.0),
    (11, 'Michael S.',  'michael.s@recruiterbot.local',  11.0),
    (12, 'Olivia P.',   'olivia.p@recruiterbot.local',   13.0),
    (13, 'Ted L.',      'ted.l@recruiterbot.local',      7.0),
    (14, 'Miranda P.',  'miranda.p@recruiterbot.local',  18.0),
    (15, 'Dwight S.',   'dwight.s@recruiterbot.local',   9.0);

ALTER SEQUENCE IF EXISTS candidates_id_seq RESTART WITH 16;

-- ============================================================
-- 5. Candidate Skills
-- ============================================================

-- 1. Sherlock H. -> deduction, pattern-recognition, forensics
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 1, id FROM skills WHERE name IN ('deduction', 'pattern-recognition', 'forensics');

-- 2. Hermione G. -> research, time-management, public-speaking
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 2, id FROM skills WHERE name IN ('research', 'time-management', 'public-speaking');

-- 3. Tony S. -> systems-design, rapid-prototyping, leadership
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 3, id FROM skills WHERE name IN ('systems-design', 'rapid-prototyping', 'leadership');

-- 4. Leslie K. -> project-management, stakeholder-management, public-speaking
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 4, id FROM skills WHERE name IN ('project-management', 'stakeholder-management', 'public-speaking');

-- 5. Ron S. -> woodworking, minimalism, negotiation
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 5, id FROM skills WHERE name IN ('woodworking', 'minimalism', 'negotiation');

-- 6. Rick S. -> systems-design, rapid-prototyping, chemistry
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 6, id FROM skills WHERE name IN ('systems-design', 'rapid-prototyping', 'chemistry');

-- 7. Elle W. -> persuasion, research, public-speaking
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 7, id FROM skills WHERE name IN ('persuasion', 'research', 'public-speaking');

-- 8. MacGyver -> rapid-prototyping, resourcefulness, chemistry, systems-design
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 8, id FROM skills WHERE name IN ('rapid-prototyping', 'resourcefulness', 'chemistry', 'systems-design');

-- 9. Sheldon C. -> theoretical-analysis, pattern-recognition, research
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 9, id FROM skills WHERE name IN ('theoretical-analysis', 'pattern-recognition', 'research');

-- 10. Katniss E. -> precision, strategy, crisis-management
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 10, id FROM skills WHERE name IN ('precision', 'strategy', 'crisis-management');

-- 11. Michael S. -> sales, public-speaking, team-building
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 11, id FROM skills WHERE name IN ('sales', 'public-speaking', 'team-building');

-- 12. Olivia P. -> crisis-management, negotiation, strategy, leadership
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 12, id FROM skills WHERE name IN ('crisis-management', 'negotiation', 'strategy', 'leadership');

-- 13. Ted L. -> team-building, optimism, mentorship, public-speaking
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 13, id FROM skills WHERE name IN ('team-building', 'optimism', 'mentorship', 'public-speaking');

-- 14. Miranda P. -> leadership, negotiation, stakeholder-management, precision
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 14, id FROM skills WHERE name IN ('leadership', 'negotiation', 'stakeholder-management', 'precision');

-- 15. Dwight S. -> sales, negotiation, security, loyalty
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT 15, id FROM skills WHERE name IN ('sales', 'negotiation', 'security', 'loyalty');
