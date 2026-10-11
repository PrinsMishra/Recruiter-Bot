# Recruiter-Bot — Recruiter Matching Backend

A **Spring Boot** backend for matching candidates to jobs (and vice versa) using a deterministic, explainable weighted scoring algorithm.

Built with **Java 17+, Spring Boot 3, JdbcTemplate, and PostgreSQL**.  
**No ORM. No Hibernate. No JPA. All SQL is handwritten.**

---

## Quick Start

### Option A — Run instantly with in-memory H2 (zero setup)

```bash
cd "path/to/project"
mvn spring-boot:run
```

The application starts on **http://localhost:8080** with H2 in-memory database (PostgreSQL compatibility mode). Schema and seed data are loaded automatically.

---

### Option B — Run with real PostgreSQL

**1. Create the database:**

```sql
CREATE DATABASE recruiter_db;
```

**2. Edit `src/main/resources/application.properties`:**

Uncomment and configure the PostgreSQL lines:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recruiter_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=your_password
```

And comment out the H2 lines.

**3. Run:**

```bash
mvn spring-boot:run
```

Schema (`schema.sql`) and seed data (`data.sql`) are auto-executed on startup via:

```properties
spring.sql.init.mode=always
```

---

### Build a JAR

```bash
mvn clean package
java -jar target/recruiter-matching-0.0.1-SNAPSHOT.jar
```

---

## API Reference

### API 1 — Find candidates for a job

```
GET /jobs/{id}/matches
```

Returns all candidates ranked from best to worst match for the given job.

**Example:**

```bash
curl http://localhost:8080/jobs/1/matches
```

**Response:**

```json
[
  {
    "candidateId": 1,
    "candidateName": "Sherlock H.",
    "score": 100.0,
    "reason": "Matched all 3 required skills. Has 8.0 years of experience, meeting the minimum requirement of 3.0 years."
  },
  {
    "candidateId": 9,
    "candidateName": "Sheldon C.",
    "score": 53.3,
    "reason": "Matched 1 of 3 required skills. Has 9.0 years of experience, meeting the minimum requirement of 3.0 years."
  }
]
```

**Error (job not found):**

```bash
curl http://localhost:8080/jobs/999/matches
```

```json
{
  "error": "Job not found",
  "resourceId": 999
}
```

---

### API 2 — Find jobs for a candidate

```
GET /candidates/{id}/matches
```

Returns all jobs ranked from best to worst match for the given candidate.

**Example:**

```bash
curl http://localhost:8080/candidates/1/matches
```

**Response:**

```json
[
  {
    "jobId": 1,
    "jobTitle": "Backend Detective",
    "score": 100.0,
    "reason": "Matched all 3 required skills. Has 8.0 years of experience, meeting the minimum requirement of 3.0 years."
  }
]
```

---

## Database Schema

Five tables with full referential integrity:

```
skills            — normalized skill names (lowercase, unique)
candidates        — candidate profiles with experience
jobs              — job postings with minimum experience
candidate_skills  — many-to-many: candidate ↔ skills
job_skills        — many-to-many: job ↔ required skills
```

All tables have:
- Primary keys (`BIGSERIAL`)
- Foreign keys with `ON DELETE CASCADE`
- Unique constraints where applicable
- Indexes on join columns for query performance

Schema is auto-created via `src/main/resources/schema.sql`.

---

## Seeded Data

The application comes pre-loaded with the official assignment seed data (`src/main/resources/data.sql`):

- **27 normalized skills** (`deduction`, `pattern-recognition`, `forensics`, `systems-design`, `rapid-prototyping`, `public-speaking`, `sales`, etc.)
- **6 fictional jobs**:
  1. *Backend Detective* (Min Exp: 3 yrs | Required: deduction, pattern-recognition, forensics)
  2. *Rapid Prototyping Engineer* (Min Exp: 2 yrs | Required: rapid-prototyping, systems-design)
  3. *Developer Relations Lead* (Min Exp: 2 yrs | Required: public-speaking, research)
  4. *Engineering Manager, Chaos Team* (Min Exp: 5 yrs | Required: team-building, stakeholder-management, leadership)
  5. *Incident Commander* (Min Exp: 4 yrs | Required: crisis-management, strategy, negotiation)
  6. *Sales Engineer* (Min Exp: 3 yrs | Required: sales, public-speaking, negotiation)
- **15 fictional candidates**: Sherlock H., Hermione G., Tony S., Leslie K., Ron S., Rick S., Elle W., MacGyver, Sheldon C., Katniss E., Michael S., Olivia P., Ted L., Miranda P., and Dwight S.

---

## Matching Algorithm

This is the core of the application.

### Design Philosophy

> Deterministic, rule-based weighted scoring — no ML, no LLM, no black boxes.

The score is fully explainable and reproducible. Every score can be computed by hand from the inputs.

---

### Step 1 — Skill Normalization

Before any comparison, all skill names are normalized:

1. **Trim** whitespace
2. **Lowercase** everything
3. **Apply alias map** for common synonyms:

| Input | Normalized |
|-------|-----------|
| `JS` | `javascript` |
| `GoLang` | `go` |
| `Node.JS` / `nodejs` | `node.js` |
| `Postgres` | `postgresql` |
| `Spring` / `SpringBoot` | `spring boot` |
| `K8s` | `kubernetes` |
| `GitHub` / `GitLab` | `git` |

---

### Step 2 — Skill Score (weight: 70%)

Measures **required-skill coverage**. Extra candidate skills don't penalize.

```
matchedSkills = candidateSkills ∩ jobRequiredSkills

