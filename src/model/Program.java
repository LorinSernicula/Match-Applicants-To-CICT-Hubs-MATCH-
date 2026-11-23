package model;

/*
 * represents one CICT academic program
 *
 * class stores basic program information only
 */
public class Program {

    private final String code;
    private final String name;
    private final int sectionCount;

    public Program(String code, String name, int sectionCount) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Program code cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Program name cannot be empty.");
        }
        if (sectionCount < 0) {
            throw new IllegalArgumentException("Section count cannot be negative.");
        }

        this.code = code.trim().toUpperCase();
        this.name = name.trim();
        this.sectionCount = sectionCount;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Program)) {
            return false;
        }
        Program other = (Program) object;
        return code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
