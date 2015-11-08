package fini.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import fini.main.model.Command;
import fini.main.model.Command.CommandType;
import fini.main.model.FiniParser;
import fini.main.model.StatusSaver;
import fini.main.model.Storage;
import fini.main.model.Task;
import fini.main.util.ModsLoader;
import fini.main.util.Sorter;
import fini.main.view.DisplayController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This Brain class is the main logic component of FINI.
 * It handles all logic process regarding the manipulation of tasks and different display modes.
 * Brain class is the only class that has the access to parser and storage classes.
 * 
 * @@author A0127483B
 */
public class Brain {
    /* ***********************************
     * Constants
     * ***********************************/
    private static final String DEFAULT_PROJECT = "Inbox";
    private static final String EMPTY_STRING = "";
    private static final String SEARCHING_STRING = "Searching...";
    private static final String INVALID_FEEDBACK = "Invalid command. Please type help for assistance.";
    private static final String ADD_EMPTY_PARAMETER = "Add CommandParameters is empty";
    private static final String UPDATE_EMPTY_PARAMETER = "Update CommandParameters is empty";
    private static final String START_EXCEEDS_END = "Start date and time should be earlier than end date time";
    private static final String EXCEEDS_PROJECT_LIMIT = "Maximum of 5 projects at the same time!";
    private static final String TASK_NOT_FOUND = "Task not found";
    private static final String ADD_MESSAGE = "Added: %1$s";
    private static final String UPDATE_MESSAGE = "Update: %1$s %2$s";
    private static final String DELETE_MESSAGE = "Delete: %1$s %2$s";
    private static final String CLEAR_MESSAGE = "All tasks have been cleared";
    private static final String UNDO_LIMIT = "Unable to undo. You've not done any changes yet.";
    private static final String UNDO_MESSAGE = "Your action has been undone.";
    private static final String REDO_LIMIT = "Unable to redo. There is nothing for me to redo.";
    private static final String REDO_MESSAGE = "Your action has been redone.";
    private static final String DISPLAY_MESSAGE = "display project: %1$s";
    private static final String UNKNOWN_DISPLAY = "Unknown displayTask method";
    private static final String DISPLAY_COMPLETED_MESSAGE = "display completed";
    private static final String DISPLAY_MAIN_MESSAGE = "display main";
    private static final String DISPLAY_ALL_MESSAGE = "display all";
    private static final String COMPLETE_MESSAGE = "Complete: %1$s %2$s";
    private static final String UNCOMPLETE_MESSAGE = "Uncomplete: %1$s %2$s";
    private static final String NO_MODS_FILE = "No nusmods file";
    private static final String MODS_LOADED = "NUSMODS loaded";
    private static final String HELP_MESSAGE = "Check help panel for more info";

    private static final int START_INDEX = 0;
    private static final int END_INDEX = 1;

    private static final int MAXIMUM_NUM_OF_PROJECTS = 5;

    private static final String USER_INPUT_DISPLAY_ALL = "all";
    private static final String USER_INPUT_DISPLAY_MAIN = "main";
    private static final String USER_INPUT_DISPLAY_COMPLETED = "completed";

    /* ***********************************
     * Fields
     * ***********************************/
    // Singleton
    private static Brain brain;
    private DisplayController displayController;

    private Storage taskOrganiser;
    private FiniParser finiParser;
    private Sorter sorter;
    private StatusSaver statusSaver;

    private ArrayList<Task> taskMasterList;
    private ObservableList<String> projectNameList = FXCollections.observableArrayList(); 
    private ObservableList<Task> taskObservableList = FXCollections.observableArrayList();
    private ObservableList<Task> taskAuxiliaryList = FXCollections.observableArrayList(); 

    private boolean searchDisplayTrigger = false;
    private boolean projectDisplayTrigger = false;
    private boolean completeDisplayTrigger = false;
    private boolean allDisplayTrigger = false;

    /* ***********************************
     * Private Constructor
     * ***********************************/
    private Brain() {
        finiParser = FiniParser.getInstance();
        taskOrganiser = Storage.getInstance();
        statusSaver = StatusSaver.getInstance();

        taskMasterList = taskOrganiser.readFile();
        sortTaskMasterList();
        taskObservableList.setAll(getIncompleteTasks());
        taskAuxiliaryList.setAll(taskObservableList); 

        for (Task task : taskAuxiliaryList) {
            if (!task.getProjectName().equals(DEFAULT_PROJECT) && !projectNameList.contains(task.getProjectName())) {
                projectNameList.add(task.getProjectName());
            }
        }
        statusSaver.saveStatus(taskMasterList, taskObservableList);
    }

    /*
     * This method is the getInstance method for the singleton pattern of
     * Brain. It initialises a new Brain if Brain is null, else returns the
     * current instance of the Brain.
     */
    public static Brain getInstance() {
        if (brain == null) {
            brain = new Brain();
        }
        return brain;
    }
    
