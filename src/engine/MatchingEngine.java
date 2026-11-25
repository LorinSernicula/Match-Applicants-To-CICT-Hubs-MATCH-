package engine;

import config.ProgramConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import model.Applicant;
import model.ChoiceResult;
import model.ChoiceStatus;
import model.MatchResult;
import model.Outcome;
import model.Program;
import model.ProgramProfile;
import model.RankedProgram;
import model.Trait;

/*
 * does ALL computation: trait scoring, program ranking, choice evaluation,
 * slot checks, and waitlisting. rank() is the only ranking method.
 *
 * scoring: core = matched core traits * coreWeight
 *          aligned = matched aligned traits * alignedWeight
 *          total = core + aligned
 * a program is "aligned" only if raw matched traits >= minTraitMatches.
 *
 * choices are evaluated in the applicant's ranked order; the first choice that
 * passes (aligned, score >= threshold, slot open) is ADMITTED.
 */
public class MatchingEngine {

    private final ProgramConfig config;
    private final WaitlistManager waitlists;
    private final Map<Program, ProgramSlots> slots = new EnumMap<>(Program.class);
    private final Map<String, MatchResult> processed = new HashMap<>();

    public MatchingEngine(ProgramConfig config, WaitlistManager waitlists) {
        this.config = config;
        this.waitlists = waitlists;
        for (Program p : Program.values()) {
            slots.put(p, new ProgramSlots(config.getProfile(p).getCapacity()));
        }
    }

    /* the only ranking method: all programs, best fit first */
    public List<RankedProgram> rank(Set<Trait> traits) {
        List<RankedProgram> ranked = new ArrayList<>();
        for (Program p : Program.values()) {
            ranked.add(score(p, traits));
        }
        ranked.sort(Comparator
                .comparingDouble(RankedProgram::getTotalScore).reversed()
                .thenComparing(Comparator.comparingInt(RankedProgram::getMatchedTraitCount).reversed())
                .thenComparingInt(rp -> rp.getProgram().ordinal()));
        return ranked;
    }

    private RankedProgram score(Program p, Set<Trait> traits) {
        ProgramProfile profile = config.getProfile(p);
        int core = 0;
        int aligned = 0;
        for (Trait t : traits) {
            if (profile.getCoreTraits().contains(t)) {
                core++;
            } else if (profile.getAlignedTraits().contains(t)) {
                aligned++;
            }
        }
        double coreScore = core * config.getCoreWeight();
        double alignedScore = aligned * config.getAlignedWeight();
        return new RankedProgram(p, coreScore, alignedScore, coreScore + alignedScore, core + aligned);
    }

    /* evaluates one applicant. already-processed applicants return their stored result. */
    public MatchResult process(Applicant a) {
        MatchResult existing = processed.get(a.getApplicantNumber());
        if (existing != null) {
            return existing;
        }

        MatchResult result = new MatchResult(a);
        List<RankedProgram> ranked = rank(a.getTraits());
        Map<Program, RankedProgram> byProgram = new EnumMap<>(Program.class);
        for (RankedProgram rp : ranked) {
            result.addRankedProgram(rp);
            byProgram.put(rp.getProgram(), rp);
        }

        processed.put(a.getApplicantNumber(), result);

        if (a.getCictScore() < config.getPassingScore()) {
            result.setOutcome(Outcome.FAILED);
            return result;
        }

        Program admittedTo = null;
        Program waitlistTarget = null;   // first choice blocked only by being full
        List<Program> choices = a.getEligiblePrograms();

        for (int i = 0; i < choices.size(); i++) {
            Program p = choices.get(i);
            RankedProgram rp = byProgram.get(p);
            ChoiceStatus status;
            String message;

            if (admittedTo != null) {
                status = ChoiceStatus.NOT_REACHED;
                message = "An earlier choice was admitted.";
            } else if (rp.getMatchedTraitCount() < config.getMinTraitMatches()) {
                status = ChoiceStatus.NOT_ALIGNED;
                message = rp.getMatchedTraitCount() + " matching trait(s); needs "
                        + config.getMinTraitMatches() + ".";
            } else if (a.getCictScore() < config.getProfile(p).getScoreThreshold()) {
                status = ChoiceStatus.CUTOFF_NOT_MET;
                message = "Score " + a.getCictScore() + " is below "
                        + config.getProfile(p).getScoreThreshold() + ".";
            } else if (!slots.get(p).tryAdmit(a)) {
                status = ChoiceStatus.FULL;
                message = "No slots left.";
                if (waitlistTarget == null) {
                    waitlistTarget = p;
                }
            } else {
                status = ChoiceStatus.ADMITTED;
                message = "Admitted.";
                admittedTo = p;
            }
            result.addChoiceResult(new ChoiceResult(i + 1, p, status, rp.getTotalScore(), message));
        }

        if (admittedTo != null) {
            result.setOutcome(Outcome.ADMITTED);
            result.setAssignedProgram(admittedTo);
        } else if (waitlistTarget != null) {
            result.setOutcome(Outcome.WAITLISTED);
            result.setAssignedProgram(waitlistTarget);
            waitlists.add(waitlistTarget, a);
        } else {
            result.setOutcome(Outcome.NOT_ALIGNED);
        }
        return result;
    }

    /* sorts by priority (score desc) first, so higher scorers claim slots first */
    public List<MatchResult> processBatch(List<Applicant> applicants) {
        List<Applicant> sorted = new ArrayList<>(applicants);
        sorted.sort(ApplicantPriority.INSTANCE);

        List<MatchResult> results = new ArrayList<>();
        for (Applicant a : sorted) {
            results.add(process(a));
        }
        return results;
    }

    /*
     * fills an open slot from the program's waitlist and updates that
     * applicant's stored result. caller should re-save the result in the repository.
     */
    public Optional<MatchResult> promoteNext(Program program) {
        Optional<Applicant> promoted = waitlists.promoteNext(program, slots.get(program));
        if (!promoted.isPresent()) {
            return Optional.empty();
        }
        MatchResult r = processed.get(promoted.get().getApplicantNumber());
        if (r != null) {
            r.setOutcome(Outcome.ADMITTED);
            r.setAssignedProgram(program);
            for (ChoiceResult c : r.getChoiceResults()) {
                // getChoiceResults returns a copy of the list, but the elements are shared
                if (c.getProgram() == program) {
                    c.setStatus(ChoiceStatus.ADMITTED);
                    c.setMessage("Promoted from waitlist.");
                }
            }
        }
        return Optional.ofNullable(r);
    }

    public ProgramSlots getSlots(Program program) {
        return slots.get(program);
    }
}
