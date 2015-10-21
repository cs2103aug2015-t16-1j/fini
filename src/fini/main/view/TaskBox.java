package fini.main.view;

import fini.main.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

// TODO: Incorporate this into RootController instead of addHBox

public class TaskBox extends HBox {
	// FXML Components
	@FXML
	private Label priority;

	@FXML
	private Label index;

	@FXML
	private Label title;

	@FXML
	private Label recurring;

	@FXML
	private Label project;

	@FXML
	private Label date;

	public TaskBox(int taskId, String typeOfTask, String taskTitle, String taskDate, String taskStartTime,
			String taskEndTime, String taskPriority, String taskProject, boolean isRecurringTask) {
		
		loadFxml();
		
		if(taskPriority != null) {
			this.priority.setText(taskPriority);
		}
		
		if(taskProject != null) {
			this.project.setText(taskProject);
		}
		
		if(isRecurringTask) {
			this.recurring.setText("R");
		}
		
		this.index.setText(Integer.toString(taskId));
		this.date.setText(taskDate);
		this.title.setText(taskTitle);
		
//		HBox hbox = new HBox();
//		hbox.setSpacing(10);
//		
//		Label id = new Label(Integer.toString(taskId));
//		Label title = new Label(taskTitle);
//		// title.setStyle("-fx-text-fill: white;");
//		Label date = new Label(taskDate);
//		Label startTime = new Label(taskStartTime);
//		Label endTime = new Label(taskEndTime);
//		Label priority = new Label(taskPriority);
//		Label project = new Label(taskProject);
//		Label isRecurring = null;
//		// Label recurringDay = null;
//
//		switch (typeOfTask) {
//		case "floating":
//			hbox.getChildren().addAll(id, title);
//			break;
//		case "deadline":
//			hbox.getChildren().addAll(id, title, date, startTime);
//			break;
//		case "event":
//			hbox.getChildren().addAll(id, title, date, startTime, endTime);
//			break;
//		default:
//			break;
//		}
//		if (taskPriority != null) {
//			hbox.getChildren().addAll(priority);
//		}
//
//		if (taskProject != null) {
//			hbox.getChildren().addAll(project);
//		}
//
//		if (isRecurringTask) {
//			// recurringDay
//			System.out.println("Recurring day is " + date.getText());
//			isRecurring = new Label("R");
//			hbox.getChildren().addAll(isRecurring);
//		}
	}

	private void loadFxml() {
		try {
			//URL location = getClass().getResource("../view/TaskBox.fxml");
	        //FXMLLoader taskBoxLoader = new FXMLLoader();
	        //taskBoxLoader.setLocation(location);
            //taskBoxLoader.setController(this);
	        //taskBoxLoader.load();
	        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskBox.fxml"));
	        loader.setRoot(this);
	        loader.setController(this);
	        loader.load();
	        
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
