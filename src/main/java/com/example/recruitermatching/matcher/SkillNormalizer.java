package com.example.recruitermatching.matcher;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * SkillNormalizer: Converts raw skill names into canonical, comparable form.
 *
 * Normalization steps applied (in order):
 *   1. Trim leading/trailing whitespace
 *   2. Convert to lowercase
 *   3. Apply known alias substitutions (e.g. "js" → "javascript", "golang" → "go")
 *
 * This prevents false negatives where the same skill is expressed differently
 * (e.g. "Node.JS" vs "nodejs" vs "node.js").
 *
 * We intentionally keep the alias map small and explicit to avoid false positives
 * that aggressive fuzzy matching might introduce.
 */
@Component
public class SkillNormalizer {

    /**
     * Static alias map: maps known synonyms/abbreviations to their canonical form.
     * Keys must already be lowercase and trimmed.
     */
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // JavaScript aliases
        ALIASES.put("js",             "javascript");
        ALIASES.put("ecmascript",     "javascript");

        // TypeScript aliases
        ALIASES.put("ts",             "typescript");

        // Go aliases
        ALIASES.put("golang",         "go");

        // Node.js aliases
        ALIASES.put("nodejs",         "node.js");
        ALIASES.put("node",           "node.js");

        // PostgreSQL aliases
        ALIASES.put("postgres",       "postgresql");
        ALIASES.put("pg",             "postgresql");

        // Spring Boot aliases
        ALIASES.put("spring",         "spring boot");
        ALIASES.put("springboot",     "spring boot");

        // React aliases
        ALIASES.put("reactjs",        "react");
        ALIASES.put("react.js",       "react");

        // Angular aliases
        ALIASES.put("angular",        "angularjs");

        // AWS aliases
        ALIASES.put("amazon web services", "aws");

        // Docker aliases
        ALIASES.put("docker container", "docker");

        // CI/CD aliases
        ALIASES.put("ci cd",          "ci/cd");
        ALIASES.put("cicd",           "ci/cd");

        // REST API aliases
        ALIASES.put("rest",           "rest api");
        ALIASES.put("restful",        "rest api");
        ALIASES.put("rest apis",      "rest api");

        // Git aliases
        ALIASES.put("github",         "git");
        ALIASES.put("gitlab",         "git");

        // Kubernetes aliases
        ALIASES.put("k8s",            "kubernetes");
    }

    /**
     * Normalizes a single raw skill name.
     *
     * @param rawSkill the skill name as entered (e.g. "Node.JS", "JAVA", "PostgreSQL")
     * @return canonical lowercase skill name (e.g. "node.js", "java", "postgresql")
     */
    public String normalize(String rawSkill) {
        if (rawSkill == null) {
            return "";
        }
        // Step 1: trim + lowercase
        String normalized = rawSkill.trim().toLowerCase();

        // Step 2: apply alias substitution if a match exists
        return ALIASES.getOrDefault(normalized, normalized);
    }
}
