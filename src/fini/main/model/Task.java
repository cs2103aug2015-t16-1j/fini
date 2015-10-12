package fini.main.model;

import fini.main.MainApp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
  public static enum TaskType {
    FLOATING, DEADLINE, EVENT
  };

  private StringProperty title;
  private SimpleObjectProperty<LocalDate> date;
  private SimpleObjectProperty<LocalTime> startTime;
  private SimpleObjectProperty<LocalTime> endTime;
  private StringProperty priority;
  private StringProperty id;
  private TaskType type;

  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  /**
   * Default constructor.
   */
  public Task() {
    this(null);
  }

  /**
   * Constructor with some initial data.
   * @param title
   */
  public Task(String title) {
    this.title = new SimpleStringProperty(title);

    // Some initial dummy data, just for convenient testing.
    this.date = new SimpleObjectProperty<LocalDate>(LocalDate.of(2015, 9, 14));
    this.priority = new SimpleStringProperty("Normal");
    setTaskId();
  }

  public void setTaskId() {
    int taskId = MainApp.getTaskData().size() + 1;
    this.id = new SimpleStringProperty(Integer.toString(taskId));
  }

  public void setTaskId(int newIndex) {
    this.id = new SimpleStringProperty(Integer.toString(newIndex));
  }

  public Task(String title, String taskDetails) {
    this.title = new SimpleStringProperty(title);
    int indexOfPriority = checkIfPriorityExists(taskDetails);
    if(indexOfPriority > 0) {
      setDate(taskDetails, indexOfPriority);
      String priorityDetails = taskDetails.substring(indexOfPriority);
      priorityDetails = priorityDetails.replace("with priority", "");
      this.priority = new SimpleStringProperty(priorityDetails);
    } else {
      setDate(taskDetails);
    }
    
   // this.priority = new SimpleStringProperty("Normal");
  }

  private int checkIfPriorityExists(String taskDetails) {
    return taskDetails.indexOf("with priority");
  }

  private void setDate(String taskDetails) {
    String[] dateArray = taskDetails.split("/");
    int day = Integer.parseInt(dateArray[0]);
    int month = Integer.parseInt(dateArray[1]);
    int year = Integer.parseInt(dateArray[2]);
    this.date = new SimpleObjectProperty<LocalDate>(LocalDate.of(year, month, day));
  }
  
  private void setDate(String taskDetails, int indexOfPriorityDetails) {
    taskDetails = taskDetails.substring(0, indexOfPriorityDetails-1);
    String[] dateArray = taskDetails.split("/");
    int day = Integer.parseInt(dateArray[0]);
    int month = Integer.parseInt(dateArray[1]);
    int year = Integer.parseInt(dateArray[2]);
    this.date = new SimpleObjectProperty<LocalDate>(LocalDate.of(year, month, day));
  }
  
  public String getTitle() {
    return title.get();
  }

  public void setTitle(String title) {
    this.title.set(title);
  }

  public StringProperty getTitleProperty() {
    return title;
  }

  public StringProperty getIdProperty() {
    return id;
  }

  public LocalDate getDate() {
    return date.get();
  }

  public void setDate(LocalDate date) {
    this.date.set(date);
  }

  public String getPriority() {
    return priority.get();
  }

  public void setPriority(String priority) {
    this.priority.set(priority);
  }

  public StringProperty getPriorityProperty() {
    return priority;
  }

  public SimpleObjectProperty<LocalDate> getDateProperty() {
    return date;
  }

  public SimpleObjectProperty<LocalTime> getStartTimeProperty() {
    return startTime;
  }

  public SimpleObjectProperty<LocalTime> getEndTimeProperty() {
    return endTime;
  }
}
