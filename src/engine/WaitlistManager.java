package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import model.Applicant;
import model.Program;

/**
 * Manages one waitlist per {@link Program}.
 *
 * <p>Each waitlist is a {@link PriorityQueue} ordered by
 * {@code new ApplicantPriority(program)}, so the strongest waitlisted applicant
 * for that program (most trait matches, then highest CICT score, then lowest
 * applicant number) is always at the front.
 *
 * <p>Business rules enforced here:
 * <ul>
 *   <li>An applicant who failed the CICT exam is never added to any waitlist.</li>
 *   <li>An applicant appears at most once in a given program's waitlist
 *       (matched by applicant number).</li>
 * </ul>
 * The caller (MatchingEngine) is responsible for only calling {@link #add}
 * when the applicant qualified for the program but it was full.
 */
public class WaitlistManager {

    /** Returned by {@link #add} when the applicant was rejected (failed exam / null). */
    public static final int REJECTED = -1;

    private final Map<Program, PriorityQueue<Applicant>> waitlists = new HashMap<>();

    /** Creates an empty waitlist for every program. */
    public WaitlistManager() {
        for (Program p : Program.values()) {
            waitlists.put(p, new PriorityQueue<>(new ApplicantPriority(p)));
        }
    }

    /**
     * Adds the applicant to the program's waitlist.
     *
     * @return the applicant's 1-based position in that waitlist; if they were
     *         already on it, their current position; {@link #REJECTED} if the
     *         applicant is null or failed the exam
     */
    public int add(Program program, Applicant applicant) {
        if (program == null || applicant == null || applicant.hasFailedExam()) {
            return REJECTED;
        }
        if (!contains(program, applicant)) {
            waitlists.get(program).add(applicant);
        }
        return getPosition(program, applicant);
    }

    /** @return the highest-priority waitlisted applicant without removing them */
    public Optional<Applicant> peek(Program program) {
        return Optional.ofNullable(waitlists.get(program).peek());
    }

    /**
     * Removes and returns the highest-priority waitlisted applicant.
     * Note: this only pops this program's queue; use {@link #promoteNext}
     * to also take the slot and clean up other waitlists.
     */
    public Optional<Applicant> pollNext(Program program) {
        return Optional.ofNullable(waitlists.get(program).poll());
    }

    /**
     * Promotes the top waitlisted applicant into the program if a slot is open.
     * The applicant is admitted in {@link ProgramSlots} and removed from every
     * waitlist (they now hold a seat). Callers should then update the
     * applicant's stored result (e.g. in ApplicantRepository).
     *
     * @return the promoted applicant, or empty if no slot is open / nobody is waiting
     */
    public Optional<Applicant> promoteNext(Program program, ProgramSlots slots) {
        while (slots.hasSlot(program)) {
            Applicant next = waitlists.get(program).poll();
            if (next == null) {
                return Optional.empty();
            }
            if (slots.tryAdmit(program, next)) {
                removeFromAll(next);
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }

    /** Removes the applicant from one program's waitlist. @return true if removed */
    public boolean remove(Program program, Applicant applicant) {
        if (applicant == null) {
            return false;
        }
        int number = applicant.getApplicantNumber();
        return waitlists.get(program).removeIf(a -> a.getApplicantNumber() == number);
    }

    /**
     * Removes the applicant from every waitlist (admitted elsewhere or withdrew).
     *
     * @return how many waitlists they were removed from
     */
    public int removeFromAll(Applicant applicant) {
        int removed = 0;
        for (Program p : Program.values()) {
            if (remove(p, applicant)) {
                removed++;
            }
        }
        return removed;
    }

    /** @return true if the applicant is on the program's waitlist */
    public boolean contains(Program program, Applicant applicant) {
        if (applicant == null) {
            return false;
        }
        int number = applicant.getApplicantNumber();
        for (Applicant a : waitlists.get(program)) {
            if (a.getApplicantNumber() == number) {
                return true;
            }
        }
        return false;
    }

    /** @return 1-based position in the program's waitlist, or 0 if not on it */
    public int getPosition(Program program, Applicant applicant) {
        if (applicant == null) {
            return 0;
        }
        List<Applicant> ordered = getWaitlist(program);
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getApplicantNumber() == applicant.getApplicantNumber()) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * @return read-only snapshot of the waitlist, highest priority first
     *         (safe for ConsoleUI to display)
     */
    public List<Applicant> getWaitlist(Program program) {
        List<Applicant> copy = new ArrayList<>(waitlists.get(program));
        copy.sort(new ApplicantPriority(program));
        return Collections.unmodifiableList(copy);
    }

    /** @return number of applicants waiting for the program */
    public int size(Program program) {
        return waitlists.get(program).size();
    }

    /** @return number of waitlist entries across all programs */
    public int totalSize() {
        int total = 0;
        for (PriorityQueue<Applicant> q : waitlists.values()) {
            total += q.size();
        }
        return total;
    }

    /** @return true if the program's waitlist is empty */
    public boolean isEmpty(Program program) {
        return waitlists.get(program).isEmpty();
    }

    /** Empties every waitlist. */
    public void clear() {
        for (PriorityQueue<Applicant> q : waitlists.values()) {
            q.clear();
        }
    }
}
