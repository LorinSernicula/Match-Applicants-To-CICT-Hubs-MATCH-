package config;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import model.Program;
import model.ProgramProfile;
import model.Trait;

/*
 * provides initial configuration for CICT programs
 *
 * program constants are declared in Program.java; this class creates the profiles associated with those programs
 *
 * core traits, aligned traits, score thresholds, and capacities
 * are placeholders until the group finalizes the matching criteria
 */
public class ProgramConfig {

    private ProgramConfig() {
        // prevents the creation of unnecessary ProgramConfig objects
    }

    /*
     * creates a profile for every available CICT program
     *
     * empty trait sets and placeholder values are used for now;
     * REPLACE THEM WHEN THE OFFICIAL MATCHING CRITERIA ARE FINALIZED
     *
     * a capacity of -1 means that the capacity is currently unknown
     */
    public static Map<Program, ProgramProfile> createDefaultProfiles() {

        Map<Program, ProgramProfile> profiles =
                new EnumMap<>(Program.class);

        Set<Trait> emptyTraits = Collections.emptySet();

        for (Program program : Program.values()) {

            ProgramProfile profile = new ProgramProfile(
                    program,
                    emptyTraits,
                    emptyTraits,
                    0.0,
                    -1
            );

            profiles.put(program, profile);
        }

        return profiles;
    }

    /*
     * returns all available CICT programs
     * the actual program constants are stored in Program.java
     */
    public static Program[] getPrograms() {
        return Program.values();
    }
}