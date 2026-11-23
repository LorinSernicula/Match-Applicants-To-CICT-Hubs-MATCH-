package model;

/*
 * stores one program's scores after MatchingEngine has computed them
 *
 * IMPORTANT:
 * this class does not calculate scores and does not sort programs; 
 * MatchingEngine is responsible for computation and ranking order.
 */
public class RankedProgram {

    private final Program program;
    private final double corePriorityScore;
    private final double traitAlignmentScore;
    private final double totalScore;

    public RankedProgram(Program program, double corePriorityScore,
                         double traitAlignmentScore, double totalScore) {

        if (program == null) {
            throw new IllegalArgumentException("Program cannot be null.");
        }

        this.program = program;
        this.corePriorityScore = corePriorityScore;
        this.traitAlignmentScore = traitAlignmentScore;
        this.totalScore = totalScore;
    }

    public Program getProgram() {
        return program;
    }

    public double getCorePriorityScore() {
        return corePriorityScore;
    }

    public double getTraitAlignmentScore() {
        return traitAlignmentScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    @Override
    public String toString() {
        return program.getCode() + " - Total: " + totalScore
                + " (Core: " + corePriorityScore
                + ", Traits: " + traitAlignmentScore + ")";
    }
}
