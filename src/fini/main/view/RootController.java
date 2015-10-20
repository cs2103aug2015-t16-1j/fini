package fini.main.view;

import org.junit.internal.runners.TestMethod;

/*
 * This is the root controller class for the display (view). The interactions on the Graphical User
 * Interface (GUI), which are the commands entered by users in the command box are handled here.
 * 
 * @author A0121828H
 */

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
import javafx.scene.layout.HBox;

public class RootController {
	@FXML
	private ListView<HBox> listView;

	@FXML
	private TextField commandBox;

	@FXML
	private ListView<String> projectsOverviewPanel;

	@FXML
	private ListView<HBox> tasksOverviewPanel;

	@FXML
	private Label displayToUser;

//	private Brain brain = Brain.getInstance();
	private Storage taskOrganiser;
	private FiniParser parser;

	private String userInput;

	public RootController() {
		parser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
	}

	@FXML
	protected void initialize() {
<<<<<<< HEAD
		taskOrganiser.readFile();
		taskOrganiser.sortTaskMasterList();
		updateMainDisplay(taskOrganiser.getTasks());
		// TODO: Uncomment when sort for display is fully implemented
		//sortForMainDisplay(taskOrganiser.getTasks());
		updateProjectsOverviewPanel(taskOrganiser.getTasks());
		updateTasksOverviewPanel(taskOrganiser.getTasks());
=======
		taskOrganiser.readFile();
		taskOrganiser.sortTaskMasterList();
		updateMainDisplay(taskOrganiser.getTasks());
		// TODO: Uncomment when sort for display is fully implemented
//		sortForMainDisplay(taskOrganiser.getTasks());
		updateProjectsOverviewPanel(taskOrganiser.getTasks());
		updateTasksOverviewPanel(taskOrganiser.getTasks());
>>>>>>> a2ea4637a3fc3b99cca0ed07b147a61deef94337
		commandBox.requestFocus();
//		taskOrganiser.updateFile();
	}

	private void sortForMainDisplay(ObservableList<Task> tasks) {
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
		HBox floatingCategory = createCategoryBox("Floating Tasks");
		displayBoxes.add(floatingCategory);
		
		for(Task task : tasks) {
			if(task.getDate() == null) {
				displayBoxes.add(getFloatingTaskBox(task));
			}
		}
		listView.setItems(displayBoxes);
	}

	private HBox getFloatingTaskBox(Task task) {
		HBox floatingTaskBox = new HBox();
		Label title = new Label(task.getTitle());
		floatingTaskBox.getChildren().add(title);
		return floatingTaskBox;
	}

	private HBox createCategoryBox(String category) {
		HBox categoryBox = new HBox();
		Label categoryLabel = new Label(category);
		categoryBox.getChildren().add(categoryLabel);
		return categoryBox;
	}

