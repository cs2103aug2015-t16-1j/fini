package fini.main.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import fini.main.MainApp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Task
 * @author gaieepo
 *
 */
public class Task {
	private StringProperty taskTitle;
	private SimpleObjectProperty<LocalDate> taskDate;
	private final StringProperty taskPriority;
	private final StringProperty taskGroup;
	private final int taskId;
	
	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	/**
	 * Default constructor.
	 */
	public Task() {
		this(null);
	}
	
	/**
	 * Constructor with some initial data.
	 * @param taskTitle
	 */
	public Task(String taskTitle) {
		this.taskTitle = new SimpleStringProperty(taskTitle);
		
		// Some initial dummy data, just for convenient testing.
		this.taskDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(2015, 9, 14));
		this.taskPriority = new SimpleStringProperty("Normal");
		this.taskGroup = new SimpleStringProperty("Inbox");
	}
	
	public Task(String taskTitle, String taskDetails) {
	  this.taskTitle = new SimpleStringProperty(taskTitle);
	  setTaskDate(taskDetails);
	  this.taskGroup = new SimpleStringProperty("Tasks");
	  this.taskPriority = new SimpleStringProperty("Normal");
	}

  private void setTaskDate(String taskDetails) {
    String[] dateArray = taskDetails.split("/");
    int day = Integer.parseInt(dateArray[0]);
    int month = Integer.parseInt(dateArray[1]);
    int year = Integer.parseInt(dateArray[2]);
    this.taskDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(year, month, day));
  }

  public String getTaskTitle() {
		return taskTitle.get();
	}
	
	public void setTaskTitle(String taskTitle) {
		this.taskTitle.set(taskTitle);
	}
	
	public StringProperty taskTitleProperty() {
		return taskTitle;
	}
	
	
	public LocalDate getTaskDate() {
		return taskDate.get();
	}
	
	public void setTaskDate(LocalDate taskDate) {
		this.taskDate.set(taskDate);
	}
	
	public ObjectProperty<LocalDate> taskDateProperty() {
		return taskDate;
	}
	
	public String getTaskPriority() {
		return taskPriority.get();
	}
	
	public void setTaskPriority(String taskPriority) {
		this.taskPriority.set(taskPriority);
	}
	
	public StringProperty taskPriorityProperty() {
		return taskPriority;
	}
	
	public String getTaskGroup() {
		return taskGroup.get();
	}
	
	public void setTaskGroup(String taskGroup) {
		this.taskGroup.set(taskGroup);
	}
	
	public StringProperty taskGroupProperty() {
		return taskGroup;
	}
}
