package fini.main;

import java.io.File;
import java.time.LocalDateTime;
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
	private ObservableList<Task> taskAuxiliaryList = FXCollections.observableArrayList(); 
	
	private boolean searchDisplayTrigger = false;
	
	private boolean completeDisplayTrigger = false;
	private boolean allDisplayTrigger = false;

	private Brain() {
		finiParser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
		statusSaver = StatusSaver.getInstance();
		
		// Everything stored here in Brain unless an updateFile is executed
		// taskMasterList: all existing tasks
		// taskObservableList: all displayed tasks
		taskMasterList = taskOrganiser.readFile();
		sortTaskMasterList();
		taskObservableList.setAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
		taskAuxiliaryList.setAll(taskObservableList); 
		statusSaver.saveStatus(taskMasterList, taskObservableList);
	}

	public static Brain getInstance() {
		if (brain == null) {
			brain = new Brain();
		}
		return brain;
	}

	// Initialize first display when Fini is started - executed in MainApp 
	public void initDisplay() {
		rootController.updateMainDisplay(taskAuxiliaryList);
		rootController.updateProjectsOverviewPanel(taskAuxiliaryList);
		rootController.updateTasksOverviewPanel(taskAuxiliaryList);
		rootController.updateFiniPoints(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
	}

	public void executeCommand(String userInput) {
		Command newCommand = new Command(userInput);
		CommandType commandType = newCommand.getCommandType();
		int objectIndex = newCommand.getObjectIndex();
		String commandParameters = newCommand.getCommandParameters();
		
		String display = "";
		switch (commandType) {
		case ADD:
			display = addTask(commandParameters);
			saveThisStatus();
			break;
		case UPDATE:
			display = updateTask(objectIndex, commandParameters);
			saveThisStatus();
			break;
		case DELETE:
			display = deleteTask(objectIndex);
			saveThisStatus();
			break;
		case CLEAR:
			display = clearAllTasks();
			saveThisStatus();
			break;
		case UNDO:
			display = undo();
			break;
		case REDO:
			display = redo();
			break;
		case DISPLAY:
			display = displayTask(commandParameters);
			break;
		case SEARCH:
		    display = "Searching...";
		    searchTask(commandParameters);
		    break;
//		case MODE:
//			MainApp.switchMode();
//			break;
		case MODS:
			display = loadNUSMods();
			saveThisStatus();
			break;
		case EXIT:
			System.exit(0);
		case COMPLETE:
			display = completeTask(objectIndex);
			saveThisStatus();
			break;
		case UNCOMPLETE:
			display = uncompleteTask(objectIndex);
			saveThisStatus();
			break;
		case INVALID:
			display = "commandType INVALID";
			break;
		default:
			break;
		}

		displayControl();
		
		sortTaskMasterList();
		taskAuxiliaryList.setAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
		rootController.updateProjectsOverviewPanel(taskAuxiliaryList);
		rootController.updateTasksOverviewPanel(taskAuxiliaryList);
		
		rootController.updateDisplayToUser(display);
		rootController.updateFiniPoints(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
	}
	
	// Display Control Methods
	private void displayControl() {
		if (completeDisplayTrigger) {
			taskObservableList.setAll(taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList()));
			rootController.updateCompletedDisplay(taskObservableList);
		} else if (searchDisplayTrigger) {
		    rootController.updateSearchDisplay(taskObservableList);
		} else if (allDisplayTrigger) {
			rootController.updateAllDisplay(taskObservableList);
		} else {
			sortTaskMasterList();
			taskObservableList.setAll(taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList()));
			rootController.updateMainDisplay(taskObservableList);
		}
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
		
		return "Update: " + objectIndex + taskToUpdate.getTitle();
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
		return "Delete: " + objectIndex + " " + taskToDelete.getTitle();
	}
	
	private String completeTask(int objectIndex) {
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
		return "Complete: " + objectIndex + taskToComplete.getTitle();
	}
	
	private String uncompleteTask(int objectIndex) {
		Task taskToUncomplete;
		try {
			taskToUncomplete = taskObservableList.get(objectIndex - 1);
		} catch (IndexOutOfBoundsException e) {
			return "Task not found";
		}
		taskToUncomplete.setIncomplete();
		if (taskToUncomplete.isRecurring()) {
			for (Iterator<Task> iterator = taskMasterList.iterator(); iterator.hasNext(); ) {
				Task taskToRemove = iterator.next();
				if (!taskToRemove.getObjectID().equals(taskToUncomplete.getObjectID()) &&
						taskToRemove.hasRecurUniqueID() &&
						taskToRemove.getRecurUniqueID().equals(taskToUncomplete.getRecurUniqueID())) {
					iterator.remove();
				}
			}
		}
		taskOrganiser.updateFile(taskMasterList);
		return "Uncomplete: " + objectIndex + taskToUncomplete.getTitle();
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
		if (statusSaver.isUndoMasterStackEmpty()) {
			return "Cannot undo lah! You haven't done any changes yet.";
		}
		statusSaver.retrieveLastStatus();
		taskMasterList = statusSaver.getLastTaskMasterList();
		taskObservableList = statusSaver.getLastTaskObservableList();
		taskOrganiser.updateFile(taskMasterList);
		return "Undo~do~do~do~do~";
	}
	
	private String redo() {
		if (statusSaver.isRedoMasterStackEmpty()) {
			return "Cannot redo lah! You dun have anything to redo alrdy.";
		}
		statusSaver.retrieveRedoStatus();
		taskMasterList = statusSaver.getLastTaskMasterList();
		taskObservableList = statusSaver.getLastTaskObservableList();
		taskOrganiser.updateFile(taskMasterList);
		return "Redo~do~do~do~do~";
	}
	
	private String displayTask(String commandParameters) {
		if (commandParameters.equals("completed")) {
			completeDisplayTrigger = true;
			searchDisplayTrigger = false;
			allDisplayTrigger = false;
			return "display completed";
		} else if(commandParameters.equals("") || commandParameters.equals("main")) {
			completeDisplayTrigger = false;
			searchDisplayTrigger = false;
			allDisplayTrigger = false;
			return "display main";
		} else if(commandParameters.equals("all")) {
			completeDisplayTrigger = false;
			searchDisplayTrigger = false;
			allDisplayTrigger = true;
			sortTaskMasterListWithIncomplete();
			taskObservableList.setAll(taskMasterList);
			return "display all";
		} else {
			return "displayTask method";
		}
	}
	
	private void searchTask(String commandParameters) {
        searchDisplayTrigger = true;
        ObservableList<Task> tempObservableList = FXCollections.observableArrayList();
        finiParser.parse(commandParameters);
        ArrayList<LocalDateTime> searchDateTimes = finiParser.getDatetimes();
        for (Task task : taskMasterList) {
            if (task.getTitle().contains(commandParameters)) {
                tempObservableList.add(task);
            } else if (searchDateTimes.size() > 0 && searchDateTimes.get(0).toLocalDate().equals(task.getStartDateTime().toLocalDate())) {
                tempObservableList.add(task);
            }
        }
        taskObservableList.setAll(tempObservableList);
    }
	
	// Utilization Methods
	private void saveThisStatus() {
		assert taskMasterList != null;
		assert taskObservableList != null;
		statusSaver.saveStatus(taskMasterList, taskObservableList);
	}
	
	public ObservableList<Task> getTaskObservableList() {
		return taskObservableList;
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
	
	private void sortTaskMasterListWithIncomplete() {
		assert taskMasterList != null;
		sorter = new Sorter(taskMasterList);
		sorter.addSortByIncomplete();
		sorter.sort();
		taskMasterList = sorter.getSortedList();
	}
}
