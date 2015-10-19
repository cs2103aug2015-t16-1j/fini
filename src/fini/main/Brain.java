package fini.main;

import java.util.ArrayList;
import java.util.stream.Collectors;

import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.model.Task;
import fini.main.util.Sorter;
import fini.main.view.RootController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * Brain
 * @author gaieepo
 */
public class Brain {
	private static Brain brain;
	private RootController rootController;

	private Storage taskOrganiser;
	private FiniParser parser;
	private Sorter sorter;

	private ArrayList<Task> taskMasterList;
	private ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

	private Brain() {
		parser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
		
		taskMasterList = taskOrganiser.readFile();

		sortAllTasks();
		
		ArrayList<Task> incompleteTasks = (ArrayList<Task>) taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList());
		taskObservableList.addAll(incompleteTasks);
	}

	public static Brain getInstance() {
		if (brain == null) {
			brain = new Brain();
			//            brain.initFirstDisplay();
		}
		return brain;
	}

	private void initFirstDisplay() {
		//    	rootController.setFeedback("Welcome to fini");
		//    	rootController.updateMainDisplay(taskMasterList);
	}

	public void executeCommand(String command) {
		boolean isOperationSuccessful;
		isOperationSuccessful = parser.parse(command);

		// Change all of these hierarchy: TODO
		rootController.updateMainDisplay(taskOrganiser.getTasks());
		rootController.updateProjectsOverviewPanel(taskOrganiser.getTasks());
		rootController.updateTasksOverviewPanel(taskOrganiser.getTasks());
		rootController.updateDisplayToUser(isOperationSuccessful);
		taskOrganiser.updateFile();

		// Handle Using Command Class: TODO
		// Command newCommand = new Command(command);
		//
		// Command.Type commandType = newCommand.getCommandType();
		// String commandAction = newCommand.getCommandAction();
		// String commandArguments = newCommand.getCommandArguments();
	}

	// Init Methods
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}

	private void sortAllTasks() {
		assert taskMasterList != null;
		sorter = new Sorter(taskMasterList);
		sorter.sort();
		taskMasterList = sorter.getSortedList();
	}
}
