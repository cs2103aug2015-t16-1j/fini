package fini.main.model;

import javax.xml.bind.ValidationEvent;

import org.w3c.dom.css.ElementCSSInlineStyle;

public class FiniParser {

	private static FiniParser parser;
	private Storage taskOrganiser;

	enum CommandType {
		ADD, DELETE, UPDATE, DISPLAY, CLEAR, SORT, SEARCH, INVALID, EXIT
	};

	public FiniParser() {
		this.taskOrganiser = Storage.getInstance();
	}

	public boolean parse(String userInput) {
		try {
			String cleanInput = getCleanString(userInput);
			System.out.println(cleanInput);
			String[] userInputSplitArray = cleanInput.split(" ");
			String commandParameters = "";
			if (userInputSplitArray.length > 1) {
				commandParameters = userInput.replace(userInputSplitArray[0], "").substring(1);
			}
			CommandType userCommand = getUserCommand(userInputSplitArray[0].toLowerCase());
			executeCommand(userCommand, commandParameters);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private CommandType getUserCommand(String command) {
		switch (command) {
		case "add":
			return CommandType.ADD;
		case "update":
			return CommandType.UPDATE;
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
		default:
			break;
		}
	}

	private void addTask(String commandParameters) {
		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
		boolean isRecurringTask = checkIfRecurringTask(commandParameters);
		boolean hasPriority = checkIfHasPriority(commandParameters);
		boolean hasProject = checkIfHasProject(commandParameters);
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
				String[] taskDetailsArray = taskDetails.split(" ");
				date = taskDetailsArray[0];
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

	private void updateTask(String commandParameters) {
		Integer taskId = checkTaskId(commandParameters);
		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
		if (0 < taskId && taskId < taskOrganiser.getSize() + 2 && hasTaskParameters) {
			Task taskForUpdate = taskOrganiser.getTasks().get(taskId - 1);
			String taskUpdateDetails = commandParameters.split("//")[1];

			boolean needToChangeTitle = checkIfHasTitle(taskUpdateDetails);
			boolean needToChangePriority = checkIfHasPriority(taskUpdateDetails);
			boolean needToChangeProject = checkIfHasProject(taskUpdateDetails);
			boolean needToChangeDate = checkIfHasDate(taskUpdateDetails);
			boolean isEvent = taskForUpdate.checkIfDeadline();
			boolean isDeadline = taskForUpdate.checkIfDeadline();
			boolean isRecurringTask = taskForUpdate.checkIfRecurring();

			if (needToChangeTitle) {
				taskForUpdate.setTitle(extractTitle(taskUpdateDetails));
			}
			if (needToChangePriority) {
				taskForUpdate.setPriority(extractPriority(taskUpdateDetails));
			}
			if (needToChangeProject) {
				taskForUpdate.setProject(extractProject(taskUpdateDetails));
			}
			if (needToChangeDate) {
				if (isRecurringTask) {
					if(isDeadline) {
						setRecurTaskDate(taskUpdateDetails);
					} else if(isEvent) {
						setRecurTaskStartAndEndTime(taskUpdateDetails);
					}
				} else {
					if(isDeadline) {
						setTaskDate(taskUpdateDetails);
					} else if (isEvent) {
						setTaskStartAndEndTime(taskUpdateDetails);
					}
				}
			}

			taskOrganiser.getTasks().set(taskId - 1, taskForUpdate);
		} else {
			System.out.println("Invalid UPDATE input");
		}
	}

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

	private boolean checkIfHasParameters(String commandParameters) {
		return commandParameters.contains("//");
	}

	private boolean checkIfHasProject(String commandParameters) {
		return commandParameters.contains("project");
	}

	private String extractProject(String commandParameters) {
		int indexOfProject = commandParameters.indexOf("project");
		String projectDetails = commandParameters.substring(indexOfProject);
		projectDetails = projectDetails.replace("project ", "");
		String[] removeProjectKeyword = projectDetails.split(" ");
		return removeProjectKeyword[0];
	}

	private String extractTitle(String commandParameters) {
		int indexOfProject = commandParameters.indexOf("title");
		String projectDetails = commandParameters.substring(indexOfProject);
		projectDetails = projectDetails.replace("project ", "");
		String[] removeProjectKeyword = projectDetails.split(" ");
		return removeProjectKeyword[0];
	}

	private String extractPriority(String commandParameters) {
		int indexOfPriority = commandParameters.indexOf("priority");
		String priorityDetails = commandParameters.substring(indexOfPriority);
		priorityDetails = priorityDetails.replace("priority ", "");
		String[] removePriorityKeyword = priorityDetails.split(" ");
		return removePriorityKeyword[0];
	}

	private boolean checkIfTaskIsDeadline(String commandParameters) {
		return commandParameters.contains("at");
	}

	private boolean checkIfTaskIsEvent(String commandParameters) {
		return commandParameters.contains("from") && commandParameters.contains("to");
	}

	private boolean checkIfHasPriority(String commandParameters) {
		return commandParameters.contains("with priority");
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
