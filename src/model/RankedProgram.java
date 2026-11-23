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
    private double corePriorityScore;
    private double traitAlignmentScore;
    private double totalScore;

    public RankedProgram(Program program, double corePriorityScore,
                         double traitAlignmentScore, double totalScore) {

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

    public void setCorePriorityScore(double corePriorityScore) {
        this.corePriorityScore = corePriorityScore;
    }

    public void setTraitAlignmentScore(double traitAlignmentScore) {
        this.traitAlignmentScore = traitAlignmentScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}