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
 * A Half-demaged Brain
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
		
		// taskMasterList: all tasks
		// taskObservableList: all incomplete tasks
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

	public void executeCommand(String command) {
		String display = finiParser.parse(command);
//		taskOrganiser.sortTaskMasterList();
//		updateMainDisplay(taskOrganiser.getTasks());
//		updateProjectsOverviewPanel(taskOrganiser.getTasks());
//		updateTasksOverviewPanel(taskOrganiser.getTasks());
		rootController.updateDisplayToUser(display);
//		taskOrganiser.updateFile();
	}

	// Init Methods
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