    /*
     * This method initialises the first display when FINI is launched
     * This method is executed from the MainApp
     */ 
    public void initDisplay() {
        displayController.setFocusToCommandBox();
        displayController.updateMainDisplay(taskAuxiliaryList);
        displayController.updateProjectsOverviewPanel(projectNameList);
        displayController.updateTasksOverviewPanel(taskAuxiliaryList);
        displayController.updateFiniPoints(getCompletedTasks());
    }

    /* ***********************************
     * Public execution method
     * ***********************************/
    public void executeCommand(String userInput) {
        Command newCommand = new Command(userInput);
        CommandType commandType = newCommand.getCommandType();
        int objectIndex = newCommand.getObjectIndex();
        String commandParameters = newCommand.getCommandParameters();

        MainApp.finiLogger.info("User's input: " + userInput);
        MainApp.finiLogger.info("Command type: " + commandType.toString());
        MainApp.finiLogger.info("Object index: " + objectIndex);
        MainApp.finiLogger.info("Parameters: " + commandParameters);

        String display = EMPTY_STRING;
        switch (commandType) {
            case ADD:
                display = addTask(commandParameters);
                saveThisStatus();
                break;
            case UPDATE:
                display = updateTask(objectIndex, commandParameters);
                saveThisStatus();
                break;
            case DELETE:
                display = deleteTask(objectIndex);
                saveThisStatus();
                break;
            case CLEAR:
                display = clearAllTasks();
                saveThisStatus();
                break;
            case UNDO:
                display = undo();
                break;
            case REDO:
                display = redo();
                break;
            case SET:
                display = setUserPrefDirectory(commandParameters);
                break;
            case DISPLAY:
                display = displayTask(commandParameters);
                break;
            case SEARCH:
                display = SEARCHING_STRING;
                searchTask(commandParameters);
                break;
            case EXIT:
                System.exit(0);
            case COMPLETE:
                display = completeTask(objectIndex);
                saveThisStatus();
                break;
            case UNCOMPLETE:
                display = uncompleteTask(objectIndex);
                saveThisStatus();
                break;
            case MODS:
                display = loadNUSMods(commandParameters);
                saveThisStatus();
                break;
            case HELP:
                display = displayHelpPanel();
                break;
            case INVALID:
                display = INVALID_FEEDBACK;
                break;
            default:
                break;
        }

        displayControl();

        sortTaskMasterList();
        taskAuxiliaryList.setAll(getIncompleteTasks());
        projectNameList = FXCollections.observableArrayList();

        for (Task task : taskAuxiliaryList) {
            if (!task.getProjectName().equals(DEFAULT_PROJECT) && !projectNameList.contains(task.getProjectName())) {
                projectNameList.add(task.getProjectName());
            }
        }

        displayController.updateProjectsOverviewPanel(projectNameList);
        displayController.updateTasksOverviewPanel(taskAuxiliaryList);

        displayController.updateDisplayToUser(display);
        displayController.updateFiniPoints(getCompletedTasks());
    }

    private List<Task> getCompletedTasks() {
        return taskMasterList.stream().filter(task -> task.isCompleted()).collect(Collectors.toList());
    }

    /* ***********************************
     * Logic methods
     * ***********************************/
    private String addTask(String commandParameters) {
        if (commandParameters.isEmpty()) {
            return ADD_EMPTY_PARAMETER;
        }

        finiParser.parse(commandParameters);

        if (finiParser.getDatetimes() != null && finiParser.getDatetimes().size() == 2 &&
                !finiParser.getDatetimes().get(START_INDEX).isBefore(finiParser.getDatetimes().get(END_INDEX))) {
            return START_EXCEEDS_END;
        }

        if (!finiParser.getProjectName().equals(DEFAULT_PROJECT) &&
                !projectNameList.contains(finiParser.getProjectName()) &&
                projectNameList.size() == MAXIMUM_NUM_OF_PROJECTS) {
            return EXCEEDS_PROJECT_LIMIT;
        }

        Task newTask = new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
                .setDatetimes(finiParser.getDatetimes())
                .setPriority(finiParser.getPriority())
                .setProjectName(finiParser.getProjectName())
                .setInterval(finiParser.getInterval())
                .setRecursUntil(finiParser.getRecursUntil()).build();

        taskMasterList.add(newTask);
        taskOrganiser.updateFile(taskMasterList);
        return String.format(ADD_MESSAGE, finiParser.getNotParsed());
    }

