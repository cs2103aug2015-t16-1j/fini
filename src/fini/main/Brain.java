package fini.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import fini.main.model.Command;
import fini.main.model.Command.CommandType;
import fini.main.model.FiniParser;
import fini.main.model.StatusSaver;
import fini.main.model.Storage;
import fini.main.model.Task;
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
		rootController.updateFiniPoints(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
	}

//	public static void main(String[] args) {
//		Brain testBrain = Brain.getInstance();
//		testBrain.executeCommand("add curry chicken tomorrow repeat everyday until dec priority high");
//	}
	
	public void executeCommand(String userInput) {
		boolean searchDisplayTrigger = false;
		boolean displayComplete = false;
		
		Command newCommand = new Command(userInput);
		CommandType commandType = newCommand.getCommandType();
		int objectIndex = newCommand.getObjectIndex();
		String commandParameters = newCommand.getCommandParameters();
		
		String display = "";
		switch (commandType) {
		case ADD:
			saveThisStatus();
			display = addTask(commandParameters);
			break;
		case UPDATE:
			saveThisStatus();
			display = updateTask(objectIndex, commandParameters);
			break;
		case DELETE:
			saveThisStatus();
			display = deleteTask(objectIndex);
			break;
		case CLEAR:
			saveThisStatus();
			display = clearAllTasks();
			break;
		case UNDO:
			display = undo();
			break;
		case DISPLAY:
			displayComplete = true;
			display = displayTask(commandParameters);
			break;
//		case SEARCH:
//			searchDisplayTrigger = true;
//			searchTask(commandParameters);
//			break;
//		case MODE:
//			MainApp.switchMode();
//			break;
		case MODS:
			saveThisStatus();
			display = loadNUSMods();
			break;
		case EXIT:
			System.exit(0);
		case COMPLETE:
			saveThisStatus();
			display = completeTask(objectIndex);
			break;
		case INVALID:
			display = "commandType INVALID";
			break;
		default:
			break;
		}

		sortTaskMasterList();
		if (!searchDisplayTrigger && !displayComplete) {
			taskObservableList.setAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
		}
		
		rootController.updateMainDisplay(taskObservableList);
		rootController.updateProjectsOverviewPanel(taskObservableList);
		rootController.updateTasksOverviewPanel(taskObservableList);
		rootController.updateDisplayToUser(display);
		rootController.updateFiniPoints(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
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
		
		taskMasterList.add(newTask);
		taskOrganiser.updateFile(taskMasterList);
		return "Added: " + finiParser.getNotParsed();
	}
	
	private String updateTask(int objectIndex, String commandParameters) {
		Task taskToUpdate;
		
		if (commandParameters.isEmpty()) {
			return "CommandParameters is empty";
		}
		
		try {
			taskToUpdate = taskObservableList.get(objectIndex - 1);
		} catch (IndexOutOfBoundsException e) {
			return "Task not found";
		}
		
		// delete first
		taskObservableList.remove(taskToUpdate);
		taskMasterList.remove(taskToUpdate);
		taskOrganiser.updateFile(taskMasterList);
		
		// add then
		finiParser.parse(commandParameters);
		
		Task newTask = new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
						   .setDatetimes(finiParser.getDatetimes())
						   .setPriority(finiParser.getPriority())
						   .setInterval(finiParser.getInterval())
						   .setRecursUntil(finiParser.getRecursUntil()).build();
		
		taskMasterList.add(newTask);
		taskOrganiser.updateFile(taskMasterList);
		
		return "Update: " + (objectIndex + 1) + taskToUpdate.getTitle();
	}
	
	// @author A0121828H
	private String clearAllTasks() {
		taskMasterList.clear();
		taskOrganiser.updateFile(taskMasterList);
		return "All tasks have been cleared";
	}
	
	private String deleteTask(int objectIndex) {
		Task taskToDelete;
		try {
			taskToDelete = taskObservableList.get(objectIndex - 1);
		} catch (IndexOutOfBoundsException e) {
			return "Task not found";
		}
		
		taskObservableList.remove(taskToDelete);
		taskMasterList.remove(taskToDelete);
		taskOrganiser.updateFile(taskMasterList);
		return "Delete: " + (objectIndex + 1) + " " + taskToDelete.getTitle();
	}
	
	private String completeTask(int objectIndex) {
//		taskTitle 
//		isRecurring 
//		priority.toString()
//		(taskStartDateTime == null ? "Null" : taskStartDateTime.toString())
//		(taskEndDateTime == null ? "Null" : taskEndDateTime.toString())
//		(recursUntil == null ? "Null" : recursUntil)
//		(interval == null ? "Null" : interval.toString()) 
//		isCompleted
//		taskType.toString()
		
		Task taskToComplete;
		try {
			taskToComplete = taskObservableList.get(objectIndex - 1);
		} catch (IndexOutOfBoundsException e) {
			return "Task not found";
		}
		
		if (taskToComplete.isRecurring() && taskToComplete.hasNext()) {
			Task copyTask = taskToComplete.makeCopy();
			copyTask.setIsComplete();
			copyTask.updateObjectID();
			
			for (Iterator<Task> iterator = taskMasterList.iterator(); iterator.hasNext(); ) {
				Task taskToRemove = iterator.next();
				if (!taskToRemove.getObjectID().equals(taskToComplete.getObjectID()) &&
						taskToRemove.hasRecurUniqueID() &&
						taskToRemove.getRecurUniqueID().equals(copyTask.getRecurUniqueID())) {
					iterator.remove();
				}
			}
			taskMasterList.add(copyTask);
			taskToComplete.toNext();
		} else {
			taskToComplete.setIsComplete();
		}
		taskOrganiser.updateFile(taskMasterList);
		return "Complete: " + (objectIndex + 1) + taskToComplete.getTitle();
	}

	/**
	 * EXTRAORDINARY FEATURE - Sync with nusmods html file
	 * @author gaieepo
	 */
	private String loadNUSMods() {
		File modsFile = new File(finiParser.getCleanParameters());
		if (modsFile.exists()) {
			ModsLoader loader = new ModsLoader(modsFile);
			taskMasterList.addAll(loader.getLessonTasks());
		} else {
			return "No nusmods file";
		}
		taskOrganiser.updateFile(taskMasterList);
		return "NUSMODS loaded";
	}
	
	private String undo() {
		if (statusSaver.isMasterStackEmpty()) {
			return "Cannot undo lah! You haven't done any changes yet.";
		}
		statusSaver.retrieveLastStatus();
		taskMasterList = statusSaver.getLastTaskMasterList();
		taskObservableList = statusSaver.getLastTaskObservableList();
		taskOrganiser.updateFile(taskMasterList);
		return "Undo~do~do~do~do~";
	}
	
	private String displayTask(String commandParameters) {
		if (commandParameters.equals("completed")) {
			taskObservableList.setAll(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
		}
		return "displayTask method";
	}
	
	private void searchTask(String commandParameters) {
		ObservableList<Task> tempObservableList = FXCollections.observableArrayList(); 
		for (Task task : taskObservableList) {
			if (task.getTitle().contains("commandParameters")) {
				tempObservableList.add(task);
			}
		}
		taskObservableList = tempObservableList;
	}
	
	private void saveThisStatus() {
		assert taskMasterList != null;
		assert taskObservableList != null;
		statusSaver.saveStatus(taskMasterList, taskObservableList);
	}

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
