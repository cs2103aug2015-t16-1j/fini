package fini.main.model;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import javafx.collections.FXCollections;
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
	
	private ArrayList<Task> tempTaskMasterList;
	private ObservableList<Task> tempTaskObservableList;
	
	
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
	
	// Public Methods
	public void saveStatus(ArrayList<Task> taskMasterList, ObservableList<Task> taskObservableList) {
		masterStack.push(copyArrayList(taskMasterList));
		observableStack.push(copyObservableList(taskObservableList));
	}
	
	public void retrieveLastStatus() {
		try {
			tempTaskMasterList = masterStack.pop();
			tempTaskObservableList = observableStack.pop();
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> getLastTaskMasterList() {
		assert  tempTaskMasterList != null;
		return tempTaskMasterList;
	}
	
	public ObservableList<Task> getLastTaskObservableList() {
		assert tempTaskObservableList != null;
		return tempTaskObservableList;
	}
	
	// Utility Methods
	public boolean isMasterStackEmpty() {
		return masterStack.isEmpty();
	}
	
	private ArrayList<Task> copyArrayList(ArrayList<Task> origin) {
		ArrayList<Task> duplicate = new ArrayList<Task>();
		for (Task task : origin) {
			duplicate.add(task.makeCopy());
		}
		return duplicate;
	}
	
	private ObservableList<Task> copyObservableList(ObservableList<Task> origin) {
		ArrayList<Task> duplicate = new ArrayList<Task>();
		for (Task task : origin) {
			duplicate.add(task.makeCopy());
		}
		return FXCollections.observableArrayList(duplicate);
	}
}