package fini.main.view;

import fini.main.model.Task.Priority;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

// TODO: Incorporate this into RootController instead of addHBox

public class TaskBox extends HBox {
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
				this.priority.setFill(Color.RED);
				break;
			case MEDIUM:
				this.priority.setFill(Color.ORANGE);
				break;
			case LOW:
				this.priority.setFill(Color.YELLOW);
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
			this.recurring.setOpacity(0);
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

	private void hideLabel(Label label) {
		label.setText("");
		label.setOpacity(0);
	}

	private void loadFxml() {
		try {
			//URL location = getClass().getResource("../view/TaskBox.fxml");
			//FXMLLoader taskBoxLoader = new FXMLLoader();
			//taskBoxLoader.setLocation(location);
			//taskBoxLoader.setController(this);
			//taskBoxLoader.load();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskBox2.fxml"));
			loader.setRoot(this);
			loader.setController(this);
			loader.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
