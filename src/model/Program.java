package model;

/*
 * represents one CICT academic program
 *
 * class stores basic program information only
 */
public enum Program {

    BLIS("Bachelor of Library and Information Science", 1),
    BSIS("Bachelor of Science in Information Systems", 1),
    BSEMC("Bachelor of Science in Entertainment and Multimedia Computing", 1),
    BSCS("Bachelor of Science in Computer Science", 2),
    BSIT("Bachelor of Science in Information Technology", 2);

    private final String fullName;
    private final int sectionCount;
    

    /* onitializes each program's information */
    Program(String fullName, int sectionCount) {
        this.fullName = fullName;
        this.sectionCount = sectionCount;
    }

    public String getCode() {
        return name();
    }

    public String getFullName() {
        return fullName;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    /* calculates total no. of sections across all programs */
    public static int getTotalSections() {
        int total = 0;

        for (Program program : values()) {
            total += program.getSectionCount();
        }

        return total;
    }

    @Override
    public String toString() {
        return getCode();
    }
}