    private String updateTask(int objectIndex, String commandParameters) {
        Task taskToUpdate;

        if (commandParameters.isEmpty()) {
            return UPDATE_EMPTY_PARAMETER;
        }

        try {
            taskToUpdate = taskObservableList.get(objectIndex - 1);
        } catch (IndexOutOfBoundsException e) {
            return TASK_NOT_FOUND;
        }

        // delete first
        taskObservableList.remove(taskToUpdate);
        taskMasterList.remove(taskToUpdate);
        taskOrganiser.updateFile(taskMasterList);

        // add then
        finiParser.parse(commandParameters);

        if (finiParser.getDatetimes() != null && finiParser.getDatetimes().size() == 2 &&
                !finiParser.getDatetimes().get(START_INDEX).isBefore(finiParser.getDatetimes().get(END_INDEX))) {
            return START_EXCEEDS_END;
        }

        if (!finiParser.getProjectName().equals(DEFAULT_PROJECT) &&
                !projectNameList.contains(finiParser.getProjectName()) &&
                projectNameList.size() == MAXIMUM_NUM_OF_PROJECTS) {
            return EXCEEDS_PROJECT_LIMIT;
        }

        Task newTask = new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
                .setDatetimes(finiParser.getDatetimes())
                .setPriority(finiParser.getPriority())
                .setProjectName(finiParser.getProjectName())
                .setInterval(finiParser.getInterval())
                .setRecursUntil(finiParser.getRecursUntil()).build();

        taskMasterList.add(newTask);
        taskOrganiser.updateFile(taskMasterList);

        return String.format(UPDATE_MESSAGE, objectIndex, taskToUpdate.getTitle());
    }

    private String deleteTask(int objectIndex) {
        Task taskToDelete;
        try {
            taskToDelete = taskObservableList.get(objectIndex - 1);
        } catch (IndexOutOfBoundsException e) {
            return TASK_NOT_FOUND;
        }

        taskObservableList.remove(taskToDelete);
        taskMasterList.remove(taskToDelete);
        taskOrganiser.updateFile(taskMasterList);
        return String.format(DELETE_MESSAGE, objectIndex, taskToDelete.getTitle());
    }

    // @@author A0121828H
    private String clearAllTasks() {
        taskMasterList.clear();
        taskOrganiser.updateFile(taskMasterList);
        return CLEAR_MESSAGE;
    }

    private String undo() {
        assert statusSaver != null;
        if (statusSaver.isUndoMasterStackEmpty()) {
            return UNDO_LIMIT;
        }
        statusSaver.retrieveLastStatus();
        taskMasterList = statusSaver.getLastTaskMasterList();
        taskObservableList = statusSaver.getLastTaskObservableList();
        taskOrganiser.updateFile(taskMasterList);
        return UNDO_MESSAGE;
    }

    private String redo() {
        if (statusSaver.isRedoMasterStackEmpty()) {
            return REDO_LIMIT;
        }
        statusSaver.retrieveRedoStatus();
        taskMasterList = statusSaver.getLastTaskMasterList();
        taskObservableList = statusSaver.getLastTaskObservableList();
        taskOrganiser.updateFile(taskMasterList);
        return REDO_MESSAGE;
    }

    private String setUserPrefDirectory(String commandParameters) {
        return taskOrganiser.setUserPrefDirectory(commandParameters);
    }

    
    private String displayTask(String commandParameters) {
        if (commandParameters.equals(USER_INPUT_DISPLAY_COMPLETED)) {
            completeDisplayTrigger = true;
            projectDisplayTrigger = false;
            searchDisplayTrigger = false;
            allDisplayTrigger = false;
            return DISPLAY_COMPLETED_MESSAGE;
        } else if(commandParameters.equals(EMPTY_STRING) || commandParameters.equals(USER_INPUT_DISPLAY_MAIN)) {
            completeDisplayTrigger = false;
            projectDisplayTrigger = false;
            searchDisplayTrigger = false;
            allDisplayTrigger = false;
            return DISPLAY_MAIN_MESSAGE;
        } else if(commandParameters.equals(USER_INPUT_DISPLAY_ALL)) {
            completeDisplayTrigger = false;
            searchDisplayTrigger = false;
            projectDisplayTrigger = false;
            allDisplayTrigger = true;
            sortTaskMasterListWithIncomplete();
            taskObservableList.setAll(taskMasterList);
            return DISPLAY_ALL_MESSAGE;
        } else if (projectNameList.contains(commandParameters)) {
            projectDisplayTrigger = true;
            completeDisplayTrigger = false;
            searchDisplayTrigger = false;
            allDisplayTrigger = false;
            ObservableList<Task> projectTasks = FXCollections.observableArrayList();
            for (Task task : taskObservableList) {
                if (task.getProjectName().equals(commandParameters)) {
                    projectTasks.add(task);
                }
            }
            taskObservableList.setAll(projectTasks);
            return String.format(DISPLAY_MESSAGE, commandParameters);
        } else {
            return UNKNOWN_DISPLAY;
        }
    }

