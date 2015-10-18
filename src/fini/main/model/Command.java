package fini.main.model;

public class Command {
    public static enum Type {
        ADD, DELETE, INVALID
    }

    private String action;
    private String arguments;
    private Type type;

    public Command(String command) {
        action = getUserAction(command);
        arguments = getUserArguments(command);
        type = determineActionType(action);
    }

    // Public Getters
    public Type getCommandType() {
        return type;
    }

    public String getCommandAction() {
        return action;
    }

    public String getCommandArguments() {
        return arguments;
    }

    // Utility
    private String getUserAction(String command) {
        return command.trim().split("\\s+")[0];
    }

    private String getUserArguments(String command) {
        return command.replaceFirst(getUserAction(command), "").trim();
    }

    private Type determineActionType(String action) {
        switch (action.toLowerCase()) {
            case "add":
                return Type.ADD;
            case "delete":
                return Type.DELETE;
            default:
                return Type.INVALID;
        }
    }
}
