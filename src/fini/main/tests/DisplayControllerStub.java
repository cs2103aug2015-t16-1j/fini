package fini.main.tests;

import java.util.ArrayList;
import java.util.List;

import fini.main.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DisplayControllerStub {
    private ArrayList<String> testMessages = new ArrayList<String>();
    private ObservableList<Task> testList = FXCollections.observableArrayList();
    
    public DisplayControllerStub() {
        
    }
    
    // Public methods
    public void updateDisplayToUser(String display) {
    }
    
    public void updateTasksOverviewPanel(ObservableList<Task> taskObservableList) {
    }

    public void updateProjectsOverviewPanel(ObservableList<String> projectNameList) {
    }

    public void updateProjectDisplay(ObservableList<Task> taskObservableList) {
    }
    
    public void updateCompletedDisplay(ObservableList<Task> taskObservableList) {
    }

    public void updateAllDisplay(ObservableList<Task> taskObservableList) {
    }

    public void updateSearchDisplay(ObservableList<Task> taskObservableList) {
    }

    public void updateMainDisplay(ObservableList<Task> taskObservableList) {
        testMessages.add("updateMainDisplay");
        testList = taskObservableList;
    }
    
    public void updateFiniPoints(List<Task> completedTasks) {
    }
    
    public void displayHelpPanel() {
        testMessages.add("displayHelpPanel");
    }

    public void hideHelpPanel() {
    }

    public void setFocusToCommandBox() {
    }
    
    public ArrayList<String> getTestMessages() {
        return testMessages;
    }
    
    public ObservableList<Task> getTestList() {
        return testList;
    }
}