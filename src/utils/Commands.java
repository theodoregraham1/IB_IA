package utils;

public class Commands {
    public static final Command[] COMMANDS = {
            new Command("start", new String[]{"start", "s", "Start", "S"}),
            new Command("end", new String[]{"end", "e", "End", "E"})
    };

    public static Command getCommand(String input) {
        for (Command command: COMMANDS) {
            if (command.inInputs(input)) {
                return command;
            }
        }
        return null;
    }

    private static class Command {
        public final String name;
        private String[] inputs;

        public Command(String name, String[] inputs) {
            this.name = name;
            this.inputs = inputs;
        }
        public boolean inInputs(String s) {
            for (String input: inputs) {
                if (s.equals(input)) {
                    return true;
                }
            }
            return false;
        }
    }
}
