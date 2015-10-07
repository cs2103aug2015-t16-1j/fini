package fini.main.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Model class for a Task
 * @author gaieepo
 *
 */
public class Task {
	private String taskTitle;
	private SimpleObjectProperty<LocalDate> endDate;
	private SimpleObjectProperty<LocalDate> startDate;
	private String taskPriority;
	private String taskGroup;
	
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
		this.taskTitle = new String(taskTitle);
		
		// Some initial dummy data, just for convenient testing.
		this.startDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(2015, 9, 13));
		this.endDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(2015, 9, 14));
		this.taskPriority = new String("Normal");
		this.taskGroup = new String("Uncategorized");
	}
	
	public Task(String taskTitle, String taskDetails) {
		this.taskTitle = new String(taskTitle);
		setTaskEndDate(taskDetails);
		this.taskGroup = new String("Events");
		this.taskPriority = new String("Normal");
	}
	
	public Task(String taskTitle, String startDetails, String endDetails) {
		this.taskTitle = new String(taskTitle);
		setTaskStartDate(startDetails);
		setTaskEndDate(endDetails);
		this.taskGroup = new String("Events");
		this.taskPriority = new String("Normal");
	}
	
	private void setTaskEndDate(String taskDetails) {
		String[] dateArray = taskDetails.split("/");
	    int day = Integer.parseInt(dateArray[0]);
	    int month = Integer.parseInt(dateArray[1]);
	    int year = Integer.parseInt(dateArray[2]);
	    this.endDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(year, month, day));
	}

	private void setTaskStartDate(String taskDetails) {
		String[] dateArray = taskDetails.split("/");
	    int day = Integer.parseInt(dateArray[0]);
	    int month = Integer.parseInt(dateArray[1]);
	    int year = Integer.parseInt(dateArray[2]);
	    this.startDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(year, month, day));
	}
	
  public String getTaskTitle() {
		return new String(taskTitle);
	}
	
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = new String(taskTitle);
	}
	
	public String taskTitleProperty() {
		return taskTitle;
	}
	
	public LocalDate getStartDate() {
		return startDate.get();
	}
	
	public void setStartDate(LocalDate startDate) {
		this.startDate.set(startDate);
	}
	
	public ObjectProperty<LocalDate> startDateProperty() {
		return startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate.get();
	}
	
	public void setEndDate(LocalDate endDate) {
		this.endDate.set(endDate);
	}
	
	public ObjectProperty<LocalDate> endDateProperty() {
		return endDate;
	}
	
	public String getTaskPriority() {
		return new String(taskPriority);
	}
	
	public void setTaskPriority(String taskPriority) {
		this.taskPriority = new String(taskPriority);
	}
	
	public String taskPriorityProperty() {
		return taskPriority;
	}
	
	public String getTaskGroup() {
		return new String(taskGroup);
	}
	
	public void setTaskGroup(String taskGroup) {
		this.taskGroup = new String(taskGroup);
	}
	
	public String taskGroupProperty() {
		return taskGroup;
	}
}
