package model;

import java.util.EnumSet;
import java.util.Set;

/*
 * stores the matching profile for one CICT program
 *
 * core traits are higher-priority traits; aligned traits are additional traits considered by MatchingEngine
 */
public class ProgramProfile {

    private final Program program;
    private EnumSet<Trait> coreTraits;
    private EnumSet<Trait> alignedTraits;
    private double scoreThreshold;
    private int capacity;

    public ProgramProfile(Program program, Set<Trait> coreTraits,
                          Set<Trait> alignedTraits,
                          double scoreThreshold, int capacity) {

        this.program = program;
        this.coreTraits = copyTraits(coreTraits);
        this.alignedTraits = copyTraits(alignedTraits);
        this.scoreThreshold = scoreThreshold;
        this.capacity = capacity;
    }

    private EnumSet<Trait> copyTraits(Set<Trait> traits) {
        EnumSet<Trait> copy = EnumSet.noneOf(Trait.class);

        if (traits != null) {
            copy.addAll(traits);
        }

        return copy;
    }

    public Program getProgram() {
        return program;
    }

    public Set<Trait> getCoreTraits() {
        return EnumSet.copyOf(coreTraits);
    }

    public Set<Trait> getAlignedTraits() {
        return EnumSet.copyOf(alignedTraits);
    }

    public double getScoreThreshold() {
        return scoreThreshold;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCoreTraits(Set<Trait> coreTraits) {
        this.coreTraits = copyTraits(coreTraits);
    }

    public void setAlignedTraits(Set<Trait> alignedTraits) {
        this.alignedTraits = copyTraits(alignedTraits);
    }

    public void setScoreThreshold(double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}