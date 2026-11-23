package model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/*
 * stores applicant's identifying information, eligible CICT
 * programs, and selected/identified traits
 *
 * does not validate admission eligibility or calculate program matches
 */
public class Applicant {

    private String applicantNumber;
    private String lastName;
    private String firstName;
    private String middleInitial;

    private double cictScore;

    public double getCictScore() { return cictScore; }
    public void setCictScore(double cictScore) { this.cictScore = cictScore; }

    private List<Program> eligiblePrograms;
    private EnumSet<Trait> traits;

    public Applicant(String applicantNumber, String lastName,
                     String firstName, String middleInitial) {

        this.applicantNumber = applicantNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleInitial = middleInitial;

        this.eligiblePrograms = new ArrayList<>();
        this.traits = EnumSet.noneOf(Trait.class);
    }

    public String getApplicantNumber() {
        return applicantNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    /* Returns a copy so the original list cannot be modified externally. */
    public List<Program> getEligiblePrograms() {
        return new ArrayList<>(eligiblePrograms);
    }

    public void addEligibleProgram(Program program) {
        if (program != null && !eligiblePrograms.contains(program)) {
            eligiblePrograms.add(program);
        }
    }

    public void removeEligibleProgram(Program program) {
        eligiblePrograms.remove(program);
    }

    public boolean isEligibleFor(Program program) {
        return eligiblePrograms.contains(program);
    }

    /* Adds or removes traits recorded for the applicant. */
    public void addTrait(Trait trait) {
        if (trait != null) {
            traits.add(trait);
        }
    }

    public void removeTrait(Trait trait) {
        traits.remove(trait);
    }

    public boolean hasTrait(Trait trait) {
        return traits.contains(trait);
    }

    public Set<Trait> getTraits() {
        EnumSet<Trait> copy = EnumSet.noneOf(Trait.class);
        copy.addAll(traits);
        return copy;
    }

    public void clearTraits() {
        traits.clear();
    }

    public String getFullName() {
        String fullName = firstName;

        if (middleInitial != null && !middleInitial.trim().isEmpty()) {
            fullName += " " + middleInitial;
        }

        fullName += " " + lastName;

        return fullName;
    }
}