skillScore = (matchedSkills / totalRequiredSkills) × 100
```

**Example:**
- Job requires: `[java, spring boot, sql, docker, kafka]` → 5 required
- Candidate has: `[java, spring boot, sql, docker, react]` → 4 matched

```
skillScore = 4/5 × 100 = 80
```

**Edge case:** If job has no required skills → `skillScore = 100`

---

### Step 3 — Experience Score (weight: 30%)

Measures experience fit, **capped at 100** to prevent over-inflation.

```
experienceScore = min(candidateExp / requiredExp, 1.0) × 100
```

**Examples:**

| Candidate | Required | Score |
|-----------|----------|-------|
| 4 years | 4 years | 100 |
| 3 years | 4 years | 75 |
| 2 years | 4 years | 50 |
| 8 years | 4 years | 100 (capped) |

**Edge case:** If `requiredExp = 0` → `experienceScore = 100`

---

### Step 4 — Final Score

```
finalScore = (0.70 × skillScore) + (0.30 × experienceScore)
```

**Example:**
```
skillScore = 80, experienceScore = 75

finalScore = 0.70 × 80 + 0.30 × 75
           = 56 + 22.5
           = 78.5
```

---

### Step 5 — Ranking

Results are sorted:

```
ORDER BY score DESC, id ASC
```

The `id ASC` tiebreaker ensures deterministic ordering when two candidates/jobs have equal scores.

---

## Running Tests

```bash
mvn test
```

Tests cover all algorithm edge cases without requiring a database or Spring context:

| Test Case | Expected Score |
|-----------|---------------|
| Perfect match (all skills + experience met) | 100.0 |
| 4/5 skills, full experience | 86.0 |
| All skills, experience 3/4 years | 92.5 |
| No matching skills, experience met | 30.0 |
| Candidate exceeds experience (capped) | 100.0 |
| No experience required (safe default) | 100.0 exp score |
| No skills required (safe default) | 100.0 skill score |
| Alias normalization ("JS" → "javascript") | 100.0 |

---

## Project Structure

```
src/main/java/com/example/recruitermatching/
├── RecruiterMatchingApplication.java
├── controller/
│   ├── JobController.java          ← GET /jobs/{id}/matches
│   ├── CandidateController.java    ← GET /candidates/{id}/matches
│   └── GlobalExceptionHandler.java ← 404/500 error responses
├── service/
│   └── MatchingService.java        ← Orchestration: fetch → score → sort → respond
├── repository/
│   ├── JobRepository.java          ← Handwritten SQL for jobs/job_skills
│   └── CandidateRepository.java    ← Handwritten SQL for candidates/candidate_skills
├── matcher/
│   ├── MatchingAlgorithm.java      ← Core scoring logic
│   └── SkillNormalizer.java        ← Skill alias resolution
├── model/
│   ├── Job.java
│   ├── Candidate.java
│   └── Skill.java
├── dto/
│   ├── CandidateMatchResponse.java
│   ├── JobMatchResponse.java
│   └── ErrorResponse.java
└── exception/
    ├── JobNotFoundException.java
    └── CandidateNotFoundException.java

