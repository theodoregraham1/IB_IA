package commands;

public class Command {
    private final String name;
    private final String[] inputs;

    /**
     * Constructs a new command
     * @param name the name associated with this command. Used to find it later
     * @param inputs the possible input variables that are associated with this command
     */
    public Command(String name, String[] inputs) {
        this.name = name;
        this.inputs = inputs;
    }

    /**
     * Determines if a string is a possible input for this command
     * @param s the string to match
     * @return true if the input is valid, false otherwise
     */
    public boolean inInputs(String s) {
        for (String input: inputs) {
            if (s.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the name of this command
     * @return a String of the name
     */
    public String getName() {
        return name;
    }


    /**
     * Determines if this command is the same as the one denoted by this name
     * @param name the name to check against this command's name
     * @return true if they are the same, false otherwise
     */
    public boolean equals(String name) {
        return this.getName().equalsIgnoreCase(name);
    }
}