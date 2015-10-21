package fini.main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.joestelmach.natty.Parser;

import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.model.Task;
import fini.main.model.FiniParser.CommandType;
import fini.main.model.Task.Priority;
import fini.main.util.Sorter;
import fini.main.view.RootController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * A Half-damaged Brain
 * @author gaieepo
 */
public class Brain {
	private static Brain brain;
	private RootController rootController;

	private Storage taskOrganiser;
	private FiniParser finiParser;
	private Sorter sorter;

	private ArrayList<Task> taskMasterList;
	private ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

	private Brain() {
		finiParser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();

		// Everything stored here in Brain unless an updateFile is executed
		// taskMasterList: all existing tasks
		// taskObservableList: all displayed tasks
		taskMasterList = taskOrganiser.readFile();
		sortTaskMasterList();
		taskObservableList.addAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
	}

	public static Brain getInstance() {
		if (brain == null) {
			brain = new Brain();
		}
		return brain;
	}

	// Initialize first display when Fini is started - executed in MainApp 
	public void initDisplay() {
		rootController.updateMainDisplay(taskObservableList);
		rootController.updateProjectsOverviewPanel(taskObservableList);
		rootController.updateTasksOverviewPanel(taskObservableList);
	}

	public void executeCommand(String userInput) {
		String display = "";

		finiParser.parse(userInput);

		System.out.println(">>>>>");
		System.out.println(finiParser.getStoredUserInput());
		System.out.println(finiParser.getCommandType());
		System.out.println(finiParser.getCommandParameters());
		System.out.println(finiParser.getCleanParameters());
		System.out.println(finiParser.getPriority());
		System.out.println(finiParser.getProjectName());
		for (LocalDateTime ldt : finiParser.getDates()) {
			System.out.println(ldt.toString());
		}
		System.out.println(finiParser.getNotParsed());
		System.out.println("<<<<<");

		CommandType commandType = finiParser.getCommandType();
		switch (commandType) {
		case ADD:
			display = addTask();
			break;
		case MODE:
			MainApp.switchMode();
			break;
		default:
			break;
		}

		sortTaskMasterList();
		rootController.updateMainDisplay(taskObservableList);
		rootController.updateProjectsOverviewPanel(taskObservableList);
		rootController.updateTasksOverviewPanel(taskObservableList);
		rootController.updateDisplayToUser(display);
	}

	// Logic Methods
	private String addTask() {
		if (finiParser.getCommandParameters().isEmpty()) {
			return "CommandParameters is empty";
		}

		ArrayList<LocalDateTime> parsedDates = finiParser.getDates();
		String notParsedString = finiParser.getNotParsed();

		//		Task newTask = new Task(isRecurringTask, title, date, startTime, endTime, priority, project);
		//		taskOrganiser.addNewTask(newTask);

		return "Add Task Method";
	}

