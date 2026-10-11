package com.example.recruitermatching.model;

/**
 * Domain model representing a normalized Skill.
 * Stored with lowercase name to ensure consistent comparisons.
 */
public class Skill {

    private Long id;
    private String name;

    public Skill() {}

    public Skill(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "Skill{id=" + id + ", name='" + name + "'}";
    }
}
