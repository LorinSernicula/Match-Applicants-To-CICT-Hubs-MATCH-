package model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/*
 * stores the matching profile for one CICT program
 *
 * core traits are higher-priority traits; aligned traits are additional traits considered by MatchingEngine
 */
public class ProgramProfile {

    private final Program program;
    private final Set<Trait> coreTraits;
    private final Set<Trait> alignedTraits;
    private final double scoreThreshold;

    /*
     * capacity is the number of available student slots, not sections
     * use -1 when the actual numerical capacity has not been configured
     */
    private final int capacity;

    public ProgramProfile(Program program, Set<Trait> coreTraits,
                          Set<Trait> alignedTraits,
                          double scoreThreshold, int capacity) {

        if (program == null) {
            throw new IllegalArgumentException("Program cannot be null.");
        }
        if (scoreThreshold < 0.0 || scoreThreshold > 100.0) {
            throw new IllegalArgumentException(
                    "Score threshold must be between 0 and 100.");
        }
        if (capacity < -1) {
            throw new IllegalArgumentException(
                    "Capacity must be -1 (unknown) or a non-negative number.");
        }

        this.program = program;
        this.coreTraits = copyTraits(coreTraits);
        this.alignedTraits = copyTraits(alignedTraits);
        this.scoreThreshold = scoreThreshold;
        this.capacity = capacity;
    }

    private Set<Trait> copyTraits(Set<Trait> source) {
        if (source == null || source.isEmpty()) {
            return EnumSet.noneOf(Trait.class);
        }
        return EnumSet.copyOf(source);
    }

    public Program getProgram() {
        return program;
    }

    public Set<Trait> getCoreTraits() {
        return Collections.unmodifiableSet(copyTraits(coreTraits));
    }

    public Set<Trait> getAlignedTraits() {
        return Collections.unmodifiableSet(copyTraits(alignedTraits));
    }

    public double getScoreThreshold() {
        return scoreThreshold;
    }

    public int getCapacity() {
        return capacity;
    }
}
