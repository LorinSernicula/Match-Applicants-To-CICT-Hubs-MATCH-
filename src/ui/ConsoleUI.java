package ui;

import engine.MatchingEngine;
import engine.WaitlistManager;
import model.Applicant;
import model.ChoiceResult;
import model.MatchResult;
import model.Program;
import model.RankedProgram;
import model.Trait;
import repository.ApplicantRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/*
 * Member 4: console intake form, menus, prompts and result printing.
 *
 * Intake flow per applicant:
 *   applicant no. -> last name -> first name -> middle initial
 *   -> trait checklist -> ranked program choices -> CICT score -> run matching
 *
 * This class only collects input and prints output. All matching, ranking
 * and waitlisting is done by MatchingEngine.
 */
public class ConsoleUI {

    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 100.0;   // placeholder: adjust to the real CICT scale

    private final Scanner in = new Scanner(System.in);
    private final ApplicantRepository repo;
    private final MatchingEngine engine;
    private final WaitlistManager waitlists;
    private final InputValidator validator;

    public ConsoleUI(ApplicantRepository repo, MatchingEngine engine,
                     WaitlistManager waitlists, InputValidator validator) {
        this.repo = repo;
        this.engine = engine;
        this.waitlists = waitlists;
        this.validator = validator;
    }

    // ---------------- main loop ----------------

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Choose an option: ");
            switch (choice) {
                case "1": registerApplicant(); break;
                case "2": viewResult();        break;
                case "3": listApplicants();    break;
                case "4": viewWaitlists();     break;
                case "0": running = false;     break;
                default:  System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    // ---------------- menu actions ----------------

    private void registerApplicant() {
        System.out.println("\n=== New Applicant ===");

        String number = askApplicantNumber();
        if (number == null) {
            System.out.println("Cancelled.");
            return;
        }

        String last = askName("Last name: ");
        String first = askName("First name: ");
        String middle = askMiddleInitial();

        Applicant applicant = new Applicant(number, last, first, middle);

        for (Trait t : askTraits()) {
            applicant.addTrait(t);
        }
        for (Program p : askPrograms()) {
            applicant.addEligibleProgram(p);
        }
        applicant.setCictScore(askScore());

        repo.save(applicant);

        MatchResult result = engine.process(applicant);
        repo.saveResult(result);
        printResult(result);
    }

    private void viewResult() {
        String number = prompt("Applicant no.: ");
        MatchResult result = repo.getResult(number).orElse(null);
        if (result == null) {
            System.out.println("No result found for applicant " + number + ".");
            return;
        }
        printResult(result);
    }

    private void listApplicants() {
        Collection<Applicant> all = repo.getAll();
        if (all.isEmpty()) {
            System.out.println("No applicants yet.");
            return;
        }
        System.out.println("\n--- Applicants ---");
        for (Applicant a : all) {
            System.out.printf("%-12s %-30s score: %.2f%n",
                    a.getApplicantNumber(), a.getFullName(), a.getCictScore());
        }
    }

    private void viewWaitlists() {
        System.out.println("\n--- Waitlists ---");
        for (Program p : Program.values()) {
            List<Applicant> list = waitlists.getWaitlist(p);
            System.out.println(p + " (" + list.size() + " waitlisted)");
            int rank = 1;
            for (Applicant a : list) {
                System.out.printf("  %d. %s - %s (score %.2f)%n",
                        rank++, a.getApplicantNumber(), a.getFullName(), a.getCictScore());
            }
        }
    }

    // ---------------- input helpers ----------------

    /* Returns null if the user cancels. */
    private String askApplicantNumber() {
        while (true) {
            String number = prompt("Applicant no. (or 'x' to cancel): ");
            if (number.equalsIgnoreCase("x")) {
                return null;
            }
            if (!validator.isValidApplicantNumber(number)) {
                System.out.println("Invalid applicant number format.");
            } else if (repo.exists(number)) {
                System.out.println("That applicant number already exists.");
            } else {
                return number;
            }
        }
    }

    private String askName(String label) {
        while (true) {
            String s = prompt(label);
            if (!s.isEmpty() && s.matches("[A-Za-z .'-]+")) {
                return s;
            }
            System.out.println("Please enter a valid name (letters only).");
        }
    }

    private String askMiddleInitial() {
        while (true) {
            String s = prompt("Middle initial (blank if none): ");
            if (s.isEmpty()) {
                return "";
            }
            if (s.length() == 1 && Character.isLetter(s.charAt(0))) {
                return s.toUpperCase();
            }
            System.out.println("Enter a single letter.");
        }
    }

    /* Checklist of traits: numbers separated by commas, blank for none. */
    private List<Trait> askTraits() {
        Trait[] all = Trait.values();
        System.out.println("\nTrait checklist (enter numbers separated by commas, blank for none):");
        for (int i = 0; i < all.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, all[i].getDisplayName());
        }
        while (true) {
            List<Trait> chosen = parseSelection(prompt("Your traits: "), all, true);
            if (chosen != null) {
                return chosen;
            }
            System.out.println("Invalid input. Use numbers from 1 to " + all.length + ".");
        }
    }

