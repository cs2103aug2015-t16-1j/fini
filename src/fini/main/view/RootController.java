package fini.main.view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

/*
 * This is the root controller class for the display (view). The interactions on the Graphical User
 * Interface (GUI), which are the commands entered by users in the command box are handled here.
 * 
 * @@author A0121828H
 */

import fini.main.Brain;
import fini.main.model.Task;
import fini.main.model.Task.Priority;
import fini.main.model.Task.Type;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

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
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	private Integer scrollIndex = 0;
	private static final Integer MAX_DISPLAY_BOXES = 11;
	private static final Integer SCROLL_INCREMENT = 1;

	public RootController() {
		// TODO With the Brain fully functioning, here we do not initialize anything
	}

	public void setFocusToCommandBox() {
		commandBox.requestFocus();
	}

	@FXML
	public void handleKeyPressEvent(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			userInput = commandBox.getText();
			System.out.println("RootController: " + userInput);
			commandBox.clear();

			brain.executeCommand(userInput);
		} else if (event.getCode() == KeyCode.SPACE) {
			userInput = commandBox.getText();
			if (userInput.toLowerCase().equals("search")) {
				brain.executeCommand(userInput);
			} else if (Pattern.matches("update\\s+[0-9]+", userInput.toLowerCase())) {
				int updateIndex = Integer.parseInt(userInput.split("\\s+")[1]);
				if (updateIndex > 0) {
					ObservableList<Task> taskObservableList = brain.getTaskObservableList();
					if (updateIndex < taskObservableList.size() + 1) {
						Task task = taskObservableList.get(updateIndex - 1);
						Task.Type taskType = task.getTaskType();

						commandBox.appendText(" " + task.getTitle());
						switch (taskType) {
						case DEFAULT:
							break;
						case DEADLINE:
							commandBox.appendText(" ");
							commandBox.appendText(task.getStartDateTime().toLocalDate().format(dateFormatter) + " " + 
									task.getStartDateTime().toLocalTime().format(timeFormatter));
							break;
						case EVENT:
							commandBox.appendText(" ");
							commandBox.appendText(task.getStartDateTime().toLocalDate().format(dateFormatter) + " " +
									task.getStartDateTime().toLocalTime().format(timeFormatter) + " to " +
									task.getEndDateTime().toLocalDate().format(dateFormatter) + " " + 
									task.getEndDateTime().toLocalTime().format(timeFormatter));
							break;
						}
						commandBox.end();
					}
				}
			} else if (userInput.toLowerCase().startsWith("add"))  {
				displayToUser.setText("add <title> <startTime> <endTime>");
			} else if (userInput.toLowerCase().startsWith("delete")) {
				displayToUser.setText("delete <TASK_NUMBER>");
			} else if (userInput.toLowerCase().startsWith("update")) {
				displayToUser.setText("update <TASK_NUMBER>");
			} else if (userInput.toLowerCase().startsWith("complete")) {
				displayToUser.setText("complete <TASK_NUMBER>");
			} else if (userInput.toLowerCase().startsWith("uncomplete")) {
				displayToUser.setText("uncomplete <TASK_NUMBER>");
			} else if (userInput.toLowerCase().startsWith("mods")) {
				displayToUser.setText("mods <FILE_NAME>");
			} else if (userInput.toLowerCase().startsWith("mode")) {
				displayToUser.setText("mode normal/night");
			}
		} else if (event.getCode().isDigitKey() || event.getCode().isLetterKey()){
			userInput = commandBox.getText();
			if (userInput.toLowerCase().startsWith("search ")) {
				brain.executeCommand(userInput + event.getCode().toString().toLowerCase());
			}
		} else if (event.getCode() == KeyCode.BACK_SPACE) {
			userInput = commandBox.getText();
			if (userInput.toLowerCase().startsWith("search ")) {
				brain.executeCommand(userInput.substring(0, userInput.length() - 1));
			}
		}
		// TODO
		// 1. SEARCH/EDIT + SPACE -> auto-trigger for instant search and auto-complete for edit
		// 2. COMMAND + TAB -> auto-complete command (Veto's idea)
		// 3. UP/DOWN -> previous/next command
		// 4. PAGEUP/PAGEDOWN -> scroll up/down

		if(event.getCode() == KeyCode.PAGE_DOWN) {
			int currentNumOfBoxes = listView.getItems().size();
			int excessBoxes = listView.getItems().size() - MAX_DISPLAY_BOXES;
			if ((currentNumOfBoxes > MAX_DISPLAY_BOXES) && (scrollIndex < excessBoxes)) {
				scrollIndex += SCROLL_INCREMENT;
			} else if (currentNumOfBoxes < MAX_DISPLAY_BOXES) {
				scrollIndex = 0;
			}
			listView.scrollTo(scrollIndex);
			System.out.println(scrollIndex);
		}

		if(event.getCode() == KeyCode.PAGE_UP) {
			if(scrollIndex >= SCROLL_INCREMENT) {
				scrollIndex -= SCROLL_INCREMENT;
			} 
			listView.scrollTo(scrollIndex);
			System.out.println(scrollIndex);
		}	
	}

	// Update Display
	public void updateDisplayToUser(String display) {
		displayToUser.setText(display);
	}

	public void updateTasksOverviewPanel(ObservableList<Task> taskObservableList) {
		ObservableList<HBox> tasksOverview = FXCollections.observableArrayList();

		Integer tasksInInbox = 0;
		Integer tasksDueToday = 0;
		Integer tasksDueThisWeek = 0;
		Integer totalTasks = taskObservableList.size();

		for (Task task : taskObservableList) {
			if(task.getProjectName().equals("Inbox")) {
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

	public void updateProjectsOverviewPanel(ObservableList<Task> taskObservableList) {
		ObservableList<String> projectsOverview = FXCollections.observableArrayList();
		for (Task task : taskObservableList) {
			if (projectsOverview.contains(task.getProjectName()) == false) {
				projectsOverview.add(task.getProjectName());
			}
		}
		projectsOverviewPanel.setItems(projectsOverview);
	}

	public void updateCompletedDisplay(ObservableList<Task> taskObservableList) {
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

		for (Task task : taskObservableList) {
			int taskId = taskObservableList.indexOf(task) + 1;

			String taskStartTime = task.getStartDateTime() == null ? null : timeFormatter.format(task.getStartDateTime());
			String taskEndTime = task.getEndDateTime() == null ? null : timeFormatter.format(task.getEndDateTime());
			String taskStartDate = task.getStartDateTime() == null ? null : task.getStartDateTime().toLocalDate().toString();
			String taskEndDate = task.getEndDateTime() == null ? null : task.getEndDateTime().toLocalDate().toString();

			String typeOfTask = "";
			if (task.getTaskType() == Type.DEFAULT) {
				typeOfTask = "floating";
			} else if (task.getTaskType() == Type.DEADLINE) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}

			TaskBox newTaskBox = new TaskBox(taskId, 
					typeOfTask, 
					task.getTitle(), 
					taskStartDate,
					taskEndDate,
					taskStartTime, 
					taskEndTime, 
					task.getPriority(), 
					task.getProjectName(), 
					task.isRecurring());
			displayBoxes.add(newTaskBox);
		}
		listView.setItems(displayBoxes);
	}

	public void updateAllDisplay(ObservableList<Task> taskObservableList) {
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

		boolean overdueAdded = false;
		boolean floatingAdded = false;
		boolean todayAdded = false;
		boolean tomorrowAdded = false;
		boolean othersAdded = false;

		displayBoxes.add(new TaskCategory("Complete"));
		for (Task task : taskObservableList) {
			if (!task.isCompleted() && task.isOverdue() && !overdueAdded) {
				displayBoxes.add(new TaskCategory("Overdue"));
				overdueAdded = true;
			} else if (!task.isOverdue() && task.getTaskType() == Type.DEFAULT && !floatingAdded) {
				displayBoxes.add(new TaskCategory("Floating"));
				floatingAdded = true;
			} else if (task.getTaskType() != Type.DEFAULT && task.isTaskDueToday() && !todayAdded) {
				displayBoxes.add(new TaskCategory("Today"));
				todayAdded = true;
			} else if (!task.isTaskDueToday() && task.isTaskDueTomorrow() && !tomorrowAdded) {
				displayBoxes.add(new TaskCategory("Tomorrow"));
				tomorrowAdded = true;
			} else if (!task.isTaskDueTomorrow() && !othersAdded) {
				displayBoxes.add(new TaskCategory("Other tasks"));
				othersAdded = true;
			}
			int taskId = taskObservableList.indexOf(task) + 1;
			String taskStartTime = task.getStartDateTime() == null ? null : timeFormatter.format(task.getStartDateTime());
			String taskEndTime = task.getEndDateTime() == null ? null : timeFormatter.format(task.getEndDateTime());
			String taskStartDate = task.getStartDateTime() == null ? null : task.getStartDateTime().toLocalDate().toString();
			String taskEndDate = task.getEndDateTime() == null ? null : task.getEndDateTime().toLocalDate().toString();

			String typeOfTask = "";
			if (task.getTaskType() == Type.DEFAULT) {
				typeOfTask = "floating";
			} else if (task.getTaskType() == Type.DEADLINE) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}

			TaskBox newTaskBox = new TaskBox(taskId, 
					typeOfTask, 
					task.getTitle(), 
					taskStartDate,
					taskEndDate,
					taskStartTime, 
					taskEndTime, 
					task.getPriority(), 
					task.getProjectName(), 
					task.isRecurring());
			displayBoxes.add(newTaskBox);
		}
		listView.setItems(displayBoxes);
	}

	public void updateSearchDisplay(ObservableList<Task> taskObservableList) {
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

		for (Task task : taskObservableList) {
			int taskId = taskObservableList.indexOf(task) + 1;

			String taskStartTime = task.getStartDateTime() == null ? null : timeFormatter.format(task.getStartDateTime());
			String taskEndTime = task.getEndDateTime() == null ? null : timeFormatter.format(task.getEndDateTime());
			String taskStartDate = task.getStartDateTime() == null ? null : task.getStartDateTime().toLocalDate().toString();
			String taskEndDate = task.getEndDateTime() == null ? null : task.getEndDateTime().toLocalDate().toString();

			String typeOfTask = "";
			if (task.getTaskType() == Type.DEFAULT) {
				typeOfTask = "floating";
			} else if (task.getTaskType() == Type.DEADLINE) {
				typeOfTask = "deadline";
			} else {
				typeOfTask = "event";
			}

			TaskBox newTaskBox = new TaskBox(taskId, 
					typeOfTask, 
					task.getTitle(), 
					taskStartDate,
					taskEndDate,
					taskStartTime, 
					taskEndTime, 
					task.getPriority(), 
					task.getProjectName(), 
					task.isRecurring());
			displayBoxes.add(newTaskBox);
		}
		listView.setItems(displayBoxes);
	}

	public void updateMainDisplay(ObservableList<Task> taskObservableList) {
		ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

		// All category boxes
		ObservableList<HBox> overdueBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> floatingBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> todayBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> tomorrowBoxes = FXCollections.observableArrayList();
		ObservableList<HBox> otherBoxes = FXCollections.observableArrayList();

		for (Task task : taskObservableList) {
			int taskId = taskObservableList.indexOf(task) + 1;

			String taskTitle = task.getTitle();
			String taskProject = task.getProjectName();
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
		updateFiniPointsWithFadeAnimation(points);
	}

	private void updateFiniPointsWithFadeAnimation(Integer points) {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), finiPoints);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();

		finiPoints.setText(points.toString());

		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), finiPoints);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();
	}
}