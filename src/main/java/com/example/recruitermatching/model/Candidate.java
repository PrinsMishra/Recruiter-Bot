package com.example.recruitermatching.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a Candidate.
 * Loaded from the 'candidates' table and enriched with skills from 'candidate_skills'.
 */
public class Candidate {

    private Long id;
    private String name;
    private String email;
    private double experienceYears;
    private List<String> skills = new ArrayList<>();

    public Candidate() {}

    public Candidate(Long id, String name, String email, double experienceYears) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.experienceYears = experienceYears;
    }

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(double experienceYears) {
        this.experienceYears = experienceYears;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Candidate{id=" + id + ", name='" + name + "', experienceYears=" + experienceYears + "}";
    }
}
