package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import model.Applicant;

/*
 * tracks admitted applicants for ONE program and signals when capacity is reached.
 * a capacity of -1 means unknown, which is treated as unlimited.
 */
public class ProgramSlots {

    private final int capacity;
    private final List<Applicant> admitted = new ArrayList<>();

    public ProgramSlots(int capacity) {
        this.capacity = capacity;
    }

    public boolean isCapacityKnown() {
        return capacity >= 0;
    }

    public boolean isFull() {
        return isCapacityKnown() && admitted.size() >= capacity;
    }

    public int remaining() {
        return isCapacityKnown() ? capacity - admitted.size() : Integer.MAX_VALUE;
    }

    /* returns false when full: this is the breach signal the engine reads */
    public boolean tryAdmit(Applicant applicant) {
        if (applicant == null || isFull() || admitted.contains(applicant)) {
            return false;
        }
        admitted.add(applicant);
        return true;
    }

    /* frees a slot, e.g. when a waitlisted applicant is promoted elsewhere */
    public boolean release(Applicant applicant) {
        return admitted.remove(applicant);
    }

    public List<Applicant> getAdmitted() {
        List<Applicant> copy = new ArrayList<>(admitted);
        copy.sort(ApplicantPriority.INSTANCE);
        return Collections.unmodifiableList(copy);
    }

    /* the lowest score currently holding a seat: the effective cutoff */
    public OptionalDouble lowestAdmittedScore() {
        return admitted.stream().mapToDouble(Applicant::getCictScore).min();
    }
}