package model;

import java.util.ArrayList;
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
    private Outcome outcome;
    private Program assignedProgram;   // admitted or waitlisted program; null otherwise

    public MatchResult(Applicant applicant) {
        this.applicant = applicant;
        this.rankedPrograms = new ArrayList<>();
        this.choiceResults = new ArrayList<>();
        this.outcome = Outcome.NOT_EVALUATED;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public List<RankedProgram> getRankedPrograms() {
        return new ArrayList<>(rankedPrograms);
    }

    public List<ChoiceResult> getChoiceResults() {
        return new ArrayList<>(choiceResults);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Program getAssignedProgram() {
        return assignedProgram;
    }

    public void addRankedProgram(RankedProgram rankedProgram) {
        if (rankedProgram != null) {
            rankedPrograms.add(rankedProgram);
        }
    }

    public void addChoiceResult(ChoiceResult choiceResult) {
        if (choiceResult != null) {
            choiceResults.add(choiceResult);
        }
    }

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public void setAssignedProgram(Program assignedProgram) {
        this.assignedProgram = assignedProgram;
    }

    /* Returns the first program in the ranking, if one exists. */
    public RankedProgram getRecommendedProgram() {
        if (rankedPrograms.isEmpty()) {
            return null;
        }

        return rankedPrograms.get(0);
    }
}
