package fini.main.view;

import fini.main.Brain;
import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class RootController extends BorderPane {
	@FXML
	private ListView<HBox> listView;

	@FXML
	private TextField commandBox;

	@FXML
	private ListView<String> projectsOverviewPanel;

	@FXML
	private ListView<String> tasksOverviewPanel;

	@FXML
	private Label displayToUser;

	private Brain brain = Brain.getInstance();
	
	private String userInput;

	public RootController() {
		displayToUser = new Label();
		displayToUser.setText("Welcome to Fini");
	}

	@FXML
	public void handleKeyPressEvent(KeyEvent event) throws Exception {
		if(event.getCode() == KeyCode.ENTER) {
			userInput = commandBox.getText();
			System.out.println(userInput);

			brain.executeCommand(userInput);
			
			commandBox.clear();
		}
	}

	// Update Display
	public void updateDisplayToUser(boolean isOperationSuccessful) {
		if(isOperationSuccessful) {
			displayToUser.setText("Operation Successful");
		} else {
			displayToUser.setText("Error Occurred");
		}
	}
	
	public void updateTasksOverviewPanel(ObservableList<Task> taskMasterList) {
		ObservableList<String> tasksOverview = FXCollections.observableArrayList();
	}

	public void updateProjectsOverviewPanel(ObservableList<Task> taskMasterList) {
		ObservableList<String> projectsOverview = FXCollections.observableArrayList();
		for(Task task: taskMasterList) {
			if(projectsOverview.contains(task.getProject()) == false) {
				projectsOverview.add(task.getProject());
			}
		}
		projectsOverviewPanel.setItems(projectsOverview);
	}

	public void updateMainDisplay(ObservableList<Task> taskMasterList) {
		//ObservableList<String> taskList = FXCollections.observableArrayList();
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
		for(Task task: taskMasterList) {
			String taskTitle = task.getTitle();
			String taskProject = task.getProject();
			String taskPriority = task.getPriority();
			String taskStartTime = task.getStartTime();
			String taskEndTime = task.getEndTime();

			boolean isRecurringTask = task.checkIfRecurring();
			boolean isDeadline = task.checkIfDeadline();
			boolean isEvent = task.checkIfEvent();
			boolean isFloating = task.checkIfFloating();
			HBox newTaskBox = null;
			String taskDate = "";
			String typeOfTask = "";

			if(isRecurringTask) {
				taskDate = task.getRecurringDay();
				System.out.println("Recurring day is " + taskDate);
			} else {
				taskDate = task.getDate();
			}
			if(isFloating) {
				typeOfTask = "floating";
			} else if(isDeadline) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}
			newTaskBox = addHBox(typeOfTask, taskTitle, taskDate, taskStartTime, taskEndTime, taskPriority, taskProject, isRecurringTask);

			displayBoxes.add(newTaskBox);
		}
		listView.setItems(displayBoxes);
	}

	// Add Display Component
	public HBox addHBox(String typeOfTask, String taskTitle, String taskDate, String taskStartTime, String taskEndTime, String taskPriority, String taskProject, boolean isRecurringTask) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		//hbox.setStyle("-fx-background-color: #006EEE; -fx-font-family: Helvetica; -fx-text-fill: white;");
		//hbox.setStyle("-fx-font-family: Raleway;");

		Label title = new Label(taskTitle);
		//title.setStyle("-fx-text-fill: white;");
		Label date = new Label(taskDate);
		Label startTime = new Label(taskStartTime);
		Label endTime = new Label(taskEndTime);
		Label priority = new Label(taskPriority);
		Label project = new Label(taskProject);
		Label isRecurring = null;
		//Label recurringDay = null;

		switch(typeOfTask) {
		case "floating":
			hbox.getChildren().addAll(title);
			break;
		case "deadline":
			hbox.getChildren().addAll(title, date, startTime);
			break;
		case "event":
			hbox.getChildren().addAll(title, date, startTime, endTime);
			break;
		default:
			break;
		}
		if(taskPriority != null) {
			hbox.getChildren().addAll(priority);
		}

		if(taskProject != null) {
			hbox.getChildren().addAll(project);
		}

		if(isRecurringTask) {
			//recurringDay
			System.out.println("Recurring day is " + date.getText());
			isRecurring = new Label("R");
			hbox.getChildren().addAll(isRecurring); 
		}
		return hbox;
	}
}
