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
  private SimpleStringProperty startTime;
  private SimpleStringProperty endTime;
  private StringProperty priority;
  private StringProperty id;
  private SimpleStringProperty recurringDay;
  private TaskType type;

  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
  private SimpleStringProperty deadline;

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
    //this.date = new SimpleObjectProperty<LocalDate>(LocalDate.of(2015, 9, 14));
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
    int indexOfStartTime = checkIfStartTimeExists(taskDetails);
    int indexOfEndTime = checkIfEndTimeExists(taskDetails);
    int indexOfDeadline = checkIfOnlyDeadlineExists(taskDetails);
    int indexOfRecurringTask = checkIfRecurringDeadlineExists(taskDetails);

    if(indexOfDeadline > 0) {
      String date = taskDetails.substring(0, indexOfDeadline-1);
      setDate(date);
      if(indexOfPriority > 0) {
        String deadlineDetails = taskDetails.substring(indexOfDeadline, indexOfPriority-1);
        deadlineDetails = deadlineDetails.replace("at ", "");
        String priorityDetails = taskDetails.substring(indexOfPriority);
        this.startTime = new SimpleStringProperty(deadlineDetails);
        priorityDetails = priorityDetails.replace("with priority", "");
        this.priority = new SimpleStringProperty(priorityDetails.toUpperCase());
      }
    }

    if(indexOfRecurringTask >= 0) {
      String dateDetails = taskDetails;
      System.out.println(dateDetails);
      if(indexOfPriority > 0) {
        dateDetails = taskDetails.substring(indexOfRecurringTask, indexOfPriority);
      }
      dateDetails = dateDetails.replace("every ", "");
      dateDetails = dateDetails.toUpperCase();
      String storeRecurringDay = "";
      System.out.println(dateDetails);
      if (dateDetails.equals("MONDAY")) {
        storeRecurringDay = "Mon";
      } else if (dateDetails.equals("TUESDAY")) {
        storeRecurringDay = "Tue";
      } else if (dateDetails.equals("WEDNESDAY")) {
        storeRecurringDay = "Wed";
      } else if (dateDetails.equals("THURSDAY")) {
        storeRecurringDay = "Thu";
      } else if (dateDetails.equals("FRIDAY")) {
        storeRecurringDay = "Fri";
      } else if (dateDetails.equals("SATURDAY")) {
        storeRecurringDay = "Sat";
      } else if (dateDetails.equals("SUNDAY")) {
        storeRecurringDay = "Sun";
      } 
      this.recurringDay = new SimpleStringProperty(storeRecurringDay);
    }

    if(indexOfPriority > 0) {
      String priorityDetails = taskDetails.substring(indexOfPriority);
      priorityDetails = priorityDetails.replace("with priority", "");
      this.priority = new SimpleStringProperty(priorityDetails.toUpperCase());
    }
  

  if((indexOfStartTime > 0) && (indexOfEndTime > 0)) {
    String dateDetails = taskDetails.substring(0, indexOfStartTime - 1);
    setDate(dateDetails);
    String startTimeDetails = taskDetails.substring(indexOfStartTime, indexOfEndTime-1);
    startTimeDetails = startTimeDetails.replace("from ", "");
    this.startTime = new SimpleStringProperty(startTimeDetails);
    if (indexOfPriority > 0) {
      String endTimeDetails = taskDetails.substring(indexOfEndTime, indexOfPriority - 1);
      this.endTime = new SimpleStringProperty(endTimeDetails.replace("to ", ""));
      String priorityDetails = taskDetails.substring(indexOfPriority);
      priorityDetails = priorityDetails.replace("with priority", "");
      this.priority = new SimpleStringProperty(priorityDetails.toUpperCase());
    } else {
      String endTimeDetails = taskDetails.substring(indexOfEndTime); 
      this.endTime = new SimpleStringProperty(endTimeDetails.replace("to ", ""));
    }
  }

  //    if(indexOfPriority > 0) {
  //      setDate(taskDetails, indexOfPriority);
  //      String priorityDetails = taskDetails.substring(indexOfPriority);
  //      priorityDetails = priorityDetails.replace("with priority", "");
  //      this.priority = new SimpleStringProperty(priorityDetails.toUpperCase());
  //    } else {
  //      setDate(taskDetails);
  //    }

  // this.priority = new SimpleStringProperty("Normal");
  setTaskId();
}

private int checkIfRecurringDeadlineExists(String taskDetails) {
  return taskDetails.indexOf("every");
}

private int checkIfOnlyDeadlineExists(String taskDetails) {
  return taskDetails.indexOf("at");
}

private int checkIfStartTimeExists(String taskDetails) {
  return taskDetails.indexOf("from");
}

private int checkIfEndTimeExists(String taskDetails) {
  return taskDetails.indexOf("to");
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

public SimpleStringProperty getStartTimeProperty() {
  return startTime;
}

public SimpleStringProperty getEndTimeProperty() {
  return endTime;
}

public SimpleStringProperty getRecurringProperty() {
  return recurringDay;
}
}
