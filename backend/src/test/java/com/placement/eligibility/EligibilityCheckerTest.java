package com.placement.eligibility;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Plain unit test - no database, no Micronaut, runs in milliseconds
class EligibilityCheckerTest {

    private final Set<String> cseAndIt = Set.of("CSE", "IT");

    @Test
    void studentMeetingAllRulesIsEligible() {
        List<String> problems = EligibilityChecker.findProblems(8.5, "CSE", 0, false, 7.0, cseAndIt, 0);
        assertTrue(problems.isEmpty());
    }

    @Test
    void lowCgpaIsNotEligible() {
        List<String> problems = EligibilityChecker.findProblems(6.9, "CSE", 0, false, 7.0, cseAndIt, 0);
        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("CGPA"));
    }

    @Test
    void cgpaExactlyAtMinimumIsEligible() {
        assertTrue(EligibilityChecker.isEligible(7.0, "IT", 0, false, 7.0, cseAndIt, 0));
    }

    @Test
    void wrongBranchIsNotEligible() {
        List<String> problems = EligibilityChecker.findProblems(9.0, "ME", 0, false, 7.0, cseAndIt, 0);
        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("Branch"));
    }

    @Test
    void emptyBranchListMeansAllBranchesAllowed() {
        assertTrue(EligibilityChecker.isEligible(9.0, "ME", 0, false, 7.0, Set.of(), 0));
    }

    @Test
    void tooManyBacklogsIsNotEligible() {
        assertFalse(EligibilityChecker.isEligible(9.0, "CSE", 2, false, 7.0, cseAndIt, 1));
        assertTrue(EligibilityChecker.isEligible(9.0, "CSE", 1, false, 7.0, cseAndIt, 1));
    }

    @Test
    void placedStudentIsNotEligible() {
        List<String> problems = EligibilityChecker.findProblems(9.0, "CSE", 0, true, 7.0, cseAndIt, 0);
        assertEquals(List.of("Student is already placed"), problems);
    }

    @Test
    void allProblemsAreReportedTogether() {
        List<String> problems = EligibilityChecker.findProblems(5.0, "ME", 3, true, 7.0, cseAndIt, 0);
        assertEquals(4, problems.size());
    }
}
