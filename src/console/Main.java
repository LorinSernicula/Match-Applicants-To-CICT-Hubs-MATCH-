package console;

import config.ProgramConfig;
import engine.MatchingEngine;
import engine.WaitlistManager;
import repository.ApplicantRepository;
import ui.ConsoleUI;
import ui.InputValidator;

/* Member 4: wires the pieces together and starts the console. */
public class Main {
    public static void main(String[] args) {
        ProgramConfig config = new ProgramConfig();
        WaitlistManager waitlists = new WaitlistManager();
        ApplicantRepository repo = new ApplicantRepository();
        InputValidator validator = new InputValidator();
        MatchingEngine engine = new MatchingEngine(config, waitlists);

        new ConsoleUI(repo, engine, waitlists, validator).run();
    }
}
