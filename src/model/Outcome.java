package model;

/*
 * represents overall result of an applicant's matching run
 * these are recommendation outcomes, not official admission decisions
 */
public enum Outcome {

    NOT_EVALUATED("Not Yet Evaluated"),
    MATCH_FOUND("Matching Program Found"),
    NO_MATCH_FOUND("No Matching Program Found");

    private final String description;

    Outcome(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
