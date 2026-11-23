package model;

/*
 * represents result/status of evaluating one eligible
 * program choice; separate from the overall Outcome
 *
 * THIS IS A PLACEHOLDER:
 * may add statuses if the final matching workflow
 * requires more detailed per-choice results
 */
public enum ChoiceStatus {

    NOT_EVALUATED("Not evaluated"),
    MATCHED("Matched"),
    NOT_MATCHED("Not matched"),
    NOT_ELIGIBLE("Not eligible");

    private final String description;

    ChoiceStatus(String description) {
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
