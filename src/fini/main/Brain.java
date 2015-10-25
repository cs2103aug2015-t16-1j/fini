package fini.main;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Spliterator;
import java.util.stream.Collectors;

import fini.main.model.Command;
import fini.main.model.FiniParser;
import fini.main.model.StatusSaver;
import fini.main.model.Storage;
import fini.main.model.Task;
import fini.main.model.Command.CommandType;
import fini.main.util.ModsLoader;
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
	private StatusSaver statusSaver;

	private ArrayList<Task> taskMasterList;
	private ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

	private Brain() {
		finiParser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
		statusSaver = StatusSaver.getInstance();
		
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

//	public static void main(String[] args) {
//		Brain testBrain = Brain.getInstance();
//		testBrain.executeCommand("add curry chicken tomorrow repeat everyday until dec priority high");
//	}
	
	public void executeCommand(String userInput) {
//		finiParser.parse(userInput);

//		System.out.println(">>>>>");
//		System.out.println("StoredInput: " + finiParser.getStoredUserInput());
//		System.out.println("CommandType: " + finiParser.getCommandType());
//		System.out.println("CommandParameters: " + finiParser.getCommandParameters());
//		System.out.println("CleanParameters: " + finiParser.getCleanParameters());
//		System.out.println("Priority: " + finiParser.getPriority());
//		System.out.println("ProjectName: " + finiParser.getProjectName());
//		for (LocalDateTime ldt : finiParser.getDatetimes()) {
//			System.out.println("Datetime: " + ldt.toString());
//		}
//		System.out.println("NotParsed: " + finiParser.getNotParsed());
//		System.out.println("<<<<<");

		Command newCommand = new Command(userInput);
		CommandType commandType = newCommand.getCommandType();
		int objectIndex = newCommand.getObjectIndex();
		String commandParameters = newCommand.getCommandParameters();
		
//		System.out.println(commandType);
		
		String display = "";
		switch (commandType) {
		case ADD:
			saveThisStatus();
			display = addTask(commandParameters);
			break;
//		case UPDATE:
//			saveThisStatus();
//			display = updateTask(objectIndex, commandParameters);
//			break;
		case DELETE:
			saveThisStatus();
			display = deleteTask(objectIndex);
			break;
		case CLEAR:
			saveThisStatus();
			display = clearAllTasks();
			break;
//		case UNDO:
//			display = undo();
//			break;
//		case MODE:
////			MainApp.switchMode();
//			break;
//		case MODS:
//			saveThisStatus();
//			display = loadNUSMods(commandParameters);
//			break;
		case EXIT:
			System.exit(0);
//		case COMPLETE:
//			saveThisStatus();
//			display = completeTask(objectIndex, );
//			break;
		case INVALID:
			display = "commandType INVALID";
			break;
		default:
			break;
		}

		sortTaskMasterList();
		taskObservableList.setAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
		
//		System.out.println("display - Bain execute: " + display);
		
		rootController.updateMainDisplay(taskObservableList);
		rootController.updateProjectsOverviewPanel(taskObservableList);
		rootController.updateTasksOverviewPanel(taskObservableList);
		rootController.updateDisplayToUser(display);
	}
	
	// Logic Methods
	private String addTask(String commandParameters) {
		if (commandParameters.isEmpty()) {
			return "CommandParameters is empty";
		}
		
		finiParser.parse(commandParameters);
		
		Task newTask = new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
						   .setDatetimes(finiParser.getDatetimes())
						   .setPriority(finiParser.getPriority())
						   .setInterval(finiParser.getInterval())
						   .setRecursUntil(finiParser.getRecursUntil()).build();
		
//		@Override
//		public String toString() {
//			return taskTitle + " " + 
//				   isRecurring + " " + 
//				   priority.toString() + " " + 
//				   (taskStartDateTime == null ? "Null" : taskStartDateTime.toString()) + " " +
//				   (taskEndDateTime == null ? "Null" : taskEndDateTime.toString()) + " " +
//				   (recursUntil == null ? "Null" : recursUntil) + " " +
//				   (interval == null ? "Null" : interval.toString()) + " " +
//				   isCompleted + " " +
//				   taskType.toString();
//		}
		System.out.println("task detail - addTask: " + newTask);
		
		taskMasterList.add(newTask);
		taskOrganiser.updateFile(taskMasterList);
		return "Added: " + finiParser.getNotParsed();
	}
	
//	private String updateTask() {
//		int taskIndex;
//		Task taskToUpdate;
//		
//		if (finiParser.getCommandParameters().split(" ").length < 2) {
//			return "Update insufficient parameters INVALID";
//		}
//		
//		String[] splitNotParsed = finiParser.getNotParsed().split(" ");
//		
//		try {
//			taskIndex = Integer.parseInt(splitNotParsed[0]) - 1;
//			taskToUpdate = taskObservableList.get(taskIndex);
//		} catch (IndexOutOfBoundsException | NumberFormatException e) {
//			return "Task not found";
//		}
//		
//		String[] fixedSplitNotParsed = (String[]) Arrays.copyOfRange(splitNotParsed, 1, splitNotParsed.length);
//		String fixedNotParsed = String.join(" ", fixedSplitNotParsed);
//		
//		// delete first
//		taskObservableList.remove(taskToUpdate);
//		taskMasterList.remove(taskToUpdate);
//		taskOrganiser.updateFile(taskMasterList);
//		
//		// add then
//		Task newTask = new Task(fixedNotParsed, 
//								finiParser.getDatetimes(), 
//								finiParser.getPriority(),
//								finiParser.getProjectName(),
//								finiParser.getIsRecurring(),
//								finiParser.getRecursUntil());
//		taskMasterList.add(newTask);
//		taskOrganiser.updateFile(taskMasterList);
//		
//		return "Update: " + (taskIndex + 1) + taskToUpdate.getTitle();
//	}
	
	// @author A0121828H
	private String clearAllTasks() {
		taskMasterList.clear();
		taskOrganiser.updateFile(taskMasterList);
		return "All tasks have been cleared";
	}
	
	private String deleteTask(int objectIndex) {
		Task taskToDelete;
		try {
			taskToDelete = taskObservableList.get(objectIndex);
		} catch (IndexOutOfBoundsException e) {
			return "Task not found";
		}
		
		taskObservableList.remove(taskToDelete);
		taskMasterList.remove(taskToDelete);
		taskOrganiser.updateFile(taskMasterList);
		return "Delete: " + (objectIndex + 1) + " " + taskToDelete.getTitle();
	}
	
//	private String completeTask() {
//		int taskIndex;
//		Task taskToComplete;
//		try {
//			taskIndex = Integer.parseInt(finiParser.getCleanParameters()) - 1;
//			taskToComplete = taskObservableList.get(taskIndex);
//			taskToComplete.setComplete();
//		} catch (IndexOutOfBoundsException e) {
//			return "Task not found";
//		}
//		return "Complete: " + (taskIndex + 1) + taskToComplete.getTitle();
//	}

	/**
	 * EXTRAORDINARY FEATURE - Sync with nusmods html file
	 * @author gaieepo
	 */
//	private String loadNUSMods() {
//		File modsFile = new File(finiParser.getCleanParameters());
//		if (modsFile.exists()) {
//			ModsLoader loader = new ModsLoader(modsFile);
//			taskMasterList.addAll(loader.getLessonTasks());
//		} else {
//			return "No nusmods file";
//		}
//		taskOrganiser.updateFile(taskMasterList);
//		return "NUSMODS loaded";
//	}
	
//	private String undo() {
//		if (statusSaver.isMasterStackEmpty()) {
//			return "Cannot undo lah! You haven't done any changes yet.";
//		}
//		statusSaver.retrieveLastStatus();
//		taskMasterList = statusSaver.getLastTaskMasterList();
//		taskObservableList = statusSaver.getLastTaskObservableList();
//		return "Undo~do~do~do~do~";
//	}
	
	private void saveThisStatus() {
		assert taskMasterList != null;
		assert taskObservableList != null;
		statusSaver.saveStatus(taskMasterList, taskObservableList);
	}

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
