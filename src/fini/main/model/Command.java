package fini.main.model;

import java.util.Arrays;

public class Command {
	public static enum CommandType {
		ADD, UPDATE, DELETE, CLEAR, UNDO, REDO, DISPLAY, SEARCH, MODE, EXIT, COMPLETE, MODS, INVALID
	};
	
	private CommandType commandType;
	private String userCommand;
	private int objectIndex = -1;
	private String commandParameters = null;
	
	public Command(String userInput) {
		String[] splitUserInput = userInput.trim().split("\\s+");
		userCommand = splitUserInput[0].toLowerCase();
		commandType = determineCommandType(userCommand);
		
		if (commandType == CommandType.UPDATE ||
			commandType == CommandType.DELETE ||
			commandType == CommandType.COMPLETE) {
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
	
	public CommandType getCommandType() {
		return commandType;
	}
	
	public int getObjectIndex() {
		return objectIndex;
	}
	
	public String getCommandParameters() {
		return commandParameters;
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
		case "display":
			return CommandType.DISPLAY;
		case "mode":
			return CommandType.MODE;
		case "exit":
			return CommandType.EXIT;
		case "complete":
			return CommandType.COMPLETE;
		case "mods":
			return CommandType.MODS;
		default:
			return CommandType.INVALID;
		}
	}
}