package fini.main.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import fini.main.model.Task.Priority;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RootController {
	@FXML
	private ListView<HBox> listView;

	@FXML
	private TextField commandBox;

	@FXML
	private ListView<String> projectsOverviewPanel;

	@FXML
	private VBox tasksOverviewPanel;

	@FXML
	private Label displayToUser;

	@FXML
	private Label inboxTasks;

	@FXML
	private Label todayTasks;

	@FXML
	private Label thisWeekTasks;

	@FXML
	private Label otherTasks;

	private Brain brain = Brain.getInstance();

	private String userInput;
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	public RootController() {
		// TODO With the Brain fully functioning, here we do not initialize anything
	}

	@FXML
	protected void initialize() {
		commandBox.requestFocus();
	}

	@FXML
	public void handleKeyPressEvent(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			userInput = commandBox.getText();
			System.out.println("RootController: " + userInput);
			commandBox.clear();

			brain.executeCommand(userInput);

			//			boolean isOperationSuccessful = parser.parse(userInput);
			//			taskOrganiser.sortTaskMasterList();
			//			updateMainDisplay(taskOrganiser.getTasks());
			//			updateProjectsOverviewPanel(taskOrganiser.getTasks());
			//			updateTasksOverviewPanel(taskOrganiser.getTasks());
			//			updateDisplayToUser(isOperationSuccessful);
			//			taskOrganiser.updateFile();
		}
		// TODO
		// 1. SEARCH/EDIT + SPACE -> auto-trigger for instant search and auto-complete for edit
		// 2. COMMAND + TAB -> auto-complete command (Veto's idea)
		// 3. UP/DOWN -> previous/next command
		// 4. PAGEUP/PAGEDOWN -> scroll up/down
	}

	//	private void sortForMainDisplay(ObservableList<Task> tasks) {
	//		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
	//		HBox floatingCategory = createCategoryBox("Floating Tasks");
	//		displayBoxes.add(floatingCategory);
	//		
	//		for(Task task : tasks) {
	//			if(task.getDate() == null) {
	//				displayBoxes.add(getFloatingTaskBox(task));
	//			}
	//		}
	//		listView.setItems(displayBoxes);
	//	}
	
	// Update Display
	public void updateDisplayToUser(String display) {
		//		if (display != null) {
		//			displayToUser.setText("Operation Successful");
		//		} else {
		//			displayToUser.setText("NULL");
		//		}
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

		inboxTasks.setText(Integer.toString(taskTypeNum[0]));
		todayTasks.setText(Integer.toString(taskTypeNum[1]));
		thisWeekTasks.setText(Integer.toString(taskTypeNum[2]));
		otherTasks.setText(Integer.toString(taskTypeNum[3]));
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
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

		// All category boxes
		ObservableList<HBox> overdueBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> floatingBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> todayBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> tomorrowBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> otherBoxes = FXCollections.observableArrayList();

		for (Task task : taskMasterList) {
			int taskId = taskMasterList.indexOf(task) + 1;

			String taskTitle = task.getTitle();
			String taskProject = task.getProject();
			String taskStartTime = task.getStartTime() == null ? null : timeFormatter.format(task.getStartTime());
			String taskEndTime = task.getEndTime() == null ? null : timeFormatter.format(task.getEndTime());
			String taskDate = "";
			String typeOfTask = "";

			Priority taskPriority = task.getPriority();

			boolean isRecurringTask = task.checkIfRecurring();
			boolean isDeadline = task.checkIfDeadline();
			boolean isEvent = task.checkIfEvent();
			boolean isFloating = task.checkIfFloating();
			boolean isOverdue = task.checkIfOverdue();

			if (isRecurringTask) {
				taskDate = task.getRecurringDay();
				System.out.println("Recurring day is " + taskDate);
			} else {
				taskDate = task.getStartDate() == null ? null : task.getStartDate().toString();
			}
			if (isFloating) {
				typeOfTask = "floating";
			} else if (isDeadline) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}
			TaskBox newTaskBox = new TaskBox(taskId, typeOfTask, taskTitle, taskDate, taskStartTime, taskEndTime,
					taskPriority, taskProject, isRecurringTask);

			if(isOverdue) {
				overdueBoxes.add(newTaskBox);
			} else {
				if(isFloating) {
					floatingBoxes.add(newTaskBox);
				} else if(task.isTaskDueToday()) {
					todayBoxes.add(newTaskBox);
				} else if(task.isTaskDueTomorrow()) {
					tomorrowBoxes.add(newTaskBox);
				} else {
					otherBoxes.add(newTaskBox);
				}
			}	
		}

		TaskCategory overdueCategory = null;
		TaskCategory floatingCategory = null;
		TaskCategory todayCategory = null;
		TaskCategory tomorrowCategory = null;
		TaskCategory othersCategory = null;
		
		// Create all category boxes
		try {
			overdueCategory = new TaskCategory("Overdue");
			floatingCategory = new TaskCategory("Floating");
			todayCategory = new TaskCategory("Today");
			tomorrowCategory = new TaskCategory("Tomorrow");
			othersCategory = new TaskCategory("Other Tasks");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Add tasks and category in sequence
		displayBoxes.add(overdueCategory);
		displayBoxes.addAll(overdueBoxes);
		displayBoxes.add(floatingCategory);
		displayBoxes.addAll(floatingBoxes);
		displayBoxes.add(todayCategory);
		displayBoxes.addAll(todayBoxes);
		displayBoxes.add(tomorrowCategory);
		displayBoxes.addAll(tomorrowBoxes);
		displayBoxes.add(othersCategory);
		displayBoxes.addAll(otherBoxes);

		listView.setItems(displayBoxes);
	}

	// Add Display Component
	public HBox addHBox(int taskId, String typeOfTask, String taskTitle, String taskDate, String taskStartTime,
			String taskEndTime, String taskPriority, String taskProject, boolean isRecurringTask) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		Label id = new Label(Integer.toString(taskId));
		Label title = new Label(taskTitle);
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
