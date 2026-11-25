package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import model.Applicant;
import model.Program;

/**
 * Manages one waitlist per {@link Program}, each a {@link PriorityQueue}
 * ordered by {@link ApplicantPriority#INSTANCE} (highest CICT score first,
 * then applicant number).
 *
 * <p>An applicant appears at most once in a given program's waitlist
 * (matched by applicant number). The caller (MatchingEngine) is responsible
 * for only calling {@link #add} when the applicant passed, qualified for the
 * program, but it was full.
 */
public class WaitlistManager {

    /** Returned by {@link #add} when the program or applicant is null. */
    public static final int REJECTED = -1;

    private final Map<Program, PriorityQueue<Applicant>> waitlists = new EnumMap<>(Program.class);

    /** Creates an empty waitlist for every program. */
    public WaitlistManager() {
        for (Program p : Program.values()) {
            waitlists.put(p, new PriorityQueue<>(ApplicantPriority.INSTANCE));
        }
    }

    /**
     * Adds the applicant to the program's waitlist.
     *
     * @return the 1-based position (current position if already on it),
     *         or {@link #REJECTED} if program or applicant is null
     */
    public int add(Program program, Applicant applicant) {
        if (program == null || applicant == null) {
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

    /** Removes and returns the top applicant. Only pops this program's queue. */
    public Optional<Applicant> pollNext(Program program) {
        return Optional.ofNullable(waitlists.get(program).poll());
    }

    /**
     * Promotes the top waitlisted applicant into the program if a slot is open.
     * The applicant is admitted in the given {@link ProgramSlots} (that program's
     * slots) and removed from every waitlist.
     *
     * @return the promoted applicant, or empty if no slot is open / nobody is waiting
     */
    public Optional<Applicant> promoteNext(Program program, ProgramSlots slots) {
        while (slots.hasSlot()) {
            Applicant next = waitlists.get(program).poll();
            if (next == null) {
                return Optional.empty();
            }
            if (slots.tryAdmit(next)) {
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
        String number = applicant.getApplicantNumber();
        return waitlists.get(program).removeIf(a -> a.getApplicantNumber().equals(number));
    }

    /** Removes the applicant from every waitlist. @return how many they were removed from */
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
        String number = applicant.getApplicantNumber();
        for (Applicant a : waitlists.get(program)) {
            if (a.getApplicantNumber().equals(number)) {
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
            if (ordered.get(i).getApplicantNumber().equals(applicant.getApplicantNumber())) {
                return i + 1;
            }
        }
        return 0;
    }

    /** @return read-only snapshot of the waitlist, highest priority first */
    public List<Applicant> getWaitlist(Program program) {
        List<Applicant> copy = new ArrayList<>(waitlists.get(program));
        copy.sort(ApplicantPriority.INSTANCE);
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
