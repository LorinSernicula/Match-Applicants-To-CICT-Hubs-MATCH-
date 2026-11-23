package model;

/*
 * stores the result of evaluating one applicant's eligible program
 * only a data holder; MatchingEngine determines the status and score
 */
public class ChoiceResult {

    private final Program program;
    private ChoiceStatus status;
    private double matchingScore;
    private String message;

    public ChoiceResult(Program program, ChoiceStatus status,
                        double matchingScore, String message) {

        this.program = program;
        this.status = status;
        this.matchingScore = matchingScore;
        this.message = message;
    }

    public Program getProgram() {
        return program;
    }

    public ChoiceStatus getStatus() {
        return status;
    }

    public double getMatchingScore() {
        return matchingScore;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(ChoiceStatus status) {
        this.status = status;
    }

    public void setMatchingScore(double matchingScore) {
        this.matchingScore = matchingScore;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}