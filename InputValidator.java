package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
 * Member 5: checks raw console input before it is used.
 *
 * Instance methods (not static) because Main creates it with
 * "new InputValidator()" and passes it into ConsoleUI.
 *
 * This class never reads from the keyboard. ConsoleUI owns the Scanner and
 * passes the typed text in, so there is only one Scanner on System.in.
 */
public class InputValidator {

    // PLACEHOLDER: change this one line once the group fixes the real format
    // (currently: 4 to 12 digits, e.g. 20260001)
    private static final Pattern APPLICANT_NUMBER = Pattern.compile("\\d{4,12}");

    private static final Pattern NAME = Pattern.compile("[A-Za-z][A-Za-z .'-]*");

    public boolean isValidApplicantNumber(String s) {
        return s != null && APPLICANT_NUMBER.matcher(s.trim()).matches();
    }

    public boolean isValidName(String s) {
        return s != null && NAME.matcher(s.trim()).matches();
    }

    /* Blank is allowed (no middle initial); otherwise exactly one letter. */
    public boolean isValidMiddleInitial(String s) {
        if (s == null) return false;
        String t = s.trim();
        return t.isEmpty() || (t.length() == 1 && Character.isLetter(t.charAt(0)));
    }

    /* Returns the score, or null if the text is not a number in [min, max]. */
    public Double parseScore(String s, double min, double max) {
        if (s == null) return null;
        try {
            double v = Double.parseDouble(s.trim());
            if (Double.isNaN(v) || Double.isInfinite(v)) return null;
            return (v >= min && v <= max) ? Double.valueOf(v) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /*
     * Parses "1, 3,5" into 0-based indexes (duplicates ignored, order kept).
     * Returns null when invalid: a bad token, a number outside 1..optionCount,
     * or empty input while allowEmpty is false.
     */
    public List<Integer> parseSelection(String line, int optionCount, boolean allowEmpty) {
        List<Integer> picked = new ArrayList<Integer>();
        if (line == null || line.trim().isEmpty()) {
            return allowEmpty ? picked : null;
        }
        for (String token : line.split(",")) {
            try {
                int n = Integer.parseInt(token.trim());
                if (n < 1 || n > optionCount) return null;
                Integer index = Integer.valueOf(n - 1);
                if (!picked.contains(index)) picked.add(index);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return picked;
    }
}
