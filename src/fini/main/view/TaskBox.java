package fini.main.view;

import fini.main.MainApp;
import fini.main.model.Task.Priority;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * This class structures each task into a displayable horizontal box that will be shown
 * in the main display.
 * 
 * @@author A0121828H
 */

public class TaskBox extends HBox {
    private static final double OPACITY_ZERO = 0.0;

    // FXML Components
    @FXML
    private Circle priority;

    @FXML
    private Label index;

    @FXML
    private Label title;

    @FXML
    private ImageView recurring;

    @FXML
    private Label project;

    @FXML
    private Label startDate;

    @FXML
    private Label endDate;

    @FXML
    private Label startTime;

    @FXML
    private Label endTime;

    public TaskBox(int taskId, String typeOfTask, String taskTitle, String taskStartDate, String taskEndDate, String taskStartTime,
            String taskEndTime, Priority taskPriority, String taskProject, boolean isRecurringTask) {

        loadFxml();

        if(taskPriority != null) {
            switch(taskPriority) {
                case HIGH:
                    this.priority.setFill(Color.INDIANRED);
                    break;
                case MEDIUM:
                    this.priority.setFill(Color.DARKORANGE);
                    break;
                case LOW:
                    this.priority.setFill(Color.GOLD);
                    break;
                default:
                    this.priority.setFill(Color.CORNFLOWERBLUE);
                    break;
            }
        }

        if(taskProject != null) {
            this.project.setText(taskProject);
        } else {
            hideLabel(project);
        }

        if(!isRecurringTask) {
            this.recurring.setOpacity(OPACITY_ZERO);
        } 

        if(taskStartDate != null) {
            this.startDate.setText(taskStartDate);
        } else {
            hideLabel(startDate);
        }

        if(taskEndDate != null) {
            this.endDate.setText(taskEndDate);
        } else {
            hideLabel(endDate);
        }

        if(taskStartTime != null) {
            startTime.setText(taskStartTime);
        } else {
            hideLabel(startTime);
        }

        if(taskEndTime != null) {
            endTime.setText(taskEndTime);
        } else {
            if(taskStartTime != null) {
                endTime.setText("-");
            } else {
                hideLabel(endTime);
            }
        }

        this.index.setText(Integer.toString(taskId));
        this.startDate.setText(taskStartDate);
        this.endDate.setText(taskEndDate);
        this.title.setText(taskTitle);
    }

    private void hideLabel(Label label) {
        label.setText("");
        label.setOpacity(OPACITY_ZERO);
    }

    private void loadFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            MainApp.finiLogger.severe("TaskBox.fxml is not loaded.");
            e.printStackTrace();
        }
    }
}
