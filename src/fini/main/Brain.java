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
 * A Half-damaged Brain
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
		String display = parser.parse(userInput);
		
		// Execute All Logic Process
		// TODO
		
		sortTaskMasterList();
		
		rootController.updateMainDisplay(taskObservableList);
		rootController.updateProjectsOverviewPanel(taskObservableList);
		rootController.updateTasksOverviewPanel(taskObservableList);
		rootController.updateDisplayToUser(display);
	}
	
	// Logic Methods -> All the CRUD should be in Brain class
	// updateFile should be in logic methods
	

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
