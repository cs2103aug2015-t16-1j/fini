package fini.main.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import fini.main.model.Task.Type;
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
	private Label totalNumberOfTasks;

	@FXML
	private Label finiPoints;

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
		displayToUser.setText(display);
	}

	public void updateTasksOverviewPanel(ObservableList<Task> taskMasterList) {
		ObservableList<HBox> tasksOverview = FXCollections.observableArrayList();

		Integer tasksInInbox = 0;
		Integer tasksDueToday = 0;
		Integer tasksDueThisWeek = 0;
		Integer totalTasks = taskMasterList.size();

		for (Task task : taskMasterList) {
			if(task.getProject().equals("Inbox")) {
				tasksInInbox += 1;
			}

			if(task.isTaskDueToday()) {
				tasksDueToday += 1;
			}

			if(task.isTaskDueWithinSevenDays()) {
				tasksDueThisWeek += 1;
			}
		}

		inboxTasks.setText(Integer.toString(tasksInInbox));
		todayTasks.setText(tasksDueToday.toString());
		thisWeekTasks.setText(tasksDueThisWeek.toString());
		totalNumberOfTasks.setText(totalTasks.toString());
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
			String taskStartTime = task.getStartDateTime() == null ? null : timeFormatter.format(task.getStartDateTime());
			String taskEndTime = task.getEndDateTime() == null ? null : timeFormatter.format(task.getEndDateTime());
			String taskStartDate = task.getStartDateTime() == null ? null : task.getStartDateTime().toLocalDate().toString();
			String taskEndDate = task.getEndDateTime() == null ? null : task.getEndDateTime().toLocalDate().toString();
			String typeOfTask = "";

			Priority taskPriority = task.getPriority();

			boolean isRecurringTask = task.isRecurring();
			boolean isDeadline = task.getTaskType() == Type.DEADLINE;
			boolean isEvent = task.getTaskType() == Type.EVENT;
			boolean isFloating = task.getTaskType() == Type.DEFAULT;
			boolean isOverdue = task.isOverdue();

//			if (isRecurringTask) {
//				taskStartDate = task.getStartDateTime().toLocalDate().toString();
//				System.out.println("Recurring day is " + taskStartDate);
//			} else {
//				taskDateTime = task.getStartDateTime() == null ? "Null" : task.getStartDateTime().toString();
//			}
			if (isFloating) {
				typeOfTask = "floating";
			} else if (isDeadline) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}
			
			TaskBox newTaskBox = new TaskBox(taskId, 
											 typeOfTask, 
											 taskTitle, 
											 taskStartDate,
											 taskEndDate,
											 taskStartTime, 
											 taskEndTime, 
											 taskPriority, 
											 taskProject, 
											 task.isRecurring());

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

	public void updateFiniPoints(List<Task> completedTasks) {
		Integer points = 0;

		for(Task task : completedTasks) {
			switch(task.getPriority()) {
			case HIGH:
				points += 30;
				break;
			case MEDIUM:
			case NORMAL:
				points += 20;
				break;
			case LOW:
				points += 10;
				break;
			default:
				points += 0;
				break;
			}
		}
		finiPoints.setText(points.toString());
	}
}