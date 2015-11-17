package fini.main.view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the root controller class for the display (view). The interactions on the Graphical User
 * Interface (GUI), which are the commands entered by users in the command box are handled here.
 * 
 * @@author A0121828H
 */

import fini.main.Brain;
import fini.main.MainApp;
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

    /* ***********************************
     * DEFINE CONSTANTS
     * ***********************************/
    // Formats for Date and Time
    private static final String PATTERN_TIME_FORMAT = "HH:mm";
    private static final String PATTERN_DATE_FORMAT = "d MMMM";

    private static final double OPACITY_FULL = 1.0;
    private static final double OPACITY_ZERO = 0.0;

    private static final int DURATION_OF_FADE_IN_ANIMATION = 500;
    private static final int DURATION_OF_FADE_OUT_ANIMATION = 500;

    // Points awarded for the various priorities
    private static final int TASK_COMPLETION_POINTS_DEFAULT_PRIORITY = 10;
    private static final int TASK_COMPLETION_POINTS_LOW_PRIORITY = 10;
    private static final int TASK_COMPLETION_POINTS_MEDIUM_PRIORITY = 20;
    private static final int TASK_COMPLETION_POINTS_HIGH_PRIORITY = 30;

    // Various Categories of tasks
    private static final String CATEGORY_COMPLETE = "Complete";
    private static final String CATEGORY_OTHER_TASKS = "Other tasks";
    private static final String CATEGORY_TOMORROW = "Tomorrow";
    private static final String CATEGORY_TODAY = "Today";
    private static final String CATEGORY_FLOATING = "Floating";
    private static final String CATEGORY_OVERDUE = "Overdue";

    private static final String PROJECT_INBOX = "Inbox";

    // Types of tasks
    private static final String EVENT_TASK = "event";
    private static final String DEADLINE_TASK = "deadline";
    private static final String FLOATING_TASK = "floating";
    private static final String EMPTY_STRING = "";

    // List of help commands displayed to user
    private static final String HELP_FOR_MODS_COMMAND = "mods <FILE_NAME.html>";
    private static final String HELP_FOR_UNCOMPLETE_COMMAND = "uncomplete <TASK_NUMBER>";
    private static final String HELP_FOR_COMPLETE_COMMAND = "complete <TASK_NUMBER>";
    private static final String HELP_FOR_UPDATE_COMMAND = "update <TASK_NUMBER>";
    private static final String HELP_FOR_DELETE_COMMAND = "delete <TASK_NUMBER>";
    private static final String HELP_FOR_ADD_COMMAND = "add <title> <startTime> <endTime>";
    private static final String HELP_FOR_DISPLAY_COMMAND = "display all/main/completed";
    private static final String HELP_FOR_STORAGE_COMMAND = "set <FILE_PATH>";

    // List of commands user enters
    private static final String COMMAND_MODS = "mods";
    private static final String COMMAND_UNCOMPLETE = "uncomplete";
    private static final String COMMAND_COMPLETE = "complete";
    private static final String COMMAND_UPDATE = "update";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_SEARCH = "search";
    private static final String COMMAND_DISPLAY = "display";
    private static final String COMMAND_STORAGE = "set";

    // Maximum number of boxes that can be displayed within the height of the stage
    private static final Integer MAX_DISPLAY_BOXES = 11;

    // Shift amount for each PageUp
    private static final Integer SCROLL_INCREMENT = 1;

    private static final int INTIAL_SCROLL_INDEX = 0;

    // Patterns in user input
    private static final String PATTERN_UPDATE_WITH_SPACE_AND_TASK_NUM = "update\\s+[0-9]+";
    private static final String PATTERN_ALL_SPACES = "\\s+";

    private static final String USER_INPUT_SEARCH = "search ";

    private static final int NONE = 0;

    /* ***********************************
     * DEFINE VARIABLES
     * ***********************************/
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(PATTERN_DATE_FORMAT);
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(PATTERN_TIME_FORMAT);

    private Integer scrollIndex = INTIAL_SCROLL_INDEX;

    /* ***********************************
     * FXML FIELDS
     * ***********************************/

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

    /* ***********************************
     * METHODS
     * ***********************************/ 

    public DisplayController() {
        MainApp.finiLogger.info("DisplayController has been initialised.");
    }

    /*
     * This method handles the various inputs of the user that is entered in the
     * command box of Fini's GUI and acts as a keyboard listener. It then passes 
     * the command to the Brain for it to be executed.
     * 
     * @param event This is a key event that is passed to the method whenever the
     *              user presses any keyboard button.
     */
    @FXML
    public void handleKeyPressEvent(KeyEvent event) throws Exception {
        String userInput = commandBox.getText();
        if (isUserInputEnter(event)) {
            commandBox.clear();
            if(isHelpPanelVisible()) {
                hideHelpPanel();
            }
            executeCommand(userInput);
        } else if (isUserInputSpace(event)) {
            if (isUserInputSearch(userInput)) {
                executeCommand(userInput);
            } else if (Pattern.matches(PATTERN_UPDATE_WITH_SPACE_AND_TASK_NUM, userInput.toLowerCase())) {
                autoCompleteTaskDetails(userInput);
            } else {
                displayRelevantHelpToUser(userInput);
            }      
        } else if (isUserInputDigitKey(event) || isUserInputLetterKey(event)){
            if (doesUserInputStartWithSearch(userInput)) {
                executeCommand(userInput + event.getCode().toString().toLowerCase());
            }
        } else if (isUserInputBackSpace(event)) {
            if (doesUserInputStartWithSearch(userInput)) {
                executeCommand(userInput.substring(0, userInput.length() - 1));
            }
        }

        if(isUserInputPageDown(event)) {
            MainApp.finiLogger.info("Page Down");
            pageDown();
        }

        if(isUserInputPageUp(event)) {
            MainApp.finiLogger.info("Page Up");
            pageUp();
        }   
    }

    /*
     * This method passes the user's input to the Brain for
     * it to be executed.
     * 
     *  @params inputToExecute is the input enter by the user
     *          into the commandBox
     */
    private void executeCommand(String inputToExecute) {
        MainApp.finiLogger.info("To be executed by Brain: " + inputToExecute);
        brain.executeCommand(inputToExecute);
    }

    private boolean isUserInputSearch(String userInput) {
        return userInput.toLowerCase().equals(COMMAND_SEARCH);
    }

    private boolean isUserInputSpace(KeyEvent event) {
        return event.getCode() == KeyCode.SPACE;
    }

    private boolean isUserInputEnter(KeyEvent event) {
        return event.getCode() == KeyCode.ENTER;
    }

    // @@author A0121298E
    private void displayRelevantHelpToUser(String userInput) {
        if (userInput.toLowerCase().startsWith(COMMAND_ADD))  {
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
        } else if (userInput.toLowerCase().startsWith(COMMAND_DISPLAY)) {
            displayToUser.setText(HELP_FOR_DISPLAY_COMMAND);
        } else if (userInput.toLowerCase().startsWith(COMMAND_STORAGE)) {
            displayToUser.setText(HELP_FOR_STORAGE_COMMAND);
        } 
    }

    private boolean isUserInputLetterKey(KeyEvent event) {
        return event.getCode().isLetterKey();
    }

    private boolean isUserInputDigitKey(KeyEvent event) {
        return event.getCode().isDigitKey();
    }

    private boolean isUserInputBackSpace(KeyEvent event) {
        return event.getCode() == KeyCode.BACK_SPACE;
    }

    private boolean isUserInputPageDown(KeyEvent event) {
        return event.getCode() == KeyCode.PAGE_DOWN;
    }

    private boolean isUserInputPageUp(KeyEvent event) {
        return event.getCode() == KeyCode.PAGE_UP;
    }

    private boolean doesUserInputStartWithSearch(String userInput) {
        return userInput.toLowerCase().startsWith(USER_INPUT_SEARCH);
    }

    // @@author A0121828H
    private void autoCompleteTaskDetails(String userInput) {
        int updateIndex = Integer.parseInt(userInput.split(PATTERN_ALL_SPACES)[1]);
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
                if (task.getPriority() != Priority.NORMAL) {
                    commandBox.appendText(" priority " + task.getPriority());
                }
                commandBox.appendText(" project " + task.getProjectName());
                if (task.isRecurring()) {
                    commandBox.appendText(" ");
                    commandBox.appendText("repeat every ");
                    if (task.getInterval().getYears() != 0) {
                        commandBox.appendText(task.getInterval().getYears() + " years");
                    } else if (task.getInterval().getMonths() != 0) {
                        commandBox.appendText(task.getInterval().getMonths() + " months");
                    } else if (task.getInterval().getDays() != 0) {
                        commandBox.appendText(task.getInterval().getDays() + " days");
                    }

                    if (task.getRecursUntil() != null) {
                        commandBox.appendText(" until " + task.getRecursUntil().toLocalDate().format(dateFormatter));
                    }
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
        logCurrentScrollIndex();
    }

    private void logCurrentScrollIndex() {
        MainApp.finiLogger.info("Current scroll index: " + scrollIndex);
    }

    private void pageDown() {
        int currentNumOfBoxes = listView.getItems().size();
        int excessBoxes = listView.getItems().size() - MAX_DISPLAY_BOXES;
        if ((currentNumOfBoxes > MAX_DISPLAY_BOXES) && (scrollIndex < excessBoxes)) {
            scrollIndex += SCROLL_INCREMENT;
        } else if (currentNumOfBoxes < MAX_DISPLAY_BOXES) {
            scrollIndex = NONE;
        }
        listView.scrollTo(scrollIndex);
        logCurrentScrollIndex();
    }

    private boolean isHelpPanelVisible() {
        return helpPanel.getOpacity() != OPACITY_ZERO;
    }

    public void updateDisplayToUser(String display) {
        displayToUser.setText(display);
    }

    public Label getDisplayToUser() {
        return displayToUser;
    }

    public Brain getBrain() {
        return brain;
    }

    public void updateTasksOverviewPanel(ObservableList<Task> taskObservableList) {
        Integer tasksInInbox = NONE;
        Integer tasksDueToday = NONE;
        Integer tasksDueThisWeek = NONE;
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

        displayBoxes.add(new TaskCategory(CATEGORY_COMPLETE));
        for (Task task : taskObservableList) {
            if (!task.isCompleted() && task.isOverdue() && !overdueAdded) {
                displayBoxes.add(new TaskCategory(CATEGORY_OVERDUE));
                overdueAdded = true;
            } else if (!task.isCompleted() && !task.isOverdue() && task.getTaskType() == Type.DEFAULT && !floatingAdded) {
                displayBoxes.add(new TaskCategory(CATEGORY_FLOATING));
                floatingAdded = true;
            } else if (!task.isCompleted() && !task.isOverdue() && task.getTaskType() != Type.DEFAULT && task.isTaskDueToday() && !todayAdded) {
                displayBoxes.add(new TaskCategory(CATEGORY_TODAY));
                todayAdded = true;
            } else if (!task.isCompleted() && !task.isOverdue() && task.getTaskType() != Type.DEFAULT && !task.isTaskDueToday() && task.isTaskDueTomorrow() && !tomorrowAdded) {
                displayBoxes.add(new TaskCategory(CATEGORY_TOMORROW));
                tomorrowAdded = true;
            } else if (!task.isCompleted() && !task.isOverdue() && task.getTaskType() != Type.DEFAULT && !task.isTaskDueToday() && !task.isTaskDueTomorrow() && !othersAdded) {
                displayBoxes.add(new TaskCategory(CATEGORY_OTHER_TASKS));
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
                assert isOverdue == false;

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
            overdueCategory = new TaskCategory(CATEGORY_OVERDUE);
            floatingCategory = new TaskCategory(CATEGORY_FLOATING);
            todayCategory = new TaskCategory(CATEGORY_TODAY);
            tomorrowCategory = new TaskCategory(CATEGORY_TOMORROW);
            othersCategory = new TaskCategory(CATEGORY_OTHER_TASKS);
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

    /*
     * This method takes in a list of completed tasks and collates the total
     * number of points the user has achieved and updates the display. Points 
     * are awarded based on the priority of the task. High gives 30 points, 
     * Medium gives 20 points and Low or Default priority gives 10 points.
     * 
     * @param completedTasks    A list of Tasks that are completed
     */
    // @@author A0121298E
    public void updateFiniPoints(List<Task> completedTasks) {
        Integer points = 0;

        for(Task task : completedTasks) {
            switch(task.getPriority()) {
                case HIGH:
                    points += TASK_COMPLETION_POINTS_HIGH_PRIORITY;
                    break;
                case MEDIUM:
                case NORMAL:
                    points += TASK_COMPLETION_POINTS_MEDIUM_PRIORITY;
                    break;
                case LOW:
                    points += TASK_COMPLETION_POINTS_LOW_PRIORITY;
                    break;
                default:
                    points += TASK_COMPLETION_POINTS_DEFAULT_PRIORITY;
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
        FadeTransition fadeOut = new FadeTransition(Duration.millis(DURATION_OF_FADE_OUT_ANIMATION), element);
        fadeOut.setFromValue(OPACITY_FULL);
        fadeOut.setToValue(OPACITY_ZERO);
        fadeOut.play();
    }

    private void fadeIn(Node element) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(DURATION_OF_FADE_IN_ANIMATION), element);
        fadeIn.setFromValue(OPACITY_ZERO);
        fadeIn.setToValue(OPACITY_FULL);
        fadeIn.play();
    }

    public void displayHelpPanel() {
        fadeIn(helpPanel);
    }

    public void hideHelpPanel() {
        fadeOut(helpPanel);
    }

    public void setFocusToCommandBox() {
        commandBox.requestFocus();
    }
}
