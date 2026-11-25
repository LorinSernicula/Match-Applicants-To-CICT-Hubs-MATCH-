package ui;

/* validates raw console input; never prints. (stub: Member 5 owns the final rules) */
public class InputValidator {

    /* accepts digits and dashes, e.g. 2026-0042 */
    public boolean isValidApplicantNumber(String number) {
        return number != null && number.matches("[0-9]{4}-?[0-9]{3,6}");
    }
}
