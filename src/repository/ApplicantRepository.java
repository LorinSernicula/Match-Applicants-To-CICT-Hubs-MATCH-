package repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import model.Applicant;
import model.ChoiceResult;
import model.ChoiceStatus;
import model.MatchResult;
import model.Outcome;
import model.Program;

/**
 * In-memory store for applicants and their match results.
 *
 * <p>Indexes (all kept in sync by {@link #add}, {@link #update}, {@link #saveResult}
 * and {@link #remove}):
 * <ul>
 *   <li>Primary: applicant number -> Applicant ({@link LinkedHashMap}, keeps insertion order)</li>
 *   <li>Applicant number -> MatchResult</li>
 *   <li>Last name (lower-case) -> applicant numbers</li>
 *   <li>Full name (lower-case, "first last" and "last, first") -> applicant numbers</li>
 *   <li>Outcome -> applicant numbers</li>
 *   <li>Sorted set of applicant numbers for next/previous navigation</li>
 * </ul>
 */
public class ApplicantRepository {

    private final Map<Integer, Applicant> applicants = new LinkedHashMap<>();
    private final Map<Integer, MatchResult> results = new HashMap<>();
    private final Map<String, Set<Integer>> lastNameIndex = new HashMap<>();
    private final Map<String, Set<Integer>> fullNameIndex = new HashMap<>();
    private final Map<Outcome, Set<Integer>> outcomeIndex = new EnumMap<>(Outcome.class);
    private final TreeSet<Integer> sortedNumbers = new TreeSet<>();

    public ApplicantRepository() {
        for (Outcome o : Outcome.values()) {
            outcomeIndex.put(o, new LinkedHashSet<>());
        }
    }

    // ------------------------------------------------------------------ save / update

    /**
     * Adds a new applicant.
     *
     * @return true if added, false if the applicant is null or the number already exists
     */
    public boolean add(Applicant applicant) {
        if (applicant == null || exists(applicant.getApplicantNumber())) {
            return false;
        }
        applicants.put(applicant.getApplicantNumber(), applicant);
        sortedNumbers.add(applicant.getApplicantNumber());
        indexNames(applicant);
        return true;
    }

    /**
     * Replaces the stored applicant that has the same applicant number
     * (e.g. after editing the name or score) and re-indexes the names.
     * The saved match result is kept; re-run matching and call
     * {@link #saveResult} if the change affects it.
     *
     * @return true if updated, false if no such applicant exists
     */
    public boolean update(Applicant applicant) {
        if (applicant == null) {
            return false;
        }
        Applicant old = applicants.get(applicant.getApplicantNumber());
        if (old == null) {
            return false;
        }
        unindexNames(old);
        applicants.put(applicant.getApplicantNumber(), applicant);
        indexNames(applicant);
        return true;
    }

    /** Adds the applicant if new, otherwise updates it. */
    public void save(Applicant applicant) {
        if (!add(applicant)) {
            update(applicant);
        }
    }

    /**
     * Stores (or replaces) the match result of an applicant, using
     * {@code result.getApplicant()} to find who it belongs to. The applicant is
     * added to the repository first if not yet stored.
     */
    public void saveResult(MatchResult result) {
        if (result == null) {
            return;
        }
        Applicant applicant = result.getApplicant();
        save(applicant);
        int number = applicant.getApplicantNumber();
        MatchResult previous = results.put(number, result);
        if (previous != null) {
            outcomeIndex.get(previous.getOutcome()).remove(number);
        }
        outcomeIndex.get(result.getOutcome()).add(number);
    }

    /** Convenience: save the applicant together with their result. */
    public void save(Applicant applicant, MatchResult result) {
        save(applicant);
        saveResult(result);
    }

    /** Removes an applicant, their result and all index entries. @return true if removed */
    public boolean remove(int applicantNumber) {
        Applicant old = applicants.remove(applicantNumber);
        if (old == null) {
            return false;
        }
        unindexNames(old);
        sortedNumbers.remove(applicantNumber);
        MatchResult previous = results.remove(applicantNumber);
        if (previous != null) {
            outcomeIndex.get(previous.getOutcome()).remove(applicantNumber);
        }
        return true;
    }

    /** Removes everything. */
    public void clear() {
        applicants.clear();
        results.clear();
        lastNameIndex.clear();
        fullNameIndex.clear();
        sortedNumbers.clear();
        for (Set<Integer> s : outcomeIndex.values()) {
            s.clear();
        }
    }

    // ------------------------------------------------------------------ lookups

    /** @return true if an applicant with this number is stored (duplicate check) */
    public boolean exists(int applicantNumber) {
        return applicants.containsKey(applicantNumber);
    }

    /** Fast primary-key lookup. */
    public Optional<Applicant> findByNumber(int applicantNumber) {
        return Optional.ofNullable(applicants.get(applicantNumber));
    }

    /** @return the stored match result of the applicant, if matching has been run */
    public Optional<MatchResult> getResult(int applicantNumber) {
        return Optional.ofNullable(results.get(applicantNumber));
    }

    /** Case-insensitive exact last-name search (uses the last-name index). */
    public List<Applicant> findByLastName(String lastName) {
        return resolve(lastNameIndex.get(normalize(lastName)));
    }

    /**
     * Case-insensitive exact full-name search. Accepts "First Last" or
     * "Last, First" (middle initial not needed).
     */
    public List<Applicant> findByFullName(String fullName) {
        return resolve(fullNameIndex.get(normalize(fullName)));
    }

