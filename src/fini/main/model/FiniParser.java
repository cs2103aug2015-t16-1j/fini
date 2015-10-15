package fini.main.model;

public class FiniParser {
    private static FiniParser parser;
    private Storage taskOrganiser;

    enum CommandType {
	ADD, DELETE, DISPLAY, CLEAR, SORT, SEARCH, INVALID, EXIT
    };

    public FiniParser() {
	this.taskOrganiser = Storage.getInstance();
    }

    public void parse(String userInput) {
	String[] userInputSplitArray = userInput.split(" ");
	String commandParameters = "";
	if(userInputSplitArray.length > 1) {
	    commandParameters= userInput.replace(userInputSplitArray[0], "").substring(1);
	}
	CommandType userCommand = getUserCommand(userInputSplitArray[0].toLowerCase());
	executeCommand(userCommand, commandParameters);
    }

    private CommandType getUserCommand(String command) {
	switch(command) {
	case "add":
	    return CommandType.ADD;
	default:
	    return CommandType.INVALID;
	}
    }

    private void executeCommand(CommandType userCommand, String commandParameters) {
	switch(userCommand) {
	case ADD:
	    addTask(commandParameters);
	    break;
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
	//System.out.println("PRINTING TASK DETAILS: " + taskDetails);

	String priority = null;
	String project = null;
	String startTime = null;
	String endTime = null;
	String date = null;
	String title = null;

	if(hasTaskParameters) {
	    splitParameters = commandParameters.split(" ");
	    splitTaskDetails = commandParameters.split("//");
	    taskDetails = splitTaskDetails[1].substring(1).toLowerCase();

	    int indexOfStartOfTaskDetails = commandParameters.indexOf(" //");
	    title = commandParameters.substring(0, indexOfStartOfTaskDetails);

	    if(hasPriority) {
		priority = extractPriority(commandParameters);
	    }

	    if(hasProject) {
		project = extractProject(commandParameters);
	    }

	    if(isRecurringTask) {
		int indexOfEvery = taskDetails.indexOf("every ");
		System.out.println("The task details for the recurring task: " + taskDetails);
		String removeEveryKeyWord = taskDetails.substring(indexOfEvery);
		removeEveryKeyWord = removeEveryKeyWord.replace("every ", "");
		System.out.println("The task details for the recurring task: " + removeEveryKeyWord);
		String[] splitRemoveEveryKeyWord = removeEveryKeyWord.split(" ");
		String recurringDay = splitRemoveEveryKeyWord[0];
		System.out.println("The task details for the recurring task: " + recurringDay);

		if(isEvent) {
		    int indexOfFrom = commandParameters.indexOf("from");
		    int indexOfTo = commandParameters.indexOf("to");
		    startTime = commandParameters.substring(indexOfFrom + 2, indexOfFrom + 5);
		    endTime = commandParameters.substring(indexOfTo + 2, indexOfTo + 5);
		} else if(isDeadline) {
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
		if(isDeadline) {
		    int indexOfStartTime = taskDetails.indexOf("at ");
		    String removeAtKeyword = taskDetails.substring(indexOfStartTime);
		    removeAtKeyword = removeAtKeyword.replace("at ", "");
		    String[] splitRremoveAtKeyword = removeAtKeyword.split(" ");
		    startTime = splitRremoveAtKeyword[0];
		    endTime = null;
		} else if(isEvent) {
		    int indexOfStartTime = taskDetails.indexOf("from ");
		    String removeFromKeyword = taskDetails.substring(indexOfStartTime);
		    removeFromKeyword = removeFromKeyword.replace("from ", "");
		    String[] splitRemoveFromKeyword = removeFromKeyword.split(" ");
		    startTime = splitRemoveFromKeyword[0];

		    int indexOfEndTime = taskDetails.indexOf("to ");
		    String removeToKeyword = taskDetails.substring(indexOfEndTime);
		    removeToKeyword = removeToKeyword.replace("to ", "");
		    String[] splitRemoveToKeyword = removeToKeyword.split(" ");
		    endTime = splitRemoveToKeyword[0];
		} else {
		    startTime = null;
		    endTime = null;
		}
	    }
	} else {
	    title = commandParameters.replaceAll(" ", "");
	}
	Task newTask = new Task(isRecurringTask, title, date, startTime, endTime, priority, project);
	taskOrganiser.addNewTask(newTask);
    }

    private boolean checkIfHasParameters(String commandParameters) {
	return commandParameters.contains("//");
    }

    private String extractProject(String commandParameters) {
	String project;
	String projectDetails = commandParameters.substring(commandParameters.indexOf("project"));
	project = projectDetails.replace("project", "");
	return project;
    }

    private boolean checkIfHasProject(String commandParameters) {
	return commandParameters.contains("project");
    }

    private String extractPriority(String commandParameters) {
	String priority;
	String priorityDetails = commandParameters.substring(commandParameters.indexOf("with priority"));
	priority = priorityDetails.replace("with priority ", "");
	return priority;
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

    public static FiniParser getInstance() {
	if(parser == null) {
	    parser = new FiniParser();
	}
	return parser;
    }
}
