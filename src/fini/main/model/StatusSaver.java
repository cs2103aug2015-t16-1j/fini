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
	
	private Stack<ArrayList<Task>> undoMasterStack;
	private Stack<ObservableList<Task>> undoObservableStack;
	
	private Stack<ArrayList<Task>> redoMasterStack;
	private Stack<ObservableList<Task>> redoObservableStack;
	
	private ArrayList<Task> tempTaskMasterList;
	private ObservableList<Task> tempTaskObservableList;
	
	
	private StatusSaver() {
		undoMasterStack = new Stack<ArrayList<Task>>();
		undoObservableStack = new Stack<ObservableList<Task>>();
		redoMasterStack = new Stack<ArrayList<Task>>();
		redoObservableStack = new Stack<ObservableList<Task>>();
	}
	
	public static StatusSaver getInstance() {
		if (statusSaver == null) {
			statusSaver = new StatusSaver();
		}
		return statusSaver;
	}
	
//	public void printer() {
//		System.out.print("Undo{");
//		for (ArrayList<Task> at : undoMasterStack) {
//			System.out.print("[");
//			for (Task t : at) {
//				System.out.print(t.getTitle() + ",");
//			}
//			System.out.print("]");
//		}
//		System.out.print("}");
//		System.out.print("Redo{");
//		for (ArrayList<Task> at : redoMasterStack) {
//			System.out.print("[");
//			for (Task t : at) {
//				System.out.print(t.getTitle() + ",");
//			}
//			System.out.print("]");
//		}
//		System.out.print("}");
//	}
	
	// Public Methods
	public void saveStatus(ArrayList<Task> taskMasterList, ObservableList<Task> taskObservableList) {
		undoMasterStack.push(copyArrayList(taskMasterList));
		undoObservableStack.push(copyObservableList(taskObservableList));
		redoMasterStack.clear();
		redoObservableStack.clear();
	}
	
	public void saveStatusToRedo(ArrayList<Task> taskMasterList, ObservableList<Task> taskObservableList) {
		redoMasterStack.push(copyArrayList(taskMasterList));
		redoObservableStack.push(copyObservableList(taskObservableList));
	}
	
	public void retrieveLastStatus() {
		try {
			tempTaskMasterList = undoMasterStack.pop();
			tempTaskObservableList = undoObservableStack.pop();
			
			if (!isUndoMasterStackEmpty()) {
				redoMasterStack.push(copyArrayList(tempTaskMasterList));
				redoObservableStack.push(copyObservableList(tempTaskObservableList));
			}
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
	}
	
	public void retrieveRedoStatus() {
		try {
			tempTaskMasterList = redoMasterStack.pop();
			tempTaskObservableList = redoObservableStack.pop();
			
			if (!isRedoMasterStackEmpty()) {
				undoMasterStack.push(copyArrayList(tempTaskMasterList));
				undoObservableStack.push(copyObservableList(tempTaskObservableList));
			}
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> getLastTaskMasterList() {
		assert tempTaskMasterList != null;
		return tempTaskMasterList;
	}
	
	public ObservableList<Task> getLastTaskObservableList() {
		assert tempTaskObservableList != null;
		return tempTaskObservableList;
	}
	
	// Utility Methods
	public boolean isUndoMasterStackEmpty() {
		return undoMasterStack.isEmpty();
	}
	
	public boolean isRedoMasterStackEmpty() {
		return redoMasterStack.isEmpty();
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