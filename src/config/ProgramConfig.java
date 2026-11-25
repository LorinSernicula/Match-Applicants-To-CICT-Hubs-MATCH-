package config;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import model.Program;
import model.ProgramProfile;
import model.Trait;

/*
 * provides configuration for CICT programs: profiles, passing score,
 * minimum trait matches, and scoring weights.
 *
 * ALL VALUES BELOW ARE PLACEHOLDERS until the group finalizes the matching criteria.
 */
public class ProgramConfig {

    private static final double PASSING_SCORE = 75.0;
    private static final int MIN_TRAIT_MATCHES = 3;     // core + aligned traits an applicant must match
    private static final double CORE_WEIGHT = 2.0;      // each matched core trait
    private static final double ALIGNED_WEIGHT = 1.0;   // each matched aligned trait
    private static final int SECTION_SIZE = 40;         // capacity = sectionCount * SECTION_SIZE

    private final Map<Program, ProgramProfile> profiles = new EnumMap<>(Program.class);

    public ProgramConfig() {
        add(Program.BSCS, 88.0,
                EnumSet.of(Trait.PROBLEM_SOLVING, Trait.LIKES_PROGRAMMING),
                EnumSet.of(Trait.ENJOYS_HARDWARE_NETWORKING));
        add(Program.BSIT, 85.0,
                EnumSet.of(Trait.ENJOYS_HARDWARE_NETWORKING, Trait.TECH_SUPPORT_MINDED),
                EnumSet.of(Trait.PROBLEM_SOLVING, Trait.LIKES_PROGRAMMING));
        add(Program.BSIS, 80.0,
                EnumSet.of(Trait.BUSINESS_MINDED, Trait.PROBLEM_SOLVING),
                EnumSet.of(Trait.TECH_SUPPORT_MINDED, Trait.LIKES_PROGRAMMING));
        add(Program.BLIS, 75.0,
                EnumSet.of(Trait.STRONG_ORGANIZATION_SKILLS, Trait.BUSINESS_MINDED),
                EnumSet.of(Trait.PROBLEM_SOLVING));
        add(Program.BSEMC, 80.0,
                EnumSet.of(Trait.EXPERIENCE_IN_3D_MODELING, Trait.INTERESTED_IN_ART),
                EnumSet.of(Trait.LIKES_PROGRAMMING));
    }

    private void add(Program program, double threshold,
                     EnumSet<Trait> core, EnumSet<Trait> aligned) {
        int capacity = program.getSectionCount() * SECTION_SIZE;
        profiles.put(program, new ProgramProfile(program, core, aligned, threshold, capacity));
    }

    public ProgramProfile getProfile(Program program) {
        return profiles.get(program);
    }

    public double getPassingScore() {
        return PASSING_SCORE;
    }

    public int getMinTraitMatches() {
        return MIN_TRAIT_MATCHES;
    }

    public double getCoreWeight() {
        return CORE_WEIGHT;
    }

    public double getAlignedWeight() {
        return ALIGNED_WEIGHT;
    }

    public Program[] getPrograms() {
        return Program.values();
    }
}