package commands;

public class Command {
    private final String name;
    private String[] inputs;

    public Command(String name, String[] inputs) {
        this.name = name;
        this.inputs = inputs;
    }

    public boolean inInputs(String s) {
        for (String input: inputs) {
            if (s.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Command command) {
        return this.getName().equals(command.getName());
    }
    public boolean equals(String name) {
        return this.getName().equalsIgnoreCase(name);
    }
}