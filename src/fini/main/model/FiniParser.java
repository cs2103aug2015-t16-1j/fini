package fini.main.model;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

import fini.main.model.Task.Priority;

public class FiniParser {
	public static enum CommandType {
		ADD, UPDATE, DELETE, CLEAR, EXIT, COMPLETE, MODS, INVALID
	};

	private static FiniParser finiParser;
	private Parser parser;
	
	private String storedUserInput;
	private CommandType commandType;
	private String commandParameters;
	private Priority priority;
	private String projectName;
	private ArrayList<LocalDateTime> dates;
	private String notParsed;
	
	public FiniParser() {
		dates = new ArrayList<LocalDateTime>();
		parser = new Parser();
	}

	public String parse(String userInput) {
		storedUserInput = userInput;
//		String cleanInput = getCleanString(userInput);
		String cleanInput = getSimpleCleanString(userInput);
		System.out.println("Clean input: " + cleanInput);
		String[] userInputSplitArray = cleanInput.split(" ");
		if (userInputSplitArray.length > 1) {
			commandParameters = userInput.replaceFirst(userInputSplitArray[0], "").substring(1);
			commandType = determineUserCommandType(userInputSplitArray[0].toLowerCase());
			priority = determinePriority(userInputSplitArray);
//			projectName = determineProjectName(userInputSplitArray);
			
			boolean isNattySuccessful = evaluateParameters(commandParameters);
			
			if (isNattySuccessful) {
				return "Parse Successful";
			}
		}
		return "Parse Error";
	}

