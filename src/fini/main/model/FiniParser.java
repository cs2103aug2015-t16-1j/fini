package fini.main.model;

import java.security.spec.ECFieldF2m;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import fini.main.MainApp;
import fini.main.model.Task.Priority;

public class FiniParser {
	public static enum CommandType {
		ADD, UPDATE, DELETE, CLEAR, UNDO, SEARCH, MODE, EXIT, COMPLETE, MODS, INVALID
	};

	private static FiniParser finiParser;
	private Parser parser;

	private String storedUserInput;
	private CommandType commandType;
	private String commandParameters;
	private String cleanParameters;
	private Priority priority;
	private String projectName;
	private ArrayList<LocalDateTime> datetimes;
	private boolean isRecurring;
	private LocalDateTime recursUntil;
	private String notParsed;

	public FiniParser() {
		initializeFields();
	}

	public String parse(String userInput) {
		try {
			initializeFields();
			storedUserInput = userInput;
			//		String cleanInput = getCleanString(userInput);
			String cleanInput = getSimpleCleanString(userInput);
			System.out.println("Clean input: " + cleanInput);
			String[] userInputSplitArray = cleanInput.split(" ");
			if (userInputSplitArray.length > 1) {
				commandParameters = userInput.replaceFirst(userInputSplitArray[0], "").substring(1);
				commandType = determineUserCommandType(userInputSplitArray[0].toLowerCase());	
				System.out.println("CMD: " + commandType);

				cleanParameters = commandParameters;
				priority = determinePriority(userInputSplitArray);
				//			projectName = determineProjectName(userInputSplitArray);

				boolean isNattySuccessful = evaluateParameters(cleanParameters);

				if (isNattySuccessful) {
					return "Parse Successful";
				}

				return "Parse Error";
			} else {
				commandType = determineUserCommandType(userInputSplitArray[0]);
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}


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
		case "undo":
			return CommandType.UNDO;
		case "exit":
			return CommandType.EXIT;
		case "complete":
			return CommandType.COMPLETE;
		case "mods":
			return CommandType.MODS;
		case "mode":
			return CommandType.MODE;
		default:
			return CommandType.INVALID;
		}
	}

	private Priority determinePriority(String[] userInputSplitArray) {
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(userInputSplitArray));
		for (String word : words) {
			if (word.toLowerCase().equals("priority")) {
				if (words.indexOf(word) != words.size() - 1) {
					System.out.println("HERE");
					String priority = words.get(words.indexOf(word) + 1);
					System.out.println(priority);
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
						cleanParameters = cleanParameters.replaceAll(word + " " + priority, "");
						cleanParameters = getSimpleCleanString(cleanParameters);
						return returnPriority;
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
	//					cleanParameters = cleanParameters.replaceAll(word + " " + projectName, "");
	//					cleanParameters = getSimpleCleanString(cleanParameters);
	//				} else {
	//					break;
	//				}
	//			}
	//		}
	//		return null;
	//	}

	private boolean evaluateParameters(String cleanParameters) {
		List<DateGroup> groups = parser.parse(cleanParameters);

		if (!groups.isEmpty()) {
			DateGroup group = groups.get(0);

			List<Date> dateList = group.getDates();
			for (Date date : dateList) {
				datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
			}

			isRecurring = group.isRecurring();
			if (isRecurring && group.getRecursUntil() != null) {
				recursUntil = LocalDateTime.ofInstant(group.getRecursUntil().toInstant(), ZoneId.systemDefault());
			}

			String matchingValue = group.getText();
			notParsed = cleanParameters;
			notParsed = notParsed.replaceAll(matchingValue, "");
			notParsed = getSimpleCleanString(notParsed);
			return true;
		} else {
			notParsed = cleanParameters;
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

	public String getCleanParameters() {
		return cleanParameters;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getProjectName() {
		return projectName;
	}

	public ArrayList<LocalDateTime> getDatetimes() {
		return datetimes;
	}

	public boolean getIsRecurring() {
		return isRecurring;
	}

	public LocalDateTime getRecursUntil() {
		return recursUntil;
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

	public String getSimpleCleanString(String userInput) {
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

	// Initialization Methods
	public static FiniParser getInstance() {
		if (finiParser == null) {
			finiParser = new FiniParser();
		}
		return finiParser;
	}

	private void initializeFields() {
		storedUserInput = "";
		commandType = null;
		priority = null;
		projectName = null;
		datetimes = new ArrayList<LocalDateTime>();
		isRecurring = false;
		recursUntil = null;
		notParsed = "";
		parser = new Parser();
	}
}
