package fini.main.model;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class handles the saving of past status of the application
 * Mainly for UNDO and REDO (possibly) functions
 * 
 * @@author Wang Jie (gaieepo) A0127483B
 */
public class StatusSaver {
    /* ***********************************
     * Fields
     * ***********************************/
    // Singleton
    private static StatusSaver statusSaver;

    private Stack<ArrayList<Task>> undoMasterStack;
    private Stack<ObservableList<Task>> undoObservableStack;

    private Stack<ArrayList<Task>> redoMasterStack;
    private Stack<ObservableList<Task>> redoObservableStack;

    private ArrayList<Task> tempTaskMasterList;
    private ObservableList<Task> tempTaskObservableList;

    /* ***********************************
     * Private constructor
     * ***********************************/
    private StatusSaver() {
        undoMasterStack = new Stack<ArrayList<Task>>();
        undoObservableStack = new Stack<ObservableList<Task>>();
        redoMasterStack = new Stack<ArrayList<Task>>();
        redoObservableStack = new Stack<ObservableList<Task>>();
    }
    
    // getInstance method
    public static StatusSaver getInstance() {
        if (statusSaver == null) {
            statusSaver = new StatusSaver();
        }
        return statusSaver;
    }

    /* ***********************************
     * Public methods
     * ***********************************/
    public void saveStatus(ArrayList<Task> taskMasterList, ObservableList<Task> taskObservableList) {
        undoMasterStack.push(copyArrayList(taskMasterList));
        undoObservableStack.push(copyObservableList(taskObservableList));
        redoMasterStack.clear();
        redoObservableStack.clear();
    }

    public void retrieveLastStatus() {
        try {
            redoMasterStack.push(copyArrayList(undoMasterStack.pop()));
            redoObservableStack.push(copyObservableList(undoObservableStack.pop()));

            tempTaskMasterList = copyArrayList(undoMasterStack.peek());
            tempTaskObservableList = copyObservableList(undoObservableStack.peek());
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }

    public void retrieveRedoStatus() {
        try {
            tempTaskMasterList = redoMasterStack.pop();
            tempTaskObservableList = redoObservableStack.pop();

            undoMasterStack.push(copyArrayList(tempTaskMasterList));
            undoObservableStack.push(copyObservableList(tempTaskObservableList));
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }

    /* ***********************************
     * Public getters
     * ***********************************/
    public ArrayList<Task> getLastTaskMasterList() {
        assert tempTaskMasterList != null;
        return tempTaskMasterList;
    }

    public ObservableList<Task> getLastTaskObservableList() {
        assert tempTaskObservableList != null;
        return tempTaskObservableList;
    }
    
    public boolean isUndoMasterStackEmpty() {
        return undoMasterStack.size() == 1;
    }

    public boolean isRedoMasterStackEmpty() {
        return redoMasterStack.isEmpty();
    }

    public int getUndoMasterStackSize() {
        return undoMasterStack.size();
    }

    public int getRedoMasterStackSize() {
        return redoMasterStack.size();
    }
    
    /* ***********************************
     * Utility methods
     * ***********************************/
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