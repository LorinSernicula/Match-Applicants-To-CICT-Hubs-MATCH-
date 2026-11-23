package model;

/*
 * stores the result of evaluating one applicant's eligible program
 * only a data holder; MatchingEngine determines the status and score
 */
public class ChoiceResult {

    private final Program program;
    private final ChoiceStatus status;
    private final double matchingScore;
    private final String message;

    public ChoiceResult(Program program, ChoiceStatus status,
                        double matchingScore, String message) {

        if (program == null) {
            throw new IllegalArgumentException("Program cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Choice status cannot be null.");
        }

        this.program = program;
        this.status = status;
        this.matchingScore = matchingScore;
        this.message = message == null ? "" : message;
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
}
