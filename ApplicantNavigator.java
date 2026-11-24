package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import model.Applicant;

/*
 * Member 5: moves through a list of applicants (first / next / previous /
 * last / jump by applicant number).
 *
 * Works on a snapshot of repo.getAll(). After applicants are added, call
 * refresh(repo.getAll()) so the navigator sees them.
 */
public class ApplicantNavigator {

    private final List<Applicant> applicants = new ArrayList<Applicant>();
    private int index = 0;

    public ApplicantNavigator(Collection<Applicant> source) {
        refresh(source);
    }

    /* Reloads the list and stays on the same applicant if still present. */
    public void refresh(Collection<Applicant> source) {
        String keep = isEmpty() ? null : current().getApplicantNumber();
        applicants.clear();
        if (source != null) applicants.addAll(source);
        index = 0;
        if (keep != null) jumpToNumber(keep);
    }

    public boolean isEmpty()     { return applicants.isEmpty(); }
    public int size()            { return applicants.size(); }
    public int position()        { return isEmpty() ? 0 : index + 1; }   // 1-based
    public boolean hasNext()     { return index < applicants.size() - 1; }
    public boolean hasPrevious() { return index > 0; }

    /* Returns null when the list is empty. */
    public Applicant current() {
        return isEmpty() ? null : applicants.get(index);
    }

    /* next/previous stay put at the ends and return the current applicant. */
    public Applicant next() {
        if (hasNext()) index++;
        return current();
    }

    public Applicant previous() {
        if (hasPrevious()) index--;
        return current();
    }

    public Applicant first() {
        index = 0;
        return current();
    }

    public Applicant last() {
        index = Math.max(0, applicants.size() - 1);
        return current();
    }

    /* Returns false (and does not move) if the number is not found. */
    public boolean jumpToNumber(String applicantNumber) {
        if (applicantNumber == null) return false;
        String wanted = applicantNumber.trim();
        for (int i = 0; i < applicants.size(); i++) {
            if (applicants.get(i).getApplicantNumber().equals(wanted)) {
                index = i;
                return true;
            }
        }
        return false;
    }
}
