package utils;

public class Commands {
    private final Command[] commands;

    public Commands(Command[] allowedCommands) {
        commands = allowedCommands;
    }

    public Command getCommand(String input) {
        for (Command command: commands) {
            if (command.inInputs(input)) {
                return command;
            }
        }
        return null;
    }

    public String getAllowedCommandsStr() {
        StringBuilder output = new StringBuilder();

        for (Command c: commands){
            output.append("- ").append(c.getName()).append("\n");
        }

        return output.toString();
    }
}
