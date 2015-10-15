package fini.main.view;

import java.util.logging.Level;
import java.util.logging.Logger;

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
  //  private ListView<HBox> listView;
  //  ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

  @FXML
  private TextField commandBox;

  @FXML
  private ListView<String> projectsOverviewPanel;

  @FXML
  private ListView<String> tasksOverviewPanel;

  @FXML
  private Label displayToUser;

  private static RootController rootController;
  private FiniParser parser;
  private Storage taskOrganiser;
  private String userInput;
  private Font defaultFont;
  private static Logger logger = Logger.getLogger("RootControllerLogger");

  public RootController() {
    listView = new ListView<HBox>();
    projectsOverviewPanel = new ListView<String>();
    tasksOverviewPanel = new ListView<String>();
    commandBox = new TextField();
    parser = FiniParser.getInstance();
    taskOrganiser = Storage.getInstance();
    defaultFont = new Font("Raleway", 14);
  }

  @FXML
  public void handleKeyPressEvent(KeyEvent event) throws Exception {
    if(event.getCode() == KeyCode.ENTER) {
      userInput = commandBox.getText();
      logger.log(Level.INFO, userInput);
      commandBox.clear();
      parser.parse(userInput);
      updateMainDisplay(taskOrganiser.getTasks());
      updateProjectsOverviewPanel(taskOrganiser.getTasks());
      //      updateTasksOverviewPanel(taskOrganiser.getTasks());
      logger.log(Level.INFO, "End of HandlingKeyPress");
    }
  }

  private void updateTasksOverviewPanel(ObservableList<Task> taskMasterList) {
    ObservableList<String> tasksOverview = FXCollections.observableArrayList();
  }

  private void updateProjectsOverviewPanel(ObservableList<Task> taskMasterList) {
    ObservableList<String> projectsOverview = FXCollections.observableArrayList();
    for(Task task: taskMasterList) {
      if(projectsOverview.contains(task.getProject()) == false) {
        projectsOverview.add(task.getProject());
      }
    }
    projectsOverviewPanel.setItems(projectsOverview);
  }

  public static RootController getInstance() {
    if(rootController == null) {
      rootController = new RootController();
    }
    return rootController;
  }

  public void updateMainDisplay(ObservableList<Task> taskMasterList) throws Exception {
    //ObservableList<String> taskList = FXCollections.observableArrayList();
    ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
    for(Task task: taskMasterList) {
      logger.log(Level.INFO, "Processing each task");
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
        logger.log(Level.INFO, "Recurring day is " + taskDate);
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

  public HBox addHBox(String typeOfTask, String taskTitle, String taskDate, String taskStartTime, String taskEndTime, String taskPriority, String taskProject, boolean isRecurringTask) {
    HBox hbox = new HBox();
    hbox.setSpacing(10);
    hbox.setStyle("-fx-background-color: #000080;");
    hbox.setStyle("-fx-font-family: Raleway;");

    Label title = new Label(taskTitle);
    Label date = new Label(taskDate);
    Label startTime = new Label(taskStartTime);
    Label endTime = new Label(taskEndTime);
    Label priority = new Label(taskPriority);
    Label project = new Label(taskProject);
    Label isRecurring = null;

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
      isRecurring = new Label("R");
      hbox.getChildren().addAll(isRecurring); 
    }
    return hbox;
  }
}
