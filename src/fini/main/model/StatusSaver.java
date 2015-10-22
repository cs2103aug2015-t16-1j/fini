package fini.main.model;

import java.util.ArrayList;
import java.util.Stack;

import javafx.collections.ObservableList;

/**
 * This class handles the saving of past status of the application
 * Mainly for UNDO and REDO (possibly) functions
 * @author gaieepo
 *
 */
public class StatusSaver {
	private static StatusSaver statusSaver;
	
	private Stack<ArrayList<Task>> masterStack;
	private Stack<ObservableList<Task>> observableStack;
	
	private ArrayList<Task> taskMasterList;
	private ObservableList<Task> taskObservableList;
	
	private StatusSaver() {
		masterStack = new Stack<ArrayList<Task>>();
		observableStack = new Stack<ObservableList<Task>>();
	}
	
	public static StatusSaver getInstance() {
		if (statusSaver == null) {
			statusSaver = new StatusSaver();
		}
		return statusSaver;
	}
	
	
}