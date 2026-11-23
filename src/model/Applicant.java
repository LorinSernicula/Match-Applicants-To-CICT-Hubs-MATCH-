package model;

import java.util.ArrayList;
import java.util.Collections;
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

    private final String applicantNumber;
    private final String lastName;
    private final String firstName;
    private final String middleInitial;

    private final List<Program> eligiblePrograms;
    private final Set<Trait> traits;

    public Applicant(String applicantNumber, String lastName,
                     String firstName, String middleInitial) {

        if (applicantNumber == null || applicantNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Applicant number cannot be empty.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }

        this.applicantNumber = applicantNumber.trim();
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
        this.middleInitial = middleInitial == null ? "" : middleInitial.trim();

        this.eligiblePrograms = new ArrayList<Program>();
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

    public String getFullName() {
        if (middleInitial.isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + middleInitial + ". " + lastName;
    }

    public void addEligibleProgram(Program program) {
        if (program != null && !eligiblePrograms.contains(program)) {
            eligiblePrograms.add(program);
        }
    }

    public void removeEligibleProgram(Program program) {
        eligiblePrograms.remove(program);
    }

    public List<Program> getEligiblePrograms() {
        return Collections.unmodifiableList(
                new ArrayList<Program>(eligiblePrograms));
    }

    public void addTrait(Trait trait) {
        if (trait != null) {
            traits.add(trait);
        }
    }

    public void removeTrait(Trait trait) {
        if (trait != null) {
            traits.remove(trait);
        }
    }

    public boolean hasTrait(Trait trait) {
        return traits.contains(trait);
    }

    public Set<Trait> getTraits() {
        return Collections.unmodifiableSet(EnumSet.copyOf(traits));
    }

    public void clearTraits() {
        traits.clear();
    }

    @Override
    public String toString() {
        return applicantNumber + " - " + getFullName();
    }
}
