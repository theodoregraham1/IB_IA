package utils;

public class Commands {
    public static final Command[] COMMANDS = {
            new Command("start", new String[]{"start", "s"}),
            new Command("end", new String[]{"end", "e"}),
            new Command("yes", new String[]{"yes", "y", "true"}),
            new Command("no", new String[]{"no", "n", "false"})
    };

    public static Command getCommand(String input) {
        for (Command command: COMMANDS) {
            if (command.inInputs(input)) {
                return command;
            }
        }
        return null;
    }
}