	private CommandType determineUserCommandType(String command) {
		switch (command) {
		case "add":
			return CommandType.ADD;
		case "update":
			return CommandType.UPDATE;
		case "delete":
			return CommandType.DELETE;
		case "clear":
			return CommandType.CLEAR;
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
	
	private Priority determinePriority(String[] userInputSplitArray) {
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(userInputSplitArray));
		for (String word : words) {
			if (word.toLowerCase().equals("priority")) {
				if (words.indexOf(word) != words.size() - 1) {
					String priority = words.get(words.indexOf(word) + 1);
					Priority returnPriority;
					switch (priority.toLowerCase()) {
					case "high":
						returnPriority = Priority.HIGH;
						break;
					case "medium":
						returnPriority = Priority.MEDIUM;
						break;
					case "low":
						returnPriority = Priority.LOW;
						break;
					default:
						returnPriority = Priority.NORMAL;
						break;
					}
					
					if (!returnPriority.equals(Priority.NORMAL)) {
						commandParameters.replaceAll(word + " " + priority, "");
						commandParameters = getSimpleCleanString(commandParameters);
					}
				} else {
					break;
				}
			}
		}
		return Priority.NORMAL;
	}
	
//	private String determineProjectName(String[] userInputSplitArray) {
//		ArrayList<String> words = new ArrayList<String>(Arrays.asList(userInputSplitArray));
//		for (String word : words) {
//			if (word.toLowerCase().equals("project")) {
//				if (words.indexOf(word) != words.size() - 1) {
//					String projectName = words.get(words.indexOf(word) + 1);
//										
//					commandParameters.replaceAll(word + " " + projectName, "");
//					commandParameters = getSimpleCleanString(commandParameters);
//				} else {
//					break;
//				}
//			}
//		}
//		return null;
//	}
	
	private boolean evaluateParameters(String commandParameters) {
		List<DateGroup> groups = parser.parse(commandParameters);
		
		if (!groups.isEmpty()) {
			DateGroup group = groups.get(0);
			
			List<Date> dateList = group.getDates();
			for (Date date : dateList) {
				dates.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
			}
			
			String matchingValue = group.getText();
			
			notParsed = commandParameters;
			notParsed = notParsed.replaceAll(matchingValue, "");
			notParsed = getSimpleCleanString(notParsed);
			return true;
		}
		return false;
	}

	// Public Getters
	public String getStoredUserInput() {
		return storedUserInput;
	}
	
	public CommandType getCommandType() {
		return commandType;
	}
	
	public String getCommandParameters() {
		return commandParameters;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public ArrayList<LocalDateTime> getDates() {
		return dates;
	}
	
	public String getNotParsed() {
		return notParsed;
	}

	// Checking Methods
//	private Integer checkTaskId(String commandParameters) {
//		String taskIdStr = commandParameters.split("//")[0].trim();
//		boolean isNum = false;
//		for (char ch : taskIdStr.toCharArray()) {
//			if (!Character.isDigit(ch)) {
//				isNum = false;
//			}
//			isNum = true;
//		}
//		Integer taskId;
//		if (isNum) {
//			taskId = Integer.valueOf(taskIdStr);
//		} else {
//			taskId = -1;
//		}
//		return taskId;
//	}
//
//	private boolean checkIfHasTitle(String commandParameters) {
//		return commandParameters.toLowerCase().contains("title");
//	}
//
//	private boolean checkIfHasDate(String commandParameters) {
//		return commandParameters.toLowerCase().contains("date");
//	}
//
//	private boolean checkIfHasTime(String commandParameters) {
//		return commandParameters.toLowerCase().contains("time");
//	}
//
//	private boolean checkIfHasParameters(String commandParameters) {
//		return commandParameters.contains("//");
//	}
//
//	private boolean checkIfHasProject(String commandParameters) {
//		return commandParameters.contains("project");
//	}
//
//	private boolean checkIfTaskIsDeadline(String commandParameters) {
//		return commandParameters.contains("at");
//	}
//
//	private boolean checkIfTaskIsEvent(String commandParameters) {
//		return commandParameters.contains("from") && commandParameters.contains("to");
//	}
//
//	private boolean checkIfHasPriority(String commandParameters) {
//		return commandParameters.contains("priority");
//	}
//
//	private boolean checkIfRecurringTask(String commandParameters) {
//		return commandParameters.contains("every");
//	}
//	
//	
//	// Utility Methods
//	private String getCleanString(String userInput) {
//		String cleanStr;
//		cleanStr = userInput.trim();
//		cleanStr = cleanStr.replaceAll("[^A-Za-z0-9/\\s+]", "");
//		cleanStr = cleanStr.replaceAll("\\s+", " ");
//		return cleanStr;
//	}
	
	private String getSimpleCleanString(String userInput) {
		String cleanStr;
		cleanStr = userInput.trim();
		cleanStr = cleanStr.replaceAll("\\s+", " ");
		return cleanStr;
	}
	
//	private String extractProject(String commandParameters) {
//		int indexOfProject = commandParameters.toLowerCase().indexOf("project");
//		String projectDetails = commandParameters.substring(indexOfProject);
//		projectDetails = projectDetails.replace("project ", "");
//		String[] removeProjectKeyword = projectDetails.split(" ");
//		return removeProjectKeyword[0];
//	}
//
//	private String extractInformation(String keyword, String commandParameters) {
//		commandParameters = commandParameters.trim();
//		int indexOfKeyword = commandParameters.toLowerCase().indexOf(keyword);
//		String commandDetails = commandParameters.substring(indexOfKeyword).trim();
//		commandDetails  = commandDetails .replace(keyword, "").trim();
//		String[] removeKeyword = commandDetails.split(" ");
//		System.out.println(removeKeyword[0]);
//		return removeKeyword[0];
//	}
//
//	private String extractPriority(String commandParameters) {
//		int indexOfPriority = commandParameters.toLowerCase().indexOf("priority");
//		String priorityDetails = commandParameters.substring(indexOfPriority);
//		priorityDetails = priorityDetails.replace("priority ", "");
//		String[] removePriorityKeyword = priorityDetails.split(" ");
//		System.out.println(removePriorityKeyword[0]);
//		return removePriorityKeyword[0];
//	}
	
	// getInstance Method
	public static FiniParser getInstance() {
		if (finiParser == null) {
			finiParser = new FiniParser();
		}
		return finiParser;
	}
}
