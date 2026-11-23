package model;

/*
 * shared list of applicant/program traits
 *
 * use same enum values in Applicant, ProgramProfile,
 * and MatchingEngine. do not create separate Trait enums
 * in other packages.
 *
 * THIS IS A PLACEHOLDER:
 * please confirm the final trait list with the group before adding,
 * removing, splitting, or renaming any values.
 */
public enum Trait {

    PROBLEM_SOLVING("Problem Solving"),
    LIKES_PROGRAMMING("Likes Programming"),
    ENJOYS_HARDWARE_NETWORKING("Enjoys Hardware and Networking"),
    TECH_SUPPORT_MINDED("Tech Support Minded"),
    BUSINESS_MINDED("Business Minded"),
    EXPERIENCE_IN_3D_MODELING("Experience in 3D Modeling"),
    INTERESTED_IN_ART("Interested in Art");

    private final String displayName;

    Trait(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