    /* Programs the applicant is eligible for / choosing from. At least one is required. */
    private List<Program> askPrograms() {
        Program[] all = Program.values();
        System.out.println("\nPrograms you are applying to, in order of preference (1st choice first, numbers separated by commas):");
        for (int i = 0; i < all.length; i++) {
            System.out.printf("  [%d] %-6s %s%n", i + 1, all[i], all[i].getFullName());
        }
        while (true) {
            List<Program> chosen = parseSelection(prompt("Your programs: "), all, false);
            if (chosen != null) {
                return chosen;
            }
            System.out.println("Pick at least one, using numbers from 1 to " + all.length + ".");
        }
    }

    private double askScore() {
        while (true) {
            String s = prompt(String.format("CICT score (%.0f-%.0f): ", MIN_SCORE, MAX_SCORE));
            try {
                double score = Double.parseDouble(s);
                if (score >= MIN_SCORE && score <= MAX_SCORE) {
                    return score;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.println("Enter a number between " + (int) MIN_SCORE + " and " + (int) MAX_SCORE + ".");
        }
    }

    /*
     * Parses "1,3,5" into the matching items. Returns null when the input is
     * invalid (or empty while allowEmpty is false). Duplicates are ignored.
     */
    private <T> List<T> parseSelection(String line, T[] options, boolean allowEmpty) {
        List<T> chosen = new ArrayList<>();
        if (line.isEmpty()) {
            return allowEmpty ? chosen : null;
        }
        for (String token : line.split(",")) {
            try {
                int n = Integer.parseInt(token.trim());
                if (n < 1 || n > options.length) {
                    return null;
                }
                T item = options[n - 1];
                if (!chosen.contains(item)) {
                    chosen.add(item);
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return chosen;
    }

    // ---------------- output ----------------

    private void printResult(MatchResult r) {
        Applicant a = r.getApplicant();

        System.out.println("\n=====================================");
        System.out.printf("Applicant: %s - %s%n", a.getApplicantNumber(), a.getFullName());
        System.out.printf("CICT score: %.2f%n", a.getCictScore());
        System.out.println("Outcome:   " + r.getOutcome());

        RankedProgram top = r.getRecommendedProgram();
        if (top != null) {
            System.out.println("Top program: " + top.getProgram()
                    + " - " + top.getProgram().getFullName());
        }

        List<RankedProgram> ranked = r.getRankedPrograms();
        if (!ranked.isEmpty()) {
            System.out.println("\nProgram ranking:");
            int rank = 1;
            for (RankedProgram rp : ranked) {
                System.out.printf("  %d. %-6s total: %.2f (core: %.2f, aligned: %.2f)%n",
                        rank++, rp.getProgram(), rp.getTotalScore(),
                        rp.getCorePriorityScore(), rp.getTraitAlignmentScore());
            }
        }

        List<ChoiceResult> choices = r.getChoiceResults();
        if (!choices.isEmpty()) {
            System.out.println("\nPer-program result:");
            for (ChoiceResult c : choices) {
                System.out.printf("  %d. %-6s %-38s %s%n",
                        c.getChoiceNumber(), c.getProgram(), c.getStatus(),
                        c.getMessage() == null ? "" : c.getMessage());
            }
        }
        System.out.println("=====================================\n");
    }

    private void printBanner() {
        System.out.println("=====================================");
        System.out.println("   MATCH: Match Applicants to CICT");
        System.out.println("=====================================");
    }

    private void printMenu() {
        System.out.println("\n[1] Register applicant");
        System.out.println("[2] View applicant result");
        System.out.println("[3] List applicants");
        System.out.println("[4] View waitlists");
        System.out.println("[0] Exit");
    }

    private String prompt(String message) {
        System.out.print(message);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }
}
