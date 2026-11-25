package engine;

import java.util.Comparator;
import model.Applicant;

/*
 * single rule for ranking applicants against each other:
 * higher CICT score first, then applicant number as a stable tie-break.
 * used by MatchingEngine, WaitlistManager, and roster views.
 */
public final class ApplicantPriority implements Comparator<Applicant> {

    public static final ApplicantPriority INSTANCE = new ApplicantPriority();

    private ApplicantPriority() { }

    @Override
    public int compare(Applicant a, Applicant b) {
        int byScore = Double.compare(b.getCictScore(), a.getCictScore()); // descending
        if (byScore != 0) {
            return byScore;
        }
        return a.getApplicantNumber().compareTo(b.getApplicantNumber());
    }
}
