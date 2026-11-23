package engine;

import model.*;
import config.ProgramConfig;
import java.util.*;


public class MatchingEngine {
    //loops over choices
    //ranks traits
    //traits aligned most to what course
    //ranks applicants based on how aligned they are and their CICT applicant exam 


    //this initializes the values

    private final ProgramConfig config;
    private final WaitlistManager waitlists;
    private final Map<Program, ProgramSlots> slots = new EnumMap<>(Program.class);

    public MatchingEngine(ProgramConfig config, WaitlistManager waitlists) {
        this.config = config;
        this.waitlists = waitlists;
        for (Program p : Program.values()) {
            slots.put(p, new ProgramSlots(config.getProfile(p).getCapacity()));
        }
    }

    //ranks ranked programs with set traits

    public List<RankedProgram> rank(Set<Trait> traits) {
        List<RankedProgram> ranked = new ArrayList<>();
        for (Program p : Program.values()) {
            ranked.add(new RankedProgram(p, countMatches(p. traits)));
        }
        

    }


}