src/main/resources/
├── application.properties
├── schema.sql           ← DDL: tables, PKs, FKs, indexes
├── data.sql             ← Seed: 30 skills, 6 jobs, 10 candidates
└── sql_detective.sql    ← Part 2 SQL Detective challenge (resource copy)

sql_detective.sql        ← Root file for Part 2 SQL Detective challenge
```

---

## Part 2 — The SQL Detective Challenge

As required in Section B (Part 2) of the assignment brief, all answers to the hiring operations SQL questions are provided in [`sql_detective.sql`](file:///c:/Users/prins/OneDrive/Desktop/New%20folder%20%282%29/sql_detective.sql).

### Summary of Questions & Detective Insights

1. **Question 1: Open Job Postings & Recruiter Owner**
   - Filters `job_postings.status = 'open'` joined to `recruiters`.
   - Results: 4 active postings managed by Priya Shah (Jobs 1 & 4), Daniel Ortiz (Job 2), and Wei Zhang (Job 5).

2. **Question 2: Final-Stage Applicants per Job Posting**
   - Uses `LEFT JOIN` on `interviews i ON jp.id = i.job_posting_id AND i.stage = 'Final'` and `COUNT(DISTINCT i.applicant_id)`.
   - Ensures job postings with 0 final-stage candidates (Jobs 5 and 6) remain in the result set rather than being dropped.

3. **Question 3: Identifying Applicants to Multiple Jobs (Data Cleaning)**
   - **Dirty Data Challenge:** Ananya Rao is entered under two accounts:
     - ID 1: `ananya.rao@mail.com` (applied to Job 1)
     - ID 2: `Ananya.Rao@mail.com` (applied to Job 4)
   - Using `LOWER(TRIM(a.email))` merges her identity across accounts.
   - Evaluates `COUNT(DISTINCT i.job_posting_id) > 1` so that multi-stage interviews for the same job are not counted as multiple applications.
   - Uncovers 3 multi-job applicants: Ananya Rao, Ben Turner, and Elena Popescu.

4. **Question 4: Recruiter Final-Stage Conversion Rate**
   - Calculates `passed / total` Final interviews, guarding against SQL integer truncation by multiplying by `1.0`.
   - Filters recruiters with `HAVING COUNT(i.id) >= 3`.
   - Results: Priya Shah (75.0% conversion, 3/4) and Daniel Ortiz (50.0% conversion, 2/4).

5. **Question 5 (Bonus): Top Recruiter per Department via Window Function**
   - Aggregates successful final placements (`stage = 'Final' AND outcome = 'passed'`).
   - Applies `DENSE_RANK() OVER (PARTITION BY jp.department ORDER BY COUNT(i.id) DESC)` to handle any potential ties gracefully.
   - Results: Engineering winner is Priya Shah (3 placements), Data winner is Fatima Al-Sayed (1 placement).

### How to Run SQL Detective Queries

```bash
# In PostgreSQL
psql -U postgres -d recruiter_db -f sql_detective.sql
```

---

## Note on AI Tools

In accordance with the assignment guidelines:
- **AI Coding Assistants:** Used as a pair programmer for scaffolding boilerplate, reviewing SQL edge cases (such as email case folding and window function tie-breaking), and drafting documentation.
- **Architectural & Algorithmic Ownership:** The core scoring model (70% Skill coverage + 30% capped experience), data normalization strategies, handwritten SQL constraint compliance, and test suite design were carefully engineered and verified against assignment specifications.

