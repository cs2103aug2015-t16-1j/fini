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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DisplayController {

    private static final String PROJECT_INBOX = "Inbox";
    /* ***********************************
     * DEFINE VARIABLES
     * ***********************************/
    // Types of tasks
    private static final String EVENT_TASK = "event";
    private static final String DEADLINE_TASK = "deadline";
    private static final String FLOATING_TASK = "floating";
    private static final String EMPTY_STRING = "";
    
    // List of help commands displayed to user
    private static final String HELP_FOR_MODS_COMMAND = "mods <FILE_NAME>";
    private static final String HELP_FOR_UNCOMPLETE_COMMAND = "uncomplete <TASK_NUMBER>";
    private static final String HELP_FOR_COMPLETE_COMMAND = "complete <TASK_NUMBER>";
    private static final String HELP_FOR_UPDATE_COMMAND = "update <TASK_NUMBER>";
    private static final String HELP_FOR_DELETE_COMMAND = "delete <TASK_NUMBER>";
    private static final String HELP_FOR_ADD_COMMAND = "add <title> <startTime> <endTime>";


    // List of commands user enters
    private static final String COMMAND_MODS = "mods";
    private static final String COMMAND_UNCOMPLETE = "uncomplete";
    private static final String COMMAND_COMPLETE = "complete";
    private static final String COMMAND_UPDATE = "update";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_SEARCH = "search";

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
    private ImageView helpPanel;

    @FXML
    private Label finiPoints;

    private Brain brain = Brain.getInstance();

    private String userInput;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private Integer scrollIndex = 0;
    private static final Integer MAX_DISPLAY_BOXES = 11;
    private static final Integer SCROLL_INCREMENT = 1;


    /* ***********************************
     * METHODS
     * ***********************************/ 
    public DisplayController() {

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
            if(isHelpPanelVisible()) {
                hideHelpPanel();
            }
            brain.executeCommand(userInput);
        } else if (event.getCode() == KeyCode.SPACE) {
            userInput = commandBox.getText();
            if (userInput.toLowerCase().equals(COMMAND_SEARCH)) {
                brain.executeCommand(userInput);
            } else if (Pattern.matches("update\\s+[0-9]+", userInput.toLowerCase())) {
                autoCompleteTaskDetails();
            } else if (userInput.toLowerCase().startsWith(COMMAND_ADD))  {
                displayToUser.setText(HELP_FOR_ADD_COMMAND);
            } else if (userInput.toLowerCase().startsWith(COMMAND_DELETE)) {
                displayToUser.setText(HELP_FOR_DELETE_COMMAND);
            } else if (userInput.toLowerCase().startsWith(COMMAND_UPDATE)) {
                displayToUser.setText(HELP_FOR_UPDATE_COMMAND);
            } else if (userInput.toLowerCase().startsWith(COMMAND_COMPLETE)) {
                displayToUser.setText(HELP_FOR_COMPLETE_COMMAND);
            } else if (userInput.toLowerCase().startsWith(COMMAND_UNCOMPLETE)) {
                displayToUser.setText(HELP_FOR_UNCOMPLETE_COMMAND);
            } else if (userInput.toLowerCase().startsWith(COMMAND_MODS)) {
                displayToUser.setText(HELP_FOR_MODS_COMMAND);
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

        if(event.getCode() == KeyCode.PAGE_DOWN) {
            pageDown();
        }

        if(event.getCode() == KeyCode.PAGE_UP) {
            pageUp();
        }   
    }

    private void autoCompleteTaskDetails() {
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
    }

    private void pageUp() {
        if(scrollIndex >= SCROLL_INCREMENT) {
            scrollIndex -= SCROLL_INCREMENT;
        } 
        listView.scrollTo(scrollIndex);
        System.out.println(scrollIndex);
    }

    private void pageDown() {
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

    private boolean isHelpPanelVisible() {
        return helpPanel.getOpacity() != 0.0;
    }

    // Update Display
    public void updateDisplayToUser(String display) {
        displayToUser.setText(display);
    }

    // For Integration testing purposes
    // Integration tests: testIntegrationDeleteTask
    //////////////////////////////////
    public Label getDisplayToUser() {
        return displayToUser;
    }

    public Brain getBrain() {
        return brain;
    }

    public void updateTasksOverviewPanel(ObservableList<Task> taskObservableList) {
        Integer tasksInInbox = 0;
        Integer tasksDueToday = 0;
        Integer tasksDueThisWeek = 0;
        Integer totalTasks = taskObservableList.size();

        for (Task task : taskObservableList) {
            if(task.getProjectName().equals(PROJECT_INBOX)) {
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

    public void updateProjectsOverviewPanel(ObservableList<String> projectNameList) {
        projectsOverviewPanel.setItems(projectNameList);
    }

    public void updateProjectDisplay(ObservableList<Task> taskObservableList) {
        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
        displayBoxes = getUpdatedDisplayBoxes(taskObservableList, displayBoxes);
        setItemsToListView(displayBoxes);
    }

    private void setItemsToListView(ObservableList<HBox> displayBoxes) {
        listView.setItems(displayBoxes);
    }

    private ObservableList<HBox> getUpdatedDisplayBoxes(ObservableList<Task> taskObservableList,
            ObservableList<HBox> displayBoxes) {
        for (Task task : taskObservableList) {

            int taskId = getTaskId(taskObservableList, task);
            String taskStartTime = getTaskStartTime(task);
            String taskEndTime = getTaskEndTime(task);
            String taskStartDate = getTaskStartDateTime(task);
            String taskEndDate = getTaskEndDateTime(task);

            String typeOfTask = EMPTY_STRING;
            if (task.getTaskType() == Type.DEFAULT) {
                typeOfTask = FLOATING_TASK;
            } else if (task.getTaskType() == Type.DEADLINE) {
                typeOfTask = DEADLINE_TASK;
            } else {
                typeOfTask = EVENT_TASK;
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
        return displayBoxes;
    }

    private int getTaskId(ObservableList<Task> taskObservableList, Task task) {
        return taskObservableList.indexOf(task) + 1;
    }

    private String getTaskEndDateTime(Task task) {
        return task.getEndDateTime() == null ? null : task.getEndDateTime().toLocalDate().toString();
    }

    private String getTaskStartDateTime(Task task) {
        return task.getStartDateTime() == null ? null : task.getStartDateTime().toLocalDate().toString();
    }

    private String getTaskEndTime(Task task) {
        return task.getEndDateTime() == null ? null : timeFormatter.format(task.getEndDateTime());
    }

    private String getTaskStartTime(Task task) {
        return task.getStartDateTime() == null ? null : timeFormatter.format(task.getStartDateTime());
    }

    public void updateCompletedDisplay(ObservableList<Task> taskObservableList) {
        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

        getUpdatedDisplayBoxes(taskObservableList, displayBoxes);
        setItemsToListView(displayBoxes);
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
            int taskId = getTaskId(taskObservableList, task);
            String taskStartTime = getTaskStartTime(task);
            String taskEndTime = getTaskEndTime(task);
            String taskStartDate = getTaskStartDateTime(task);
            String taskEndDate = getTaskEndDateTime(task);

            String typeOfTask = EMPTY_STRING;
            if (task.getTaskType() == Type.DEFAULT) {
                typeOfTask = FLOATING_TASK;
            } else if (task.getTaskType() == Type.DEADLINE) {
                typeOfTask = DEADLINE_TASK;
            } else {
                typeOfTask = EVENT_TASK;
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
        setItemsToListView(displayBoxes);
    }

    public void updateSearchDisplay(ObservableList<Task> taskObservableList) {
        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

        getUpdatedDisplayBoxes(taskObservableList, displayBoxes);
        setItemsToListView(displayBoxes);
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
            int taskId = getTaskId(taskObservableList, task);

            String taskTitle = task.getTitle();
            String taskProject = task.getProjectName();
            String taskStartTime = getTaskStartTime(task);
            String taskEndTime = getTaskEndTime(task);
            String taskStartDate = getTaskStartDateTime(task);
            String taskEndDate = getTaskEndDateTime(task);
            String typeOfTask = EMPTY_STRING;

            Priority taskPriority = task.getPriority();

            boolean isDeadline = task.getTaskType() == Type.DEADLINE;
            boolean isEvent = task.getTaskType() == Type.EVENT;
            boolean isFloating = task.getTaskType() == Type.DEFAULT;
            boolean isOverdue = task.isOverdue();

            if (isFloating) {
                typeOfTask = FLOATING_TASK;
            } else if (isDeadline) {
                typeOfTask = DEADLINE_TASK;
            } else {
                assert isEvent == true;
                typeOfTask = EVENT_TASK;
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
        addToDisplayBoxes(displayBoxes, overdueBoxes, floatingBoxes, todayBoxes, tomorrowBoxes,
                otherBoxes, overdueCategory, floatingCategory, todayCategory, tomorrowCategory,
                othersCategory);

        setItemsToListView(displayBoxes);
    }

    private void addToDisplayBoxes(ObservableList<HBox> displayBoxes,
            ObservableList<HBox> overdueBoxes, ObservableList<HBox> floatingBoxes,
            ObservableList<HBox> todayBoxes, ObservableList<HBox> tomorrowBoxes,
            ObservableList<HBox> otherBoxes, TaskCategory overdueCategory,
            TaskCategory floatingCategory, TaskCategory todayCategory,
            TaskCategory tomorrowCategory, TaskCategory othersCategory) {
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
            case FLOATING_TASK:
                hbox.getChildren().addAll(id, title);
                break;
            case DEADLINE_TASK:
                hbox.getChildren().addAll(id, title, date, startTime);
                break;
            case EVENT_TASK:
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
                    points += 10;
                    break;
            }
        }
        updateFiniPointsWithFadeAnimation(points);
    }

    private void updateFiniPointsWithFadeAnimation(Integer points) {
        fadeOut(finiPoints);

        finiPoints.setText(points.toString());

        fadeIn(finiPoints);
    }

    private void fadeOut(Node element) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), element);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
    }

    private void fadeIn(Node element) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), element);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void displayHelpPanel() {
        fadeIn(helpPanel);
    }

    public void hideHelpPanel() {
        fadeOut(helpPanel);
    }
}
