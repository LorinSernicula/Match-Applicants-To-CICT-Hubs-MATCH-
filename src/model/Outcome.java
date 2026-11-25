package model;

/*
 * represents overall result of an applicant's matching run
 */
public enum Outcome {

    NOT_EVALUATED("Not yet evaluated"),
    ADMITTED("Admitted"),
    WAITLISTED("Waitlisted"),
    NOT_ALIGNED("No aligned program among choices"),
    FAILED("Did not reach the passing score");

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