	@FXML
	public void handleKeyPressEvent(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			userInput = commandBox.getText();
			System.out.println(userInput);
			commandBox.clear();
			
			boolean isOperationSuccessful = parser.parse(userInput);
			taskOrganiser.sortTaskMasterList();
			updateMainDisplay(taskOrganiser.getTasks());
			updateProjectsOverviewPanel(taskOrganiser.getTasks());
			updateTasksOverviewPanel(taskOrganiser.getTasks());
			updateDisplayToUser(isOperationSuccessful);
			taskOrganiser.updateFile();
		}
	}

	// Update Display
	public void updateDisplayToUser(boolean isOperationSuccessful) {
		if (isOperationSuccessful) {
			displayToUser.setText("Operation Successful");
		} else {
			displayToUser.setText("Error Occurred");
		}
	}
	
	public void setDisplayToUser(String display) {
		displayToUser.setText(display);
	}

	public void updateTasksOverviewPanel(ObservableList<Task> taskMasterList) {
		ObservableList<HBox> tasksOverview = FXCollections.observableArrayList();
		String[] taskTypeName = new String[] {"Inbox", "Today", "This Week", "Total"};
		Integer[] taskTypeNum = new Integer[] {0, 0, 0, 0};

		for (Task task : taskMasterList) {
			for (int i = 0; i < 3; i++) {
				if (task.getLabelForTaskOverviewPane() == taskTypeName[i]) {
					taskTypeNum[i]++;
				}
			}
		}

		taskTypeNum[3] = taskTypeNum[0] + taskTypeNum[1] + taskTypeNum[2];

		for (int i = 0; i < 4; i++) {
			HBox newOverviewBox = new HBox();
			Label name = new Label(taskTypeName[i]);
			Label num = new Label(taskTypeNum[i].toString());
			newOverviewBox.getChildren().addAll(name, num);
			newOverviewBox.setSpacing(30);
			tasksOverview.add(newOverviewBox);
		}
		tasksOverviewPanel.setItems(tasksOverview);
	}

	public void updateProjectsOverviewPanel(ObservableList<Task> taskMasterList) {
		ObservableList<String> projectsOverview = FXCollections.observableArrayList();
		for (Task task : taskMasterList) {
			if (projectsOverview.contains(task.getProject()) == false) {
				projectsOverview.add(task.getProject());
			}
		}
		projectsOverviewPanel.setItems(projectsOverview);
	}

	public void updateMainDisplay(ObservableList<Task> taskMasterList) {
		// ObservableList<String> taskList = FXCollections.observableArrayList();
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
		for (Task task : taskMasterList) {
			int taskId = taskMasterList.indexOf(task) + 1;
			String taskTitle = task.getTitle();
			String taskProject = task.getProject();
			String taskPriority = task.getPriority();
			String taskStartTime = task.getStartTime() == null ? "No time" : task.getStartTime().toString();
			String taskEndTime = task.getEndTime() == null ? "No time" : task.getEndTime().toString();

			boolean isRecurringTask = task.checkIfRecurring();
			boolean isDeadline = task.checkIfDeadline();
			boolean isEvent = task.checkIfEvent();
			boolean isFloating = task.checkIfFloating();
			HBox newTaskBox = null;
			String taskDate = "";
			String typeOfTask = "";

			if (isRecurringTask) {
				taskDate = task.getRecurringDay();
				System.out.println("Recurring day is " + taskDate);
			} else {
				taskDate = task.getDate() == null ? "No date" : task.getDate().toString();
			}
			if (isFloating) {
				typeOfTask = "floating";
			} else if (isDeadline) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}
			newTaskBox = addHBox(taskId, typeOfTask, taskTitle, taskDate, taskStartTime, taskEndTime,
					taskPriority, taskProject, isRecurringTask);

			displayBoxes.add(newTaskBox);
		}
		listView.setItems(displayBoxes);
	}

	// Add Display Component
	public HBox addHBox(int taskId, String typeOfTask, String taskTitle, String taskDate, String taskStartTime,
			String taskEndTime, String taskPriority, String taskProject, boolean isRecurringTask) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		// hbox.setStyle("-fx-background-color: #006EEE; -fx-font-family: Helvetica; -fx-text-fill:
		// white;");
		// hbox.setStyle("-fx-font-family: Raleway;");
		Label id = new Label(Integer.toString(taskId));
		Label title = new Label(taskTitle);
		// title.setStyle("-fx-text-fill: white;");
		Label date = new Label(taskDate);
		Label startTime = new Label(taskStartTime);
		Label endTime = new Label(taskEndTime);
		Label priority = new Label(taskPriority);
		Label project = new Label(taskProject);
		Label isRecurring = null;
		// Label recurringDay = null;

		switch (typeOfTask) {
		case "floating":
			hbox.getChildren().addAll(id, title);
			break;
		case "deadline":
			hbox.getChildren().addAll(id, title, date, startTime);
			break;
		case "event":
			hbox.getChildren().addAll(id, title, date, startTime, endTime);
			break;
		default:
			break;
		}
		if (taskPriority != null) {
			hbox.getChildren().addAll(priority);
		}

		if (taskProject != null) {
			hbox.getChildren().addAll(project);
		}

		if (isRecurringTask) {
			// recurringDay
			System.out.println("Recurring day is " + date.getText());
			isRecurring = new Label("R");
			hbox.getChildren().addAll(isRecurring);
		}
		return hbox;
	}
}
