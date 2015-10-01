package onej.t16.fini.model;

import java.time.LocalDate;

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
	private final StringProperty taskTitle;
	private final ObjectProperty<LocalDate> taskDate;
	private final StringProperty taskPriority;
	private final StringProperty taskGroup;
	
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
		this.taskPriority = new SimpleStringProperty("VERY HIGH");
		this.taskGroup = new SimpleStringProperty("OUR GROUP");
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