    private void searchTask(String commandParameters) {
        searchDisplayTrigger = true;
        ObservableList<Task> tempObservableList = FXCollections.observableArrayList();
        finiParser.parse(commandParameters);
        for (Task task : taskMasterList) {
            if (task.getTitle().contains(commandParameters)) {
                tempObservableList.add(task);
            }
        }
        taskObservableList.setAll(tempObservableList);
    }

    private String completeTask(int objectIndex) {
        Task taskToComplete;
        try {
            taskToComplete = taskObservableList.get(objectIndex - 1);
        } catch (IndexOutOfBoundsException e) {
            return TASK_NOT_FOUND;
        }

        if (taskToComplete.isRecurring() && taskToComplete.hasNext()) {
            Task copyTask = taskToComplete.makeCopy();
            copyTask.setIsComplete();
            copyTask.updateObjectID();

            for (Iterator<Task> iterator = taskMasterList.iterator(); iterator.hasNext(); ) {
                Task taskToRemove = iterator.next();
                if (!taskToRemove.getObjectID().equals(taskToComplete.getObjectID()) &&
                        taskToRemove.hasRecurUniqueID() &&
                        taskToRemove.getRecurUniqueID().equals(copyTask.getRecurUniqueID())) {
                    iterator.remove();
                }
            }
            taskMasterList.add(copyTask);
            taskToComplete.toNext();
        } else {
            taskToComplete.setIsComplete();
        }
        taskOrganiser.updateFile(taskMasterList);
        return String.format(COMPLETE_MESSAGE, objectIndex, taskToComplete.getTitle());
    }

    private String uncompleteTask(int objectIndex) {
        Task taskToUncomplete;
        try {
            taskToUncomplete = taskObservableList.get(objectIndex - 1);
        } catch (IndexOutOfBoundsException e) {
            return TASK_NOT_FOUND;
        }
        taskToUncomplete.setIncomplete();
        if (taskToUncomplete.isRecurring()) {
            for (Iterator<Task> iterator = taskMasterList.iterator(); iterator.hasNext(); ) {
                Task taskToRemove = iterator.next();
                if (!taskToRemove.getObjectID().equals(taskToUncomplete.getObjectID()) &&
                        taskToRemove.hasRecurUniqueID() &&
                        taskToRemove.getRecurUniqueID().equals(taskToUncomplete.getRecurUniqueID())) {
                    iterator.remove();
                }
            }
        }
        taskOrganiser.updateFile(taskMasterList);
        return String.format(UNCOMPLETE_MESSAGE, objectIndex, taskToUncomplete.getTitle());
    }

    private String loadNUSMods(String commandParameters) {
        File modsFile = new File(commandParameters);
        if (modsFile.exists()) {
            ModsLoader loader = new ModsLoader(modsFile);
            taskMasterList.addAll(loader.getLessonTasks());
        } else {
            return NO_MODS_FILE;
        }
        taskOrganiser.updateFile(taskMasterList);
        return MODS_LOADED;
    }

    private String displayHelpPanel() {
        displayController.displayHelpPanel();
        return HELP_MESSAGE;
    }

    /* ***********************************
     * Utilization methods
     * ***********************************/
    private void saveThisStatus() {
        assert taskMasterList != null;
        assert taskObservableList != null;
        statusSaver.saveStatus(taskMasterList, taskObservableList);
    }

    private void displayControl() {
        if (completeDisplayTrigger) {
            taskObservableList.setAll(getCompletedTasks());
            displayController.updateCompletedDisplay(taskObservableList);
        } else if (searchDisplayTrigger) {
            displayController.updateSearchDisplay(taskObservableList);
        } else if (allDisplayTrigger) {
            displayController.updateAllDisplay(taskObservableList);
        } else if (projectDisplayTrigger) {
            displayController.updateProjectDisplay(taskObservableList);
        } else {
            sortTaskMasterList();
            taskObservableList.setAll(getIncompleteTasks());
            displayController.updateMainDisplay(taskObservableList);
        }
    }

    private List<Task> getIncompleteTasks() {
        return taskMasterList.stream().filter(task -> !task.isCompleted()).collect(Collectors.toList());
    }

    private void sortTaskMasterList() {
        assert taskMasterList != null;
        sorter = new Sorter(taskMasterList);
        sorter.sort();
        taskMasterList = sorter.getSortedList();
    }

    private void sortTaskMasterListWithIncomplete() {
        assert taskMasterList != null;
        sorter = new Sorter(taskMasterList);
        sorter.addSortByIncomplete();
        sorter.sort();
        taskMasterList = sorter.getSortedList();
    }

    /* ***********************************
     * Getter and setters
     * ***********************************/
    public ObservableList<Task> getTaskObservableList() {
        return taskObservableList;
    }

    public void setRootController(DisplayController displayController) {
        this.displayController = displayController;
    }

    public DisplayController getRootController() {
        return displayController;
    }
}
