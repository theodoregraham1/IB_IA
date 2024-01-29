package commands;

import java.util.Scanner;

public class Commands {
    private final Command[] commands;


    /**
     * Constructs a wrapper class for a list of commands
     * @param allowedCommands the commands in this list
     */
    public Commands(Command[] allowedCommands) {
        commands = allowedCommands;
    }

    /**
     * Finds the command that matches this input
     * @param input the input String to test
     * @return the command that matches, or null if none match
     */
    public Command getCommand(String input) {
        for (Command command: commands) {
            if (command.inInputs(input)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Returns a printable list of the inputs
     * @return a String of the commands listed
     */
    public String getAllowedCommandsStr() {
        StringBuilder output = new StringBuilder();

        for (Command c: commands){
            output.append("- ").append(c.getName()).append("\n");
        }

        return output.toString();
    }

    /**
     * Prints the command that is next found in the inputs from a scanner
     * @param scanner the Scanner to get the input from
     * @return the Command that is inputted
     */
    public Command scanCommand(Scanner scanner) {
        // Get command
        Command command = null;
        while (command == null) {
            command = getCommand(scanner.next());

            if (command == null) {
                // Output error message
                System.out.println("Allowed commands: ");
                System.out.println(getAllowedCommandsStr());
            }
        }
        return command;
    }
}
