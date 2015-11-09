package fini.main.model;

import java.util.Arrays;

/**
 * This Command class determines the command type before the command is passed to parser.
 * It also divide the command into meaningful segment for later usage by brain.  
 * 
 * A meaningful command should goes like:
 * 
 * <Command Key word><SPACE>(Optional: <Object index>)<SPACE><Command Parameters>
 * 
 * @@author A0130047W
 */
public class Command {
    /* ***********************************
     * Constants
     * ***********************************/
    public static enum CommandType {
        ADD, UPDATE, DELETE, CLEAR, UNDO, REDO, SET, DISPLAY, SEARCH, EXIT, COMPLETE, UNCOMPLETE, MODS, HELP, INVALID
    };
    
    private static final String ONE_OR_MORE_SPACE = "\\s+";
    private static final String EMPTY_SPACE = "";
    private static final int INVALID_INDEX = -1;

    /* ***********************************
     * Fields
     * ***********************************/
    private CommandType commandType;
    private String userCommand;
    private int objectIndex = INVALID_INDEX;
    private String commandParameters = EMPTY_SPACE;

    public Command(String userInput) {
        String[] splitUserInput = userInput.trim().split(ONE_OR_MORE_SPACE);
        userCommand = splitUserInput[0].toLowerCase();
        commandType = determineCommandType(userCommand);

        // Commands which require an object index
        if (commandType == CommandType.UPDATE ||
                commandType == CommandType.DELETE ||
                commandType == CommandType.COMPLETE ||
                commandType == CommandType.UNCOMPLETE) {
            try {
                objectIndex = Integer.parseInt(splitUserInput[1]);
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                commandType = CommandType.INVALID;
            }
            commandParameters = String.join(" ", Arrays.copyOfRange(splitUserInput, 2, splitUserInput.length));
        } else {
            commandParameters = String.join(" ", Arrays.copyOfRange(splitUserInput, 1, splitUserInput.length));
        }
    }
    
    private CommandType determineCommandType(String userCommand) {
        switch (userCommand) {
            case "add":
                return CommandType.ADD;
            case "update":
                return CommandType.UPDATE;
            case "delete":
                return CommandType.DELETE;
            case "clear":
                return CommandType.CLEAR;
            case "undo":
                return CommandType.UNDO;
            case "redo":
                return CommandType.REDO;
            case "set":
                return CommandType.SET;
            case "display":
                return CommandType.DISPLAY;
            case "search":
                return CommandType.SEARCH;
            case "exit":
                return CommandType.EXIT;
            case "complete":
                return CommandType.COMPLETE;
            case "uncomplete":
                return CommandType.UNCOMPLETE;
            case "mods":
                return CommandType.MODS;
            case "help":
                return CommandType.HELP;
            default:
                return CommandType.INVALID;
        }
    }
    
    /* ***********************************
     * Public getters
     * ***********************************/
    public CommandType getCommandType() {
        return commandType;
    }

    public int getObjectIndex() {
        return objectIndex;
    }

    public String getCommandParameters() {
        return commandParameters;
    }


}