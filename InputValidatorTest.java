package ui;

import java.util.Arrays;
import java.util.List;
import model.Applicant;

/* Plain-Java tests (no JUnit needed). Run: java -cp out ui.InputValidatorTest */
public class InputValidatorTest {

    private static int passed = 0, failed = 0;

    private static void check(String name, boolean ok) {
        if (ok) passed++; else { failed++; System.out.println("FAIL: " + name); }
    }

    public static void main(String[] args) {
        InputValidator v = new InputValidator();

        // applicant number
        check("good number", v.isValidApplicantNumber("20260001"));
        check("number trimmed", v.isValidApplicantNumber(" 20260001 "));
        check("null number", !v.isValidApplicantNumber(null));
        for (String s : TestData.BAD_APPLICANT_NUMBERS)
            check("bad number [" + s + "]", !v.isValidApplicantNumber(s));

        // names
        for (String s : new String[] { "Ana", "Dela Cruz", "O'Brien", "Garcia-Lopez", "Jr." })
            check("good name " + s, v.isValidName(s));
        for (String s : TestData.BAD_NAMES)
            check("bad name [" + s + "]", !v.isValidName(s));
        check("null name", !v.isValidName(null));

        // middle initial
        check("mi blank", v.isValidMiddleInitial(""));
        check("mi letter", v.isValidMiddleInitial("m"));
        check("mi two letters", !v.isValidMiddleInitial("mm"));
        check("mi digit", !v.isValidMiddleInitial("1"));

        // score
        check("score 0", v.parseScore("0", 0, 100) != null);
        check("score 100", v.parseScore("100", 0, 100) != null);
        check("score 87.5", v.parseScore(" 87.5 ", 0, 100) == 87.5);
        for (String s : TestData.BAD_SCORES)
            check("bad score [" + s + "]", v.parseScore(s, 0, 100) == null);

        // selection
        check("sel 1,3", v.parseSelection("1,3", 7, false).equals(Arrays.asList(0, 2)));
        check("sel spaces", v.parseSelection(" 2 , 4 ", 7, false).equals(Arrays.asList(1, 3)));
        check("sel dupes", v.parseSelection("2,2,2", 7, false).equals(Arrays.asList(1)));
        check("sel blank allowed", v.parseSelection("", 7, true).isEmpty());
        check("sel blank refused", v.parseSelection("", 7, false) == null);
        for (String s : TestData.BAD_SELECTIONS)
            check("bad selection [" + s + "]", v.parseSelection(s, 7, true) == null);

        // navigator
        List<Applicant> data = TestData.sampleApplicants();
        ApplicantNavigator nav = new ApplicantNavigator(data);
        check("nav size", nav.size() == data.size());
        check("nav starts at 1", nav.position() == 1);
        check("prev at start stays", nav.previous().getApplicantNumber().equals("20260001"));
        check("next", nav.next().getApplicantNumber().equals("20260002"));
        check("jump found", nav.jumpToNumber("20260010")
                && nav.current().getApplicantNumber().equals("20260010"));
        check("jump missing does not move", !nav.jumpToNumber("99999999")
                && nav.current().getApplicantNumber().equals("20260010"));
        check("last", nav.last().getApplicantNumber().equals("20260012") && !nav.hasNext());
        check("next at end stays", nav.next().getApplicantNumber().equals("20260012"));
        check("first", nav.first().getApplicantNumber().equals("20260001") && !nav.hasPrevious());

        ApplicantNavigator empty = new ApplicantNavigator(new java.util.ArrayList<Applicant>());
        check("empty current null", empty.current() == null && empty.isEmpty());
        check("empty next safe", empty.next() == null && empty.previous() == null);
        check("empty position 0", empty.position() == 0);

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
