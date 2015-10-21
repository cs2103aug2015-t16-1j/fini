package fini.main.model;

import java.io.File;

import fini.main.util.ModsLoader;

public class FiniParser {
	public static enum CommandType {
		ADD, DELETE, UPDATE, DISPLAY, CLEAR, SORT, SEARCH, INVALID, EXIT, COMPLETE, MODS
	};

	private static FiniParser parser;

	private CommandType commandType;
	
	public FiniParser() {
		// TODO Nothing should be initialized yet
	}

	public String parse(String userInput) {
		try {
			String cleanInput = getCleanString(userInput);
			System.out.println(cleanInput);
			String[] userInputSplitArray = cleanInput.split(" ");
			String commandParameters = "";
			if (userInputSplitArray.length > 1) {
				commandParameters = userInput.replace(userInputSplitArray[0], "").substring(1);
			}
			commandType = getUserCommandType(userInputSplitArray[0].toLowerCase());
			executeCommand(commandType, commandParameters);
			return "Operation Successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error Occurred";
		}
	}

	private CommandType getUserCommandType(String command) {
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

	private void executeCommand(CommandType userCommand, String commandParameters) {
		switch (userCommand) {
		case ADD:
			addTask(commandParameters);
			break;
		case UPDATE:
			updateTask(commandParameters);
			break;
		case DELETE:
			deleteTask(commandParameters);
			break;
		case CLEAR:
			clearTasks(commandParameters);
			break;
		case EXIT:
			System.exit(0);
			break;
		case COMPLETE:
			completeTask(commandParameters);
			break;
		case MODS:
			loadNUSMods(commandParameters);
			break;
		default:
			break;
		}
	}
	
	/**
	 * EXTRAORDINARY FEATURE - Sync with nusmods html file
	 * @author gaieepo
	 */
	private void loadNUSMods(String commandParameters) {
		File modsFile = new File(commandParameters);
		if (modsFile.exists()) {
			ModsLoader loader = new ModsLoader(modsFile);
		} else {
			System.out.println("No Mods File");
		}
	}

	private void clearTasks(String commandParameters) {
		taskOrganiser.clearTasks();
	}

	// @author A0121828H
	private void addTask(String commandParameters) {
		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
		boolean isRecurringTask = checkIfRecurringTask(commandParameters);
		boolean hasPriority = checkIfHasPriority(commandParameters);
		boolean hasProject = checkIfHasProject(commandParameters);
		boolean hasDate = checkIfDateIsAvailable(commandParameters);
		boolean isEvent = checkIfTaskIsEvent(commandParameters);
		boolean isDeadline = checkIfTaskIsDeadline(commandParameters);
		String[] splitParameters = null;
		String[] splitTaskDetails = null;
		String taskDetails = "";
		// System.out.println("PRINTING TASK DETAILS: " + taskDetails);

		String priority = null;
		String project = null;
		String startTime = null;
		String endTime = null;
		String date = null;
		String title = null;

		if (hasTaskParameters) {
			splitParameters = commandParameters.split(" ");
			splitTaskDetails = commandParameters.split("//");
			taskDetails = splitTaskDetails[1].substring(1);

			int indexOfStartOfTaskDetails = commandParameters.indexOf(" //");
			title = commandParameters.substring(0, indexOfStartOfTaskDetails);

			if (hasPriority) {
				priority = extractPriority(commandParameters);
			}

			if (hasProject) {
				project = extractProject(commandParameters);
			}

			if (isRecurringTask) {
				int indexOfEvery = taskDetails.indexOf("every ");
				System.out.println("The task details for the recurring task: " + taskDetails);
				String removeEveryKeyWord = taskDetails.substring(indexOfEvery);
				removeEveryKeyWord = removeEveryKeyWord.replace("every ", "");
				System.out
				.println("The task details for the recurring task: " + removeEveryKeyWord);
				String[] splitRemoveEveryKeyWord = removeEveryKeyWord.split(" ");
				date = splitRemoveEveryKeyWord[0];
				System.out.println("The task details for the recurring task: " + date);

				if (isEvent) {
					int indexOfFrom = taskDetails.indexOf("from");
					int indexOfTo = taskDetails.indexOf("to");

					String removeFromKeyword = taskDetails.substring(indexOfFrom);
					removeFromKeyword = removeFromKeyword.replace("from ", "");
					String[] splitRemoveFromKeyword = removeFromKeyword.split(" ");
					startTime = splitRemoveFromKeyword[0];

					String removeToKeyword = taskDetails.substring(indexOfTo);
					removeToKeyword = removeToKeyword.replace("to ", "");
					String[] splitRemoveToKeyword = removeToKeyword.split(" ");
					endTime = splitRemoveToKeyword[0];

				} else if (isDeadline) {
					int indexOfAt = taskDetails.indexOf("at ");
					String removeAtKeyword = taskDetails.substring(indexOfAt);
					removeAtKeyword = removeAtKeyword.replace("at ", "");
					String[] splitRemoveAtKeyword = removeAtKeyword.split(" ");
					startTime = splitRemoveAtKeyword[0];
					endTime = null;
				}
			} else {
				System.out.println(taskDetails);
				String[] taskDetailsArray = taskDetails.split(" ");
				if(hasDate) {
					date = taskDetailsArray[0];
				} else {
					date = null;
				}
				System.out.println(date);
				if (isDeadline) {
					int indexOfStartTime = taskDetails.indexOf("at ");
					String removeAtKeyword = taskDetails.substring(indexOfStartTime);
					removeAtKeyword = removeAtKeyword.replace("at ", "");
					String[] splitRremoveAtKeyword = removeAtKeyword.split(" ");
					startTime = splitRremoveAtKeyword[0];
					endTime = null;
				} else if (isEvent) {
					int indexOfStartTime = taskDetails.indexOf("from ");
					String removeFromKeyword = taskDetails.substring(indexOfStartTime);
					removeFromKeyword = removeFromKeyword.replace("from ", "");
					String[] splitRemoveFromKeyword = removeFromKeyword.split(" ");
					startTime = splitRemoveFromKeyword[0];
					System.out.println(startTime);

					int indexOfEndTime = taskDetails.indexOf("to ");
					String removeToKeyword = taskDetails.substring(indexOfEndTime);
					removeToKeyword = removeToKeyword.replace("to ", "");
					String[] splitRemoveToKeyword = removeToKeyword.split(" ");
					endTime = splitRemoveToKeyword[0];
					System.out.println(endTime);
				} else {
					startTime = null;
					endTime = null;
				}
			}
		} else {
			title = commandParameters.trim();
		}
		Task newTask =
				new Task(isRecurringTask, title, date, startTime, endTime, priority, project);
		taskOrganiser.addNewTask(newTask);
	}

	private boolean checkIfDateIsAvailable(String commandParameters) {
		commandParameters = commandParameters.replace("//", "");
		return (commandParameters.length() - commandParameters.replace("/", "").length() == 2);
	}

	//author A0121298E
	private void deleteTask(String commandParameters) {
		Integer taskId = checkTaskId(commandParameters);

		if (taskId > taskOrganiser.getSize()) {
			System.out.println("Invalid TaskID input!");
		} else {
			Task deletedTask = taskOrganiser.getTasks().get(taskId-1);
			taskOrganiser.deleteTask(taskId);
			System.out.println("Task " + taskId + ": " + deletedTask.getTitle() + " has been deleted!");
		}
	}

	private void updateTask(String commandParameters) {
		Integer taskId = checkTaskId(commandParameters);
		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
		if (0 < taskId && taskId < taskOrganiser.getSize() + 2 && hasTaskParameters) {
			Task taskForUpdate = taskOrganiser.getTasks().get(taskId - 1);
			System.out.println("updating task number " + taskId + ": "+ taskForUpdate.getTitle());
			String taskUpdateDetails = commandParameters.split("//")[1].trim();

			boolean needToChangeTitle = checkIfHasTitle(taskUpdateDetails);
			boolean needToChangePriority = checkIfHasPriority(taskUpdateDetails);
			boolean needToChangeProject = checkIfHasProject(taskUpdateDetails);
			boolean needToChangeDate = checkIfHasDate(taskUpdateDetails);
			boolean needToChangeTime = checkIfHasTime(taskUpdateDetails);
			boolean isEvent = taskForUpdate.checkIfDeadline();
			boolean isDeadline = taskForUpdate.checkIfDeadline();
			boolean isRecurringTask = taskForUpdate.checkIfRecurring();

			// TODO cannot add more than one word for title. need to fix
			if (needToChangeTitle) {
				taskForUpdate.setTitle(extractInformation("title", taskUpdateDetails));
			}
			if (needToChangePriority) {
				taskForUpdate.setPriority(extractInformation("priority", taskUpdateDetails));
			}
			if (needToChangeProject) {
				taskForUpdate.setProject(extractInformation("project", taskUpdateDetails));
			}
			if(needToChangeDate) {
				taskForUpdate.setTaskDate(extractInformation("date", taskUpdateDetails));
			}
			if (needToChangeTime) {
				/* recurring function not implemented
				 * if (isRecurringTask) {
					if(isDeadline) {
						setRecurTaskDate(taskUpdateDetails);
					} else if(isEvent) {
						setRecurTaskStartAndEndTime(taskUpdateDetails);
					}
				} else {
				 */
				if(isDeadline) {
					taskForUpdate.setStartTime(extractInformation("time", taskUpdateDetails));
				} else if (isEvent) {
					String timeDetail = extractInformation("time", taskUpdateDetails);
					taskForUpdate.setStartTime(extractInformation("from", timeDetail));
					taskForUpdate.setEndTime(extractInformation("to", timeDetail));
				}
			}

			taskOrganiser.getTasks().set(taskId - 1, taskForUpdate);
		} else {
			System.out.println("Invalid UPDATE input");
		}
	}
	
	/**
	 * Set Task as completed
	 * @param commandParameters
	 * @author gaieepo
	 */
	private void completeTask(String commandParameters) {
		// TODO
	}

	// Utility Methods
	private Integer checkTaskId(String commandParameters) {
		String taskIdStr = commandParameters.split("//")[0].trim();
		boolean isNum = false;
		for (char ch : taskIdStr.toCharArray()) {
			if (!Character.isDigit(ch)) {
				isNum = false;
			}
			isNum = true;
		}
		Integer taskId;
		if (isNum) {
			taskId = Integer.valueOf(taskIdStr);
		} else {
			taskId = -1;
		}
		return taskId;
	}

	private boolean checkIfHasTitle(String commandParameters) {
		return commandParameters.toLowerCase().contains("title");
	}

	private boolean checkIfHasDate(String commandParameters) {
		return commandParameters.toLowerCase().contains("date");
	}

	private boolean checkIfHasTime(String commandParameters) {
		return commandParameters.toLowerCase().contains("time");
	}

	private boolean checkIfHasParameters(String commandParameters) {
		return commandParameters.contains("//");
	}

	private boolean checkIfHasProject(String commandParameters) {
		return commandParameters.contains("project");
	}

	private String extractProject(String commandParameters) {
		int indexOfProject = commandParameters.toLowerCase().indexOf("project");
		String projectDetails = commandParameters.substring(indexOfProject);
		projectDetails = projectDetails.replace("project ", "");
		String[] removeProjectKeyword = projectDetails.split(" ");
		return removeProjectKeyword[0];
	}

	private String extractInformation(String keyword, String commandParameters) {
		commandParameters = commandParameters.trim();
		int indexOfKeyword = commandParameters.toLowerCase().indexOf(keyword);
		String commandDetails = commandParameters.substring(indexOfKeyword).trim();
		commandDetails  = commandDetails .replace(keyword, "").trim();
		String[] removeKeyword = commandDetails.split(" ");
		System.out.println(removeKeyword[0]);
		return removeKeyword[0];
	}

	private String extractPriority(String commandParameters) {
		int indexOfPriority = commandParameters.toLowerCase().indexOf("priority");
		String priorityDetails = commandParameters.substring(indexOfPriority);
		priorityDetails = priorityDetails.replace("priority ", "");
		String[] removePriorityKeyword = priorityDetails.split(" ");
		System.out.println(removePriorityKeyword[0]);
		return removePriorityKeyword[0];
	}

	private boolean checkIfTaskIsDeadline(String commandParameters) {
		return commandParameters.contains("at");
	}

	private boolean checkIfTaskIsEvent(String commandParameters) {
		return commandParameters.contains("from") && commandParameters.contains("to");
	}

	private boolean checkIfHasPriority(String commandParameters) {
		return commandParameters.contains("priority");
	}

	private boolean checkIfRecurringTask(String commandParameters) {
		return commandParameters.contains("every");
	}

	private String getCleanString(String userInput) {
		String cleanStr;
		cleanStr = userInput.trim();
		cleanStr = cleanStr.replaceAll("[^A-Za-z0-9/\\s+]", "");
		cleanStr = cleanStr.replaceAll("\\s+", " ");
		return cleanStr;
	}

	public static FiniParser getInstance() {
		if (parser == null) {
			parser = new FiniParser();
		}
		return parser;
	}
}