    /**
     * Case-insensitive partial search: matches if the text appears in the
     * first name, last name, or either full-name form. Results in insertion order.
     */
    public List<Applicant> searchByName(String text) {
        String q = normalize(text);
        List<Applicant> found = new ArrayList<>();
        if (q.isEmpty()) {
            return found;
        }
        for (Applicant a : applicants.values()) {
            if (normalize(a.getFirstName()).contains(q)
                    || normalize(a.getLastName()).contains(q)
                    || firstLast(a).contains(q)
                    || lastFirst(a).contains(q)) {
                found.add(a);
            }
        }
        return found;
    }

    // ------------------------------------------------------------------ filters

    /** @return applicants whose overall outcome is the given one, in save order */
    public List<Applicant> findByOutcome(Outcome outcome) {
        return resolve(outcomeIndex.get(outcome));
    }

    /** @return how many applicants have the given outcome */
    public int countByOutcome(Outcome outcome) {
        return outcomeIndex.get(outcome).size();
    }

    /** @return applicants admitted to the given program */
    public List<Applicant> findByProgram(Program program) {
        return findByProgramAndStatus(program, ChoiceStatus.ADMITTED);
    }

    /** @return applicants whose choice for the program has the given status (e.g. WAITLISTED) */
    public List<Applicant> findByProgramAndStatus(Program program, ChoiceStatus status) {
        List<Applicant> found = new ArrayList<>();
        for (Map.Entry<Integer, MatchResult> e : results.entrySet()) {
            for (ChoiceResult c : e.getValue().getChoices()) {
                if (c.getProgram() == program && c.getStatus() == status) {
                    found.add(applicants.get(e.getKey()));
                    break;
                }
            }
        }
        found.sort((a, b) -> Integer.compare(a.getApplicantNumber(), b.getApplicantNumber()));
        return found;
    }

    // ------------------------------------------------------------------ listing / navigation

    /** @return all applicants in insertion order */
    public List<Applicant> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(applicants.values()));
    }

    /** @return all applicants sorted by applicant number */
    public List<Applicant> getAllSortedByNumber() {
        List<Applicant> list = new ArrayList<>();
        for (int n : sortedNumbers) {
            list.add(applicants.get(n));
        }
        return list;
    }

    /** @return all applicants sorted by last name, then first name (case-insensitive) */
    public List<Applicant> getAllSortedByName() {
        List<Applicant> list = new ArrayList<>(applicants.values());
        list.sort((a, b) -> {
            int c = normalize(a.getLastName()).compareTo(normalize(b.getLastName()));
            if (c != 0) {
                return c;
            }
            c = normalize(a.getFirstName()).compareTo(normalize(b.getFirstName()));
            return c != 0 ? c : Integer.compare(a.getApplicantNumber(), b.getApplicantNumber());
        });
        return list;
    }

    /** @return the applicant with the next higher number (for ApplicantNavigator) */
    public Optional<Applicant> next(int applicantNumber) {
        Integer n = sortedNumbers.higher(applicantNumber);
        return n == null ? Optional.empty() : Optional.of(applicants.get(n));
    }

    /** @return the applicant with the next lower number (for ApplicantNavigator) */
    public Optional<Applicant> previous(int applicantNumber) {
        Integer n = sortedNumbers.lower(applicantNumber);
        return n == null ? Optional.empty() : Optional.of(applicants.get(n));
    }

    /** @return the lowest-numbered applicant, if any */
    public Optional<Applicant> first() {
        return sortedNumbers.isEmpty() ? Optional.empty()
                : Optional.of(applicants.get(sortedNumbers.first()));
    }

    /** @return the highest-numbered applicant, if any */
    public Optional<Applicant> last() {
        return sortedNumbers.isEmpty() ? Optional.empty()
                : Optional.of(applicants.get(sortedNumbers.last()));
    }

    /** @return number of stored applicants */
    public int size() {
        return applicants.size();
    }

    /** @return true if nothing is stored */
    public boolean isEmpty() {
        return applicants.isEmpty();
    }

    // ------------------------------------------------------------------ internals

    private List<Applicant> resolve(Set<Integer> numbers) {
        List<Applicant> list = new ArrayList<>();
        if (numbers != null) {
            for (int n : numbers) {
                list.add(applicants.get(n));
            }
        }
        return list;
    }

    private void indexNames(Applicant a) {
        int n = a.getApplicantNumber();
        lastNameIndex.computeIfAbsent(normalize(a.getLastName()), k -> new LinkedHashSet<>()).add(n);
        fullNameIndex.computeIfAbsent(firstLast(a), k -> new LinkedHashSet<>()).add(n);
        fullNameIndex.computeIfAbsent(lastFirst(a), k -> new LinkedHashSet<>()).add(n);
    }

    private void unindexNames(Applicant a) {
        int n = a.getApplicantNumber();
        removeKey(lastNameIndex, normalize(a.getLastName()), n);
        removeKey(fullNameIndex, firstLast(a), n);
        removeKey(fullNameIndex, lastFirst(a), n);
    }

    private static void removeKey(Map<String, Set<Integer>> index, String key, int number) {
        Set<Integer> set = index.get(key);
        if (set != null) {
            set.remove(number);
            if (set.isEmpty()) {
                index.remove(key);
            }
        }
    }

    private static String firstLast(Applicant a) {
        return normalize(a.getFirstName()) + " " + normalize(a.getLastName());
    }

    private static String lastFirst(Applicant a) {
        return normalize(a.getLastName()) + ", " + normalize(a.getFirstName());
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
