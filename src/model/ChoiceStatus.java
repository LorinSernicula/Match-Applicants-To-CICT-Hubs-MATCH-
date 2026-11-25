package model;

/*
 * represents result/status of evaluating one applied program choice;
 * separate from the overall Outcome
 */
public enum ChoiceStatus {

    NOT_EVALUATED("Not evaluated"),
    ADMITTED("Admitted"),
    NOT_ALIGNED("Not aligned (too few matching traits)"),
    CUTOFF_NOT_MET("Score below program threshold"),
    FULL("Program full"),
    NOT_REACHED("Not reached");

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
