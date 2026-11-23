package engine;

import model.*;
import config.ProgramConfig;
import java.util.*;

public class MatchingEngine {

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

    // STEP 2: the only ranking method in the project
    public List<RankedProgram> rank(Set<Trait> traits) {
        List<RankedProgram> ranked = new ArrayList<>();
        for (Program p : Program.values()) {
            ranked.add(new RankedProgram(p, countMatches(p, traits)));
        }
        ranked.sort(Comparator
                .comparingInt(RankedProgram::matches).reversed()
                .thenComparingInt(rp -> rp.program().ordinal()));   // stable tie-break
        return ranked;
    }

    private int countMatches(Program p, Set<Trait> traits) {
        Set<Trait> wanted = config.getProfile(p).getTraits();
        int count = 0;
        for (Trait t : traits) {
            if (wanted.contains(t)) count++;
        }
        return count;
    }

    // STEP 3: one applicant
    public MatchResult process(Applicant a) {
        List<RankedProgram> ranked = rank(a.getTraits());

        // Check 1: passing score
        if (a.getCictScore() < config.getPassingScore()) {
            MatchResult failed = new MatchResult(Outcome.FAILED, null, ranked, List.of());
            a.setResult(failed);
            return failed;
        }

        List<ChoiceResult> report = new ArrayList<>();
        Program admittedTo = null;
        Program waitlistTarget = null;   // highest-ranked choice blocked only by FULL

        List<Program> choices = a.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            Program p = choices.get(i);
            int matches = countMatches(p, a.getTraits());
            ChoiceStatus status;

            if (admittedTo != null) {
                status = ChoiceStatus.NOT_REACHED;
            } else if (matches < config.getMinTraitMatches()) {
                status = ChoiceStatus.NOT_ALIGNED;
            } else if (a.getCictScore() < config.getProfile(p).getCutoff()) {
                status = ChoiceStatus.CUTOFF_NOT_MET;
            } else if (!slots.get(p).tryAdmit(a)) {          // breach signal
                status = ChoiceStatus.FULL;
                if (waitlistTarget == null) waitlistTarget = p;
            } else {
                status = ChoiceStatus.ADMITTED;
                admittedTo = p;
            }
            report.add(new ChoiceResult(i + 1, p, matches, status));
        }

        Outcome outcome;
        Program assigned = null;
        if (admittedTo != null) {
            outcome = Outcome.ADMITTED;
            assigned = admittedTo;
        } else if (waitlistTarget != null) {
            outcome = Outcome.WAITLISTED;
            assigned = waitlistTarget;
            waitlists.add(waitlistTarget, a);
        } else {
            outcome = Outcome.NOT_ALIGNED;
        }

        MatchResult result = new MatchResult(outcome, assigned, ranked, report);
        a.setResult(result);
        return result;
    }

    // STEP 4: sort first, then process
    public List<MatchResult> processBatch(List<Applicant> applicants) {
        List<Applicant> sorted = new ArrayList<>(applicants);
        sorted.sort(ApplicantPriority.INSTANCE);

        List<MatchResult> results = new ArrayList<>();
        for (Applicant a : sorted) {
            results.add(process(a));
        }
        return results;
    }
}