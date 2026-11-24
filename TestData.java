package ui;

import java.util.ArrayList;
import java.util.List;
import model.Applicant;
import model.Program;
import model.Trait;

/* Member 5: sample applicants for the whole team. Covers edge cases on purpose. */
public class TestData {

    private static Applicant make(String no, String last, String first, String mi,
                                  double score, Program[] programs, Trait... traits) {
        Applicant a = new Applicant(no, last, first, mi);
        a.setCictScore(score);
        for (Program p : programs) a.addEligibleProgram(p);
        for (Trait t : traits) a.addTrait(t);
        return a;
    }

    public static List<Applicant> sampleApplicants() {
        Program[] all = Program.values();
        List<Applicant> list = new ArrayList<Applicant>();

        // clear single-hub profiles
        list.add(make("20260001", "Reyes", "Ana", "M", 92.5, all,
                Trait.PROBLEM_SOLVING, Trait.LIKES_PROGRAMMING));
        list.add(make("20260002", "Santos", "Ben", "", 88.0, all,
                Trait.ENJOYS_HARDWARE_NETWORKING, Trait.TECH_SUPPORT_MINDED));
        list.add(make("20260003", "Cruz", "Carla", "D", 85.0, all,
                Trait.BUSINESS_MINDED));
        list.add(make("20260004", "Lim", "Dan", "T", 80.0, all,
                Trait.EXPERIENCE_IN_3D_MODELING, Trait.INTERESTED_IN_ART));

        // EDGE: no traits at all
        list.add(make("20260005", "Tan", "Ella", "", 75.0, all));
        // EDGE: every trait selected
        list.add(make("20260006", "Uy", "Finn", "G", 70.0, all, Trait.values()));
        // EDGE: boundary scores (0 and 100)
        list.add(make("20260007", "Go", "Gia", "", 100.0, all, Trait.PROBLEM_SOLVING));
        list.add(make("20260008", "Ong", "Hal", "", 0.0, all, Trait.BUSINESS_MINDED));
        // EDGE: ties, same score and same traits as 20260001's scoring peers
        list.add(make("20260009", "Dela Cruz", "Ivy", "A", 92.5, all,
                Trait.PROBLEM_SOLVING, Trait.LIKES_PROGRAMMING));
        // EDGE: only one eligible program (tests slot-full / waitlist paths)
        list.add(make("20260010", "O'Brien", "Jon", "P",  60.0,
                new Program[] { Program.BSCS }, Trait.LIKES_PROGRAMMING));
        list.add(make("20260011", "Garcia-Lopez", "Kim", "", 59.9,
                new Program[] { Program.BSCS }, Trait.LIKES_PROGRAMMING));
        // EDGE: no eligible programs
        list.add(make("20260012", "Ramos", "Lee", "", 50.0, new Program[0],
                Trait.PROBLEM_SOLVING));
        return list;
    }

    /* Inputs the validator must REJECT. */
    public static final String[] BAD_APPLICANT_NUMBERS =
            { "", "   ", "abc", "12", "2026-0001", "2026 0001", "1234567890123", "-20260001" };
    public static final String[] BAD_NAMES =
            { "", "   ", "123", "Ana3", "@na", "-Ana" };
    public static final String[] BAD_SCORES =
            { "", "abc", "-1", "100.1", "NaN", "Infinity", "1e999" };
    public static final String[] BAD_SELECTIONS =
            { "0", "8", "1,,2", "a", "1;2", "-1", "1,9" };
}
