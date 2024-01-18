package utils;

public class Commands {
    private Command[] commands;

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
}