	//	/**
	//	 * EXTRAORDINARY FEATURE - Sync with nusmods html file
	//	 * @author gaieepo
	//	 */
	//	private void loadNUSMods(String commandParameters) {
	//		File modsFile = new File(commandParameters);
	//		if (modsFile.exists()) {
	//			ModsLoader loader = new ModsLoader(modsFile);
	//		} else {
	//			System.out.println("No Mods File");
	//		}
	//	}
	//
	//	private void clearTasks(String commandParameters) {
	//		taskOrganiser.clearTasks();
	//	}
	//
	//	private void addTask(String commandParameters) {
	//		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
	//		boolean isRecurringTask = checkIfRecurringTask(commandParameters);
	//		boolean hasPriority = checkIfHasPriority(commandParameters);
	//		boolean hasProject = checkIfHasProject(commandParameters);
	//		boolean hasDate = checkIfDateIsAvailable(commandParameters);
	//		boolean isEvent = checkIfTaskIsEvent(commandParameters);
	//		boolean isDeadline = checkIfTaskIsDeadline(commandParameters);
	//		String[] splitParameters = null;
	//		String[] splitTaskDetails = null;
	//		String taskDetails = "";
	//		// System.out.println("PRINTING TASK DETAILS: " + taskDetails);
	//
	//		String priority = null;
	//		String project = null;
	//		String startTime = null;
	//		String endTime = null;
	//		String date = null;
	//		String title = null;
	//
	//		if (hasTaskParameters) {
	//			splitParameters = commandParameters.split(" ");
	//			splitTaskDetails = commandParameters.split("//");
	//			taskDetails = splitTaskDetails[1].substring(1);
	//
	//			int indexOfStartOfTaskDetails = commandParameters.indexOf(" //");
	//			title = commandParameters.substring(0, indexOfStartOfTaskDetails);
	//
	//			if (hasPriority) {
	//				priority = extractPriority(commandParameters);
	//			}
	//
	//			if (hasProject) {
	//				project = extractProject(commandParameters);
	//			}
	//
	//			if (isRecurringTask) {
	//				int indexOfEvery = taskDetails.indexOf("every ");
	//				System.out.println("The task details for the recurring task: " + taskDetails);
	//				String removeEveryKeyWord = taskDetails.substring(indexOfEvery);
	//				removeEveryKeyWord = removeEveryKeyWord.replace("every ", "");
	//				System.out
	//				.println("The task details for the recurring task: " + removeEveryKeyWord);
	//				String[] splitRemoveEveryKeyWord = removeEveryKeyWord.split(" ");
	//				date = splitRemoveEveryKeyWord[0];
	//				System.out.println("The task details for the recurring task: " + date);
	//
	//				if (isEvent) {
	//					int indexOfFrom = taskDetails.indexOf("from");
	//					int indexOfTo = taskDetails.indexOf("to");
	//
	//					String removeFromKeyword = taskDetails.substring(indexOfFrom);
	//					removeFromKeyword = removeFromKeyword.replace("from ", "");
	//					String[] splitRemoveFromKeyword = removeFromKeyword.split(" ");
	//					startTime = splitRemoveFromKeyword[0];
	//
	//					String removeToKeyword = taskDetails.substring(indexOfTo);
	//					removeToKeyword = removeToKeyword.replace("to ", "");
	//					String[] splitRemoveToKeyword = removeToKeyword.split(" ");
	//					endTime = splitRemoveToKeyword[0];
	//
	//				} else if (isDeadline) {
	//					int indexOfAt = taskDetails.indexOf("at ");
	//					String removeAtKeyword = taskDetails.substring(indexOfAt);
	//					removeAtKeyword = removeAtKeyword.replace("at ", "");
	//					String[] splitRemoveAtKeyword = removeAtKeyword.split(" ");
	//					startTime = splitRemoveAtKeyword[0];
	//					endTime = null;
	//				}
	//			} else {
	//				System.out.println(taskDetails);
	//				String[] taskDetailsArray = taskDetails.split(" ");
	//				if(hasDate) {
	//					date = taskDetailsArray[0];
	//				} else {
	//					date = null;
	//				}
	//				System.out.println(date);
	//				if (isDeadline) {
	//					int indexOfStartTime = taskDetails.indexOf("at ");
	//					String removeAtKeyword = taskDetails.substring(indexOfStartTime);
	//					removeAtKeyword = removeAtKeyword.replace("at ", "");
	//					String[] splitRremoveAtKeyword = removeAtKeyword.split(" ");
	//					startTime = splitRremoveAtKeyword[0];
	//					endTime = null;
	//				} else if (isEvent) {
	//					int indexOfStartTime = taskDetails.indexOf("from ");
	//					String removeFromKeyword = taskDetails.substring(indexOfStartTime);
	//					removeFromKeyword = removeFromKeyword.replace("from ", "");
	//					String[] splitRemoveFromKeyword = removeFromKeyword.split(" ");
	//					startTime = splitRemoveFromKeyword[0];
	//					System.out.println(startTime);
	//
	//					int indexOfEndTime = taskDetails.indexOf("to ");
	//					String removeToKeyword = taskDetails.substring(indexOfEndTime);
	//					removeToKeyword = removeToKeyword.replace("to ", "");
	//					String[] splitRemoveToKeyword = removeToKeyword.split(" ");
	//					endTime = splitRemoveToKeyword[0];
	//					System.out.println(endTime);
	//				} else {
	//					startTime = null;
	//					endTime = null;
	//				}
	//			}
	//		} else {
	//			title = commandParameters.trim();
	//		}
	//		Task newTask =
	//				new Task(isRecurringTask, title, date, startTime, endTime, priority, project);
	//		taskOrganiser.addNewTask(newTask);
	//	}
	//
	//	private void deleteTask(String commandParameters) {
	//		Integer taskId = checkTaskId(commandParameters);
	//
	//		if (taskId > taskOrganiser.getSize()) {
	//			System.out.println("Invalid TaskID input!");
	//		} else {
	//			Task deletedTask = taskOrganiser.getTasks().get(taskId-1);
	//			taskOrganiser.deleteTask(taskId);
	//			System.out.println("Task " + taskId + ": " + deletedTask.getTitle() + " has been deleted!");
	//		}
	//	}
	//
	//	private void updateTask(String commandParameters) {
	//		Integer taskId = checkTaskId(commandParameters);
	//		boolean hasTaskParameters = checkIfHasParameters(commandParameters);
	//		if (0 < taskId && taskId < taskOrganiser.getSize() + 2 && hasTaskParameters) {
	//			Task taskForUpdate = taskOrganiser.getTasks().get(taskId - 1);
	//			System.out.println("updating task number " + taskId + ": "+ taskForUpdate.getTitle());
	//			String taskUpdateDetails = commandParameters.split("//")[1].trim();
	//
	//			boolean needToChangeTitle = checkIfHasTitle(taskUpdateDetails);
	//			boolean needToChangePriority = checkIfHasPriority(taskUpdateDetails);
	//			boolean needToChangeProject = checkIfHasProject(taskUpdateDetails);
	//			boolean needToChangeDate = checkIfHasDate(taskUpdateDetails);
	//			boolean needToChangeTime = checkIfHasTime(taskUpdateDetails);
	//			boolean isEvent = taskForUpdate.checkIfDeadline();
	//			boolean isDeadline = taskForUpdate.checkIfDeadline();
	//			boolean isRecurringTask = taskForUpdate.checkIfRecurring();
	//
	//			// TODO cannot add more than one word for title. need to fix
	//			if (needToChangeTitle) {
	//				taskForUpdate.setTitle(extractInformation("title", taskUpdateDetails));
	//			}
	//			if (needToChangePriority) {
	//				taskForUpdate.setPriority(extractInformation("priority", taskUpdateDetails));
	//			}
	//			if (needToChangeProject) {
	//				taskForUpdate.setProject(extractInformation("project", taskUpdateDetails));
	//			}
	//			if(needToChangeDate) {
	//				taskForUpdate.setTaskDate(extractInformation("date", taskUpdateDetails));
	//			}
	//			if (needToChangeTime) {
	//				/* recurring function not implemented
	//				 * if (isRecurringTask) {
	//				if(isDeadline) {
	//					setRecurTaskDate(taskUpdateDetails);
	//				} else if(isEvent) {
	//					setRecurTaskStartAndEndTime(taskUpdateDetails);
	//				}
	//			} else {
	//				 */
	//				if(isDeadline) {
	//					taskForUpdate.setStartTime(extractInformation("time", taskUpdateDetails));
	//				} else if (isEvent) {
	//					String timeDetail = extractInformation("time", taskUpdateDetails);
	//					taskForUpdate.setStartTime(extractInformation("from", timeDetail));
	//					taskForUpdate.setEndTime(extractInformation("to", timeDetail));
	//				}
	//			}
	//
	//			taskOrganiser.getTasks().set(taskId - 1, taskForUpdate);
	//		} else {
	//			System.out.println("Invalid UPDATE input");
	//		}
	//	}


	// Initialization Methods
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}

	private void sortTaskMasterList() {
		assert taskMasterList != null;
		sorter = new Sorter(taskMasterList);
		sorter.sort();
		taskMasterList = sorter.getSortedList();
	}
}
