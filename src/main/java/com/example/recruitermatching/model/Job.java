package com.example.recruitermatching.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a Job posting.
 * Loaded from the 'jobs' table and enriched with skills from 'job_skills'.
 */
public class Job {

    private Long id;
    private String title;
    private String description;
    private double minimumExperience;
    private List<String> requiredSkills = new ArrayList<>();

    public Job() {}

    public Job(Long id, String title, String description, double minimumExperience) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.minimumExperience = minimumExperience;
    }

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMinimumExperience() {
        return minimumExperience;
    }

    public void setMinimumExperience(double minimumExperience) {
        this.minimumExperience = minimumExperience;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    @Override
    public String toString() {
        return "Job{id=" + id + ", title='" + title + "', minimumExperience=" + minimumExperience + "}";
    }
}
