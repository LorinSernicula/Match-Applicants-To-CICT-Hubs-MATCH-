package config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import model.Program;
import model.ProgramProfile;
import model.Trait;

/*
 * central config for the CICT programs
 *
 * section counts below follow the group's current information:
 * BLIS (1), BSIS (1), BSEMC (1), BSCS (2), BSIT (2).
 *
 * THESE ARE PLACEHOLDERS:
 * actual student capacity per section, matching thresholds,
 * and official core/aligned trait assignments have not yet been
 * confirmed. please do not treat the placeholder profile values as official.
 */
public final class ProgramConfig {

    private ProgramConfig() {
        // prevents creating instances of this configuration class
    }

    public static final int BLIS_SECTIONS = 1;
    public static final int BSIS_SECTIONS = 1;
    public static final int BSEMC_SECTIONS = 1;
    public static final int BSCS_SECTIONS = 2;
    public static final int BSIT_SECTIONS = 2;

    /*
     * THIS IS A PLACEHOLDER:
     * initial threshold values only.
     * please replace them with agreed scoring cutoffs
     */
    public static final double DEFAULT_SCORE_THRESHOLD = 0.0;

    /*
     * -1 means the actual student-slot capacity is not yet configured
     * section count is stored separately in Program
     */
    public static final int UNKNOWN_CAPACITY = -1;

    public static Map<String, Program> createPrograms() {
        Map<String, Program> programs =
                new LinkedHashMap<String, Program>();

        programs.put("BLIS", new Program(
                "BLIS", "Bachelor of Library and Information Science",
                BLIS_SECTIONS));

        programs.put("BSIS", new Program(
                "BSIS", "Bachelor of Science in Information Systems",
                BSIS_SECTIONS));

        programs.put("BSEMC", new Program(
                "BSEMC", "Bachelor of Science in Entertainment and Multimedia Computing",
                BSEMC_SECTIONS));

        programs.put("BSCS", new Program(
                "BSCS", "Bachelor of Science in Computer Science",
                BSCS_SECTIONS));

        programs.put("BSIT", new Program(
                "BSIT", "Bachelor of Science in Information Technology",
                BSIT_SECTIONS));

        return Collections.unmodifiableMap(programs);
    }

    /*
     * creates placeholder profiles for all five programs
     *
     * empty trait sets are intentional: the group has not yet
     * supplied the official core and aligned trait mapping
     *
     * replace empty sets and the threshold/capacity values
     * once ProgramProfile requirements are finalized
     */
    public static Map<String, ProgramProfile> createDefaultProfiles() {
        Map<String, Program> programs = createPrograms();
        Map<String, ProgramProfile> profiles =
                new LinkedHashMap<String, ProgramProfile>();

        for (Map.Entry<String, Program> entry : programs.entrySet()) {
            ProgramProfile profile = new ProgramProfile(
                    entry.getValue(),
                    Collections.<Trait>emptySet(),
                    Collections.<Trait>emptySet(),
                    DEFAULT_SCORE_THRESHOLD,
                    UNKNOWN_CAPACITY
            );

            profiles.put(entry.getKey(), profile);
        }

        return Collections.unmodifiableMap(profiles);
    }
}
