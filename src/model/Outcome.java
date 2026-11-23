package model;

/*
 * represents overall result of an applicant's matching run
 * these are recommendation outcomes, not official admission decisions
 */
public enum Outcome {

    NOT_EVALUATED("Not yet evaluated"),
    MATCH_FOUND("Matching programs found"),
    NO_MATCH_FOUND("No matching programs found");

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
