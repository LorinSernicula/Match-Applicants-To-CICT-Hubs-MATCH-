package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * stores the complete result returned by MatchingEngine for one applicant
 *
 * the engine computes the results; this class can only store and expose them
 */
public class MatchResult {

    private final Applicant applicant;
    private final List<RankedProgram> rankedPrograms;
    private final List<ChoiceResult> choiceResults;
    private final Outcome outcome;

    public MatchResult(Applicant applicant,
                       List<RankedProgram> rankedPrograms,
                       List<ChoiceResult> choiceResults,
                       Outcome outcome) {

        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null.");
        }
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null.");
        }

        this.applicant = applicant;
        this.rankedPrograms = rankedPrograms == null
                ? new ArrayList<RankedProgram>()
                : new ArrayList<RankedProgram>(rankedPrograms);
        this.choiceResults = choiceResults == null
                ? new ArrayList<ChoiceResult>()
                : new ArrayList<ChoiceResult>(choiceResults);
        this.outcome = outcome;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    /*
     * returned list preserves the order supplied by MatchingEngine; no sorting
     */
    public List<RankedProgram> getRankedPrograms() {
        return Collections.unmodifiableList(
                new ArrayList<RankedProgram>(rankedPrograms));
    }

    public List<ChoiceResult> getChoiceResults() {
        return Collections.unmodifiableList(
                new ArrayList<ChoiceResult>(choiceResults));
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Program getRecommendedProgram() {
        if (rankedPrograms.isEmpty()) {
            return null;
        }
        return rankedPrograms.get(0).getProgram();
    }

    public boolean hasRecommendation() {
        return outcome == Outcome.MATCH_FOUND
                && getRecommendedProgram() != null;
    }
}
