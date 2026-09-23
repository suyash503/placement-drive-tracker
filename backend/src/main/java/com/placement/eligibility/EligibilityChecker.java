package com.placement.eligibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Checks if a student is allowed to apply to a drive.
 *
 * Written in plain Java (the rest of the backend is Kotlin) to show that
 * both languages can live in the same Micronaut project and call each other.
 *
 * It has no database or framework code, so it is very easy to unit test.
 */
public class EligibilityChecker {

    /**
     * Returns the list of reasons why the student is NOT eligible.
     * An empty list means the student is eligible.
     */
    public static List<String> findProblems(
            double studentCgpa,
            String studentBranch,
            int studentBacklogs,
            boolean studentAlreadyPlaced,
            double minCgpa,
            Set<String> allowedBranches,
            int maxBacklogs
    ) {
        List<String> problems = new ArrayList<>();

        if (studentCgpa < minCgpa) {
            problems.add("CGPA " + studentCgpa + " is below the minimum of " + minCgpa);
        }

        // an empty set means "all branches allowed"
        if (!allowedBranches.isEmpty() && !allowedBranches.contains(studentBranch)) {
            problems.add("Branch " + studentBranch + " is not allowed for this drive");
        }

        if (studentBacklogs > maxBacklogs) {
            problems.add(studentBacklogs + " active backlog(s), maximum allowed is " + maxBacklogs);
        }

        // college rule: once you get an offer you are out of the placement process
        if (studentAlreadyPlaced) {
            problems.add("Student is already placed");
        }

        return problems;
    }

    public static boolean isEligible(
            double studentCgpa,
            String studentBranch,
            int studentBacklogs,
            boolean studentAlreadyPlaced,
            double minCgpa,
            Set<String> allowedBranches,
            int maxBacklogs
    ) {
        return findProblems(studentCgpa, studentBranch, studentBacklogs, studentAlreadyPlaced,
                minCgpa, allowedBranches, maxBacklogs).isEmpty();
    }
}
